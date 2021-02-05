package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.Inducement;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.LeaderState;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.InducementSet;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.TurnData;
import com.balancedbytes.games.ffb.model.change.ModelChangeList;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.report.ReportInducement;
import com.balancedbytes.games.ffb.report.ReportLeader;
import com.balancedbytes.games.ffb.report.ReportList;
import com.balancedbytes.games.ffb.report.ReportMasterChefRoll;
import com.balancedbytes.games.ffb.report.ReportPlayerAction;
import com.balancedbytes.games.ffb.report.ReportStartHalf;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.net.SessionManager;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilActingPlayer;
import org.eclipse.jetty.websocket.api.Session;

/**
 *
 * @author Kalimar
 */
public class UtilServerGame {

	public static boolean syncGameModel(IStep pStep) {
		if ((pStep != null) && (pStep.getResult() != null) && pStep.getResult().isSynchronize()) {
			ReportList reportList = pStep.getResult().getReportList();
			Animation animation = pStep.getResult().getAnimation();
			SoundId sound = pStep.getResult().getSound();
			pStep.getResult().reset();
			return syncGameModel(pStep.getGameState(), reportList, animation, sound);
		} else {
			return false;
		}
	}

	public static boolean syncGameModel(GameState pGameState, ReportList pReportList, Animation pAnimation,
			SoundId pSound) {
		boolean synced = false;
		Game game = pGameState.getGame();
		FantasyFootballServer server = pGameState.getServer();
		UtilServerTimer.syncTime(pGameState, System.currentTimeMillis());
		ModelChangeList modelChanges = pGameState.fetchChanges();
		if ((modelChanges.size() > 0) || ((pReportList != null) && (pReportList.size() > 0)) || (pAnimation != null)
				|| (pSound != null)) {
			server.getCommunication().sendModelSync(pGameState, modelChanges, pReportList, pAnimation, pSound,
					game.getGameTime(), game.getTurnTime());
			synced = true;
		}
		return synced;
	}

	public static void changeActingPlayer(IStep pStep, String pActingPlayerId, PlayerAction pPlayerAction,
			boolean pLeaping) {
		Game game = pStep.getGameState().getGame();
		PlayerAction oldPlayerAction = game.getActingPlayer().getPlayerAction();
		if (UtilActingPlayer.changeActingPlayer(game, pActingPlayerId, pPlayerAction, pLeaping) && (pPlayerAction != null)
				&& ((oldPlayerAction == null) || (pPlayerAction.getType() != oldPlayerAction.getType()))) {
			if (oldPlayerAction == null) {
				pStep.getResult().setSound(SoundId.CLICK);
			}
			pStep.getResult().addReport(new ReportPlayerAction(pActingPlayerId, pPlayerAction));
		}
	}

	public static void startHalf(IStep pStep, int pHalf) {
		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		game.setHalf(pHalf);
		game.getTurnDataHome().setTurnNr(0);
		game.getTurnDataAway().setTurnNr(0);
		if (game.isHomeFirstOffense()) {
			game.setHomePlaying(game.getHalf() % 2 == 0);
		} else {
			game.setHomePlaying(game.getHalf() % 2 > 0);
		}
		game.getFieldModel().setBallCoordinate(null);
		game.getFieldModel().setBallInPlay(false);
		pStep.getResult().addReport(new ReportStartHalf(game.getHalf()));
		// handle Apothecaries + Wandering Apothecaries
		if (game.getHalf() < 2) {
			addApothecaries(pStep, true);
			addApothecaries(pStep, false);
		}
		// handle ReRolls + Extra Team Training
		if (game.getHalf() < 3) {
			addReRolls(pStep, true);
			addReRolls(pStep, false);

			// handle Master Chefs
			int reRollsStolenHome = rollMasterChef(pStep, true);
			int reRollsStolenAway = rollMasterChef(pStep, false);
			int delta = reRollsStolenHome - reRollsStolenAway;
			if (delta > 0) {
				game.getTurnDataHome().setReRolls(game.getTurnDataHome().getReRolls() + delta);
				game.getTurnDataAway().setReRolls(game.getTurnDataAway().getReRolls() - delta);
				if (game.getTurnDataAway().getReRolls() < 0) {
					game.getTurnDataAway().setReRolls(0);
				}
			}
			if (delta < 0) {
				delta = -delta;
				game.getTurnDataAway().setReRolls(game.getTurnDataAway().getReRolls() + delta);
				game.getTurnDataHome().setReRolls(game.getTurnDataHome().getReRolls() - delta);
				if (game.getTurnDataHome().getReRolls() < 0) {
					game.getTurnDataHome().setReRolls(0);
				}
			}
		}
		resetLeaderState(game);
		updateLeaderReRolls(pStep);

	}

	protected static void resetLeaderState(Game pGame) {
		if (pGame.getHalf() <= 2) {
			pGame.getTurnDataHome().setLeaderState(LeaderState.NONE);
			pGame.getTurnDataAway().setLeaderState(LeaderState.NONE);
		}
	}

	public static void updateLeaderReRolls(IStep pStep) {
		Game game = pStep.getGameState().getGame();
		updateLeaderReRollsForTeam(game.getTurnDataHome(), game.getTeamHome(), game.getFieldModel(), pStep);
		updateLeaderReRollsForTeam(game.getTurnDataAway(), game.getTeamAway(), game.getFieldModel(), pStep);
	}

	protected static void updateLeaderReRollsForTeam(TurnData pTurnData, Team pTeam, FieldModel pFieldModel,
			IStep pStep) {
		if (!LeaderState.USED.equals(pTurnData.getLeaderState())) {
			if (teamHasLeaderOnField(pTeam, pFieldModel)) {
				if (LeaderState.NONE.equals(pTurnData.getLeaderState())) {
					pTurnData.setLeaderState(LeaderState.AVAILABLE);
					pTurnData.setReRolls(pTurnData.getReRolls() + 1);
					pStep.getResult().addReport(new ReportLeader(pTeam.getId(), pTurnData.getLeaderState()));
				}
			} else {
				if (LeaderState.AVAILABLE.equals(pTurnData.getLeaderState())) {
					pTurnData.setLeaderState(LeaderState.NONE);
					pTurnData.setReRolls(Math.max(pTurnData.getReRolls() - 1, 0));
					pStep.getResult().addReport(new ReportLeader(pTeam.getId(), pTurnData.getLeaderState()));
				}
			}
		}
	}

	protected static boolean teamHasLeaderOnField(Team pTeam, FieldModel pFieldModel) {
		for (Player<?> player : pTeam.getPlayers()) {
			if (playerOnField(player, pFieldModel)
					&& player.hasSkillWithProperty(NamedProperties.grantsTeamRerollWhenOnPitch)) {
				return true;
			}
		}
		return false;
	}

	protected static boolean playerOnField(Player<?> pPlayer, FieldModel pFieldModel) {
		FieldCoordinate fieldCoordinate = pFieldModel.getPlayerCoordinate(pPlayer);
		return ((fieldCoordinate != null) && !fieldCoordinate.isBoxCoordinate());
	}

	private static void addApothecaries(IStep pStep, boolean pHomeTeam) {
		Game game = pStep.getGameState().getGame();
		Team team = pHomeTeam ? game.getTeamHome() : game.getTeamAway();
		TurnData turnData = pHomeTeam ? game.getTurnDataHome() : game.getTurnDataAway();
		turnData.setApothecaries(team.getApothecaries());
		Inducement wanderingApothecaries = turnData.getInducementSet().get(InducementType.WANDERING_APOTHECARIES);
		if ((wanderingApothecaries != null) && (wanderingApothecaries.getValue() > 0)) {
			turnData.setApothecaries(turnData.getApothecaries() + wanderingApothecaries.getValue());
			pStep.getResult().addReport(
					new ReportInducement(team.getId(), InducementType.WANDERING_APOTHECARIES, wanderingApothecaries.getValue()));
		}
	}

	private static void addReRolls(IStep pStep, boolean pHomeTeam) {
		Game game = pStep.getGameState().getGame();
		Team team = pHomeTeam ? game.getTeamHome() : game.getTeamAway();
		TurnData turnData = pHomeTeam ? game.getTurnDataHome() : game.getTurnDataAway();
		turnData.setReRolls(team.getReRolls());
		Inducement extraTraining = turnData.getInducementSet().get(InducementType.EXTRA_TEAM_TRAINING);
		if ((extraTraining != null) && (extraTraining.getValue() > 0)) {
			turnData.setReRolls(turnData.getReRolls() + extraTraining.getValue());
			pStep.getResult()
					.addReport(new ReportInducement(team.getId(), InducementType.EXTRA_TEAM_TRAINING, extraTraining.getValue()));
		}
	}

	private static int rollMasterChef(IStep pStep, boolean pHomeTeam) {
		int reRollsStolenTotal = 0;
		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		InducementSet inducementSet = pHomeTeam ? game.getTurnDataHome().getInducementSet()
				: game.getTurnDataAway().getInducementSet();
		Inducement masterChef = inducementSet.get(InducementType.MASTER_CHEF);
		if ((masterChef != null) && (masterChef.getValue() > 0)) {
			for (int i = 0; i < masterChef.getValue(); i++) {
				Team team = pHomeTeam ? game.getTeamHome() : game.getTeamAway();
				int[] masterChefRoll = gameState.getDiceRoller().rollMasterChef();
				int reRollsStolen = DiceInterpreter.getInstance().interpretMasterChefRoll(masterChefRoll);
				pStep.getResult().addReport(new ReportMasterChefRoll(team.getId(), masterChefRoll, reRollsStolen));
				reRollsStolenTotal += reRollsStolen;
			}
		}
		return reRollsStolenTotal;
	}

	public static void closeGame(GameState pGameState) {
		if (pGameState != null) {
			FantasyFootballServer server = pGameState.getServer();
			SessionManager sessionManager = server.getSessionManager();
			Session[] sessions = sessionManager.getSessionsForGameId(pGameState.getId());
			for (int i = 0; i < sessions.length; i++) {
				server.getCommunication().close(sessions[i]);
			}
		}
	}

	// this might be overkill, we'll see how it does in practice
	public static void handleInvalidTeam(String pTeamId, GameState gameState, FantasyFootballServer server, Throwable pThrowable) {
		server.getDebugLog().log(IServerLogLevel.ERROR, StringTool.bind("Error loading Team $1.", pTeamId));
		server.getDebugLog().log(pThrowable);
		server.getCommunication().sendStatus(gameState, ServerStatus.FUMBBL_ERROR,
			StringTool.bind("Unable to load Team with id $1.", pTeamId));
		UtilServerGame.closeGame(gameState);
	}
}
