package com.mromanak.loadoutoptimizer.model.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class SetBonusSkill {

    @NotNull(message = "Skill must be non-null")
    //@ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Skill skill;

    @Min(value = 1, message = "Required pieces must be at least 1")
    @Max(value = 5, message = "Required pieces must be at most 5")
    @Column(nullable = false)
    private Integer requiredPieces;
}
