package com.mromanak.loadoutoptimizer.scoring;

import com.mromanak.loadoutoptimizer.model.ArmorType;

import java.util.function.Function;

public abstract class LoadoutScoringUtils {

    private static final Function<Integer, Double> ZERO_WEIGHT_FUNCTION = i -> 0.0;

    /**
     * @return a weighting function that always returns 0
     */
    public static Function<Integer, Double> zeroWeightFunction() {
        return ZERO_WEIGHT_FUNCTION;
    }

    public static Function<Integer, Double> simpleSkillWeightFunction(double weight, int skillMaximum) {
        return skillLevel -> (skillLevel > skillMaximum) ? 0.0 : weight * skillLevel;
    }

    public static Function<Integer, Double> preferSmallerLoadouts(double weight) {
        return loadoutSize -> weight * (1.0 - (double) loadoutSize / ArmorType.values().length);
    }
}
