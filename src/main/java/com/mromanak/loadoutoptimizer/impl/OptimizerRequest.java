package com.mromanak.loadoutoptimizer.impl;

import com.google.common.collect.ImmutableMap;
import com.mromanak.loadoutoptimizer.model.ArmorType;
import com.mromanak.loadoutoptimizer.model.Loadout;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class OptimizerRequest {

    private final Map<String, Integer> skills;
    private final ArmorType armorType;

    private OptimizerRequest(Builder builder) {
        skills = ImmutableMap.copyOf(builder.skills);
        armorType = builder.armorType;
    }

    public static Builder builderForLoadout(Loadout loadout, ArmorType armorType) {
        if(loadout == null) {
            throw new NullPointerException("Loadout must not be null");
        }

        return builder().
            withSkills(loadout.getSkills()).
            withArmorType(armorType);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(OptimizerRequest copy) {
        Builder builder = new Builder();
        builder.skills = copy.getSkills();
        builder.armorType = copy.getArmorType();
        return builder;
    }

    public static final class Builder {
        private Map<String, Integer> skills = new HashMap<>();
        private ArmorType armorType;

        private Builder() {
        }

        public Builder retainSkills(Set<String> skillNames) {
            skills.entrySet().removeIf(e -> e.getKey() != null && !skillNames.contains(e.getKey()));
            return this;
        }

        public Builder withSkills(Map<String, Integer> skills) {
            if (skills == null) {
                throw new NullPointerException("Skills map must not be null");
            }
            this.skills = new HashMap<>(skills);
            return this;
        }

        public Builder withArmorType(ArmorType val) {
            armorType = val;
            return this;
        }

        public OptimizerRequest build() {
            if (armorType == null) {
                throw new NullPointerException("Armor type must not be null");
            }
            return new OptimizerRequest(this);
        }
    }
}
