package com.mromanak.loadoutoptimizer.model.jpa.weapon;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = GreatSword.class, name = "Great Sword"),
    @JsonSubTypes.Type(value = LongSword.class, name = "Long Sword"),
    @JsonSubTypes.Type(value = SwordAndShield.class, name = "Sword & Shield"),
    @JsonSubTypes.Type(value = DualBlades.class, name = "Dual Blades"),
    @JsonSubTypes.Type(value = HuntingHorn.class, name = "Hunting Horn"),
    @JsonSubTypes.Type(value = Hammer.class, name = "Hammer"),
    @JsonSubTypes.Type(value = Lance.class, name = "Lance"),
    @JsonSubTypes.Type(value = Gunlance.class, name = "Gunlance"),
    @JsonSubTypes.Type(value = SwitchAxe.class, name = "Switch Axe"),
    @JsonSubTypes.Type(value = ChargeBlade.class, name = "Charge Blade"),
    @JsonSubTypes.Type(value = InsectGlaive.class, name = "Insect Glaive"),
    @JsonSubTypes.Type(value = Bow.class, name = "Bow"),
    @JsonSubTypes.Type(value = LightBowgun.class, name = "Light Bowgun"),
    @JsonSubTypes.Type(value = HeavyBowgun.class, name = "Heavy Bowgun"),
})
public abstract class AbstractWeapon<T extends AbstractWeapon> {

    private final Double displayAttackMultiplier;

    @Setter(AccessLevel.NONE)
    @Id
    @Column(columnDefinition = "varchar")
    private String id;

    @NotBlank(message = "Name must be non-blank")
    @Column(columnDefinition = "varchar", nullable = false)
    private String name;

    @NotBlank(message = "Set name must be non-blank")
    @Column(columnDefinition = "varchar", nullable = false)
    private String setName;

    @NotNull(message = "Display attack must be non-null")
    @Min(value = 0, message = "Display attack must be non-negative")
    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer displayAttack = 0;

    @NotNull(message = "Defense must be non-null")
    @Min(value = 0, message = "Defense must be non-negative")
    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer defense = 0;

    @NotNull(message = "Affinity must be non-null")
    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer affinity = 0;

    @NotNull(message = "Number of level 1 slots must be non-null")
    @Min(value = 0, message = "Number of level 1 slots must be at least 0")
    @Max(value = 3, message = "Number of level 1 slots must be at most 3")
    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer level1Slots = 0;

    @NotNull(message = "Number of level 2 slots must be non-null")
    @Min(value = 0, message = "Number of level 2 slots must be at least 0")
    @Max(value = 3, message = "Number of level 2 slots must be at most 3")
    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer level2Slots = 0;

    @NotNull(message = "Number of level 3 slots must be non-null")
    @Min(value = 0, message = "Number of level 3 slots must be at least 0")
    @Max(value = 3, message = "Number of level 3 slots must be at most 3")
    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer level3Slots = 0;

    @NotNull(message = "Elderseal level must be non-null")
    private EldersealLevel eldersealLevel = EldersealLevel.NONE;

    @NotNull(message = "Rarity must be non-null")
    @Min(value = 1, message = "Rarity must be at least 1")
    @Column(nullable = false, columnDefinition = "int default 1")
    private Integer rarity = 1;

    @Valid
    @ManyToOne(fetch = FetchType.LAZY)
    private T parent;

    @Valid
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    private Set<T> children = new HashSet<>();



    protected AbstractWeapon(Double displayAttackMultiplier) {
        this.displayAttackMultiplier = displayAttackMultiplier;
    }

    public Integer getRawAttack() {
        return (int) Math.floor(displayAttack / displayAttackMultiplier);
    }
}
