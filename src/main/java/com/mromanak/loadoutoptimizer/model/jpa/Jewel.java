package com.mromanak.loadoutoptimizer.model.jpa;

import com.mromanak.loadoutoptimizer.utils.NameUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Jewel {

    @Setter(AccessLevel.NONE)
    @Id
    @Column(columnDefinition = "varchar")
    private String id;

    @NotBlank(message = "Name must be non-blank")
    @Column(columnDefinition = "varchar", nullable = false)
    private String name;

    @Min(value = 1, message = "Jewel requiredPieces must be at least 1")
    @Max(value = 4, message = "Jewel requiredPieces must be at most 4")
    @Column(nullable = false)
    private Integer jewelLevel;

    @Valid
    @Size(min = 1, message = "Skills must contain at least 1 element")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "jewel")
    private List<JewelSkill> skills = new ArrayList<>();

    public void setName(String name) {
        this.id = NameUtils.toSlug(name);
        this.name = name;
    }

    public void setSkills(List<JewelSkill> skills) {
        this.skills.clear();
        if (skills != null) {
            this.skills.addAll(skills);
        }
    }
}
