package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.ApothecaryStatus;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.CardFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.ReportRaiseDead;
import com.fumbbl.ffb.report.ReportRegenerationRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeBallAndChain;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeServer;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.util.UtilBox;
import com.fumbbl.ffb.util.UtilPlayer;

/**
 *
 * @author Kalimar
 */
public class UtilServerInjury {

	public static InjuryResult handleInjury(IStep pStep, InjuryTypeServer<?> pInjuryType, Player<?> pAttacker,
	                                        Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate,
	                                        InjuryResult pOldInjuryResult, ApothecaryMode pApothecaryMode) {

		if (pDefender == null) {
			throw new IllegalArgumentException("Parameter defender must not be null.");
		}
		if (pInjuryType == null) {
			throw new IllegalArgumentException("Parameter injuryTypeServer must not be null.");
		}

		InjuryContext injuryContext = pInjuryType.injuryContext();

		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		DiceRoller diceRoller = gameState.getDiceRoller();
		injuryContext.setInjuryType(pInjuryType.injuryType());
		injuryContext.setDefenderId(pDefender.getId());
		injuryContext.setAttackerId((pAttacker != null) ? pAttacker.getId() : null);
		injuryContext.setDefenderCoordinate(pDefenderCoordinate);
		injuryContext.setApothecaryStatus(ApothecaryStatus.NO_APOTHECARY);
		injuryContext.setApothecaryMode(pApothecaryMode);

		// ball and chain always breaks armor on being knocked down
		if (pDefender.hasSkillProperty(NamedProperties.placedProneCausesInjuryRoll)) {
			injuryContext.setArmorBroken(true);
		}

		InjuryContext oldInjuryContext;
		if (pOldInjuryResult != null) {
			oldInjuryContext = pOldInjuryResult.injuryContext();
		} else {
			oldInjuryContext = new InjuryContext();
		}

		pInjuryType.handleInjury(pStep, game, gameState, diceRoller, pAttacker, pDefender, pDefenderCoordinate,
			fromCoordinate, oldInjuryContext, pApothecaryMode);

		if (injuryContext.isArmorBroken()) {
			if (pDefender.hasSkillProperty(NamedProperties.ignoreFirstArmourBreak) && (injuryContext.getArmorRoll() != null)) {
				injuryContext.setArmorBroken(false);
				injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
				String source = pDefender.getSource(NamedProperties.ignoreFirstArmourBreak);
				Card card = ((CardFactory) game.getFactory(FactoryType.Factory.CARD)).forName(source);
				UtilServerCards.deactivateCard(pStep, card);
			}
		}

		if (injuryContext.isSeriousInjury()) {
			RollMechanic rollMechanic = ((RollMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name()));
			injuryContext
					.setSeriousInjury(rollMechanic.interpretSeriousInjuryRoll(injuryContext));
			if (pDefender.hasSkillProperty(NamedProperties.requiresSecondCasualtyRoll)) {
				injuryContext.setSeriousInjuryDecay(
					rollMechanic.interpretSeriousInjuryRoll(injuryContext, true));
			}
		}

		if (pDefender.hasSkillProperty(NamedProperties.convertStunToKO)
				&& (injuryContext.getInjury() != null) && (injuryContext.getInjury().getBase() == PlayerState.STUNNED)) {
			injuryContext.setInjury(new PlayerState(PlayerState.KNOCKED_OUT));
		}

		if (injuryContext.getPlayerState() != null) {
			if (injuryContext.isCasualty() || injuryContext.isKnockedOut()) {
				injuryContext.setSufferedInjury(injuryContext.getPlayerState());
				if (!pInjuryType.canUseApo() || (injuryContext.isKnockedOut()
						&& pDefender.hasSkillProperty(NamedProperties.placedProneCausesInjuryRoll))) {
					injuryContext.setApothecaryStatus(ApothecaryStatus.NO_APOTHECARY);
				} else if ((game.getTeamHome().hasPlayer(pDefender) && (game.getTurnDataHome().getApothecaries() > 0)
						&& pDefender.getPlayerType() != PlayerType.STAR)
						|| (game.getTeamAway().hasPlayer(pDefender) && (game.getTurnDataAway().getApothecaries() > 0)
								&& pDefender.getPlayerType() != PlayerType.STAR)) {
					injuryContext.setApothecaryStatus(ApothecaryStatus.DO_REQUEST);
				} else {
					injuryContext.setApothecaryStatus(ApothecaryStatus.NO_APOTHECARY);
				}
			}
		}

		if (injuryContext.isCasualty() || injuryContext.isKnockedOut() || injuryContext.isReserve()) {
			injuryContext.setSendToBoxTurn(game.getTurnData().getTurnNr());
			injuryContext.setSendToBoxHalf(game.getHalf());

			injuryContext.setSendToBoxReason(pInjuryType.sendToBoxReason());
		}

		if (injuryContext.getPlayerState() != null) {
			switch (injuryContext.getPlayerState().getBase()) {
			case PlayerState.RIP:
				injuryContext.setSound(SoundId.RIP);
				break;
			case PlayerState.SERIOUS_INJURY:
			case PlayerState.BADLY_HURT:
				injuryContext.setSound(SoundId.INJURY);
				break;
			case PlayerState.KNOCKED_OUT:
				injuryContext.setSound(SoundId.KO);
				break;
			default:
				if (injuryContext.getInjuryType().shouldPlayFallSound()) {
					injuryContext.setSound(SoundId.FALL);
				}
				break;
			}
		}

		InjuryResult injuryResult = new InjuryResult();
		injuryResult.setInjuryContext(pInjuryType.injuryContext());
		return injuryResult;

	}
	public static boolean handleRegeneration(IStep pStep, Player<?> pPlayer) {
		return handleRegeneration(pStep, pPlayer, null);
	}

	public static boolean handleRegeneration(IStep pStep, Player<?> pPlayer, PlayerState givenPlayerState) {
		boolean successful = false;
		if (pPlayer != null) {
			GameState gameState = pStep.getGameState();
			Game game = gameState.getGame();
			PlayerState playerState = givenPlayerState != null ? givenPlayerState : game.getFieldModel().getPlayerState(pPlayer);
			if ((playerState != null) && playerState.isCasualty()
					&& pPlayer.hasSkillProperty(NamedProperties.canRollToSaveFromInjury)) {
				DiceRoller diceRoller = gameState.getDiceRoller();
				DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();
				int roll = diceRoller.rollSkill();
				successful = diceInterpreter.isRegenerationSuccessful(roll);
				if (successful) {
					game.getFieldModel().setPlayerState(pPlayer, playerState.changeBase(PlayerState.RESERVE));
					GameResult gameResult = game.getGameResult();
					PlayerResult playerResult = gameResult.getPlayerResult(pPlayer);
					playerResult.setSeriousInjury(null);
					playerResult.setSeriousInjuryDecay(null);
					UtilBox.putPlayerIntoBox(game, pPlayer);
					UtilBox.refreshBoxes(game);
					UtilServerGame.updateLeaderReRolls(pStep);
				}
				pStep.getResult()
						.addReport(new ReportRegenerationRoll(pPlayer.getId(), successful, roll, 4, false, null));
			}
		}
		return successful;
	}

	public static boolean handleRaiseDead(IStep pStep, InjuryResult pInjuryResult) {

		RosterPlayer raisedPlayer = null;
		boolean nurglesRot = false;
		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		Player<?> deadPlayer = game.getPlayerById(pInjuryResult.injuryContext().getDefenderId());
		Team necroTeam = UtilPlayer.findOtherTeam(game, deadPlayer);
		TeamResult necroTeamResult = (game.getTeamHome() == necroTeam) ? game.getGameResult().getTeamResultHome()
				: game.getGameResult().getTeamResultAway();
		boolean deadPlayerPreventsRaisedFromDead = deadPlayer.hasSkillProperty(NamedProperties.preventRaiseFromDead);

		if ((pInjuryResult != null) && (pInjuryResult.injuryContext().getPlayerState() != null)
				&& (PlayerState.RIP == pInjuryResult.injuryContext().getPlayerState().getBase())) {
			if (necroTeam.getRoster().hasNecromancer() && (necroTeamResult.getRaisedDead() == 0)
					&& (deadPlayer.getStrength() <= 4) && !deadPlayerPreventsRaisedFromDead) {
				raisedPlayer = raisePlayer(game, necroTeam, necroTeamResult, deadPlayer.getName(), nurglesRot,
						deadPlayer.getId());
			} else {
				Player<?> attacker = game.getPlayerById(pInjuryResult.injuryContext().getAttackerId());
				if ((attacker != null) && attacker.hasSkillProperty(NamedProperties.hasNurglesRot)
						&& (deadPlayer.getStrength() <= 4) && !deadPlayerPreventsRaisedFromDead
						&& !deadPlayer.hasSkillProperty(NamedProperties.requiresSecondCasualtyRoll)) {
					RosterPosition zombiePosition = necroTeam.getRoster().getRaisedRosterPosition();
					if (zombiePosition != null) {
						nurglesRot = true;
						raisedPlayer = raisePlayer(game, necroTeam, necroTeamResult, deadPlayer.getName(), nurglesRot,
								deadPlayer.getId());
					}
				}
			}
		}

		if (raisedPlayer != null) {

			// communicate raised player to clients
			gameState.getServer().getCommunication().sendAddPlayer(gameState, necroTeam.getId(), raisedPlayer,
					game.getFieldModel().getPlayerState(raisedPlayer), game.getGameResult().getPlayerResult(raisedPlayer));
			pStep.getResult().addReport(new ReportRaiseDead(raisedPlayer.getId(), nurglesRot));
			pStep.getResult().setSound(SoundId.ORGAN);

			return true;

		} else {
			return false;
		}

	}

	private static RosterPlayer raisePlayer(Game pGame, Team pNecroTeam, TeamResult pNecroTeamResult, String pPlayerName,
			boolean pNurglesRot, String killedId) {
		RosterPlayer raisedPlayer = null;
		RosterPosition zombiePosition = pNecroTeam.getRoster().getRaisedRosterPosition();
		if (zombiePosition != null) {
			pNecroTeamResult.setRaisedDead(pNecroTeamResult.getRaisedDead() + 1);
			raisedPlayer = new RosterPlayer();
			StringBuilder raisedPlayerId = new StringBuilder().append(killedId).append("R")
					.append(pNecroTeamResult.getRaisedDead());
			raisedPlayer.setId(raisedPlayerId.toString());
			raisedPlayer.updatePosition(zombiePosition, pGame.getRules());
			raisedPlayer.setName(pPlayerName);
			raisedPlayer.setNr(pNecroTeam.getMaxPlayerNr() + 1);
			raisedPlayer.setType(PlayerType.RAISED_FROM_DEAD);
			pNecroTeam.addPlayer(raisedPlayer);
			PlayerResult playerResult = pGame.getGameResult().getPlayerResult(raisedPlayer);
			playerResult.setSendToBoxHalf(pGame.getHalf());
			playerResult.setSendToBoxTurn(pGame.getTurnData().getTurnNr());
			if (pNurglesRot) {
				pGame.getFieldModel().setPlayerState(raisedPlayer, new PlayerState(PlayerState.MISSING));
				playerResult.setSendToBoxReason(SendToBoxReason.NURGLES_ROT);
			} else {
				pGame.getFieldModel().setPlayerState(raisedPlayer, new PlayerState(PlayerState.RESERVE));
				playerResult.setSendToBoxReason(SendToBoxReason.RAISED);
			}
			UtilBox.putPlayerIntoBox(pGame, raisedPlayer);
		}
		return raisedPlayer;
	}

	public static StepParameterSet dropPlayer(IStep pStep, Player<?> pPlayer, ApothecaryMode pApothecaryMode) {
		return dropPlayer(pStep, pPlayer, PlayerState.PRONE, pApothecaryMode, false);
	}

	public static StepParameterSet stunPlayer(IStep pStep, Player<?> pPlayer, ApothecaryMode pApothecaryMode) {
		return dropPlayer(pStep, pPlayer, PlayerState.STUNNED, pApothecaryMode, false);
	}

	public static StepParameterSet dropPlayer(IStep pStep, Player<?> pPlayer, ApothecaryMode pApothecaryMode, boolean eligibleForSafePairOfHands) {
		return dropPlayer(pStep, pPlayer, PlayerState.PRONE, pApothecaryMode, eligibleForSafePairOfHands);
	}

	public static StepParameterSet stunPlayer(IStep pStep, Player<?> pPlayer, ApothecaryMode pApothecaryMode, boolean eligibleForSafePairOfHands) {
		return dropPlayer(pStep, pPlayer, PlayerState.STUNNED, pApothecaryMode, eligibleForSafePairOfHands);
	}

	// drops the given player
	// sets stepParameter END_TURN if player is on acting team and drops the ball
	// sets stepParameter INJURY_RESULT if player has skill Ball&Chain
	private static StepParameterSet dropPlayer(IStep pStep, Player<?> pPlayer, int pPlayerBase,
			ApothecaryMode pApothecaryMode, boolean eligibleForSafePairOfHands) {
		StepParameterSet stepParameters = new StepParameterSet();
		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((playerCoordinate != null) && (playerState != null)) {
			if (pPlayer.hasSkillProperty(NamedProperties.placedProneCausesInjuryRoll)) {
				pStep.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, UtilServerInjury.handleInjury(pStep,
						new InjuryTypeBallAndChain(), null, pPlayer, playerCoordinate, null, null, pApothecaryMode)));
			} else {
				if ((playerState.getBase() != PlayerState.PRONE) && (playerState.getBase() != PlayerState.STUNNED)) {
					playerState = playerState.changeBase(pPlayerBase);
					if ((pPlayer == game.getActingPlayer().getPlayer()) || (PlayerState.STUNNED == pPlayerBase)) {
						playerState = playerState.changeActive(false);
					}
				}
				playerState = playerState.changeRooted(false);
				game.getFieldModel().setPlayerState(pPlayer, playerState);
			}

			if (eligibleForSafePairOfHands && UtilPlayer.hasBall(game, pPlayer)) {
				stepParameters.add(StepParameter.from(StepParameterKey.DROPPED_BALL_CARRIER, pPlayer.getId()));
			}

			if (playerCoordinate.equals(game.getFieldModel().getBallCoordinate()) && game.getTurnMode() != TurnMode.BLITZ) {
				game.getFieldModel().setBallMoving(true);
				stepParameters
						.add(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
				// check for turnover
				boolean endTurn;
				switch (game.getTurnMode()) {
				case BOMB_HOME:
				case BOMB_HOME_BLITZ:
					endTurn = game.getTeamHome().hasPlayer(pPlayer);
					break;
				case BOMB_AWAY:
				case BOMB_AWAY_BLITZ:
					endTurn = game.getTeamAway().hasPlayer(pPlayer);
					break;
				case PASS_BLOCK:
					endTurn = false;
					break;
				default:
					Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
					endTurn = actingTeam.hasPlayer(pPlayer);
					break;
				}
				if (endTurn) {
					stepParameters.add(new StepParameter(StepParameterKey.END_TURN, true));
				}
			}
		}
		UtilServerPlayerMove.updateMoveSquares(gameState, false);
		return stepParameters;
	}

}