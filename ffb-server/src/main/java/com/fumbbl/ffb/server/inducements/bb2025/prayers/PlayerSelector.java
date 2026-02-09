package com.fumbbl.ffb.server.inducements.bb2025.prayers;

import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerSelector extends com.fumbbl.ffb.server.inducements.mixed.prayers.PlayerSelector {

	public static PlayerSelector INSTANCE = new PlayerSelector();

	public List<Player<?>> eligiblePlayers(Team team, Game game, Set<Skill> skills) {
		return Arrays.stream(team.getPlayers()).filter(
			player -> (skills.isEmpty() || !player.getSkillsIncludingTemporaryOnes().containsAll(skills))
				&& player.getPlayerType() != PlayerType.STAR
				&& skills.stream().allMatch(s -> s.canBeAssignedTo(player))).collect(Collectors.toList());
	}

}
