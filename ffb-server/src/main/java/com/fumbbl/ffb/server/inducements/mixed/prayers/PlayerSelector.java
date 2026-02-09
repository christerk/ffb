package com.fumbbl.ffb.server.inducements.mixed.prayers;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class PlayerSelector {

	public List<Player<?>> selectPlayers(Team team, Game game, int amount, Set<Skill> skills) {
		List<Player<?>> selected = new ArrayList<>();
		List<Player<?>> available = eligiblePlayers(determineTeam(team, game), game, skills);

		for (int i = 0; i < Math.min(amount, available.size()); i++) {
			Collections.shuffle(available);
			selected.add(available.remove(0));
		}
		return selected;
	}

	public Team determineTeam(Team team, Game game) {
		return team;
	}

	public abstract List<Player<?>> eligiblePlayers(Team team, Game game, Set<Skill> skills);

}
