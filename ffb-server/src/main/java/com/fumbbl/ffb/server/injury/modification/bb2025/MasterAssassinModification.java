package com.fumbbl.ffb.server.injury.modification.bb2025;

import com.fumbbl.ffb.injury.Stab;
import java.util.Collections;

public class MasterAssassinModification extends RerollArmourModification {
	public MasterAssassinModification() {
		super(Collections.singleton(Stab.class));
	}
}
