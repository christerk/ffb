package com.fumbbl.ffb.server.step;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.commands.ICommandWithActingPlayer;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

/**
 * @author Kalimar
 */
public class UtilServerSteps {

	public static void validateStepId(IStep pStep, StepId pReceivedId) {
		if (pStep == null) {
			throw new IllegalArgumentException("Parameter step must not be null.");
		}
		if (pStep.getId() != pReceivedId) {
			throw new IllegalStateException("Wrong step id. Expected " + pStep.getId().getName() + " received "
				+ ((pReceivedId != null) ? pReceivedId.getName() : "null"));
		}
	}

	public static boolean checkCommandIsFromCurrentPlayer(GameState gameState, ReceivedCommand pReceivedCommand) {
		Game game = gameState.getGame();
		if (game.isHomePlaying()) {
			return checkCommandIsFromHomePlayer(gameState, pReceivedCommand);
		} else {
			return checkCommandIsFromAwayPlayer(gameState, pReceivedCommand);
		}
	}

	public static boolean checkCommandIsFromPassivePlayer(GameState gameState, ReceivedCommand pReceivedCommand) {
		Game game = gameState.getGame();
		if (!game.isHomePlaying()) {
			return checkCommandIsFromHomePlayer(gameState, pReceivedCommand);
		} else {
			return checkCommandIsFromAwayPlayer(gameState, pReceivedCommand);
		}
	}

	public static boolean checkCommandIsFromHomePlayer(GameState gameState, ReceivedCommand pReceivedCommand) {
		return (gameState.getServer().getSessionManager().getSessionOfHomeCoach(gameState.getId()) == pReceivedCommand
			.getSession());
	}

	public static boolean checkCommandIsFromAwayPlayer(GameState gameState, ReceivedCommand pReceivedCommand) {
		return (gameState.getServer().getSessionManager().getSessionOfAwayCoach(gameState.getId()) == pReceivedCommand
			.getSession());
	}

	public static boolean checkCommandWithActingPlayer(GameState gameState,
																										 ICommandWithActingPlayer pActingPlayerCommand) {
		Game game = gameState.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return (StringTool.isProvided(pActingPlayerCommand.getActingPlayerId())
			&& pActingPlayerCommand.getActingPlayerId().equals(actingPlayer.getPlayerId()));
	}

	public static void changePlayerAction(IStep pStep, String pPlayerId, PlayerAction pPlayerAction, boolean jumping) {
		ActingPlayer actingPlayer = pStep.getGameState().getGame().getActingPlayer();
		UtilServerGame.changeActingPlayer(pStep, pPlayerId, pPlayerAction, jumping);
		if (StringTool.isProvided(pPlayerId)) {
			UtilServerPlayerMove.updateMoveSquares(pStep.getGameState(), actingPlayer.isJumping());
			ServerUtilBlock.updateDiceDecorations(pStep.getGameState().getGame());
		}
	}

	public static void sendAddedPlayers(GameState gameState, Team pTeam, RosterPlayer[] pAddedPlayers) {
		if (ArrayTool.isProvided(pAddedPlayers) && (pTeam != null)) {
			Game game = gameState.getGame();
			FantasyFootballServer server = gameState.getServer();
			for (RosterPlayer addedPlayer : pAddedPlayers) {
				server.getCommunication().sendAddPlayer(gameState, pTeam.getId(), addedPlayer,
					game.getFieldModel().getPlayerState(addedPlayer), game.getGameResult().getPlayerResult(addedPlayer));
			}
		}
	}

	public static boolean checkTouchdown(GameState gameState) {
		boolean touchdown = false;
		Game game = gameState.getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		if (game.getFieldModel().isBallInPlay() && !game.getFieldModel().isBallMoving()) {
			FieldCoordinate ballPosition = game.getFieldModel().getBallCoordinate();
			Player<?> ballCarrier = game.getFieldModel().getPlayer(ballPosition);
			PlayerState ballCarrierState = game.getFieldModel().getPlayerState(ballCarrier);
			ActingPlayer actingPlayer = game.getActingPlayer();
			if ((ballCarrier != null) && (ballCarrierState != null) && !ballCarrierState.isProneOrStunned()
				&& ((ballCarrier != actingPlayer.getPlayer()) || !actingPlayer.isSufferingBloodLust() || !mechanic.allowMovementInEndZone())) {
				touchdown = ((game.getTeamHome().hasPlayer(ballCarrier)
					&& FieldCoordinateBounds.ENDZONE_AWAY.isInBounds(ballPosition))
					|| (game.getTeamAway().hasPlayer(ballCarrier)
					&& FieldCoordinateBounds.ENDZONE_HOME.isInBounds(ballPosition)));
			}
		}
		return touchdown;
	}

	public static boolean checkEndOfHalf(GameState gameState) {
		Game game = gameState.getGame();
		return (game.getTurnDataHome().getTurnNr() >= 8) && (game.getTurnDataAway().getTurnNr() >= 8);
	}

}
