package com.mromanak.loadoutoptimizer.model.api;

import com.mromanak.loadoutoptimizer.model.ArmorType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@ApiModel(description = "A request for optimized loadouts")
public class LoadoutRequest {

    @ApiModelProperty(
        notes = "A map of the name of a desired skill to an object containing its desired value and relative worth",
        required = true)
    private Map<String, SkillWeight> skillWeights = new HashMap<>();

    @ApiModelProperty(
        notes = "A map of armor slots to the name of an armor piece that must be included in the returned loadouts. " +
            "(This functionality is not  yet implemented)")
    private Map<ArmorType, String> requiredArmorPieces = new HashMap<>();

    @ApiModelProperty(notes = "The relative value of a level 1 decoration slot")
    private double level1SlotWeight = 0.0;

    @ApiModelProperty(notes = "The relative value of a level 2 decoration slot")
    private double level2SlotWeight = 0.0;

    @ApiModelProperty(notes = "The relative value of a level 3 decoration slot")
    private double level3SlotWeight = 0.0;

    @ApiModelProperty(
        notes = "The relative value of including a piece of armor in a loadout. In most cases, this should be a " +
            "small, negative number to encourage the optimizer to use as few pieces of armor as possible")
    private double loadoutSizeWeight = 0.0;
}

