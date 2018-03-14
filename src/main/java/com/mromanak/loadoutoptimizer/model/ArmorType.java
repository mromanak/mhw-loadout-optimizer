package com.mromanak.loadoutoptimizer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static java.util.Arrays.asList;

public enum ArmorType {
    HEAD("head"),
    ARMS("arms"),
    BODY("body"),
    WAIST("waist"),
    LEGS("legs"),
    CHARM("charm");

    private static final Map<String, ArmorType> nameToValueMap;

    static {
        ImmutableMap.Builder<String, ArmorType> nameToValueBuilder = ImmutableMap.builder();
        for(ArmorType armorType : values()) {
            nameToValueBuilder.put(armorType.getName(), armorType);
        }
        nameToValueMap = nameToValueBuilder.build();
    }

    private final String name;

    ArmorType(String name) {
        this.name = name;
    }

    @JsonValue
    private String getName() {
        return name;
    }

    @JsonCreator
    private ArmorType forName(String name) {
        ArmorType armorType = nameToValueMap.get(name);
        if(armorType == null) {
            throw new IllegalArgumentException(
                name + " is not a recognized armor type. Recognized armor types are: " + asList(values()));
        }
        return armorType;
    }
}
