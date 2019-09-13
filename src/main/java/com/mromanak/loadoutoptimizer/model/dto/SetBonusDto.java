package com.mromanak.loadoutoptimizer.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mromanak.loadoutoptimizer.utils.NameUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.SortedSet;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SetBonusDto {

    @Setter(AccessLevel.NONE)
    private String id;

    @NotBlank(message = "Name must be non-blank")
    private String name;

    @Valid
    @Size(min = 1, message = "Set bonus must provide at least one skill")
    SortedSet<SetBonusSkillDto> skills;

    @Valid
    @Size(min = 1, message = "Set bonus must contain at least one armor piece")
    SortedSet<String> armorPieces;

    public void setName(String name) {
        this.id = NameUtils.toSlug(name);
        this.name = name;
    }
}
