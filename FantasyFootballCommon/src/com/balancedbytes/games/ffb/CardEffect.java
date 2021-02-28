package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.skill.BoneHead;
import com.balancedbytes.games.ffb.skill.JumpUp;
import com.balancedbytes.games.ffb.skill.NoHands;
import com.balancedbytes.games.ffb.skill.ReallyStupid;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Kalimar
 */
public enum CardEffect implements INamedObject {

	DISTRACTED("Distracted") {
		@Override
		public Set<Class<? extends Skill>> skills() {
			return Collections.singleton(BoneHead.class);
		}
	},
	ILLEGALLY_SUBSTITUTED("IllegallySubstituted"),
	MAD_CAP_MUSHROOM_POTION("MadCapMushroomPotion") {
		@Override
		public Set<Class<? extends Skill>> skills() {
			return new HashSet<Class<? extends Skill>>() {{
				add(JumpUp.class);
				add(NoHands.class);
			}};
		}
	},
	SEDATIVE("Sedative") {
		@Override
		public Set<Class<? extends Skill>> skills() {
			return Collections.singleton(ReallyStupid.class);
		}
	},
	POISONED("Poisoned");

	private String fName;

	CardEffect(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

	public Set<Class<? extends Skill>> skills() {
		return Collections.emptySet();
	}
}
