package com.mromanak.loadoutoptimizer.model.jpa;

import lombok.Data;

import javax.persistence.*;
import javax.validation.Valid;
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
public class SetBonus {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "set_bonus_id_generator")
    @SequenceGenerator(name = "set_bonus_id_generator", sequenceName = "set_bonus_id_seq")
    private Long id;

    @NotBlank(message = "Name must be non-blank")
    @Column(nullable = false)
    private String name;

    @Valid
    @Size(min = 1, message = "Set bonus must provide at least one skill")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "set_bonus_skills",
        joinColumns = @JoinColumn(referencedColumnName = "id")
    )
    @Column(nullable = false)
    Set<SetBonusSkill> skills;
}
