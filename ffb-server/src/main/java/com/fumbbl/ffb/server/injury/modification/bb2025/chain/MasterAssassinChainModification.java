package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.Stab;

import java.util.Collections;

public class MasterAssassinChainModification extends ArmourReRollChainModification {

	public MasterAssassinChainModification() {
		super(Collections.singleton(Stab.class));
	}
}
