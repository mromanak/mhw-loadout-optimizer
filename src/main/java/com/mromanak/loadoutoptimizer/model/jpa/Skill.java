package com.mromanak.loadoutoptimizer.model.jpa;

import lombok.Data;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
    }
)
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "skill_id_generator")
    @SequenceGenerator(name = "skill_id_generator", sequenceName = "skill_id_seq")
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
    //@OneToOne(fetch = FetchType.LAZY)
    @OneToOne
    @JoinTable(
        name = "uncapping_skills",
        joinColumns = @JoinColumn(referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(referencedColumnName = "id")
    )
    private Skill uncappedBy;

    @Valid
    @Size(min = 1, message = "Skill must provide at least one effect")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "skill_effects",
        joinColumns = @JoinColumn(referencedColumnName = "id")
    )
    private Set<SkillEffect> providedEffects;
}
