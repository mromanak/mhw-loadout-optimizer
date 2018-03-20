package com.mromanak.loadoutoptimizer.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mromanak.loadoutoptimizer.impl.LoadoutOptimizer;
import com.mromanak.loadoutoptimizer.model.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.ArmorType;
import com.mromanak.loadoutoptimizer.model.Loadout;
import com.mromanak.loadoutoptimizer.model.SetBonus;
import com.mromanak.loadoutoptimizer.model.api.LoadoutRequest;
import com.mromanak.loadoutoptimizer.model.api.SkillWeight;
import com.mromanak.loadoutoptimizer.scoring.SimpleLoadoutScoringFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.mromanak.loadoutoptimizer.model.ArmorType.hasNextArmorType;
import static com.mromanak.loadoutoptimizer.model.ArmorType.nextArmorType;
import static com.mromanak.loadoutoptimizer.scoring.LoadoutScoringUtils.simpleSkillWeightFunction;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.*;

@Service
public class LoadoutOptimizerService {

    private final ArmorPieceService armorPieceService;
    private final SetBonusService setBonusService;

    @Autowired
    public LoadoutOptimizerService(ArmorPieceService armorPieceService, SetBonusService setBonusService) {
        this.armorPieceService = armorPieceService;
        this.setBonusService = setBonusService;
    }

    public LoadoutRequest getSampleRequest() {
        Map<String, SkillWeight> skillWeights = new HashMap<>();
        skillWeights.put("Earplugs", new SkillWeight(5, 1.0));
        skillWeights.put("Windproof", new SkillWeight(3, 1.0));
        skillWeights.put("Tremor Resistance", new SkillWeight(3, 1.0));

        LoadoutRequest request = new LoadoutRequest();
        request.setSkillWeights(skillWeights);
        request.setLevel1SlotWeight(0.25);
        request.setLevel2SlotWeight(0.5);
        request.setLevel3SlotWeight(0.5);
        request.setLoadoutSizeWeight(-0.25);
        return request;
    }

    public List<Loadout> optimize(LoadoutRequest loadoutRequest) {
        SimpleLoadoutScoringFunction scoringFunction = scoringFunctionFor(loadoutRequest);

        Set<String> desiredSkills = scoringFunction.getSkillWieghtFunctions().keySet();
        Pattern namesToIgnorePattern = Pattern.compile("^$");
        Predicate<ArmorPiece> filter = (ArmorPiece armorPiece) -> {
            boolean hasDesiredSkill = !Sets.intersection(desiredSkills, armorPiece.getSkills().keySet()).isEmpty();
            boolean isIgnored = namesToIgnorePattern.matcher(armorPiece.getName()).matches();
            return hasDesiredSkill && !isIgnored;
        };

        Set<ArmorPiece> armorPieces = armorPieceService.getArmorPieces(filter);

        if(loadoutRequest.getSetBonus() != null) {
            List<Loadout> startingLoadouts = generateStartingLoadoutsForSetBonus(loadoutRequest.getSetBonus());
            return LoadoutOptimizer.findBestLoadoutsGiven(startingLoadouts, armorPieces, scoringFunction);
        } else {
            return LoadoutOptimizer.findBestLoadouts(armorPieces, scoringFunction);
        }
    }

    private SimpleLoadoutScoringFunction scoringFunctionFor(LoadoutRequest loadoutRequest) {
        validateSkillWeights(loadoutRequest);

        SimpleLoadoutScoringFunction.Builder scoringFunctionBuilder = SimpleLoadoutScoringFunction.builder();
        for(Map.Entry<String, SkillWeight> entry : loadoutRequest.getSkillWeights().entrySet()) {
            String skillName = entry.getKey();
            double weight = entry.getValue().getWeight();
            int maximum = entry.getValue().getMaximum();
            scoringFunctionBuilder.withSkillWeightFunction(skillName, simpleSkillWeightFunction(weight, maximum));
        }

        double loadoutSizeWeight = loadoutRequest.getLoadoutSizeWeight();

        return scoringFunctionBuilder.
            withLevel1SlotWeight(loadoutRequest.getLevel1SlotWeight()).
            withLevel2SlotWeight(loadoutRequest.getLevel2SlotWeight()).
            withLevel3SlotWeight(loadoutRequest.getLevel3SlotWeight()).
            withLoadoutSizeWeightFunction(size -> loadoutSizeWeight * size).
            build();
    }

    private void validateSkillWeights(LoadoutRequest loadoutRequest) {
        Map<String, SkillWeight> skillWeights = loadoutRequest.getSkillWeights();
        if(skillWeights == null) {
            throw new IllegalArgumentException("Loadout request must include at least one desired skill");
        }
        skillWeights.entrySet().removeIf((Map.Entry<String, SkillWeight> entry) -> {
            SkillWeight skillWeight = entry.getValue();
            return skillWeight == null ||
                skillWeight.getMaximum() <= 0 &&
                skillWeight.getWeight() == 0.0;
        });
        if(skillWeights.isEmpty()) {
            throw new IllegalArgumentException("Loadout request must include at least one desired skill");
        }
    }

    public List<Loadout> generateStartingLoadoutsForSetBonus(String bonusName) {
        Optional<SetBonus> setBonusOpt = setBonusService.findSetBonus(bonusName);
        if(!setBonusOpt.isPresent()) {
            throw new IllegalArgumentException("Could not find a set bonus that provides skill " + bonusName);
        }

        SetBonus setBonus = setBonusOpt.get();
        Set<String> armorPieceNames = setBonus.getArmorPieces();
        Set<ArmorPiece> armorPieces = armorPieceService.getArmorPieces(ap -> armorPieceNames.contains(ap.getName()));
        int minimumPieces = setBonus.getBonusRequirements().get(bonusName);

        if(armorPieceNames.size() != armorPieces.size()) {
            Set<String> loadedArmorPieceNames = armorPieces.stream().
                map(ArmorPiece::getName).
                collect(toSet());
            Set<String> missingArmorPieceNames = Sets.difference(armorPieceNames, loadedArmorPieceNames);
            throw new IllegalStateException(
                missingArmorPieceNames.size() + " armor pieces for set bonus " + bonusName + " (" + setBonus.getName() +
                    ") were not found: " + missingArmorPieceNames);
        }

        return generateCombinations(armorPieces, minimumPieces);
    }

    private List<Loadout> generateCombinations(Set<ArmorPiece> armorPieces, int minimumPieces) {
        Map<ArmorType, List<ArmorPiece>> armorPiecesMap = armorPieces.stream().
            collect(toMap(
                ArmorPiece::getArmorType,
                ImmutableList::of,
                (l1, l2) -> ImmutableList.<ArmorPiece>builder().addAll(l1).addAll(l2).build()
            ));

        return generateCombinations(Loadout.empty(), armorPiecesMap, nextArmorType(null)).stream().
            filter(l -> l.getArmorPieces().size() >= minimumPieces).
            collect(toList());
    }

    private List<Loadout> generateCombinations(Loadout currentLoadout, Map<ArmorType, List<ArmorPiece>> armorPiecesMap,
        ArmorType armorType)
    {
        if(hasNextArmorType(armorType)) {
            List<ArmorPiece> currentArmorPieces = armorPiecesMap.getOrDefault(armorType, emptyList());
            if(currentArmorPieces.isEmpty()) {
                return generateCombinations(currentLoadout, armorPiecesMap, nextArmorType(armorType));
            }

            List<Loadout> loadoutsWithNextPiece = generateCombinations(currentLoadout, armorPiecesMap, nextArmorType(armorType));
            List<Loadout> loadoutsToReturn = new ArrayList<>(loadoutsWithNextPiece);
            for (Loadout loadoutWithNextPiece : loadoutsWithNextPiece) {
                for (ArmorPiece armorPiece : currentArmorPieces) {
                    loadoutsToReturn.add(Loadout.builder(loadoutWithNextPiece).
                        withArmorPiece(armorPiece).
                        build());
                }
            }
            return loadoutsToReturn;
        } else {
            List<ArmorPiece> currentArmorPieces = armorPiecesMap.getOrDefault(armorType, emptyList());
            List<Loadout> loadoutsToReturn = new ArrayList<>();
            loadoutsToReturn.add(currentLoadout);
            for (ArmorPiece armorPiece : currentArmorPieces) {
                loadoutsToReturn.add(Loadout.builder().
                    withArmorPiece(armorPiece).
                    build());
            }
            return loadoutsToReturn;
        }
    }
}
