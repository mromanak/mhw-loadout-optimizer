package com.mromanak.loadoutoptimizer.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.Data;

import java.util.*;

@Data
public class SetBonus {

    private final String name;
    private final Map<String, Integer> bonusRequirements;
    private final Set<String> armorPieces;

    private SetBonus(Builder builder) {
        this.name = builder.name;
        this.bonusRequirements = ImmutableMap.copyOf(builder.bonusRequirements);
        this.armorPieces = ImmutableSet.copyOf(builder.armorPieces);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SetBonus copy) {
        Builder builder = new Builder();
        builder.name = copy.name;
        builder.bonusRequirements = copy.getBonusRequirements();
        builder.armorPieces = copy.getArmorPieces();
        return builder;
    }

    public static final class Builder {
        private String name;
        private Map<String, Integer> bonusRequirements = new LinkedHashMap<>();
        private Set<String> armorPieces = new LinkedHashSet<>();

        private Builder() {
        }

        public Builder withName(String name) {
            if(name == null) {
                throw new NullPointerException("Name must not be null");
            }
            this.name = name;
            return this;
        }

        public Builder withBonusRequirements(Map<String, Integer> bonusRequirements) {
            if(bonusRequirements == null) {
                throw new NullPointerException("Bonus requirements map must not be null");
            } else if (bonusRequirements.values().contains(null)) {
                throw new NullPointerException("Bonus requirement must not be null");
            }

            for(Integer requirement : bonusRequirements.values()) {
                if (requirement <= 0) {
                    throw new IllegalArgumentException("Bonus requirement must be greater than zero");
                }
            }

            this.bonusRequirements = bonusRequirements;
            return this;
        }

        public Builder withBonusRequirement(String bonusName, int requirement) {
            if(bonusName == null) {
                throw new NullPointerException("Bonus name must not be null");
            } else if (requirement <= 0) {
                throw new IllegalArgumentException("Bonus requirement must be greater than zero");
            }
            bonusRequirements.put(bonusName, requirement);
            return this;
        }

        public Builder withArmorPieces(Set<String> armorPieces) {
            if(armorPieces == null) {
                throw new NullPointerException("Armor pieces set must not be null");
            }
            this.armorPieces = armorPieces;
            return this;
        }

        public Builder withArmorPiece(String armorPiece) {
            if(armorPiece == null) {
                throw new NullPointerException("Armor piece must not be null");
            }
            armorPieces.add(armorPiece);
            return this;
        }

        public SetBonus build() {
            if(bonusRequirements.isEmpty()) {
                throw new IllegalArgumentException("Bonus requirements map must not be empty");
            } else if(armorPieces.isEmpty()) {
                throw new IllegalArgumentException("Armor pieces set must not be empty");
            }
            return new SetBonus(this);
        }
    }
}
