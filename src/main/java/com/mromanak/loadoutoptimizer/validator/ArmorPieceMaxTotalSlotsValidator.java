package com.mromanak.loadoutoptimizer.validator;

import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;

public class ArmorPieceMaxTotalSlotsValidator extends MaxTotalSlotsValidator<ArmorPiece> {

    @Override
    protected int extractLevel1Slots(ArmorPiece armorPiece) {
        return armorPiece.getLevel1Slots();
    }

    @Override
    protected int extractLevel2Slots(ArmorPiece armorPiece) {
        return armorPiece.getLevel2Slots();
    }

    @Override
    protected int extractLevel3Slots(ArmorPiece armorPiece) {
        return armorPiece.getLevel3Slots();
    }

    @Override
    protected int extractLevel4Slots(ArmorPiece armorPiece) {
        return armorPiece.getLevel4Slots();
    }
}
