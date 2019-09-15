package com.mromanak.loadoutoptimizer.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mromanak.loadoutoptimizer.impl.LoadoutOptimizer;
import com.mromanak.loadoutoptimizer.model.Loadout;
import com.mromanak.loadoutoptimizer.model.api.LoadoutRequest;
import com.mromanak.loadoutoptimizer.model.api.SkillWeight;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.model.jpa.SetBonusSkill;
import com.mromanak.loadoutoptimizer.scoring.SimpleLoadoutScoringFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.mromanak.loadoutoptimizer.model.jpa.ArmorType.hasNextArmorType;
import static com.mromanak.loadoutoptimizer.model.jpa.ArmorType.nextArmorType;
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
        request.setLevel4SlotWeight(0.5);
        request.setLoadoutSizeWeight(-0.25);
        return request;
    }

    public List<Loadout> optimize(LoadoutRequest loadoutRequest) {
        SimpleLoadoutScoringFunction scoringFunction = scoringFunctionFor(loadoutRequest);

        Set<String> desiredSkills = scoringFunction.getSkillWieghtFunctions().keySet();
        List<Pattern> excludePatterns = loadoutRequest.getExcludePatterns();
        Predicate<ArmorPiece> filter = (ArmorPiece armorPiece) -> {
            boolean isIgnored = excludePatterns.stream().
                anyMatch((excludePattern) -> excludePattern.matcher(armorPiece.getName()).matches());
            return !isIgnored;
        };

        Set<ArmorPiece> armorPieces = armorPieceService.getArmorPiecesWithSkillsNamed(desiredSkills, filter);

        if(loadoutRequest.getSetBonus() != null) {
            List<Loadout> startingLoadouts = generateStartingLoadoutsForSetBonus(loadoutRequest.getSetBonus(), loadoutRequest.getExcludePatterns());
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
            withLevel4SlotWeight(loadoutRequest.getLevel4SlotWeight()).
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

    private List<Loadout> generateStartingLoadoutsForSetBonus(String bonusName, List<Pattern> excludePatterns) {
        Set<SetBonusSkill> setBonusSkills = setBonusService.getSetBonusSkillsWithSkillsNamed(bonusName);
        if(setBonusSkills.isEmpty()) {
            throw new IllegalArgumentException("Could not find a set bonus that provides skill " + bonusName);
        }

        List<Loadout> loadouts = new ArrayList<>();

        for(SetBonusSkill setBonusSkill : setBonusSkills) {
            Set<ArmorPiece> armorPieces = setBonusSkill.getSetBonus().getArmorPieces().stream().
                filter((ArmorPiece armorPiece) -> {
                    return excludePatterns.stream().noneMatch((Pattern pattern) -> {
                        return pattern.matcher(armorPiece.getName()).matches();
                    });
                }).
                collect(toSet());
            Set<String> armorPieceNames = armorPieces.stream().
                map(ArmorPiece::getName).
                collect(toSet());
            int minimumPieces = setBonusSkill.getRequiredPieces();

            if (armorPieceNames.size() != armorPieces.size()) {
                Set<String> loadedArmorPieceNames = armorPieces.stream().
                    map(ArmorPiece::getName).
                    collect(toSet());
                Set<String> missingArmorPieceNames = Sets.difference(armorPieceNames, loadedArmorPieceNames);
                int missingPiecesCount = armorPieces.size() - missingArmorPieceNames.size();
                throw new IllegalStateException(
                    missingPiecesCount + " armor pieces for set bonus " + bonusName + " (" +
                        setBonusSkill.getSetBonus().getName() + ") were not found: " + missingArmorPieceNames);
            }
            loadouts.addAll(generateCombinations(armorPieces, minimumPieces));
        }

        return loadouts;
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
