package com.mromanak.loadoutoptimizer.model.api;

import com.mromanak.loadoutoptimizer.model.ArmorType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class LoadoutResponse {

    @ApiModelProperty(notes = "The names of the armor pieces that make up the loadout", allowableValues = "head,arms,body,waist,legs,charm")
    private Map<ArmorType, String> armor;

    @ApiModelProperty(notes = "The level of the skills granted by the loadout")
    private Map<String, Integer> skills;

    @ApiModelProperty(notes = "The number of unused level 1 decoration slots granted by the loadout")
    private int level1Slots;

    @ApiModelProperty(notes = "The number of unused level 2 decoration slots granted by the loadout")
    private int level2Slots;

    @ApiModelProperty(notes = "The number of unused level 3 decoration slots granted by the loadout")
    private int level3Slots;
}
