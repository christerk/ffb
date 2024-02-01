package com.fumbbl.ffb;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ReRollSources {

	public static final ReRollSource TEAM_RE_ROLL = new ReRollSource("Team ReRoll");
	public static final ReRollSource BRILLIANT_COACHING_RE_ROLL = new ReRollSource("Brilliant Coaching ReRoll");
	public static final ReRollSource DODGE = new ReRollSource("Dodge");
	public static final ReRollSource PRO = new ReRollSource("Pro");
	public static final ReRollSource SURE_FEET = new ReRollSource("Sure Feet");
	public static final ReRollSource SURE_HANDS = new ReRollSource("Sure Hands");
	public static final ReRollSource CATCH = new ReRollSource("Catch");
	public static final ReRollSource PASS = new ReRollSource("Pass");
	public static final ReRollSource WINNINGS = new ReRollSource("Winnings");
	public static final ReRollSource LONER = new ReRollSource("Loner");
	public static final ReRollSource LEADER = new ReRollSource("Leader");
	public static final ReRollSource MONSTROUS_MOUTH = new ReRollSource("Monstrous Mouth");
	public static final ReRollSource BRAWLER = new ReRollSource("Brawler");
	public static final ReRollSource BRIBERY_AND_CORRUPTION = new ReRollSource("Bribery and Corruption");
	public static final ReRollSource BLIND_RAGE = new ReRollSource("Blind Rage");
	public static final ReRollSource THE_BALLISTA = new ReRollSource("The Ballista", 2);
	public static final ReRollSource MESMERIZING_DANCE = new ReRollSource("Mesmerizing Dance");
	public static final ReRollSource LORD_OF_CHAOS = new ReRollSource("Lord of Chaos");
	public static final ReRollSource CONSUMMATE_PROFESSIONAL = new ReRollSource("Consummate Professional");
	public static final ReRollSource PUMP_UP_THE_CROWD = new ReRollSource("Pump up the Crowd");
	public static final ReRollSource SHOW_STAR = new ReRollSource("Star of the Show");
	public static final ReRollSource WHIRLING_DERVISH = new ReRollSource("Whirling Dervish");
	public static final ReRollSource THINKING_MANS_TROLL = new ReRollSource("Thinking Man's Troll");
	public static final ReRollSource HALFLING_LUCK = new ReRollSource("Halfling Luck");

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
