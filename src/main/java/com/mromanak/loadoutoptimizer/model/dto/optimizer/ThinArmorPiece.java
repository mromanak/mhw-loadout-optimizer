package com.mromanak.loadoutoptimizer.model.dto.optimizer;

import com.google.common.collect.ImmutableList;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.model.jpa.SetType;
import lombok.Data;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class ThinArmorPiece {

    private final String id;
    private final String name;
    private final ArmorType armorType;
    private final SetType setType;
    private final Integer level1Slots;
    private final Integer level2Slots;
    private final Integer level3Slots;
    private final Integer level4Slots;
    private final List<ThinArmorPieceSkill> skills;

    public ThinArmorPiece(ArmorPiece armorPiece) {
        this.id = armorPiece.getId();
        this.name = armorPiece.getName();
        this.armorType = armorPiece.getArmorType();
        this.setType = armorPiece.getSetType();
        this.level1Slots = armorPiece.getLevel1Slots();
        this.level2Slots = armorPiece.getLevel2Slots();
        this.level3Slots = armorPiece.getLevel3Slots();
        this.level4Slots = armorPiece.getLevel4Slots();
        this.skills = ImmutableList.copyOf(armorPiece.getSkills().stream().
            map(ThinArmorPieceSkill::new).
            collect(toList()));
    }
}