package com.mromanak.loadoutoptimizer.model.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ProvidedSkill {

    @NotNull(message = "Skill must be non-null")
    //@ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Skill skill;

    @Min(value = 1, message = "Skill level must be at least one")
    @Column(nullable = false)
    private Integer skillLevel;
}
