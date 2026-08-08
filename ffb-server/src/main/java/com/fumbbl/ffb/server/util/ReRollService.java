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
		if (reRollSkill == null) {
			ActingPlayer skillSource = actingPlayer != null ? actingPlayer : gameState.getGame().getActingPlayer();
			reRollSkill = findReRollSkill(gameState, skillSource, request.getReRolledAction(), request.getIgnoreSkills());
		}

		return rollMechanic(gameState.getGame()).askForReRollIfAvailable(gameState, player, request.getReRolledAction(),
			request.getMinimumRoll(), request.isFumble(), request.getModifyingSkill(), reRollSkill,
			request.getMenuProperty(), request.getDefaultValueKey(), request.getMessages());
	}

	private Skill findReRollSkill(GameState gameState, ActingPlayer actingPlayer, ReRolledAction reRolledAction,
	                              Set<Skill> ignoreSkills) {
		Game game = gameState.getGame();
		ReRollSource reRollSource = UtilCards.getUnusedRerollSource(actingPlayer, reRolledAction, ignoreSkills);
		return reRollSource != null ? reRollSource.getSkill(game) : null;
	}

	private RollMechanic rollMechanic(Game game) {
		MechanicsFactory factory = game.getFactory(FactoryType.Factory.MECHANIC);
		return (RollMechanic) factory.forName(Mechanic.Type.ROLL.name());
	}
}
