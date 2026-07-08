package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillClassWithValue;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.modifiers.TemporaryEnhancements;
import com.fumbbl.ffb.skill.bb2025.BreakTackle;
import com.fumbbl.ffb.skill.bb2025.Dodge;

import java.util.HashSet;
import java.util.Set;

/**
 * Grak's Description:
 * I'll Carry You: Grak & Crumbleberry must be hired as a pair.
 * Additionally, once per half, if Grak begins his activation adjacent to Crumbleberry,
 * he may pick up Crumbleberry; temporarily remove Crumbleberry from the pitch.
 * At the end of Grak's activation, place Crumbleberry in an unoccupied square
 * adjacent to Grak.
 */

/**
 * Crumbleberry's Description:
 * I'll Carry You: Grak & Crumbleberry must be hired as a pair.
 * Additionally, whilst Crumbleberry is being carried by Grak, Grak gains the
 * Break Tackle and Dodge Skills.
 */

/**
 * FUMBBL HOUSE RULE:
 * If Grak picks up Crumbleberry and ends his move in a position where there are
 * no unoccupied squares available, there is no description of what happens.
 * This can happen for example if Grak jumps into a cage, or if a shadowing opponent follows Grak into the last such square.

 * RECOMMENDED INTERPRETATION
 * In these situations, the suggested resolution is as follows:

 * - If Grak is adjacent to a Sideline or within an End Zone, Crumbleberry is
 *    treated as being Pushed into the Crowd, after which a Turnover is caused.
 * - Otherwise, Crumbleberry is dropped onto a chosen square with a player, and
 *    this is resolved as if Crumbleberry landed in an occupied square according
 *    to the rules listed in the Throw Team-Mate section.
 *
 * If Grak is removed from the pitch during an activation where he is carrying Crumbleberry,
 * resolve the placement of Crumbleberry before taking Grak off of the pitch.
 */

@RulesCollection(Rules.BB2025)
public class IllCarryYou extends Skill {

	public static final String VARIANT_CARRIER = "carrier";
	public static final String VARIANT_CARRIED = "carried";

	public IllCarryYou() {
		super("I'll Carry You", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_HALF);
		Set<SkillClassWithValue> skills = new HashSet<>();
		skills.add(new SkillClassWithValue(BreakTackle.class));
		skills.add(new SkillClassWithValue(Dodge.class));
		setEnhancements(new TemporaryEnhancements().withSkills(skills));
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canCarryPartner);
	}

}
