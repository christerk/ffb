package com.fumbbl.ffb.server.injury.modification.bb2025;

import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.DropDodge;
import com.fumbbl.ffb.injury.DropDodgeForSpp;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.server.injury.modification.ModificationParams;

import java.util.HashSet;

public class DwarfenGritModification extends RerollArmourModification {

	public DwarfenGritModification() {
		super(new HashSet<Class<? extends InjuryType>>() {{
			add(Block.class);
			add(DropDodge.class);
			add(DropDodgeForSpp.class);
		}});
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
