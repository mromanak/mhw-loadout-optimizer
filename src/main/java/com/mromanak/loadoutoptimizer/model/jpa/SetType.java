package com.mromanak.loadoutoptimizer.model.jpa;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static com.mromanak.loadoutoptimizer.model.jpa.Rank.*;
import static java.util.Arrays.asList;

public enum SetType {

    NONE("(LR)", LOW_RANK),
    ALPHA("α", HIGH_RANK),
    BETA("β", HIGH_RANK),
    GAMMA("γ", HIGH_RANK),
    ALPHA_PLUS("α +", MASTER_RANK),
    BETA_PLUS("β +", MASTER_RANK),
    GAMMA_PLUS("γ +", MASTER_RANK);

    private final String name;
    private final Rank rank;

    private static final Map<String, SetType> nameToValueMap;

    static {
        ImmutableMap.Builder<String, SetType> nameToValueBuilder = ImmutableMap.builder();
        for(SetType setType : values()) {
            nameToValueBuilder.put(setType.getName(), setType);
        }
        nameToValueMap = nameToValueBuilder.build();
    }

    SetType(String name, Rank rank) {
        this.name = name;
        this.rank = rank;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public SetType forName(String name) {
        SetType setType = nameToValueMap.get(name);
        if(setType == null) {
            throw new IllegalArgumentException(
                name + " is not a recognized set type. Recognized set types are: " + asList(values()));
        }
        return setType;
    }
}
