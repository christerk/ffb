package com.balancedbytes.games.ffb.server.request.fumbbl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jetty.websocket.api.Session;
import org.xml.sax.InputSource;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.PasswordChallenge;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.util.UtilServerHttpClient;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.XmlHandler;

/**
 * 
 * @author Kalimar
 */
public class UtilFumbblRequest {

	public static final String CHARACTER_ENCODING = "UTF-8";

	private static final Pattern _PATTERN_CHALLENGE = Pattern.compile("<challenge>([^<]+)</challenge>");
	private static final String _UNKNOWN_FUMBBL_ERROR = "Unknown problem accessing Fumbbl.";

	public static FumbblGameState processFumbblGameStateRequest(FantasyFootballServer pServer, String pRequestUrl) {
		if ((pServer == null) || !StringTool.isProvided(pRequestUrl)) {
			return null;
		}
		try {
			String responseXml = UtilServerHttpClient.fetchPage(pRequestUrl);
			pServer.getDebugLog().log(IServerLogLevel.DEBUG, DebugLog.FUMBBL_RESPONSE, responseXml);
			return processFumbblGameStateResponse(pServer, pRequestUrl, responseXml);
		} catch (IOException ioe) {
			throw new FantasyFootballException(ioe);
		}
	}

	private static FumbblGameState processFumbblGameStateResponse(FantasyFootballServer pServer, String pRequestUrl,
			String pResponseXml) {
		if ((pServer == null) || !StringTool.isProvided(pResponseXml)) {
			return null;
		}
		FumbblGameState gameState;
		try (BufferedReader xmlReader = new BufferedReader(new StringReader(pResponseXml));) {
			InputSource xmlSource = new InputSource(xmlReader);
			gameState = new FumbblGameState(pRequestUrl);
			XmlHandler.parse(null, xmlSource, gameState);
		} catch (IOException ioe) {
			throw new FantasyFootballException(ioe);
		}
		return gameState;
	}

	public static String getFumbblAuthChallengeResponseForFumbblUser(FantasyFootballServer pServer) {
		if (pServer == null) {
			return null;
		}
		String fumbblUser = pServer.getProperty(IServerProperty.FUMBBL_USER);
		String challenge = getFumbblAuthChallengeFor(pServer, fumbblUser);
		String fumbblUserPassword = pServer.getProperty(IServerProperty.FUMBBL_PASSWORD);
		return createFumbblAuthChallengeResponse(challenge, fumbblUserPassword);
	}

	public static String createFumbblAuthChallengeResponse(String pChallenge, String pPassword) {
		if (!StringTool.isProvided(pChallenge) || !StringTool.isProvided(pPassword)) {
			return null;
		}
		try {
			byte[] encodedPassword = PasswordChallenge.fromHexString(pPassword);
			return PasswordChallenge.createResponse(pChallenge, encodedPassword);
		} catch (IOException pIoE) {
			throw new FantasyFootballException(pIoE);
		} catch (NoSuchAlgorithmException pNsaE) {
			throw new FantasyFootballException(pNsaE);
		}
	}

	public static String getFumbblAuthChallengeFor(FantasyFootballServer pServer, String pCoach) {
		if ((pServer == null) || !StringTool.isProvided(pCoach)) {
			return null;
		}
		try {
			String challenge = null;
			String challengeUrl = StringTool.bind(pServer.getProperty(IServerProperty.FUMBBL_AUTH_CHALLENGE),
					URLEncoder.encode(pCoach, CHARACTER_ENCODING));
			pServer.getDebugLog().log(IServerLogLevel.DEBUG, DebugLog.FUMBBL_REQUEST, challengeUrl);
			String responseXml = UtilServerHttpClient.fetchPage(challengeUrl);
			if (StringTool.isProvided(responseXml)) {
				pServer.getDebugLog().log(IServerLogLevel.DEBUG, DebugLog.FUMBBL_RESPONSE, responseXml);
				try (BufferedReader xmlReader = new BufferedReader(new StringReader(responseXml))) {
					String line = null;
					while ((line = xmlReader.readLine()) != null) {
						Matcher challengeMatcher = _PATTERN_CHALLENGE.matcher(line);
						if (challengeMatcher.find()) {
							challenge = challengeMatcher.group(1);
							break;
						}
					}
				}
			}
			return challenge;
		} catch (IOException pIoE) {
			throw new FantasyFootballException(pIoE);
		}
	}

	public static void reportFumbblError(GameState pGameState, FumbblGameState pFumbblState) {
		if (pGameState == null) {
			return;
		}
		FantasyFootballServer server = pGameState.getServer();
		Session[] sessions = server.getSessionManager().getSessionsForGameId(pGameState.getId());
		if (pFumbblState != null) {
			server.getDebugLog().log(IServerLogLevel.ERROR, pFumbblState.toXml(false));
			server.getCommunication().sendStatus(sessions, ServerStatus.FUMBBL_ERROR, pFumbblState.getDescription());
		} else {
			server.getDebugLog().log(IServerLogLevel.ERROR, _UNKNOWN_FUMBBL_ERROR);
			server.getCommunication().sendStatus(sessions, ServerStatus.FUMBBL_ERROR, _UNKNOWN_FUMBBL_ERROR);
		}
	}

	public static Team loadFumbblTeam(Game game, FantasyFootballServer pServer, String pTeamId) {
		if ((pServer == null) || !StringTool.isProvided(pTeamId)) {
			return null;
		}
		Team team = null;
		try {
			String teamUrl = StringTool.bind(pServer.getProperty(IServerProperty.FUMBBL_TEAM), pTeamId);
			String teamXml = UtilServerHttpClient.fetchPage(teamUrl);
			if (StringTool.isProvided(teamXml)) {
				team = new Team();
				try (BufferedReader xmlReader = new BufferedReader(new StringReader(teamXml))) {
					InputSource xmlSource = new InputSource(xmlReader);
					XmlHandler.parse(game, xmlSource, team);
				}
			}
		} catch (IOException ioe) {
			throw new FantasyFootballException(ioe);
		}
		return team;
	}

	public static Roster loadFumbblRosterForTeam(Game game, FantasyFootballServer pServer, String pTeamId) {
		if ((pServer == null) || !StringTool.isProvided(pTeamId)) {
			return null;
		}
		Roster roster = null;
		try {
			String rosterUrl = StringTool.bind(pServer.getProperty(IServerProperty.FUMBBL_ROSTER_TEAM), pTeamId);
			String rosterXml = UtilServerHttpClient.fetchPage(rosterUrl);
			if (StringTool.isProvided(rosterXml)) {
				roster = new Roster();
				try (BufferedReader xmlReader = new BufferedReader(new StringReader(rosterXml))) {
					InputSource xmlSource = new InputSource(xmlReader);
					XmlHandler.parse(game, xmlSource, roster);
				}
			}
		} catch (IOException ioe) {
			throw new FantasyFootballException(ioe);
		}
		return roster;
	}

	public static void main(String[] args) {

		try (FileInputStream fileStream = new FileInputStream(args[0]);
				BufferedInputStream propertyInputStream = new BufferedInputStream(fileStream)) {

			Properties properties = new Properties();
			properties.load(propertyInputStream);

			FantasyFootballServer server = new FantasyFootballServer(ServerMode.STANDALONE, properties);

			String responseXml = "<gamestate>" + "<result>OK</result>" + "<options>"
					+ "<option name=\"overtime\" value=\"false\"/>" + "<option name=\"turntime\" value=\"240\"/>"
					+ "<option name=\"maxCards\" value=\"5\"/>" + "<option name=\"cardGold\" value=\"0\"/>"
					+ "<option name=\"inducementGold\" value=\"0\"/>" + "<option name=\"wideZonePlayers\" value=\"2\"/>"
					+ "<option name=\"playersOnField\" value=\"11\"/>" + "<option name=\"playersOnLos\" value=\"3\"/>"
					+ "<option name=\"spikedBall\" value=\"false\"/>" + "<option name=\"clawNoStack\" value=\"false\"/>"
					+ "<option name=\"pilingOnNoStack\" value=\"false\"/>" + "<option name=\"pilingOnKoDouble\" value=\"false\"/>"
					+ "<option name=\"sneakyAsFoul\" value=\"false\"/>" + "<option name=\"sneakyBanToKo\" value=\"false\"/>"
					+ "<option name=\"standFirmNoFall\" value=\"false\"/>"
					+ "<option name=\"rightStuffCancelTackle\" value=\"false\"/>" + "<option name=\"extraMvp\" value=\"true\"/>"
					+ "<option name=\"freeInducementMoney\" value=\"100000\"/>" + "</options>" + "</gamestate>";

			FumbblGameState fumbblGameState = processFumbblGameStateResponse(server, "http://fumbbl.com/", responseXml);
			GameOptions gameOptions = new GameOptions(new Game());
			gameOptions.init(fumbblGameState.getOptions());
			System.out.println(gameOptions.toJsonValue());

		} catch (Exception pAny) {
			pAny.printStackTrace();
		}

	}

}
