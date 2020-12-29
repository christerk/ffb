package com.balancedbytes.games.ffb.server.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.factory.GameOptionFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.option.GameOptionBoolean;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.GameOptionString;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbStatementFactory;
import com.balancedbytes.games.ffb.server.db.query.DbPlayerMarkersQuery;
import com.balancedbytes.games.ffb.server.db.query.DbUserSettingsQuery;
import com.balancedbytes.games.ffb.server.net.SessionManager;
import com.balancedbytes.games.ffb.server.request.fumbbl.FumbblRequestResumeGamestate;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class UtilServerStartGame {

	public static boolean joinGameAsPlayerAndCheckIfReadyToStart(GameState pGameState, Session pSession, String pCoach,
			boolean pHomeTeam) {
		Game game = pGameState.getGame();
		FantasyFootballServer server = pGameState.getServer();
		if ((game.getTeamAway() != null) && (game.getTeamHome() != null)
				&& StringTool.isProvided(game.getTeamAway().getId())
				&& game.getTeamAway().getId().equals(game.getTeamHome().getId())) {
			server.getCommunication().sendStatus(pSession, ServerStatus.ERROR_SAME_TEAM, null);
		} else {
			if (sendServerJoin(pGameState, pSession, pCoach, pHomeTeam, ClientMode.PLAYER) > 1) {
				return true;
			}
		}
		return false;
	}

	public static int sendServerJoin(GameState pGameState, Session pSession, String pCoach, boolean pHomeTeam,
			ClientMode pMode) {

		FantasyFootballServer server = pGameState.getServer();
		SessionManager sessionManager = server.getSessionManager();
		sessionManager.addSession(pSession, pGameState.getId(), pCoach, pMode, pHomeTeam);

		List<String> playerList = new ArrayList<String>();

		Session[] sessions = sessionManager.getSessionsForGameId(pGameState.getId());
		for (int i = 0; i < sessions.length; i++) {
			String coach = sessionManager.getCoachForSession(sessions[i]);
			ClientMode mode = sessionManager.getModeForSession(sessions[i]);
			if (mode == ClientMode.PLAYER) {
				if (sessions[i] == sessionManager.getSessionOfHomeCoach(pGameState.getId())) {
					playerList.add(0, coach);
				} else {
					playerList.add(coach);
				}
			}
		}
		String[] players = playerList.toArray(new String[playerList.size()]);

		server.getCommunication().sendJoin(sessions, pCoach, pMode, players, sessions.length - playerList.size());

		sendUserSettings(pGameState, pCoach, pSession);

		return players.length;

	}

	public static void sendUserSettings(GameState pGameState, String pCoach, Session pSession) {
		FantasyFootballServer server = pGameState.getServer();
		List<String> settingNames = new ArrayList<String>();
		List<String> settingValues = new ArrayList<String>();
		// always send any client settings defined in server.ini
		for (String serverProperty : server.getProperties()) {
			if (serverProperty.startsWith("client.")) {
				settingNames.add(serverProperty);
				settingValues.add(server.getProperty(serverProperty));
			}
		}
		IDbStatementFactory statementFactory = server.getDbQueryFactory();
		DbUserSettingsQuery userSettingsQuery = (DbUserSettingsQuery) statementFactory
				.getStatement(DbStatementId.USER_SETTINGS_QUERY);
		userSettingsQuery.execute(pCoach);
		for (String userSettingName : userSettingsQuery.getSettingNames()) {
			settingNames.add(userSettingName);
		}
		for (String userSettingValue : userSettingsQuery.getSettingValues()) {
			settingValues.add(userSettingValue);
		}
		if ((settingNames.size() > 0) && (settingValues.size() > 0)) {
			server.getCommunication().sendUserSettings(pSession, settingNames.toArray(new String[settingNames.size()]),
					settingValues.toArray(new String[settingValues.size()]));
		}
	}

	public static boolean startGame(GameState gameState) {
		Game game = gameState.getGame();
		FantasyFootballServer server = gameState.getServer();
		boolean ownershipOk = true;
		if (!game.isTesting() && UtilGameOption.isOptionEnabled(game, GameOptionId.CHECK_OWNERSHIP)) {
			if (!server.getSessionManager().isHomeCoach(game.getId(), game.getTeamHome().getCoach())) {
				ownershipOk = false;
				server.getCommunication().sendStatus(server.getSessionManager().getSessionOfHomeCoach(game.getId()),
						ServerStatus.ERROR_NOT_YOUR_TEAM, null);
			}
			if (!server.getSessionManager().isAwayCoach(game.getId(), game.getTeamAway().getCoach())) {
				ownershipOk = false;
				server.getCommunication().sendStatus(server.getSessionManager().getSessionOfAwayCoach(game.getId()),
						ServerStatus.ERROR_NOT_YOUR_TEAM, null);
			}
		}
		if (ownershipOk) {
			addDefaultGameOptions(gameState);
			if ((game.getFinished() == null) && (gameState.getStepStack().size() == 0)) {
				SequenceGenerator.getInstance().pushStartGameSequence(gameState);
			} else {
				if (server.getMode() == ServerMode.FUMBBL) {
					server.getRequestProcessor().add(new FumbblRequestResumeGamestate(gameState));
				}
			}
			if (GameStatus.PAUSED == gameState.getStatus()) {
				gameState.setStatus(GameStatus.ACTIVE);
				server.getGameCache().queueDbUpdate(gameState, true);
			}
			if (!game.isWaitingForOpponent()) {
				UtilServerTimer.startTurnTimer(gameState, System.currentTimeMillis());
			}
			DbPlayerMarkersQuery dbPlayerMarkersQuery = (DbPlayerMarkersQuery) server.getDbQueryFactory()
					.getStatement(DbStatementId.PLAYER_MARKERS_QUERY);
			dbPlayerMarkersQuery.execute(gameState);
			server.getCommunication().sendGameState(gameState);
			gameState.fetchChanges(); // clear changes after sending the whole model
			return true;
		} else {
			return false;
		}
	}

	private static void addDefaultGameOptions(GameState pGameState) {
		Game game = pGameState.getGame();
		FantasyFootballServer server = pGameState.getServer();
		if (ServerMode.STANDALONE == server.getMode()) {
			GameOptionFactory optionFactory = new GameOptionFactory();
			GameOptionString pitchUrl = (GameOptionString) optionFactory.createGameOption(GameOptionId.PITCH_URL);
			pitchUrl.setValue("http://localhost:2224/icons/pitches/fumbblcup.zip");
			game.getOptions().addOption(pitchUrl);
			GameOptionBoolean wizard = (GameOptionBoolean) optionFactory.createGameOption(GameOptionId.WIZARD_AVAILABLE);
			wizard.setDefault(true);
			game.getOptions().addOption(wizard);
		}
	}

}
