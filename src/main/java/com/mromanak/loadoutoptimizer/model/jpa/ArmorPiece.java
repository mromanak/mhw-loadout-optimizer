package com.mromanak.loadoutoptimizer.model.jpa;

import lombok.Data;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"}),
        @UniqueConstraint(columnNames = {"set_name", "armor_type", "set_type"})
    }
)
public class ArmorPiece {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name must be non-blank")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Set name must be non-blank")
    @Column(nullable = false)
    private String setName;

    @NotNull(message = "Armor type must be non-null")
    @Column(nullable = false)
    private ArmorType armorType;

    @NotNull(message = "Set type must be non-null")
    @Column(nullable = false)
    private SetType setType;

    @Valid
    @ElementCollection
    @CollectionTable(
        name = "armor_piece_provided_skills",
        joinColumns = @JoinColumn(referencedColumnName = "armor_piece_id")
    )
    @OneToMany(fetch = FetchType.LAZY)
    Set<ProvidedSkill> providedSkills;
}
