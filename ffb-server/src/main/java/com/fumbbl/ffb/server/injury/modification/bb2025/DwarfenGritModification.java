package com.fumbbl.ffb.server.injury.modification.bb2025;

import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.server.injury.modification.ModificationParams;

import java.util.Collections;

public class DwarfenGritModification extends RerollArmourModification {

	public DwarfenGritModification() {
		super(Collections.<Class<? extends InjuryType>>singleton(Block.class));
	}

	@Override
	protected boolean tryArmourRollModification(ModificationParams params) {
		return params.getNewContext().isArmorBroken();
	}

	@Override
	public boolean appliesToDefender() {
		return true;
	}
}
