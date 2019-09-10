package com.mromanak.loadoutoptimizer.model.jpa;

import lombok.Data;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Set;

@Data
@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
    }
)
public class Jewel {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "jewel_id_generator")
    @SequenceGenerator(name = "jewel_id_generator", sequenceName = "jewel_id_seq")
    private Long id;

    @NotBlank(message = "Name must be non-blank")
    @Column(nullable = false)
    private String name;

    @Min(value = 1, message = "Jewel level must be at least 1")
    @Max(value = 4, message = "Jewel level must be at most 4")
    @Column(nullable = false)
    private Integer jewelLevel;

    @Valid
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "jewel_provided_skills",
        joinColumns = @JoinColumn(referencedColumnName = "id")
    )
    Set<ProvidedSkill> providedSkills;
}
