package com.fumbbl.ffb.skill.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillValueEvaluator;

/**
 * Loners, through inexperience, arrogance, animal ferocity or just plain
 * stupidity, do not work well with the rest of the team. As a result, a Loner
 * may use team reRolls but has to roll a D6 first. On a roll of 4+, he may use
 * the team re-roll as normal. On a roll of 1-3 the original result stands
 * without being re-rolled but the team re-roll is lost (i.e. used).
 */
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class Loner extends Skill {

	public Loner() {
		super("Loner", SkillCategory.TRAIT, 4);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.preventCardRabbitsFoot);
		registerProperty(NamedProperties.hasToRollToUseTeamReroll);
	}

	@Override
	public int getCost(Player<?> player) {
		return 0;
	}

	@Override
	public SkillValueEvaluator evaluator() {
		return SkillValueEvaluator.ROLL;
	}
}
