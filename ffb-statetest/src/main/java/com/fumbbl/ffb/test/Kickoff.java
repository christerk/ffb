package com.fumbbl.ffb.test;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandEndTurn;
import com.fumbbl.ffb.net.commands.ClientCommandKickoff;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;

import java.util.HashMap;

public class Kickoff {

	/**
	 * Starts a game built with {@link GameStateBuilder#initialState()} and drives it through the initial kickoff
	 * setup (inducements, both teams' setups, Dwarfen Wisdom, Swarming and Master Chef steps). Returns the current
	 * step, which is the KICKOFF step waiting for a {@link ClientCommandKickoff}.
	 */
	public static IStep throughSetup(GameState gameState) {
		IStep step = StepEngine.start(gameState);
		return endSetups(gameState, step);
	}

	/**
	 * Sends the kickoff command, placing the ball at the given coordinate.
	 */
	public static IStep kick(GameState gameState, FieldCoordinate ballCoordinate) {
		return StepEngine.respond(gameState, new ClientCommandKickoff(ballCoordinate));
	}

	/**
	 * Answers the Kick skill dialog shown by the kickoff scatter roll step, using (or declining) the Kick skill of
	 * the given player.
	 */
	public static IStep useKick(GameState gameState, String playerId, boolean useKick) {
		Player<?> player = gameState.getGame().getPlayerById(playerId);
		Skill kick = player.getSkillWithProperty(NamedProperties.canReduceKickDistance);
		return StepEngine.respond(gameState, Commands.useSkill(kick, useKick, playerId));
	}

	private static IStep endSetups(GameState gameState, IStep step) {
		int guard = 0;
		while (step != null && step.getId() == StepId.SETUP && guard++ < 10) {
			step = StepEngine.respond(gameState,
				new ClientCommandEndTurn(gameState.getGame().getTurnMode(), new HashMap<>()));
		}
		return step;
	}
}
