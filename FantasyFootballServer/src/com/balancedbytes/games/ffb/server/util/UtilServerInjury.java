package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.ApothecaryStatus;
import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.model.RosterPlayer;
import com.balancedbytes.games.ffb.model.RosterPosition;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.TeamResult;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportRaiseDead;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeBallAndChain;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeServer;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.util.UtilBox;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;



/**
 * 
 * @author Kalimar
 */
public class UtilServerInjury {

	public static InjuryResult handleInjury(
			IStep pStep,
			InjuryTypeServer<?> pInjuryType,
			Player pAttacker,
			Player pDefender,
			FieldCoordinate pDefenderCoordinate,
			InjuryResult pOldInjuryResult,
			ApothecaryMode pApothecaryMode
			) {

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
		if (UtilCards.hasSkillWithProperty(pDefender, NamedProperties.placedProneCausesInjuryRoll)) {
			injuryContext.setArmorBroken(true);
		}
		
		pInjuryType.handleInjury(pStep, game, gameState, diceRoller, pAttacker, pDefender, pDefenderCoordinate, pOldInjuryResult.injuryContext(), pApothecaryMode);
		
		if (injuryContext.isArmorBroken()) {
			if (UtilCards.hasCard(game, pDefender, Card.LUCKY_CHARM) && (injuryContext.getArmorRoll() != null)) {
				injuryContext.setArmorBroken(false);
				injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
				UtilServerCards.deactivateCard(pStep, Card.LUCKY_CHARM);
			}
		}
		
		if (injuryContext.isSeriousInjury()) {
			injuryContext.setSeriousInjury(DiceInterpreter.getInstance().interpretRollSeriousInjury(injuryContext.getCasualtyRoll()));
			if (UtilCards.hasSkillWithProperty(pDefender, NamedProperties.requiresSecondCasualtyRoll)) {
				injuryContext.setSeriousInjuryDecay(DiceInterpreter.getInstance().interpretRollSeriousInjury(injuryContext.getCasualtyRollDecay()));
			}
		}

		if (UtilCards.hasSkillWithProperty(pDefender, NamedProperties.convertStunToKO) && (injuryContext.getInjury() != null)
				&& (injuryContext.getInjury().getBase() == PlayerState.STUNNED)) {
			injuryContext.setInjury(new PlayerState(PlayerState.KNOCKED_OUT));
		}
		
		if (injuryContext.getPlayerState() != null) {
			if (injuryContext.isCasualty() || injuryContext.isKnockedOut()) {
				injuryContext.setSufferedInjury(injuryContext.getPlayerState());
				if (pInjuryType.canUseApo()
						|| (injuryContext.isKnockedOut() && UtilCards.hasSkillWithProperty(pDefender, NamedProperties.placedProneCausesInjuryRoll))) {
					injuryContext.setApothecaryStatus(ApothecaryStatus.NO_APOTHECARY);
				} else if ((game.getTeamHome().hasPlayer(pDefender) && (game.getTurnDataHome().getApothecaries() > 0) && pDefender.getPlayerType() != PlayerType.STAR)
						|| (game.getTeamAway().hasPlayer(pDefender) && (game.getTurnDataAway().getApothecaries() > 0) && pDefender.getPlayerType() != PlayerType.STAR)) {
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


		if ((injuryContext != null) && (injuryContext.getPlayerState() != null)) {
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


	public static boolean handleRegeneration(IStep pStep, Player pPlayer) {
		boolean successful = false;
		if (pPlayer != null) {
			GameState gameState = pStep.getGameState();
			Game game = gameState.getGame();
			PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
			if ((playerState != null) && playerState.isCasualty() && UtilCards.hasSkillWithProperty(pPlayer, NamedProperties.canRollToSaveFromInjury)) {
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
				pStep.getResult().addReport(new ReportSkillRoll(ReportId.REGENERATION_ROLL, pPlayer.getId(), successful, roll, 4, false));
			}
		}
		return successful;
	}

	public static boolean handleRaiseDead(IStep pStep, InjuryResult pInjuryResult) {

		RosterPlayer raisedPlayer = null;
		boolean nurglesRot = false;
		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		Player deadPlayer = game.getPlayerById(pInjuryResult.injuryContext().getDefenderId());
		Team necroTeam = UtilPlayer.findOtherTeam(game, deadPlayer);
		TeamResult necroTeamResult = (game.getTeamHome() == necroTeam) ? game.getGameResult().getTeamResultHome() : game.getGameResult().getTeamResultAway();
		boolean deadPlayerPreventsRaisedFromDead = UtilCards.hasSkillWithProperty(deadPlayer, NamedProperties.preventRaiseFromDead);

		if ((pInjuryResult != null) && (pInjuryResult.injuryContext().getPlayerState() != null) && (PlayerState.RIP == pInjuryResult.injuryContext().getPlayerState().getBase())) {
			if (necroTeam.getRoster().hasNecromancer() && (necroTeamResult.getRaisedDead() == 0) && (deadPlayer.getStrength() <= 4)
					&& !deadPlayerPreventsRaisedFromDead) {
				raisedPlayer = raisePlayer(game, necroTeam, necroTeamResult, deadPlayer.getName(), nurglesRot, deadPlayer.getId());
			} else {
				Player attacker = game.getPlayerById(pInjuryResult.injuryContext().getAttackerId());
				if ((attacker != null) && UtilCards.hasSkillWithProperty(attacker, NamedProperties.hasNurglesRot) && (deadPlayer.getStrength() <= 4)
						&& !deadPlayerPreventsRaisedFromDead
						&& !UtilCards.hasSkillWithProperty(deadPlayer, NamedProperties.requiresSecondCasualtyRoll)) {
					RosterPosition zombiePosition = necroTeam.getRoster().getRaisedRosterPosition();
					if (zombiePosition != null) {
						nurglesRot = true;
						raisedPlayer = raisePlayer(game, necroTeam, necroTeamResult, deadPlayer.getName(), nurglesRot, deadPlayer.getId());
					}
				}
			}
		}

		if (raisedPlayer != null) {

			// communicate raised player to clients
			gameState.getServer().getCommunication().sendAddPlayer(
					gameState, necroTeam.getId(), raisedPlayer, game.getFieldModel().getPlayerState(raisedPlayer), game.getGameResult().getPlayerResult(raisedPlayer)
					);
			pStep.getResult().addReport(new ReportRaiseDead(raisedPlayer.getId(), nurglesRot));
			pStep.getResult().setSound(SoundId.ORGAN);

			return true;

		} else {
			return false;
		}

	}

	private static RosterPlayer raisePlayer(Game pGame, Team pNecroTeam, TeamResult pNecroTeamResult, String pPlayerName, boolean pNurglesRot, String killedId) {
		RosterPlayer raisedPlayer = null;
		RosterPosition zombiePosition = pNecroTeam.getRoster().getRaisedRosterPosition();
		if (zombiePosition != null) {
			pNecroTeamResult.setRaisedDead(pNecroTeamResult.getRaisedDead() + 1);
			raisedPlayer = new RosterPlayer();
			StringBuilder raisedPlayerId = new StringBuilder().append(killedId).append("R").append(pNecroTeamResult.getRaisedDead());
			raisedPlayer.setId(raisedPlayerId.toString());
			raisedPlayer.updatePosition(zombiePosition);
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

	public static StepParameterSet dropPlayer(IStep pStep, Player pPlayer, ApothecaryMode pApothecaryMode) {
		return dropPlayer(pStep, pPlayer, PlayerState.PRONE, pApothecaryMode);
	}

	public static StepParameterSet stunPlayer(IStep pStep, Player pPlayer, ApothecaryMode pApothecaryMode) {
		return dropPlayer(pStep, pPlayer, PlayerState.STUNNED, pApothecaryMode);
	}

	// drops the given player
	// sets stepParameter END_TURN if player is on acting team and drops the ball
	// sets stepParameter INJURY_RESULT if player has skill Ball&Chain
	private static StepParameterSet dropPlayer(IStep pStep, Player pPlayer, int pPlayerBase, ApothecaryMode pApothecaryMode) {
		StepParameterSet stepParameters = new StepParameterSet();
		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((playerCoordinate != null) && (playerState != null)) {
			if (UtilCards.hasSkillWithProperty(pPlayer, NamedProperties.placedProneCausesInjuryRoll)) {
				pStep.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT,
						UtilServerInjury.handleInjury(pStep, new InjuryTypeBallAndChain(), null, pPlayer, playerCoordinate, null, pApothecaryMode))
						);
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
			if (playerCoordinate.equals(game.getFieldModel().getBallCoordinate()) && game.getTurnMode() != TurnMode.BLITZ) {
				game.getFieldModel().setBallMoving(true);
				stepParameters.add(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
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
