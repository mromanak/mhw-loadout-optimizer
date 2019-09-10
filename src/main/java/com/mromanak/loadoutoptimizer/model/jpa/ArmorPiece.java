package com.mromanak.loadoutoptimizer.model.jpa;

import lombok.Data;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"}),
        @UniqueConstraint(columnNames = {"setName", "armorType", "setType"})
    }
)
public class ArmorPiece {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "armor_piece_id_generator")
    @SequenceGenerator(name = "armor_piece_id_generator", sequenceName = "armor_piece_id_seq")
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
    
    @Min(value = 0, message = "Number of level 1 slots must be at least 0")
    @Max(value = 3, message = "Number of level 1 slots must be at most 3")
    private Integer level1Slots;
    
    @Min(value = 0, message = "Number of level 2 slots must be at least 0")
    @Max(value = 3, message = "Number of level 2 slots must be at most 3")
    private Integer level2Slots;
    
    @Min(value = 0, message = "Number of level 3 slots must be at least 0")
    @Max(value = 3, message = "Number of level 3 slots must be at most 3")
    private Integer level3Slots;
    
    @Min(value = 0, message = "Number of level 4 slots must be at least 0")
    @Max(value = 3, message = "Number of level 4 slots must be at most 3")
    private Integer level4Slots;

    @Valid
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "armor_piece_provided_skills",
        joinColumns = @JoinColumn(referencedColumnName = "id")
    )
    Set<ProvidedSkill> providedSkills;
}
