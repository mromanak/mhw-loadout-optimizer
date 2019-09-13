package com.mromanak.loadoutoptimizer.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.model.jpa.SetBonusSkill;
import com.mromanak.loadoutoptimizer.model.jpa.SetType;
import com.mromanak.loadoutoptimizer.utils.NameUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.SortedSet;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArmorPieceDto {

    @Setter(AccessLevel.NONE)
    private String id;

    @NotBlank(message = "Name must be non-blank")
    private String name;

    @NotBlank(message = "Set name must be non-blank")
    private String setName;

    @NotNull(message = "Armor type must be non-null")
    private ArmorType armorType;

    @NotNull(message = "Set type must be non-null")
    private SetType setType;

    // TODO Validate there aren't more than 3 slots total
    @Min(value = 0, message = "Number of level 1 slots must be at least 0")
    @Max(value = 3, message = "Number of level 1 slots must be at most 3")
    private Integer level1Slots;

    @Min(value = 0, message = "Number of level 2 slots must be at least 0")
    @Max(value = 3, message = "Number of level 2 slots must be at most 3")
    private Integer level2Slots;

    @Min(value = 0, message = "Number of level 3 slots must be at least 0")
    @Max(value = 3, message = "Number of level 3 slots must be at most 3")
    private Integer level3Slots;

    @Min(value = 0, message = "Number of level 4 slots must be at least 0")
    @Max(value = 3, message = "Number of level 4 slots must be at most 3")
    private Integer level4Slots;

    @Valid
    private SortedSet<ProvidedSkillDto> skills;

    private String setBonusId;

    // TODO Find a less-redundant, similarly-compact solution for deriving id from these properties
    public void setSetName(String setName) {
        this.id = NameUtils.toSlug(setName, armorType, setType);
        this.setName = setName;
    }

    public void setArmorType(ArmorType armorType) {
        this.id = NameUtils.toSlug(setName, armorType, setType);
        this.armorType = armorType;
    }

    public void setSetType(SetType setType) {
        this.id = NameUtils.toSlug(setName, armorType, setType);
        this.setType = setType;
    }
}
