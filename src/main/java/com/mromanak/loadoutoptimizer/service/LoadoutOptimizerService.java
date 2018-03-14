package com.mromanak.loadoutoptimizer.service;

import com.google.common.collect.Sets;
import com.mromanak.loadoutoptimizer.impl.LoadoutOptimizer;
import com.mromanak.loadoutoptimizer.model.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.ArmorType;
import com.mromanak.loadoutoptimizer.model.Loadout;
import com.mromanak.loadoutoptimizer.model.api.LoadoutRequest;
import com.mromanak.loadoutoptimizer.model.api.SkillWeight;
import com.mromanak.loadoutoptimizer.scoring.SimpleLoadoutScoringFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.mromanak.loadoutoptimizer.scoring.LoadoutScoringUtils.simpleSkillWeightFunction;

@Service
public class LoadoutOptimizerService {

    private final ArmorPieceService armorPieceService;

    @Autowired
    public LoadoutOptimizerService(ArmorPieceService armorPieceService) {
        this.armorPieceService = armorPieceService;
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
        request.setLoadoutSizeWeight(-1.0/ ArmorType.values().length);
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
        return LoadoutOptimizer.findBestLoadouts(armorPieces, scoringFunction);
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
}
