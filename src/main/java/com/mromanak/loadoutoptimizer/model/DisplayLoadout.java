package com.mromanak.loadoutoptimizer.model;

import lombok.Data;

import java.util.Map;

@Data
public class DisplayLoadout {
    private Map<ArmorType, String> armor;
    private Map<String, Integer> skills;
    private int level1Slots;
    private int level2Slots;
    private int level3Slots;
}
