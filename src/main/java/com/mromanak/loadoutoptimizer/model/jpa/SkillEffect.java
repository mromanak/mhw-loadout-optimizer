package com.mromanak.loadoutoptimizer.model.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class SkillEffect {

    @Min(value = 1, message = "Skill level must be at least 1")
    @Max(value = 7, message = "Skill level must be at most 7")
    @Column(nullable = false)
    private Integer skillLevel;

    @NotBlank(message = "Effect description")
    @Column(nullable = false)
    private String effectDescription;
}
