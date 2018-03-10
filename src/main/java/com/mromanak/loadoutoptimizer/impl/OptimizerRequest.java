package com.mromanak.loadoutoptimizer.impl;

import com.google.common.collect.ImmutableMap;
import com.mromanak.loadoutoptimizer.model.ArmorType;
import com.mromanak.loadoutoptimizer.model.Loadout;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class OptimizerRequest {

    private final Map<String, Integer> skills;
    private final ArmorType armorType;

    public static OptimizerRequest forLoadout(Loadout loadout, ArmorType armorType) {
        Map<String, Integer> skills = (loadout == null) ? ImmutableMap.of() : ImmutableMap.copyOf(loadout.getSkills());
        return new OptimizerRequest(skills, armorType);
    }
}
