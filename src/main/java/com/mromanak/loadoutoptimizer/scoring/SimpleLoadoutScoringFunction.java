package com.mromanak.loadoutoptimizer.scoring;

import com.google.common.collect.ImmutableMap;
import com.mromanak.loadoutoptimizer.model.Loadout;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.mromanak.loadoutoptimizer.scoring.LoadoutScoringUtils.zeroWeightFunction;

@Data
public class SimpleLoadoutScoringFunction implements LoadoutScoringFunction {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleLoadoutScoringFunction.class);

    private final Map<String, Function<Integer, Double>> skillWieghtFunctions;
    private final double level1SlotWeight;
    private final double level2SlotWeight;
    private final double level3SlotWeight;
    private final double level4SlotWeight;
    private final double defenseWeight;
    private final double fireResistanceWeight;
    private final double waterResistanceWeight;
    private final double thunderResistanceWeight;
    private final double iceResistanceWeight;
    private final double dragonResistanceWeight;
    private final Function<Integer, Double> loadoutSizeWeightFunction;

    private SimpleLoadoutScoringFunction(Builder builder) {
        skillWieghtFunctions = ImmutableMap.copyOf(builder.skillWieghtingFunctions);
        level1SlotWeight = builder.level1SlotWeight;
        level2SlotWeight = builder.level2SlotWeight;
        level3SlotWeight = builder.level3SlotWeight;
        level4SlotWeight = builder.level4SlotWeight;
        defenseWeight = builder.defenseWeight;
        fireResistanceWeight = builder.fireResistanceWeight;
        waterResistanceWeight = builder.waterResistanceWeight;
        thunderResistanceWeight = builder.thunderResistanceWeight;
        iceResistanceWeight = builder.iceResistanceWeight;
        dragonResistanceWeight = builder.dragonResistanceWeight;
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
        builder.level4SlotWeight = copy.level4SlotWeight;
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
        score += level4SlotWeight * loadout.getLevel4Slots();
        score += defenseWeight * loadout.getDefense();
        score += fireResistanceWeight * loadout.getFireResistance();
        score += waterResistanceWeight * loadout.getWaterResistance();
        score += thunderResistanceWeight * loadout.getThunderResistance();
        score += iceResistanceWeight * loadout.getIceResistance();
        score += dragonResistanceWeight * loadout.getDragonResistance();

        score += loadoutSizeWeightFunction.apply(loadout.getArmorPieces().size());

        return score;
    }

    @Override
    public boolean needsDefense() {
        return defenseWeight != 0.0;
    }

    @Override
    public boolean needsFireResistance() {
        return fireResistanceWeight != 0.0;
    }

    @Override
    public boolean needsWaterResistance() {
        return waterResistanceWeight != 0.0;
    }

    @Override
    public boolean needsThunderResistance() {
        return thunderResistanceWeight != 0.0;
    }

    @Override
    public boolean needsIceResistance() {
        return iceResistanceWeight != 0.0;
    }

    @Override
    public boolean needsDragonResistance() {
        return dragonResistanceWeight != 0.0;
    }

    public static final class Builder {
        private Map<String, Function<Integer, Double>> skillWieghtingFunctions = new HashMap<>();
        private double level1SlotWeight = 0.0;
        private double level2SlotWeight = 0.0;
        private double level3SlotWeight = 0.0;
        private double level4SlotWeight = 0.0;
        private double defenseWeight = 0.0;
        private double fireResistanceWeight = 0.0;
        private double waterResistanceWeight = 0.0;
        private double thunderResistanceWeight = 0.0;
        private double iceResistanceWeight = 0.0;
        private double dragonResistanceWeight = 0.0;
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

        public Builder withLevel4SlotWeight(double val) {
            level4SlotWeight = val;
            return this;
        }

        public Builder withDefenseWeight(double val) {
            defenseWeight = val;
            return this;
        }

        public Builder withFireResistanceWeight(double val) {
            fireResistanceWeight = val;
            return this;
        }

        public Builder withWaterResistanceWeight(double val) {
            waterResistanceWeight = val;
            return this;
        }

        public Builder withThunderResistanceWeight(double val) {
            thunderResistanceWeight = val;
            return this;
        }

        public Builder withIceResistanceWeight(double val) {
            iceResistanceWeight = val;
            return this;
        }

        public Builder withDragonResistanceWeight(double val) {
            dragonResistanceWeight = val;
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
