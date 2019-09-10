package com.mromanak.loadoutoptimizer.model.jpa;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Embeddable
public class ProvidedSkill {

    @NotNull(message = "Skill must be non-null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    @Column(nullable = false)
    private Skill skill;

    @Min(value = 1, message = "Skill level must be at least one")
    @Column(nullable = false)
    private Integer skillLevel;
}
