package com.mromanak.loadoutoptimizer.model.jpa.weapon;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static java.util.Arrays.asList;

public enum EldersealLevel {
    NONE("None"),
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private static final Map<String, EldersealLevel> nameToValueMap;

    static {
        ImmutableMap.Builder<String, EldersealLevel> nameToValueBuilder = ImmutableMap.builder();
        for(EldersealLevel eldersealLevel : values()) {
            nameToValueBuilder.put(eldersealLevel.getName(), eldersealLevel);
        }
        nameToValueMap = nameToValueBuilder.build();
    }

    private final String name;

    EldersealLevel(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public EldersealLevel forName(String name) {
        EldersealLevel eldersealLevel = nameToValueMap.get(name);
        if(eldersealLevel == null) {
            throw new IllegalArgumentException(
                name + " is not a recognized elderseal level. Recognized elderseal levels are: " + asList(values()));
        }
        return eldersealLevel;
    }
}
