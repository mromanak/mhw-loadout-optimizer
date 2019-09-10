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
    private String name;

    @Valid
    private List<Jewel> jewels;

    @Valid
    @ElementCollection
    @CollectionTable(
        name = "jewel_provided_skills",
        joinColumns = @JoinColumn(referencedColumnName = "jewel_id")
    )
    @OneToMany(fetch = FetchType.LAZY)
    Set<SetBonusSkill> skills;
}
