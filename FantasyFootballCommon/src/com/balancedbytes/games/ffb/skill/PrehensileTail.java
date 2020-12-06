package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.DodgeModifiers;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * The player has a long, thick tail which he can use to trip up opposing
 * players. To represent this, opposing players must subtract 1 from the D6 roll
 * if they attempt to dodge out of any of the player's tackle zones.
 */
public class PrehensileTail extends Skill {

	public PrehensileTail() {
		super("Prehensile Tail", SkillCategory.MUTATION);

		registerModifier(DodgeModifiers.PREHENSILE_TAIL_1);
		registerModifier(DodgeModifiers.PREHENSILE_TAIL_2);
		registerModifier(DodgeModifiers.PREHENSILE_TAIL_3);
		registerModifier(DodgeModifiers.PREHENSILE_TAIL_4);
		registerModifier(DodgeModifiers.PREHENSILE_TAIL_5);
		registerModifier(DodgeModifiers.PREHENSILE_TAIL_6);
		registerModifier(DodgeModifiers.PREHENSILE_TAIL_7);
		registerModifier(DodgeModifiers.PREHENSILE_TAIL_8);
	}

}
