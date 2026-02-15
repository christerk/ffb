package com.fumbbl.ffb.server.inducements.bb2025.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2025.Prayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2025)
public class IronManHandler extends com.fumbbl.ffb.server.inducements.mixed.prayers.IronManHandler {
	@Override
	public Prayer handledPrayer() {
		return Prayer.IRON_MAN;
	}

	@Override
	public PlayerSelector selector() {
		return new IronManPlayerSelector();
	}

	private static class IronManPlayerSelector extends PlayerSelector {
		@Override
		public List<Player<?>> eligiblePlayers(Team team, Game game, Set<Skill> skills) {
			return super.eligiblePlayers(team, game, skills).stream()
				.filter(player -> player.getArmour() < 11)
				.collect(Collectors.toList());
		}
	}
}
