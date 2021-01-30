package com.mromanak.loadoutoptimizer.scoring;

import com.mromanak.loadoutoptimizer.model.Loadout;

public interface LoadoutScoringFunction {

    String keyFor(Loadout loadout);

    double scoreFor(Loadout loadout);
}
