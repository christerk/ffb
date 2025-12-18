package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.JumpContext;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.modifiers.ModifierType;

/**
 * A player with the Leap skill is allowed to also jump over squares that are
 * empty or occupied by a standing player. Additionally modifiers from being
 * marked can be reduced by 1 to a minimum of -1.
 */
@RulesCollection(Rules.BB2025)
public class Leap extends Skill {

	public Leap() {
		super("Leap", SkillCategory.AGILITY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canLeap);
		registerModifier(new JumpModifier("Leap", -1, ModifierType.DEPENDS_ON_SUM_OF_OTHERS) {
			@Override
			public boolean appliesToContext(Skill skill, JumpContext context) {
				if (context.getAccumulatedModifiers() > 1) {
					context.addModififerValue(getModifier());
					return true;
				}
				return false;
			}
		});
		registerConflictingProperty(NamedProperties.movesRandomly);
	}

}
