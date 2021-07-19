package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.GameStatus;
import com.fumbbl.ffb.factory.GameOptionFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.ServerStatus;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionInt;
import com.fumbbl.ffb.option.GameOptionString;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.ServerMode;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.IDbStatementFactory;
import com.fumbbl.ffb.server.db.query.DbPlayerMarkersQuery;
import com.fumbbl.ffb.server.db.query.DbUserSettingsQuery;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestResumeGamestate;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.StartGame;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class UtilServerStartGame {

	public static boolean joinGameAsPlayerAndCheckIfReadyToStart(GameState pGameState, Session pSession, String pCoach,
			boolean pHomeTeam, List<String> pAccountProperties) {
		Game game = pGameState.getGame();
		FantasyFootballServer server = pGameState.getServer();
		if ((game.getTeamAway() != null) && (game.getTeamHome() != null)
				&& StringTool.isProvided(game.getTeamAway().getId())
				&& game.getTeamAway().getId().equals(game.getTeamHome().getId())) {
			server.getCommunication().sendStatus(pSession, ServerStatus.ERROR_SAME_TEAM, null);
		} else {
			return sendServerJoin(pGameState, pSession, pCoach, pHomeTeam, ClientMode.PLAYER, pAccountProperties) > 1;
		}
		return false;
	}

	public static int sendServerJoin(GameState pGameState, Session pSession, String pCoach, boolean pHomeTeam,
			ClientMode pMode, List<String> pAccountProperties) {

		FantasyFootballServer server = pGameState.getServer();
		SessionManager sessionManager = server.getSessionManager();
		sessionManager.addSession(pSession, pGameState.getId(), pCoach, pMode, pHomeTeam, pAccountProperties);

		List<String> playerList = new ArrayList<>();

		int numVisibleSpectators = 0;
		Session[] sessions = sessionManager.getSessionsForGameId(pGameState.getId());
		for (Session session : sessions) {
			String coach = sessionManager.getCoachForSession(session);
			ClientMode mode = sessionManager.getModeForSession(session);
			if (mode == ClientMode.PLAYER) {
				if (session == sessionManager.getSessionOfHomeCoach(pGameState.getId())) {
					playerList.add(0, coach);
				} else {
					playerList.add(coach);
				}
			} else if (!sessionManager.isSessionAdmin(session)) {
				numVisibleSpectators++;
			}
		}
		String[] players = playerList.toArray(new String[0]);

		boolean silentJoin = pMode == ClientMode.SPECTATOR && pAccountProperties.contains("ADMIN");
		if (!silentJoin) {
			server.getCommunication().sendJoin(sessions, pCoach, pMode, players, numVisibleSpectators);
		}

		sendUserSettings(pGameState, pCoach, pSession);

		return players.length;

	}

	public static void sendUserSettings(GameState pGameState, String pCoach, Session pSession) {
		FantasyFootballServer server = pGameState.getServer();
		List<String> settingNames = new ArrayList<>();
		List<String> settingValues = new ArrayList<>();
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
		Collections.addAll(settingNames, userSettingsQuery.getSettingNames());
		Collections.addAll(settingValues, userSettingsQuery.getSettingValues());
		if ((settingNames.size() > 0) && (settingValues.size() > 0)) {
			server.getCommunication().sendUserSettings(pSession, settingNames.toArray(new String[0]),
					settingValues.toArray(new String[0]));
		}
	}

	public static void startGame(GameState gameState) {
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
			if ((game.getFinished() == null) && (gameState.getStepStack().size() == 0)) {
				SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
				((StartGame)factory.forName(SequenceGenerator.Type.StartGame.name()))
					.pushSequence(new SequenceGenerator.SequenceParams(gameState));
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
		}
	}

	public static void addDefaultGameOptions(GameState pGameState) {
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
			GameOptionInt igors = (GameOptionInt) optionFactory.createGameOption(GameOptionId.INDUCEMENT_IGORS_MAX);
			igors.setDefault(9);
			game.getOptions().addOption(igors);
			GameOptionInt apos = (GameOptionInt) optionFactory.createGameOption(GameOptionId.INDUCEMENT_APOS_MAX);
			apos.setDefault(9);
			game.getOptions().addOption(apos);
			GameOptionInt mvps = (GameOptionInt) optionFactory.createGameOption(GameOptionId.MVP_NOMINATIONS);
			mvps.setValue(0);
			game.getOptions().addOption(mvps);
			GameOptionString ruleSet = (GameOptionString) optionFactory.createGameOption(GameOptionId.RULESVERSION);
			ruleSet.setValue("BB2016");
			//game.getOptions().addOption(ruleSet);
			GameOptionBoolean overtime = (GameOptionBoolean) optionFactory.createGameOption(GameOptionId.OVERTIME);
			overtime.setDefault(true);
			//		game.getOptions().addOption(overtime);
			GameOptionBoolean claw = (GameOptionBoolean) optionFactory.createGameOption(GameOptionId.CLAW_DOES_NOT_STACK);
			claw.setDefault(true);
			//		game.getOptions().addOption(claw);
		}
	}

}
