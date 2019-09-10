package com.mromanak.loadoutoptimizer.model.jpa;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Embeddable
public class SkillEffect {

    @Min(value = 1, message = "Skill level must be at least 1")
    @Min(value = 7, message = "Skill level must be at most 7")
    private Integer skillLevel;

    @NotBlank(message = "Effect description")
    private String effectDescription;
}
