package com.mromanak.loadoutoptimizer.model.jpa;

import lombok.Data;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
public class Skill {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Name must be non-blank")
    @Column(nullable = false)
    private String name;

    @Min(value = 1, message = "Max level must be at least 1")
    @Max(value = 7, message = "Max level must be at most 7")
    @Column(nullable = false)
    private Integer maxLevel;

    private Integer maxUncappedLevel;

    @Valid
    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(
        name = "uncapping_skills",
        joinColumns = @JoinColumn(referencedColumnName = "uncapping_skill_id"),
        inverseJoinColumns = @JoinColumn(referencedColumnName = "uncapped_skill_id")
    )
    private Skill uncappedBy;

    @Valid
    @ElementCollection
    @CollectionTable(
        name = "skill_effects",
        joinColumns = @JoinColumn(referencedColumnName = "skill_id")
    )
    @OneToMany(fetch = FetchType.LAZY)
    private Set<SkillEffect> providedEffects;
}
