package com.mromanak.loadoutoptimizer.model.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mromanak.loadoutoptimizer.utils.NameUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.SortComparator;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.*;

@Data
@Entity
public class Skill {

    @Setter(AccessLevel.NONE)
    @Id
    @Column(columnDefinition = "varchar")
    private String id;

    @NotBlank(message = "Name must be non-blank")
    @Column(columnDefinition = "varchar", nullable = false)
    private String name;

    @NotNull(message = "Max requiredPieces must be non-null")
    @Min(value = 1, message = "Max requiredPieces must be at least 1")
    @Max(value = 7, message = "Max requiredPieces must be at most 7")
    @Column(nullable = false)
    private Integer maxLevel;

    // TODO Validate that this is greater than maxLevel

    @Min(value = 1, message = "Max uncapped requiredPieces must be at least 1")
    @Max(value = 7, message = "Max uncapped requiredPieces must be at most 7")
    private Integer maxUncappedLevel;

    @Valid
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name = "uncapping_skills",
        joinColumns = @JoinColumn(referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(referencedColumnName = "id")
    )
    private Skill uncappedBy;

    @NotBlank(message = "Description must be non-blank")
    @Column(columnDefinition = "varchar", nullable = false)
    private String description;

    // TODO Validate that this has the expected number of entries
    @Valid
    @NotNull(message = "Effects must be non-null")
    @Size(min = 1, max = 7, message = "Effects must contain between 1 and 7, inclusive, entries")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "skill_effect",
        joinColumns = @JoinColumn(referencedColumnName = "id")
    )
    @MapKeyColumn(name = "skill_level")
    @Column(name = "effect", nullable = false, columnDefinition = "varchar")
    private Map<Integer, String> effects;

    @Valid
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<ArmorPieceSkill> armorPieces;

    @Valid
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<JewelSkill> jewels;

    @Valid
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<SetBonusSkill> setBonuses;

    public void setName(String name) {
        this.id = NameUtils.toSlug(name);
        this.name = name;
    }
}
