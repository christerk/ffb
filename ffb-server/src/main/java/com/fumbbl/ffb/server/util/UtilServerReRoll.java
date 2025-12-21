package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.ReportReRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.server.step.HasIdForSingleUseReRoll;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepResult;
import com.fumbbl.ffb.server.step.mixed.pass.state.PassState;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Kalimar
 */
public class UtilServerReRoll {

	public static boolean useReRoll(IStep pStep, ReRollSource pReRollSource, Player<?> pPlayer) {
		if (pPlayer == null) {
			throw new IllegalArgumentException("Parameter player must not be null.");
		}
		boolean successful = false;
		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		StepResult stepResult = pStep.getResult();
		GameMechanic gameMechanic =
				(GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		RollMechanic rollMechanic =
				(RollMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name());
		if (pReRollSource != null) {
			boolean teamReRoll = ReRollSources.TEAM_RE_ROLL == pReRollSource;
			boolean lordOfChaos = ReRollSources.LORD_OF_CHAOS == pReRollSource && pStep instanceof HasIdForSingleUseReRoll;
			Skill reRollSourceSkill = pReRollSource.getSkill(game);
			if (teamReRoll || lordOfChaos) {
				TurnData turnData = game.getTurnData();
				ReRollSource usedAdditionalReRollSource = null;

				if (teamReRoll) {
					usedAdditionalReRollSource = gameMechanic.updateTurnDataAfterReRollUsage(turnData);
				}

				if (teamReRoll && usedAdditionalReRollSource != null) {
					stepResult.addReport(new ReportReRoll(pPlayer.getId(), usedAdditionalReRollSource, successful, 0));
				} else if (teamReRoll && LeaderState.AVAILABLE.equals(turnData.getLeaderState())) {
					stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.LEADER, successful, 0));
					turnData.setLeaderState(LeaderState.USED);
				} else {
					stepResult.addReport(new ReportReRoll(pPlayer.getId(), pReRollSource, successful, 0));
					if (lordOfChaos) {
						game.getPlayerById(((HasIdForSingleUseReRoll) pStep).idForSingleUseReRoll())
								.markUsed(reRollSourceSkill, game);
						UtilServerGame.updateSingleUseReRolls(turnData, pPlayer.getTeam(), game.getFieldModel());
					}
				}

				if (pPlayer.hasSkillProperty(NamedProperties.hasToRollToUseTeamReroll)) {
					int roll = gameState.getDiceRoller().rollSkill();
					int minimumRoll = rollMechanic.minimumLonerRoll(pPlayer);
					successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
					stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.LONER, successful, roll));
				} else {
					successful = true;
				}

			}
			if (!teamReRoll && !lordOfChaos && reRollSourceSkill != null) {
				if (ReRollSources.PRO == pReRollSource) {
					PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
					successful = (pPlayer.hasSkillProperty(NamedProperties.canRerollOncePerTurn)
							&& !playerState.hasUsedPro());
					if (successful) {
						game.getFieldModel().setPlayerState(pPlayer, playerState.changeUsedPro(true));
						int roll = gameState.getDiceRoller().rollSkill();
						successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, rollMechanic.minimumProRoll());
						stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.PRO, successful, roll));
					}
				} else {
					if (reRollSourceSkill.getSkillUsageType().isTrackOutsideActivation()) {
						successful = !pPlayer.isUsed(reRollSourceSkill);
					} else {
						successful = UtilCards.hasSkill(pPlayer, reRollSourceSkill);
					}
					stepResult.addReport(new ReportReRoll(pPlayer.getId(), pReRollSource, successful, 0));
				}
				ActingPlayer actingPlayer = game.getActingPlayer();
				if (actingPlayer.getPlayer() == pPlayer) {
					actingPlayer.markSkillUsed(reRollSourceSkill);
				} else if (reRollSourceSkill.getSkillUsageType().isTrackOutsideActivation()) {
					pPlayer.markUsed(reRollSourceSkill, game);
				}
			}
		}
		return successful;
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
