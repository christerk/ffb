package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillValueEvaluator;

/**
 * When this player is activated, they can declare a Hypnotic aze Special Action; 
 * there is no limit to the number of players that can declare this Special Action 
 * each Turn. When a player declares a Hypnotic Gaze Special Action they are first 
 * allowed to make a Move Action, though they cannot continue to move after the 
 * Hypnotic Gaze Special Action has been attempted. 
 * When a player performs a Hypnotic Gaze Special Action, they select a Standing 
 * opposition player adjacent to them and roll a D6. On a 1-2, nothing happens and 
 * this player’s activation immediately ends. On a 3+, the selected opposition 
 * player becomes Distracted and this player’s activation immediately ends.
 */
@RulesCollection(Rules.BB2025)
public class HypnoticGaze extends Skill {

	public HypnoticGaze() {
		super("Hypnotic Gaze", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.inflictsConfusion);
	}

	@Override
	public SkillValueEvaluator evaluator() {
		return SkillValueEvaluator.ROLL;
	}
}
