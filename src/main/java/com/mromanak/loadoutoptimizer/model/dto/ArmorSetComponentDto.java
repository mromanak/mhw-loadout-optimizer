package com.mromanak.loadoutoptimizer.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mromanak.loadoutoptimizer.annotations.MaxTotalSlots;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@MaxTotalSlots(value = 3, message = "Total number of jewel slots must be at most 3")
public class ArmorSetComponentDto {

    @NotBlank(message = "Name must be non-blank")
    private String name;

    @Min(value = 0, message = "Number of level 1 jewel slots must be at least 0")
    @Max(value = 3, message = "Number of level 1 jewel slots must be at most 3")
    private Integer level1Slots;

    @Min(value = 0, message = "Number of level 2 jewel slots must be at least 0")
    @Max(value = 3, message = "Number of level 2 jewel slots must be at most 3")
    private Integer level2Slots;

    @Min(value = 0, message = "Number of level 3 jewel slots must be at least 0")
    @Max(value = 3, message = "Number of level 3 jewel slots must be at most 3")
    private Integer level3Slots;

    @Min(value = 0, message = "Number of level 4 slots must be at least 0")
    @Max(value = 3, message = "Number of level 4 slots must be at most 3")
    private Integer level4Slots;

    @Valid
    private SortedSet<ProvidedSkillDto> skills;
}
