package com.fumbbl.ffb.server;

import com.fumbbl.ffb.injury.InjuryType;

import java.util.Map;

public class InjuryTypes {

	private static Map<String, InjuryType> values;

	public static Map<String, InjuryType> values() {
		return values;
	}
	/*
	 * public InjuryTypes() { try { Class<?> c = this.getClass(); Class<?>
	 * cModifierType = CatchModifier.class.getClass(); for (Field f :
	 * c.getDeclaredFields()) { if (f.getType() == cModifierType) { InjuryType
	 * modifier = (InjuryType) f.get(this);
	 * values.put(modifier.getName().toLowerCase(), modifier); } }
	 * 
	 * } catch (IllegalArgumentException | IllegalAccessException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } }
	 */
}
