package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class PlayerSelector {

	static PlayerSelector INSTANCE = new PlayerSelector();

	List<Player<?>> selectPlayers(Team team, Game game, int amount) {
		List<Player<?>> selected = new ArrayList<>();
		List<Player<?>> available = eligiblePlayers(determineTeam(team, game), game);

		for (int i = 0; i < Math.min(amount, available.size()); i++) {
			Collections.shuffle(available);
			selected.add(available.remove(0));
		}
		return selected;
	}

	protected Team determineTeam(Team team, Game game) {
		return team;
	}

	List<Player<?>> eligiblePlayers(Team team, Game game) {
		return Arrays.stream(team.getPlayers()).filter(player -> {
				if (game.getTurnMode() == TurnMode.KICKOFF) {
					return FieldCoordinateBounds.FIELD.isInBounds(game.getFieldModel().getPlayerCoordinate(player));
				} else {
					return game.getFieldModel().getPlayerState(player).getBase() == PlayerState.RESERVE;
				}
			}
		).filter(player -> !player.hasSkillProperty(NamedProperties.hasToRollToUseTeamReroll)).collect(Collectors.toList());
	}

}
