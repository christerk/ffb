package com.fumbbl.ffb;

import com.fumbbl.ffb.skill.Catch;
import com.fumbbl.ffb.skill.Dodge;
import com.fumbbl.ffb.skill.Leader;
import com.fumbbl.ffb.skill.Pass;
import com.fumbbl.ffb.skill.Pro;
import com.fumbbl.ffb.skill.SureFeet;
import com.fumbbl.ffb.skill.SureHands;
import com.fumbbl.ffb.skill.bb2020.Brawler;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ReRollSources {

	public static final ReRollSource TEAM_RE_ROLL = new ReRollSource("Team ReRoll");
	public static final ReRollSource BRILLIANT_COACHING_RE_ROLL = new ReRollSource("Brilliant Coaching ReRoll");
	public static final ReRollSource DODGE = new ReRollSource(Dodge.class);
	public static final ReRollSource PRO = new ReRollSource(Pro.class);
	public static final ReRollSource SURE_FEET = new ReRollSource(SureFeet.class);
	public static final ReRollSource SURE_HANDS = new ReRollSource(SureHands.class);
	public static final ReRollSource CATCH = new ReRollSource(Catch.class);
	public static final ReRollSource PASS = new ReRollSource(Pass.class);
	public static final ReRollSource WINNINGS = new ReRollSource("Winnings");
	public static final ReRollSource LONER = new ReRollSource("Loner");
	public static final ReRollSource LEADER = new ReRollSource(Leader.class);
	public static final ReRollSource MONSTROUS_MOUTH = new ReRollSource("Monstrous Mouth");
	public static final ReRollSource BRAWLER = new ReRollSource(Brawler.class);
	
	public static final ReRollSource BLIND_RAGE_RE_ROLL = new ReRollSource("Blind Rage ReRoll");
	public static final ReRollSource BALLISTA_RE_ROLL = new ReRollSource("The Ballista ReRoll");
	public static final ReRollSource MESMERIZING_DANCE_RE_ROLL = new ReRollSource("Mesmerizing Dance ReRoll");



	private final Map<String, ReRollSource> values;

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
