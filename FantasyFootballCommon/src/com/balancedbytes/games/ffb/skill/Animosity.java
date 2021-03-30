package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.model.skill.SkillValueEvaluator;

/**
 * A player with this skill does not like players from his team that are a
 * different race than he is and will often refuse to play with them despite the
 * coach's orders. If this player at the end of his Hand-off or Pass Action
 * attempts to hand-of for pass the ball to a team-mate that is not the same
 * race as the Animosity player, roll a D6. On a 2+, the pass / hand-off is
 * carried out as normal. On a 1, the player refuses to try to give the ball to
 * any team-mate except one of his own race. The coach may choose to change the
 * target of the pass/hand-off to another team-mate of the same race as the
 * Animosity player, however no more movement is allowed for the Animosity
 * player, so the current Action may be lost for the turn.
 */
@RulesCollection(Rules.COMMON)
public class Animosity extends Skill {

	public Animosity() {
		super("Animosity", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.hasToRollToPassBallOn);
	}

	@Override
	public SkillValueEvaluator evaluator() {
		return SkillValueEvaluator.ANIMOSITY;
	}
}
