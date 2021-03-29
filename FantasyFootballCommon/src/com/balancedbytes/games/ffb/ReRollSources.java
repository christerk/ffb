package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.skill.Catch;
import com.balancedbytes.games.ffb.skill.Dodge;
import com.balancedbytes.games.ffb.skill.Leader;
import com.balancedbytes.games.ffb.skill.MonstrousMouth;
import com.balancedbytes.games.ffb.skill.Pass;
import com.balancedbytes.games.ffb.skill.Pro;
import com.balancedbytes.games.ffb.skill.SureFeet;
import com.balancedbytes.games.ffb.skill.SureHands;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ReRollSources {

	public static final ReRollSource TEAM_RE_ROLL = new ReRollSource("Team ReRoll");
	public static final ReRollSource DODGE = new ReRollSource(Dodge.class);
	public static final ReRollSource PRO = new ReRollSource(Pro.class);
	public static final ReRollSource SURE_FEET = new ReRollSource(SureFeet.class);
	public static final ReRollSource SURE_HANDS = new ReRollSource(SureHands.class);
	public static final ReRollSource CATCH = new ReRollSource(Catch.class);
	public static final ReRollSource PASS = new ReRollSource(Pass.class);
	public static final ReRollSource WINNINGS = new ReRollSource("Winnings");
	public static final ReRollSource LONER = new ReRollSource("Loner");
	public static final ReRollSource LEADER = new ReRollSource(Leader.class);
	public static final ReRollSource MONSTROUS_MOUTH = new ReRollSource(MonstrousMouth.class);

	private Map<String, ReRollSource> values;

	public Map<String, ReRollSource> values() {
		return values;
	}

	public ReRollSources() {
		values = new HashMap<>();
		try {
			Class<?> c = this.getClass();
			Class<?> cModifierType = ReRollSource.class;
			for (Field f : c.getDeclaredFields()) {
				if (f.getType() == cModifierType) {
					ReRollSource source = (ReRollSource) f.get(this);
					values.put(source.getName().toLowerCase(), source);
				}
			}

		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
