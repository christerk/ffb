package com.fumbbl.ffb.server.inducements.bb2025.prayers;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.bb2025.Prayer;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.inducements.mixed.prayers.PlayerSelector;

import java.util.Collections;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2025)
public class BadHabitsHandler extends com.fumbbl.ffb.server.inducements.mixed.prayers.BadHabitsHandler {
	@Override
	public Prayer handledPrayer() {
		return Prayer.BAD_HABITS;
	}

	@Override
	protected PlayerSelector selector() {
		return OpponentPlayerSelector.INSTANCE;
	}

	@Override
	protected Set<Skill> addedSkills(GameState gameState) {
		SkillFactory skillFactory = gameState.getGame().getFactory(FactoryType.Factory.SKILL);
		Skill skill = skillFactory.forProperty(NamedProperties.hasToRollToUseTeamReroll);
		return Collections.singleton(skill);
	}
}
