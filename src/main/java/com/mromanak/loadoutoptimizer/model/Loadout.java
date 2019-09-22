package com.mromanak.loadoutoptimizer.model;

import com.google.common.collect.ImmutableMap;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPieceSkill;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

@Data
public class Loadout {
    private final Map<ArmorType, ThinArmorPiece> armorPieces;
    private final Map<String, Integer> skills;
    private final int level1Slots;
    private final int level2Slots;
    private final int level3Slots;
    private final int level4Slots;
    private final int defense;
    private final int fireResistance;
    private final int waterResistance;
    private final int thunderResistance;
    private final int iceResistance;
    private final int dragonResistance;
    private final double score;

    private Loadout(Builder builder) {
        armorPieces = builder.armorPieces;
        Map<String, Integer> skillsTmp = new TreeMap<>();
        int level1SlotsTmp = 0;
        int level2SlotsTmp = 0;
        int level3SlotsTmp = 0;
        int level4SlotsTmp = 0;
        int defenseTmp = 0;
        int fireResistanceTmp = 0;
        int waterResistanceTmp = 0;
        int thunderResistanceTmp = 0;
        int iceResistanceTmp = 0;
        int dragonResistanceTmp = 0;
        for(ThinArmorPiece armorPiece : armorPieces.values()) {
            for(ThinArmorPieceSkill skillMapping : armorPiece.getSkills()) {
                skillsTmp.merge(skillMapping.getSkill().getName(), skillMapping.getSkillLevel(), (x, y) -> x + y);
            }
            level1SlotsTmp += armorPiece.getLevel1Slots();
            level2SlotsTmp += armorPiece.getLevel2Slots();
            level3SlotsTmp += armorPiece.getLevel3Slots();
            level4SlotsTmp += armorPiece.getLevel4Slots();
            defenseTmp += armorPiece.getDefense();
            fireResistanceTmp += armorPiece.getFireResistance();
            waterResistanceTmp += armorPiece.getWaterResistance();
            thunderResistanceTmp += armorPiece.getThunderResistance();
            iceResistanceTmp += armorPiece.getIceResistance();
            dragonResistanceTmp += armorPiece.getDragonResistance();
        }
        skills = ImmutableMap.copyOf(skillsTmp);
        level1Slots = level1SlotsTmp;
        level2Slots = level2SlotsTmp;
        level3Slots = level3SlotsTmp;
        level4Slots = level4SlotsTmp;
        defense = defenseTmp;
        fireResistance = fireResistanceTmp;
        waterResistance = waterResistanceTmp;
        thunderResistance = thunderResistanceTmp;
        iceResistance = iceResistanceTmp;
        dragonResistance = dragonResistanceTmp;
        score = builder.score;
    }

    public static Loadout empty() {
        return new Builder().build();
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
        private double score = 0.0;
        private Map<ArmorType, ThinArmorPiece> armorPieces = new TreeMap<>();

        private Builder() {
        }

        public Builder withScore(double score) {
            this.score = score;
            return this;
        }

        public Builder withArmorPiece(ThinArmorPiece val) {
            if(val == null) {
                throw new NullPointerException("Armor piece must not be null");
            }

            armorPieces.put(val.getArmorType(), val);

            return this;
        }

        public Builder withArmorPieces(Iterable<ThinArmorPiece> val) {
            if(val == null) {
                throw new NullPointerException("Armor pieces must not be null");
            }

            for(ThinArmorPiece armorPiece : val) {
                armorPieces.put(armorPiece.getArmorType(), armorPiece);
            }

            return this;
        }

        public Loadout build() {
            return new Loadout(this);
        }
    }
}
