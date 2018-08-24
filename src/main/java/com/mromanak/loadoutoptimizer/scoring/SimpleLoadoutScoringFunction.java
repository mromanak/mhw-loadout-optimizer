package com.mromanak.loadoutoptimizer.scoring;

import com.google.common.collect.ImmutableMap;
import com.mromanak.loadoutoptimizer.model.Loadout;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.mromanak.loadoutoptimizer.scoring.LoadoutScoringUtils.zeroWeightFunction;

@Data
public class SimpleLoadoutScoringFunction implements LoadoutScoringFunction {

    private final Map<String, Function<Integer, Double>> skillWieghtFunctions;
    private final double level1SlotWeight;
    private final double level2SlotWeight;
    private final double level3SlotWeight;
    private final Function<Integer, Double> loadoutSizeWeightFunction;

    private SimpleLoadoutScoringFunction(Builder builder) {
        skillWieghtFunctions = ImmutableMap.copyOf(builder.skillWieghtingFunctions);
        level1SlotWeight = builder.level1SlotWeight;
        level2SlotWeight = builder.level2SlotWeight;
        level3SlotWeight = builder.level3SlotWeight;
        loadoutSizeWeightFunction = builder.loadoutSizeWeightFunction;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SimpleLoadoutScoringFunction copy) {
        Builder builder = new Builder();
        builder.skillWieghtingFunctions = new HashMap<>(copy.skillWieghtFunctions);
        builder.level1SlotWeight = copy.level1SlotWeight;
        builder.level2SlotWeight = copy.level2SlotWeight;
        builder.level3SlotWeight = copy.level3SlotWeight;
        builder.loadoutSizeWeightFunction = copy.loadoutSizeWeightFunction;
        return builder;
    }

    @Override
    public Set<String> getDesiredSkills() {
        return skillWieghtFunctions.keySet();
    }

    @Override
    public Double apply(Loadout loadout) {
        if(loadout == null) {
            throw new NullPointerException("Loadout must not be null");
        }
        
        double score = 0;
        for(Map.Entry<String, Function<Integer, Double>> entry : skillWieghtFunctions.entrySet()) {
            String skillName = entry.getKey();
            int skillLevel = loadout.getSkills().getOrDefault(skillName, 0);
            Function<Integer, Double> weightFunction = entry.getValue();
            score += weightFunction.apply(skillLevel);
        }
        
        score += level1SlotWeight * loadout.getLevel1Slots();
        score += level2SlotWeight * loadout.getLevel2Slots();
        score += level3SlotWeight * loadout.getLevel3Slots();

        score += loadoutSizeWeightFunction.apply(loadout.getArmorPieces().size());

        return score;
    }

    public static final class Builder {
        private Map<String, Function<Integer, Double>> skillWieghtingFunctions = new HashMap<>();
        private double level1SlotWeight = 0.0;
        private double level2SlotWeight = 0.0;
        private double level3SlotWeight = 0.0;
        private Function<Integer, Double> loadoutSizeWeightFunction = zeroWeightFunction();

        private Builder() {
        }

        public Builder withSkillWieghtingFunctions(Map<String, Function<Integer, Double>> skillWieghtFunctions) {
            if(skillWieghtFunctions == null) {
                throw new NullPointerException("Skill weight functions map must not be null");
            }

            this.skillWieghtingFunctions = skillWieghtFunctions;
            return this;
        }

        public Builder withSkillWeightFunction(String skillName, Function<Integer, Double> weightFunction) {
            if(skillName == null) {
                throw new NullPointerException("Skill name must not be null");
            } else if (weightFunction == null) {
                throw new NullPointerException("Skill weight function must not be null");
            }
            skillWieghtingFunctions.put(skillName, weightFunction);
            return this;
        }

        public Builder withLevel1SlotWeight(double val) {
            level1SlotWeight = val;
            return this;
        }

        public Builder withLevel2SlotWeight(double val) {
            level2SlotWeight = val;
            return this;
        }

        public Builder withLevel3SlotWeight(double val) {
            level3SlotWeight = val;
            return this;
        }

        public Builder withLoadoutSizeWeightFunction(Function<Integer, Double> loadoutSizeWeightFunction) {
            if(loadoutSizeWeightFunction == null) {
                throw new NullPointerException("Loadout size weight function cannot be null");
            }

            this.loadoutSizeWeightFunction = loadoutSizeWeightFunction;
            return this;
        }

        public SimpleLoadoutScoringFunction build() {
            return new SimpleLoadoutScoringFunction(this);
        }
    }
}
