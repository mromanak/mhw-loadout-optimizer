package com.mromanak.loadoutoptimizer.model;

import com.google.common.collect.ImmutableMap;
import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

@Data
public class ArmorPiece {
    private final String name;
    private final ArmorType armorType;
    private final Map<String, Integer> skills;
    private final int level1Slots;
    private final int level2Slots;
    private final int level3Slots;

    private ArmorPiece(Builder builder) {
        name = builder.name;
        armorType = builder.armorType;
        skills = ImmutableMap.copyOf(builder.skills);
        level1Slots = builder.level1Slots;
        level2Slots = builder.level2Slots;
        level3Slots = builder.level3Slots;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private ArmorType armorType;
        private Map<String, Integer> skills = new TreeMap<>();
        private int level1Slots;
        private int level2Slots;
        private int level3Slots;

        private Builder() {
        }

        public Builder withName(String name) {
            if(name == null) {
                throw new NullPointerException("Name must not be null");
            }
            this.name = name;
            return this;
        }

        public Builder withArmorType(ArmorType armorType) {
            if(armorType == null) {
                throw new NullPointerException("Armor type must not be null");
            }
            this.armorType = armorType;
            return this;
        }

        public Builder withSkill(String skillName, int skillLevel) {
            if(skillName == null) {
                throw new NullPointerException("Skill name must not be null");
            } else if (skillLevel <= 0) {
                throw new IllegalArgumentException("Skill level must be greater than 0");
            }
            skills.put(skillName, skillLevel);
            return this;
        }

        public Builder withSkills(Map<String, Integer> skills) {
            if(skills == null) {
                throw new NullPointerException("Skills map must not be null");
            }
            this.skills = skills;
            return this;
        }

        public Builder withLevel1Slots(int level1Slots) {
            if(level1Slots < 0 || level1Slots > 3) {
                throw new IllegalArgumentException("Level 1 slots must be between 0 and 3 (inclusive)");
            }
            this.level1Slots = level1Slots;
            return this;
        }

        public Builder withLevel2Slots(int level2Slots) {
            if(level2Slots < 0 || level2Slots > 3) {
                throw new IllegalArgumentException("Level 2 slots must be between 0 and 3 (inclusive)");
            }
            this.level2Slots = level2Slots;
            return this;
        }

        public Builder withLevel3Slots(int level3Slots) {
            if(level3Slots < 0 || level3Slots > 3) {
                throw new IllegalArgumentException("Level 3 slots must be between 0 and 3 (inclusive)");
            }
            this.level3Slots = level3Slots;
            return this;
        }

        public ArmorPiece build() {
            if(name == null) {
                throw new NullPointerException("Name must not be null");
            } else if(armorType == null) {
                throw new NullPointerException("Armor type must not be null");
            } else if(level1Slots + level2Slots + level3Slots > 3) {
                throw new IllegalArgumentException("Total number of slots must be between 0 and 3 (inclusive)");
            }
            return new ArmorPiece(this);
        }
    }
}
