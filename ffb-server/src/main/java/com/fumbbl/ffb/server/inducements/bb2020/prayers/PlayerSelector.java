package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerSelector {

	static PlayerSelector INSTANCE = new PlayerSelector();

	List<Player<?>> selectPlayers(Team team, Game game, int amount) {
		List<Player<?>> selected = new ArrayList<>();
		List<Player<?>> available = eligiblePlayers(determineTeam(team, game), game, Collections.emptySet());

		for (int i = 0; i < Math.min(amount, available.size()); i++) {
			Collections.shuffle(available);
			selected.add(available.remove(0));
		}
		return selected;
	}

	protected Team determineTeam(Team team, Game game) {
		return team;
	}

	List<Player<?>> eligiblePlayers(Team team, Game game, Set<Skill> skills) {
		return Arrays.stream(team.getPlayers()).filter(player -> {
				if (game.getTurnMode() == TurnMode.START_GAME) {
					return game.getFieldModel().getPlayerState(player).getBase() == PlayerState.RESERVE;
				} else {
					return FieldCoordinateBounds.FIELD.isInBounds(game.getFieldModel().getPlayerCoordinate(player));
				}
			}
		).filter(player -> !player.hasSkillProperty(NamedProperties.hasToRollToUseTeamReroll)
			&& (skills.isEmpty() || !player.getSkillsIncludingTemporaryOnes().containsAll(skills))
			&& skills.stream().allMatch(s -> s.canBeAssignedTo(player))).collect(Collectors.toList());
	}

}
