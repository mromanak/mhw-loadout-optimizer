package com.mromanak.loadoutoptimizer.validator;

import com.mromanak.loadoutoptimizer.model.jpa.weapon.AbstractWeapon;

public class AbstractWeaponMaxTotalSlotsValidator extends MaxTotalSlotsValidator<AbstractWeapon> {

    @Override
    protected int extractLevel1Slots(AbstractWeapon weapon) {
        return nullToZero(weapon.getLevel1Slots());
    }

    @Override
    protected int extractLevel2Slots(AbstractWeapon weapon) {
        return nullToZero(weapon.getLevel2Slots());
    }

    @Override
    protected int extractLevel3Slots(AbstractWeapon weapon) {
        return nullToZero(weapon.getLevel3Slots());
    }

    @Override
    protected int extractLevel4Slots(AbstractWeapon weapon) {
        return 0;
    }
}
