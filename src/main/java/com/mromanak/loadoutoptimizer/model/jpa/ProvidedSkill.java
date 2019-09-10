package com.mromanak.loadoutoptimizer.model.jpa;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Embeddable
public class ProvidedSkill {

    @NotNull(message = "Skill must be non-null")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private Skill skill;

    @Min(value = 1, message = "Skill level must be at least one")
    private Integer skillLevel;
}
