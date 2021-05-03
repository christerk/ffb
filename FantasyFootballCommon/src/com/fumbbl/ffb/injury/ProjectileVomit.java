package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.InjuryType;
import com.fumbbl.ffb.SendToBoxReason;

public class ProjectileVomit extends InjuryType {

	public ProjectileVomit() {
		super("projectileVomit", false, SendToBoxReason.PROJECTILE_VOMIT);
	}

}
