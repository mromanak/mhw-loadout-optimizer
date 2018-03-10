package com.mromanak.loadoutoptimizer.model;

import com.google.common.collect.ImmutableMap;
import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

@Data
public class Loadout {
    private final Map<ArmorType, ArmorPiece> armorPieces;
    private final Map<String, Integer> skills;
    private final int level1Slots;
    private final int level2Slots;
    private final int level3Slots;

    private Loadout(Builder builder) {
        armorPieces = builder.armorPieces;
        Map<String, Integer> skillsTmp = new TreeMap<>();
        int level1SlotsTmp = 0;
        int level2SlotsTmp = 0;
        int level3SlotsTmp = 0;
        for(ArmorPiece armorPiece : armorPieces.values()) {
            for(Map.Entry<String, Integer> skillEntry : armorPiece.getSkills().entrySet()) {
                skillsTmp.merge(skillEntry.getKey(), skillEntry.getValue(), (x, y) -> x + y);
            }
            level1SlotsTmp += armorPiece.getLevel1Slots();
            level2SlotsTmp += armorPiece.getLevel2Slots();
            level3SlotsTmp += armorPiece.getLevel3Slots();
        }
        skills = ImmutableMap.copyOf(skillsTmp);
        level1Slots = level1SlotsTmp;
        level2Slots = level2SlotsTmp;
        level3Slots = level3SlotsTmp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Loadout copy) {
        Builder builder = new Builder();
        builder.armorPieces = new TreeMap<>(copy.getArmorPieces());
        return builder;
    }

    public static final class Builder {
        private Map<ArmorType, ArmorPiece> armorPieces = new TreeMap<>();

        private Builder() {
        }

        public Builder withArmorPiece(ArmorPiece val) {
            if(val == null) {
                throw new NullPointerException("Armor piece must not be null");
            }

            armorPieces.put(val.getArmorType(), val);

            return this;
        }

        public Builder withArmorPieces(Iterable<ArmorPiece> val) {
            if(val == null) {
                throw new NullPointerException("Armor pieces must not be null");
            }

            for(ArmorPiece armorPiece : val) {
                armorPieces.put(armorPiece.getArmorType(), armorPiece);
            }

            return this;
        }

        public Loadout build() {
            return new Loadout(this);
        }
    }
}
