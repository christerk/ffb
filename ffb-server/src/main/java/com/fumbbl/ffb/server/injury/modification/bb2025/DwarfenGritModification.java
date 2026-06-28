package com.fumbbl.ffb.server.injury.modification.bb2025;

import com.fumbbl.ffb.server.injury.modification.ModificationParams;


public class DwarfenGritModification extends RerollArmourModification {

	public DwarfenGritModification() {
		super(true); // no injury type restriction
	}

	@Override
	protected boolean tryArmourRollModification(ModificationParams params) {
		return params.getNewContext().getArmorRoll() != null
			&& params.getNewContext().isArmorBroken();
	}

	@Override
	public boolean appliesToDefender() {
		return true;
	}
}
