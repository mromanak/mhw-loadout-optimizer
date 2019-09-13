package com.mromanak.loadoutoptimizer.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Comparator;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProvidedSkillDto implements Comparable<ProvidedSkillDto> {

    private static final Comparator<ProvidedSkillDto> COMPARATOR = Comparator.
        comparingInt(ProvidedSkillDto::getLevel).
        thenComparing(ProvidedSkillDto::getSkillId);

    @NotBlank(message = "Skill ID must be non-blank")
    private String skillId;

    @NotNull(message = "Level must be non-null")
    @Min(value = 1, message = "Level must be at least 1")
    @Max(value = 7, message = "Level must be at most 7")
    private Integer level;

    @Override
    public int compareTo(ProvidedSkillDto that) {
        return COMPARATOR.compare(this, that);
    }
}
