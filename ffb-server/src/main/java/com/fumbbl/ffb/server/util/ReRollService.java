package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.mixed.pass.state.PassState;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Set;

/**
 * Service handling re-roll requests. It resolves the values that used to be calculated by the various
 * {@code UtilServerReRoll.askForReRollIfAvailable} overloads and delegates to the ruleset mechanic.
 */
public class ReRollService {

	public boolean askForReRollIfAvailable(ReRollRequest request) {
		GameState gameState = request.getGameState();
		ActingPlayer actingPlayer = request.getActingPlayer();
		Player<?> player = request.getPlayer();
		if (player == null && actingPlayer != null) {
			player = actingPlayer.getPlayer();
		}

		Skill reRollSkill = request.getReRollSkill();
		if (request.isResolveReRollSkill()) {
			ActingPlayer skillSource = actingPlayer != null ? actingPlayer : gameState.getGame().getActingPlayer();
			reRollSkill = findReRollSkill(gameState, skillSource, request.getReRolledAction(), request.getIgnoreSkills());
		}

		return rollMechanic(gameState.getGame()).askForReRollIfAvailable(gameState, player, request.getReRolledAction(),
			request.getMinimumRoll(), request.isFumble(), request.getModifyingSkill(), reRollSkill,
			request.getMenuProperty(), request.getDefaultValueKey(), request.getMessages());
	}

	public Skill findReRollSkill(GameState gameState, ActingPlayer actingPlayer, ReRolledAction reRolledAction,
	                             Set<Skill> ignoreSkills) {
		Game game = gameState.getGame();
		ReRollSource reRollSource = UtilCards.getUnusedRerollSource(actingPlayer, reRolledAction, ignoreSkills);
		return reRollSource != null ? reRollSource.getSkill(game) : null;
	}

	public boolean useReRoll(IStep step, ReRollSource reRollSource, Player<?> player) {
		return rollMechanic(step.getGameState().getGame()).useReRoll(step, reRollSource, player);
	}

	public boolean isProReRollAvailable(Player<?> player, Game game, PassState passState) {
		return rollMechanic(game).isProReRollAvailable(player, game, passState);
	}

	public boolean isSingleUseReRollAvailable(GameState gameState, Player<?> player) {
		return rollMechanic(gameState.getGame()).isSingleUseReRollAvailable(gameState, player);
	}

	public boolean isTeamReRollAvailable(GameState gameState, Player<?> player) {
		return rollMechanic(gameState.getGame()).isTeamReRollAvailable(gameState, player);
	}

	private RollMechanic rollMechanic(Game game) {
		MechanicsFactory factory = game.getFactory(FactoryType.Factory.MECHANIC);
		return (RollMechanic) factory.forName(Mechanic.Type.ROLL.name());
	}
}
