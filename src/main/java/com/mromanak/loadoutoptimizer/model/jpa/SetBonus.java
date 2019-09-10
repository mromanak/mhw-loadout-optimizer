package com.mromanak.loadoutoptimizer.model.jpa;

import lombok.Data;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class SetBonus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name must be non-blank")
    @Column(nullable = false)
    private String name;

    @Valid
    @Size(min = 1, message = "Set bonus must provide at least one skill")
    @ElementCollection
    @CollectionTable(
        name = "set_bonus_skills",
        joinColumns = @JoinColumn(referencedColumnName = "set_bonus_id")
    )
    @ManyToMany(fetch = FetchType.LAZY)
    @Column(nullable = false)
    Set<SetBonusSkill> skills;
}
