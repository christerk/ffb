package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerSelector extends com.fumbbl.ffb.server.inducements.mixed.prayers.PlayerSelector {

	public static PlayerSelector INSTANCE = new PlayerSelector();

	public List<Player<?>> eligiblePlayers(Team team, Game game, Set<Skill> skills) {
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
