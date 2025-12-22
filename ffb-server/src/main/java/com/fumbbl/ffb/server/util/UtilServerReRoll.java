package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.CommonProperty;
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

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class UtilServerReRoll {

	public static boolean useReRoll(IStep pStep, ReRollSource pReRollSource, Player<?> pPlayer) {
		Game game = pStep.getGameState().getGame();
		MechanicsFactory factory = game.getFactory(FactoryType.Factory.MECHANIC);
		RollMechanic mechanic = (RollMechanic) factory.forName(Mechanic.Type.ROLL.name());
		return mechanic.useReRoll(pStep, pReRollSource, pPlayer);
	}

	public static boolean askForReRollIfAvailable(GameState gameState, ActingPlayer actingPlayer,
	                                              ReRolledAction reRolledAction,
	                                              int minimumRoll, boolean fumble) {
		return askForReRollIfAvailable(gameState, actingPlayer, reRolledAction, minimumRoll, fumble, null);
	}

	public static boolean askForReRollIfAvailable(GameState gameState, ActingPlayer actingPlayer,
	                                              ReRolledAction reRolledAction,
	                                              int minimumRoll, boolean fumble, Skill modifyingSkill) {
		return askForReRollIfAvailable(gameState, actingPlayer, reRolledAction, minimumRoll, fumble, modifyingSkill,
				Collections.emptySet());
	}

	public static boolean askForReRollIfAvailable(GameState gameState, ActingPlayer actingPlayer,
	                                              ReRolledAction reRolledAction,
	                                              int minimumRoll, boolean fumble, Skill modifyingSkill,
	                                              Set<Skill> ignoreSkills) {

		Game game = gameState.getGame();
		ReRollSource reRollSource = UtilCards.getUnusedRerollSource(actingPlayer, reRolledAction, ignoreSkills);
		Skill reRollSkill = reRollSource != null ? reRollSource.getSkill(game) : null;
		Player<?> player = actingPlayer.getPlayer();

		return askForReRollIfAvailable(gameState, player, reRolledAction, minimumRoll, fumble, modifyingSkill, reRollSkill);

	}

	public static boolean askForReRollIfAvailable(GameState gameState, Player<?> player, ReRolledAction reRolledAction,
	                                              int minimumRoll, boolean fumble, Skill modificationSkill,
	                                              Skill reRollSkill) {

		return askForReRollIfAvailable(gameState, player, reRolledAction, minimumRoll, fumble, modificationSkill,
				reRollSkill, null, null);
	}

	public static boolean askForReRollIfAvailable(GameState gameState, Player<?> player, ReRolledAction reRolledAction,
	                                              int minimumRoll, boolean fumble, Skill modificationSkill,
	                                              Skill reRollSkill,
	                                              CommonProperty menuProperty, String defaultValueKey) {
		return askForReRollIfAvailable(gameState, player, reRolledAction, minimumRoll, fumble, modificationSkill,
				reRollSkill,
				menuProperty, defaultValueKey, null);
	}

	public static boolean askForReRollIfAvailable(GameState gameState, Player<?> player, ReRolledAction reRolledAction,
	                                              int minimumRoll, boolean fumble, Skill modificationSkill,
	                                              Skill reRollSkill,
	                                              CommonProperty menuProperty, String defaultValueKey,
	                                              List<String> messages) {
		Game game = gameState.getGame();
		MechanicsFactory factory = game.getFactory(FactoryType.Factory.MECHANIC);
		RollMechanic mechanic = (RollMechanic) factory.forName(Mechanic.Type.ROLL.name());

		return mechanic.askForReRollIfAvailable(gameState, player, reRolledAction, minimumRoll, fumble, modificationSkill,
				reRollSkill, menuProperty, defaultValueKey, messages);
	}

	public static boolean askForReRollIfAvailable(GameState gameState, Player<?> player, ReRolledAction reRolledAction,
	                                              int minimumRoll, boolean fumble) {
		return askForReRollIfAvailable(gameState, player, reRolledAction, minimumRoll, fumble, null, null);
	}

	public static boolean askForReRollIfAvailable(GameState gameState, Player<?> player, ReRolledAction reRolledAction,
	                                              int minimumRoll, List<String> messages) {
		return askForReRollIfAvailable(gameState, player, reRolledAction, minimumRoll, false, null, null,
				null, null, messages);
	}

	public static boolean isProReRollAvailable(Player<?> player, Game game, PassState passState) {
		MechanicsFactory factory = game.getFactory(FactoryType.Factory.MECHANIC);
		RollMechanic mechanic = (RollMechanic) factory.forName(Mechanic.Type.ROLL.name());

		return mechanic.isProReRollAvailable(player, game, passState);
	}

	public static boolean isSingleUseReRollAvailable(GameState pGameState, Player<?> pPlayer) {
		Game game = pGameState.getGame();
		MechanicsFactory factory = game.getFactory(FactoryType.Factory.MECHANIC);
		RollMechanic mechanic = (RollMechanic) factory.forName(Mechanic.Type.ROLL.name());

		return mechanic.isSingleUseReRollAvailable(pGameState, pPlayer);
	}

	public static boolean isTeamReRollAvailable(GameState pGameState, Player<?> pPlayer) {
		Game game = pGameState.getGame();
		MechanicsFactory factory = game.getFactory(FactoryType.Factory.MECHANIC);
		RollMechanic mechanic = (RollMechanic) factory.forName(Mechanic.Type.ROLL.name());

		return mechanic.isTeamReRollAvailable(pGameState, pPlayer);
	}

}
