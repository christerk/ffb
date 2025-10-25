package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.factory.GameOptionFactory;
import com.fumbbl.ffb.marking.SortMode;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.ServerStatus;
import com.fumbbl.ffb.option.*;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.ServerMode;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.IDbStatementFactory;
import com.fumbbl.ffb.server.db.query.DbUserSettingsQuery;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestLoadPlayerMarkings;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestResumeGamestate;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.StartGame;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

import java.util.*;

/**
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

		List<String> visibleSpectators = new ArrayList<>();
		Session[] sessions = sessionManager.getSessionsForGameId(pGameState.getId());
		for (Session session : sessions) {
			if (session.isOpen()) {
				String coach = sessionManager.getCoachForSession(session);
				ClientMode mode = sessionManager.getModeForSession(session);
				if (mode == ClientMode.PLAYER) {
					if (session == sessionManager.getSessionOfHomeCoach(pGameState.getId())) {
						playerList.add(0, coach);
					} else {
						playerList.add(coach);
					}
				} else if (!sessionManager.isSessionAdmin(session)) {
					visibleSpectators.add(coach);
				}
			}
		}
		String[] players = playerList.toArray(new String[0]);

		Map<CommonProperty, String> settingsMap = sendUserSettings(pGameState.getServer(), pCoach, pSession);

		boolean silentJoin = pMode == ClientMode.SPECTATOR && pAccountProperties.contains("ADMIN");
		if (!silentJoin) {
			server.getCommunication().sendJoin(sessions, pCoach, pMode, players, visibleSpectators);
		}

		if (pMode == ClientMode.SPECTATOR) {
			String settingValue = settingsMap.get(CommonProperty.SETTING_PLAYER_MARKING_TYPE);
			if (CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO.equalsIgnoreCase(settingValue)) {
				server.getRequestProcessor().add(new FumbblRequestLoadPlayerMarkings(pGameState, pSession, SortMode.DEFAULT));
			} else if (CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO_NO_SORT.equalsIgnoreCase(settingValue)) {
				server.getRequestProcessor().add(new FumbblRequestLoadPlayerMarkings(pGameState, pSession, SortMode.NONE));
			}
		}

		return players.length;

	}

	public static Map<CommonProperty, String> sendUserSettings(FantasyFootballServer server, String pCoach, Session pSession) {
		List<CommonProperty> settingNames = new ArrayList<>();
		List<String> settingValues = new ArrayList<>();
		// always send any client settings defined in server.ini
		for (String serverProperty : server.getPropertyKeys()) {
			if (serverProperty.startsWith("client.")) {
				settingNames.add(CommonProperty.forKey(serverProperty));
				settingValues.add(server.getProperty(serverProperty));
			}
		}
		IDbStatementFactory statementFactory = server.getDbQueryFactory();
		DbUserSettingsQuery userSettingsQuery = (DbUserSettingsQuery) statementFactory
			.getStatement(DbStatementId.USER_SETTINGS_QUERY);
		userSettingsQuery.execute(pCoach);
		Collections.addAll(settingNames, userSettingsQuery.getSettingNames());
		Collections.addAll(settingValues, userSettingsQuery.getSettingValues());
		if ((!settingNames.isEmpty()) && (!settingValues.isEmpty())) {
			server.getCommunication().sendUserSettings(pSession, settingNames.toArray(new CommonProperty[0]),
				settingValues.toArray(new String[0]));
		}
		Map<CommonProperty, String> settingsMap = new HashMap<>();
		for (int i = 0; i < settingNames.size() && i < settingValues.size(); i++) {
			settingsMap.put(settingNames.get(i), settingValues.get(i));
		}

		return settingsMap;
	}

	public static void startGame(GameState gameState) {
		Game game = gameState.getGame();
		FantasyFootballServer server = gameState.getServer();
		boolean ownershipOk = true;
		SessionManager sessionManager = server.getSessionManager();
		Session sessionOfHomeCoach = sessionManager.getSessionOfHomeCoach(game.getId());
		Session sessionOfAwayCoach = sessionManager.getSessionOfAwayCoach(game.getId());
		if (!game.isTesting() && UtilGameOption.isOptionEnabled(game, GameOptionId.CHECK_OWNERSHIP)) {
			if (!sessionManager.isHomeCoach(game.getId(), game.getTeamHome().getCoach())) {
				ownershipOk = false;
				server.getCommunication().sendStatus(sessionOfHomeCoach,
					ServerStatus.ERROR_NOT_YOUR_TEAM, null);
			}
			if (!sessionManager.isAwayCoach(game.getId(), game.getTeamAway().getCoach())) {
				ownershipOk = false;
				server.getCommunication().sendStatus(sessionOfAwayCoach,
					ServerStatus.ERROR_NOT_YOUR_TEAM, null);
			}
		}
		boolean resumed = false;
		if (ownershipOk) {
			if ((game.getFinished() == null) && (gameState.getStepStack().size() == 0)) {
				SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
				((StartGame) factory.forName(SequenceGenerator.Type.StartGame.name()))
					.pushSequence(new SequenceGenerator.SequenceParams(gameState));
			} else {
				resumed = true;
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

			server.getCommunication().sendGameState(gameState);
			gameState.fetchChanges(); // clear changes after sending the whole model

			if (resumed) {
				// Otherwise we will send all markers again which would overwrite new markings
				// coaches with manual marking made in this game so far
				// The markings are contained in the gamestate already anyway
				return;
			}

			MarkerLoadingService loadingService = new MarkerLoadingService();

			IDbStatementFactory statementFactory = server.getDbQueryFactory();
			DbUserSettingsQuery userSettingsQuery = (DbUserSettingsQuery) statementFactory
				.getStatement(DbStatementId.USER_SETTINGS_QUERY);

			boolean loadAuto = false;
			SortMode sortMode = null;
			String settingValue;

			userSettingsQuery.execute(sessionManager.getCoachForSession(sessionOfHomeCoach));
			settingValue = userSettingsQuery.getSettingValue(CommonProperty.SETTING_PLAYER_MARKING_TYPE);
			if (CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO.equalsIgnoreCase(settingValue)) {
				loadAuto = true;
				sortMode = SortMode.DEFAULT;
			} else if (CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO_NO_SORT.equalsIgnoreCase(settingValue)) {
				loadAuto = true;
				sortMode = SortMode.NONE;
			}
			loadingService.loadMarker(gameState, sessionOfHomeCoach, true, loadAuto, sortMode);

			loadAuto = false;
			sortMode = null;

			userSettingsQuery.execute(sessionManager.getCoachForSession(sessionOfAwayCoach));
			settingValue = userSettingsQuery.getSettingValue(CommonProperty.SETTING_PLAYER_MARKING_TYPE);
			if (CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO.equalsIgnoreCase(settingValue)) {
				loadAuto = true;
				sortMode = SortMode.DEFAULT;
			} else if (CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO_NO_SORT.equalsIgnoreCase(settingValue)) {
				loadAuto = true;
				sortMode = SortMode.NONE;
			}
			loadingService.loadMarker(gameState, sessionOfAwayCoach, false, loadAuto, sortMode);
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
			ruleSet.setValue("BB2025");
			//ruleSet.setValue("BB2016");
			game.getOptions().addOption(ruleSet);
			GameOptionBoolean overtime = (GameOptionBoolean) optionFactory.createGameOption(GameOptionId.OVERTIME);
			overtime.setValue(true);
			game.getOptions().addOption(overtime);
			GameOptionBoolean allowConcessions = (GameOptionBoolean) optionFactory.createGameOption(GameOptionId.ALLOW_CONCESSIONS);
			allowConcessions.setValue(false);
			//game.getOptions().addOption(allowConcessions);
			GameOptionBoolean prayer = (GameOptionBoolean) optionFactory.createGameOption(GameOptionId.INDUCEMENT_PRAYERS_AVAILABLE_FOR_UNDERDOG);
			prayer.setValue(false);
			game.getOptions().addOption(prayer);
			GameOptionBoolean claw = (GameOptionBoolean) optionFactory.createGameOption(GameOptionId.CLAW_DOES_NOT_STACK);
			claw.setValue(false);
			game.getOptions().addOption(claw);
			GameOptionInt rookies = (GameOptionInt) optionFactory.createGameOption(GameOptionId.INDUCEMENT_RIOTOUS_ROOKIES_MAX);
			rookies.setValue(9);
			//game.getOptions().addOption(rookies);
			GameOptionBoolean staff = (GameOptionBoolean) optionFactory.createGameOption(GameOptionId.ALLOW_STAFF_ON_BOTH_TEAMS);
			staff.setValue(true);
			game.getOptions().addOption(staff);
			GameOptionBoolean sameTv = (GameOptionBoolean) optionFactory.createGameOption(GameOptionId.INDUCEMENTS_ALLOW_SPENDING_TREASURY_ON_EQUAL_CTV);
			sameTv.setValue(true);
			//	game.getOptions().addOption(sameTv);
			GameOptionBoolean alwaysTreasury = (GameOptionBoolean) optionFactory.createGameOption(GameOptionId.INDUCEMENTS_ALWAYS_USE_TREASURY);
			alwaysTreasury.setValue(true);
			//	game.getOptions().addOption(alwaysTreasury);
			GameOptionString chainsaw = (GameOptionString) optionFactory.createGameOption(GameOptionId.CHAINSAW_TURNOVER);
			chainsaw.setValue(GameOptionString.CHAINSAW_TURNOVER_NEVER);
			//game.getOptions().addOption(chainsaw);
			GameOptionBoolean bombardier = (GameOptionBoolean) optionFactory.createGameOption(GameOptionId.BOMBER_PLACED_PRONE_IGNORES_TURNOVER);
			bombardier.setValue(true);
			//game.getOptions().addOption(bombardier);
			GameOptionBoolean sneaky = (GameOptionBoolean) optionFactory.createGameOption(GameOptionId.SNEAKY_GIT_CAN_MOVE_AFTER_FOUL);
			sneaky.setValue(true);
			//		game.getOptions().addOption(sneaky);
			GameOptionBoolean bomb = (GameOptionBoolean) optionFactory.createGameOption(GameOptionId.BOMB_USES_MB);
			bomb.setValue(true);
			//	game.getOptions().addOption(bomb);
			GameOptionBoolean overtimeGG = (GameOptionBoolean) optionFactory.createGameOption(GameOptionId.OVERTIME_GOLDEN_GOAL);
			overtimeGG.setValue(true);
			//game.getOptions().addOption(overtimeGG);
			GameOptionString overtimeKO = (GameOptionString) optionFactory.createGameOption(GameOptionId.OVERTIME_KICK_OFF_RESULTS);
			overtimeKO.setValue(GameOptionString.OVERTIME_KICK_OFF_RANDOM_BLITZ_OR_SOLID_DEFENCE);
			//game.getOptions().addOption(overtimeKO);
			GameOptionBoolean overdogSpending = (GameOptionBoolean) optionFactory.createGameOption(GameOptionId.INDUCEMENTS_ALLOW_OVERDOG_SPENDING);
			overdogSpending.setValue(false);
			//game.getOptions().addOption(overdogSpending);
			GameOptionBoolean ballAndChainRr = (GameOptionBoolean) optionFactory.createGameOption(GameOptionId.ALLOW_BALL_AND_CHAIN_RE_ROLL);
			ballAndChainRr.setValue(true);
			game.getOptions().addOption(ballAndChainRr);
			GameOptionBoolean tacklezones = (GameOptionBoolean) optionFactory.createGameOption(GameOptionId.ENABLE_TACKLEZONE_OVERLAYS);
			tacklezones.setValue(true);
			game.getOptions().addOption(tacklezones);
		}
	}

}
