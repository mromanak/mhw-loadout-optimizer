package com.mromanak.loadoutoptimizer.model.jpa;

import lombok.Data;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Set;

@Data
@Entity
public class Jewel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name must be non-blank")
    private String name;

    @Min(value = 1, message = "Jewel level must be at least 1")
    @Max(value = 4, message = "Jewel level must be at most 4")
    private Integer jewelLevel;

    @Valid
    @ElementCollection
    @CollectionTable(
        name = "jewel_provided_skills",
        joinColumns = @JoinColumn(referencedColumnName = "jewel_id")
    )
    @OneToMany(fetch = FetchType.LAZY)
    Set<ProvidedSkill> providedSkills;
}
