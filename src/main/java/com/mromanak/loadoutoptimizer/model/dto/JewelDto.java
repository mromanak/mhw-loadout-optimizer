package com.mromanak.loadoutoptimizer.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mromanak.loadoutoptimizer.utils.NameUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.SortedSet;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JewelDto {

    @Setter(AccessLevel.NONE)
    private String id;

    @NotBlank(message = "Name must be non-blank")
    private String name;

    @NotNull
    @Min(value = 1, message = "Jewel requiredPieces must be at least 1")
    @Max(value = 4, message = "Jewel requiredPieces must be at most 4")
    private Integer jewelLevel;

    @Valid
    @Size(min = 1, message = "Skills must contain at least one element")
    SortedSet<ProvidedSkillDto> skills;

    public void setName(String name) {
        this.id = NameUtils.toSlug(name);
        this.name = name;
    }
}
