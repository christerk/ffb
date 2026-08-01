package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.mixed.pass.state.PassState;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class UtilServerReRoll {

	public static boolean useReRoll(IStep pStep, ReRollSource pReRollSource, Player<?> pPlayer) {
		return new ReRollService().useReRoll(pStep, pReRollSource, pPlayer);
	}

	public static boolean askForReRollIfAvailable(GameState gameState, ActingPlayer actingPlayer,
	                                              ReRolledAction reRolledAction, int minimumRoll, boolean fumble) {
		return askForReRollIfAvailable(gameState, actingPlayer, reRolledAction, minimumRoll, fumble, null);
	}

	public static boolean askForReRollIfAvailable(GameState gameState, ActingPlayer actingPlayer,
	                                              ReRolledAction reRolledAction, int minimumRoll, boolean fumble,
	                                              Skill modifyingSkill) {
		return askForReRollIfAvailable(gameState, actingPlayer, reRolledAction, minimumRoll, fumble, modifyingSkill,
			Collections.emptySet());
	}

	public static boolean askForReRollIfAvailable(GameState gameState, ActingPlayer actingPlayer,
	                                              ReRolledAction reRolledAction, int minimumRoll, boolean fumble,
	                                              Skill modifyingSkill, Set<Skill> ignoreSkills) {

		Skill reRollSkill = getReRollSkill(gameState, actingPlayer, reRolledAction, ignoreSkills);
		Player<?> player = actingPlayer.getPlayer();

		return askForReRollIfAvailable(gameState, player, reRolledAction, minimumRoll, fumble, modifyingSkill, reRollSkill);

	}


	public static boolean askForReRollIfAvailable(GameState gameState, Player<?> player, ReRolledAction reRolledAction,
	                                              int minimumRoll, boolean fumble, Skill modificationSkill) {

		Skill reRollSkill = getReRollSkill(gameState, gameState.getGame().getActingPlayer(), reRolledAction,
			Collections.emptySet());
		return askForReRollIfAvailable(gameState, player, reRolledAction, minimumRoll, fumble, modificationSkill,
			reRollSkill);
	}

	public static boolean askForReRollIfAvailable(GameState gameState, Player<?> player, ReRolledAction reRolledAction,
	                                              int minimumRoll, boolean fumble, Skill modificationSkill,
	                                              Skill reRollSkill) {

		return askForReRollIfAvailable(gameState, player, reRolledAction, minimumRoll, fumble, modificationSkill,
			reRollSkill, null, null);
	}

	public static boolean askForReRollIfAvailable(GameState gameState, Player<?> player, ReRolledAction reRolledAction,
	                                              int minimumRoll, boolean fumble, Skill modificationSkill,
	                                              Skill reRollSkill, CommonProperty menuProperty,
	                                              String defaultValueKey) {
		return askForReRollIfAvailable(gameState, player, reRolledAction, minimumRoll, fumble, modificationSkill,
			reRollSkill, menuProperty, defaultValueKey, null);
	}

	public static boolean askForReRollIfAvailable(GameState gameState, Player<?> player, ReRolledAction reRolledAction,
	                                              int minimumRoll, boolean fumble, Skill modificationSkill,
	                                              Skill reRollSkill, CommonProperty menuProperty,
	                                              String defaultValueKey, List<String> messages) {
		return new ReRollService().askForReRollIfAvailable(
			ReRollRequest.forPlayer(gameState, player, reRolledAction, minimumRoll)
				.fumble(fumble)
				.modifyingSkill(modificationSkill)
				.reRollSkill(reRollSkill)
				.menu(menuProperty, defaultValueKey)
				.messages(messages)
				.build());
	}

	public static boolean askForReRollIfAvailable(GameState gameState, Player<?> player, ReRolledAction reRolledAction,
	                                              int minimumRoll, boolean fumble) {
		return askForReRollIfAvailable(gameState, player, reRolledAction, minimumRoll, fumble, null, null);
	}

	public static boolean askForReRollIfAvailable(GameState gameState, Player<?> player, ReRolledAction reRolledAction,
	                                              int minimumRoll, List<String> messages) {
		return askForReRollIfAvailable(gameState, player, reRolledAction, minimumRoll, false, messages);
	}

	public static boolean askForReRollIfAvailable(GameState gameState, Player<?> player, ReRolledAction reRolledAction,
	                                              int minimumRoll, boolean fumble, List<String> messages) {
		Skill reRollSkill = getReRollSkill(gameState, gameState.getGame().getActingPlayer(), reRolledAction,
			Collections.emptySet());
		return askForReRollIfAvailable(gameState, player, reRolledAction, minimumRoll, fumble, null, reRollSkill,
			null, null, messages);
	}

	public static boolean isProReRollAvailable(Player<?> player, Game game, PassState passState) {
		return new ReRollService().isProReRollAvailable(player, game, passState);
	}

	public static boolean isSingleUseReRollAvailable(GameState pGameState, Player<?> pPlayer) {
		return new ReRollService().isSingleUseReRollAvailable(pGameState, pPlayer);
	}

	public static boolean isTeamReRollAvailable(GameState pGameState, Player<?> pPlayer) {
		return new ReRollService().isTeamReRollAvailable(pGameState, pPlayer);
	}

	private static Skill getReRollSkill(GameState gameState, ActingPlayer actingPlayer, ReRolledAction reRolledAction,
	                                    Set<Skill> ignoreSkills) {
		return new ReRollService().findReRollSkill(gameState, actingPlayer, reRolledAction, ignoreSkills);
	}

}
