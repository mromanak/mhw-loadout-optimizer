package com.mromanak.loadoutoptimizer.model.jpa.weapon;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static java.util.Arrays.asList;

public enum SharpnessLevel {
    RED("Red", 0.5, 0.25),
    ORANGE("Orange", 0.75, 0.5),
    YELLOW("Yellow", 1.0, 0.75),
    GREEN("Green", 1.05, 1.0),
    BLUE("Blue", 1.2, 1.0625),
    WHITE("White", 1.32, 1.125),
    PURPLE("Purple", 1.39, 1.2);

    private static final Map<String, SharpnessLevel> nameToValueMap;

    static {
        ImmutableMap.Builder<String, SharpnessLevel> nameToValueBuilder = ImmutableMap.builder();
        for(SharpnessLevel sharpnessLevel : values()) {
            nameToValueBuilder.put(sharpnessLevel.getName(), sharpnessLevel);
        }
        nameToValueMap = nameToValueBuilder.build();
    }

    private final String name;
    private final Double physicalMultiplier;
    private final Double elementalMultiplier;

    SharpnessLevel(String name, Double physicalMultiplier, Double elementalMultiplier) {
        this.name = name;
        this.physicalMultiplier = physicalMultiplier;
        this.elementalMultiplier = elementalMultiplier;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public Double getPhysicalMultiplier() {
        return physicalMultiplier;
    }

    public Double getElementalMultiplier() {
        return elementalMultiplier;
    }

    @JsonCreator
    public SharpnessLevel forName(String name) {
        SharpnessLevel sharpnessLevel = nameToValueMap.get(name);
        if(sharpnessLevel == null) {
            throw new IllegalArgumentException(
                name + " is not a recognized sharpness level. Recognized sharpness levels are: " + asList(values()));
        }
        return sharpnessLevel;
    }
}
