package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.PasswordChallenge;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamSkeleton;
import com.fumbbl.ffb.net.ServerStatus;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.ServerUrlProperty;
import com.fumbbl.ffb.server.util.UtilServerHttpClient;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.xml.XmlHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			pServer.getDebugLog().logWithOutGameId(IServerLogLevel.DEBUG, DebugLog.FUMBBL_RESPONSE, responseXml);
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
		try (BufferedReader xmlReader = new BufferedReader(new StringReader(pResponseXml))) {
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
		} catch (IOException | NoSuchAlgorithmException pIoE) {
			throw new FantasyFootballException(pIoE);
		}
	}

	public static String getFumbblAuthChallengeFor(FantasyFootballServer pServer, String pCoach) {
		if ((pServer == null) || !StringTool.isProvided(pCoach)) {
			return null;
		}
		try {
			String challenge = null;
			String challengeUrl = StringTool.bind(ServerUrlProperty.FUMBBL_AUTH_CHALLENGE.url(pServer.getProperties()),
					URLEncoder.encode(pCoach, CHARACTER_ENCODING));
			pServer.getDebugLog().logWithOutGameId(IServerLogLevel.DEBUG, DebugLog.FUMBBL_REQUEST, challengeUrl);
			String responseXml = UtilServerHttpClient.fetchPage(challengeUrl);
			if (StringTool.isProvided(responseXml)) {
				pServer.getDebugLog().logWithOutGameId(IServerLogLevel.DEBUG, DebugLog.FUMBBL_RESPONSE, responseXml);
				try (BufferedReader xmlReader = new BufferedReader(new StringReader(responseXml))) {
					String line;
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
			server.getDebugLog().log(IServerLogLevel.ERROR, pGameState.getGame().getId(), pFumbblState.toXml(false));
			server.getCommunication().sendStatus(sessions, ServerStatus.FUMBBL_ERROR, pFumbblState.getDescription());
		} else {
			server.getDebugLog().log(IServerLogLevel.ERROR, pGameState.getGame().getId(), _UNKNOWN_FUMBBL_ERROR);
			server.getCommunication().sendStatus(sessions, ServerStatus.FUMBBL_ERROR, _UNKNOWN_FUMBBL_ERROR);
		}
	}

	public static Team loadFumbblTeam(FantasyFootballServer pServer, String pTeamId) {
		if ((pServer == null) || !StringTool.isProvided(pTeamId)) {
			return null;
		}
		TeamSkeleton team = null;
		try {
			String teamUrl = StringTool.bind(ServerUrlProperty.FUMBBL_TEAM.url(pServer.getProperties()), pTeamId);
			String teamXml = UtilServerHttpClient.fetchPage(teamUrl);
			if (StringTool.isProvided(teamXml)) {
				team = new TeamSkeleton(pServer);
				try (BufferedReader xmlReader = new BufferedReader(new StringReader(teamXml))) {
					InputSource xmlSource = new InputSource(xmlReader);
					XmlHandler.parse(null, xmlSource, team);
				}
				team.setXmlContent(teamXml);
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
			String rosterUrl = StringTool.bind(ServerUrlProperty.FUMBBL_ROSTER_TEAM.url(pServer.getProperties()), pTeamId);
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

}
