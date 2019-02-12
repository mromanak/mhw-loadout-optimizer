package com.mromanak.loadoutoptimizer.model.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Data
@ApiModel(description = "A request for optimized loadouts")
public class LoadoutRequest {

    @ApiModelProperty(
        notes = "A map of the name of a desired skill to an object containing its desired value and relative worth",
        required = true)
    private Map<String, SkillWeight> skillWeights = new HashMap<>();

    @ApiModelProperty(
        notes = "The name of a set bonus skill that the returned loadout must include. Note that this must be the " +
            "name of the skill itself (e.g. Adrenaline or Staminia Cap Up for the Anjanath set), not the name of the " +
            "set bonus itself (e.g. Anajanath Will for the Anjanath set.)")
    private String setBonus;

    @ApiModelProperty(notes = "The relative value of a level 1 decoration slot.")
    private double level1SlotWeight = 0.0;

    @ApiModelProperty(notes = "The relative value of a level 2 decoration slot.")
    private double level2SlotWeight = 0.0;

    @ApiModelProperty(notes = "The relative value of a level 3 decoration slot.")
    private double level3SlotWeight = 0.0;

    @ApiModelProperty(
        notes = "The relative value of including a piece of armor in a loadout. In most cases, this should be a " +
            "small, negative number to encourage the optimizer to use as few pieces of armor as possible.")
    private double loadoutSizeWeight = 0.0;

    @ApiModelProperty(
        notes = "A list of regular expressions that match the names of armor pieces that should not be included in " +
            "the final loadout. If setBonus is also defined, the API may use armor whose name matches an exclude " +
            "pattern (e.g. if excludePatterns excludes all Kushala Daora armor pieces, but setBonus specifies Nullify" +
            " Wind Pressure, the API may return a loadout containing the minimum number of pieces of Kushala Daora " +
            "armor required to obtain the set bonus.)")
    private List<Pattern> excludePatterns = new ArrayList<>();

    public void setSkillWeights(Map<String, SkillWeight> skillWeights) {
        this.skillWeights = (skillWeights == null) ? new HashMap<>() : skillWeights;
    }

    public void setExcludePatterns(List<Pattern> excludePatterns) {
        this.excludePatterns = (excludePatterns == null) ? new ArrayList<>() : excludePatterns;
    }
}

