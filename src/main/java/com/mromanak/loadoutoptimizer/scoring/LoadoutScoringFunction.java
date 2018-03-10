package com.mromanak.loadoutoptimizer.scoring;

import com.mromanak.loadoutoptimizer.model.Loadout;

import java.util.function.Function;

public interface LoadoutScoringFunction extends Function<Loadout, Double> {
}
