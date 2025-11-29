package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.StatsMechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillClassWithValue;
import com.fumbbl.ffb.report.mixed.ReportPrayerWasted;
import com.fumbbl.ffb.server.GameState;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class DialogPrayerHandler extends PrayerHandler {

	PlayerSelector selector = PlayerSelector.INSTANCE;

	@Override
	final boolean initEffect(GameState gameState, Team prayingTeam) {
		SkillFactory factory = gameState.getGame().getFactory(FactoryType.Factory.SKILL);
		StatsMechanic mechanic = (StatsMechanic) gameState.getGame().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.STAT.name());

		Set<Skill> skillsFromEnhancement = handledPrayer().enhancements(mechanic).getSkills().stream().map(SkillClassWithValue::getSkill).map(factory::forClass).collect(Collectors.toSet());

		List<Player<?>> players = selector.eligiblePlayers(prayingTeam, gameState.getGame(), skillsFromEnhancement);
		if (players.isEmpty()) {
			reports.add(new ReportPrayerWasted(this.handledPrayer().getName()));
			return true;
		}
		createDialog(players, gameState, prayingTeam);
		return handled(gameState.getGame());
	}

	protected abstract void createDialog(List<Player<?>> players, GameState gameState, Team prayingTeam);

	protected abstract boolean handled(Game game);

	@Override
	public final void removeEffectInternal(GameState gameState, Team team) {
		enhancementRemover.removeEnhancement(gameState, team, selector, handledPrayer());
	}

}
