package com.fumbbl.ffb.server.admin;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.PasswordChallenge;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameCache;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.xml.UtilXml;
import org.eclipse.jetty.websocket.api.Session;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.sax.TransformerHandler;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
public class GameStateServlet extends HttpServlet {

	public static final String BEHAVIOURS = "behaviours";
	public static final String CHALLENGE = "challenge";
	public static final String GET = "get";
	public static final String SET = "set";
	public static final String AUTO = "auto";

	private static final String _STATUS_OK = "ok";
	private static final String _STATUS_FAIL = "fail";

	private static final String _PARAMETER_RESPONSE = "response";
	private static final String _PARAMETER_GAME_ID = "gameId";
	private static final String _PARAMETER_FROM_DB = "fromDb";
	private static final String _PARAMETER_INCLUDE_LOG = "includeLog";

	private static final String _XML_TAG_ADMIN = "admin";
	private static final String _XML_TAG_CHALLENGE = "challenge";
	private static final String _XML_TAG_STATUS = "status";

	private final FantasyFootballServer fServer;
	private String fLastChallenge;

	public GameStateServlet(FantasyFootballServer pServer) {
		fServer = pServer;
	}

	public FantasyFootballServer getServer() {
		return fServer;
	}

	@Override
	protected void doPost(HttpServletRequest pRequest, HttpServletResponse pResponse) throws ServletException, IOException {
		boolean isOk;

		String command = pRequest.getPathInfo();
		if ((command != null) && (command.length() > 1) && command.startsWith("/")) {
			command = command.substring(1);
		}
		Map<String, String[]> parameters = pRequest.getParameterMap();

		String body = pRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

		isOk = checkResponse(ArrayTool.firstElement(parameters.get(_PARAMETER_RESPONSE)));
		if (isOk) {
			String response;
			if (SET.equals(command)) {
				response = handleSet(body);
			} else {
				JsonObject someObject = new JsonObject();
				someObject.add("message", "method '" + command + "' not found");
				response = someObject.toString();
				pResponse.setStatus(404);
			}
			pResponse.getWriter().write(response);
			pResponse.getWriter().flush();
			pResponse.getWriter().close();
		}
	}

	@Override
	protected void doGet(HttpServletRequest pRequest, HttpServletResponse pResponse)
		throws IOException {

		boolean isOk;

		String command = pRequest.getPathInfo();
		if ((command != null) && (command.length() > 1) && command.startsWith("/")) {
			command = command.substring(1);
		}
		Map<String, String[]> parameters = pRequest.getParameterMap();

		if (CHALLENGE.equals(command)) {
			handleChallenge(pResponse);
		} else {
			pResponse.setContentType("application/json; charset=UTF-8");

			isOk = checkResponse(ArrayTool.firstElement(parameters.get(_PARAMETER_RESPONSE)));
			if (isOk) {
				String response;
				if (BEHAVIOURS.equals(command)) {
					response = handleBehaviours(parameters, pResponse);
				} else if (GET.equals(command)) {
					response = handleGet(parameters, pResponse);
				} else {
					JsonObject someObject = new JsonObject();
					someObject.add("message", "method '" + command + "' not found");
					response = someObject.toString();
					pResponse.setStatus(404);
				}
				pResponse.getWriter().write(response);
				pResponse.getWriter().flush();
				pResponse.getWriter().close();
			}
		}

	}

	private String handleGet(Map<String, String[]> pParameters, HttpServletResponse pResponse) {
		String gameIdString = ArrayTool.firstElement(pParameters.get(_PARAMETER_GAME_ID));
		long gameId = parseGameId(gameIdString);
		String fromDbString = ArrayTool.firstElement(pParameters.get(_PARAMETER_FROM_DB));
		Boolean fromDb = null;
		if (StringTool.isProvided(fromDbString) && !AUTO.equals(fromDbString)) {
			fromDb = Boolean.parseBoolean(fromDbString);
		}

		String includeString = ArrayTool.firstElement(pParameters.get(_PARAMETER_INCLUDE_LOG));
		boolean include = Boolean.parseBoolean(includeString);

		GameCache gameCache = getServer().getGameCache();
		GameState gameState = null;
		if (fromDb == null || !fromDb) {
			gameState = gameCache.getGameStateById(gameId);
		}

		if (gameState == null && (fromDb == null || fromDb)) {
			gameState = gameCache.queryFromDb(gameId);
		}
		JsonObject jsonObject = new JsonObject();

		if (gameState == null) {
			pResponse.setStatus(404);
			return jsonObject.add("message", "Game '" + gameId + "' not found").toString();
		} else {
			return gameState.toJsonValue(include).toString();
		}
	}

	private String handleSet(String body) {
		GameState gameState = new GameState(getServer()).initFrom(getServer().getFactorySource(), JsonValue.readFrom(body));

		SessionManager sessionManager = getServer().getSessionManager();
		Session[] sessions = sessionManager.getSessionsForGameId(gameState.getId());
		for (Session session : sessions) {
			getServer().getCommunication().close(session);
		}

		getServer().getGameCache().queueDbUpdate(gameState, true);
		getServer().getGameCache().addGame(gameState);
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("status", "ok");
		return jsonObject.toString();
	}


	private String handleBehaviours(Map<String, String[]> pParameters, HttpServletResponse pResponse) {
		String gameIdString = ArrayTool.firstElement(pParameters.get(_PARAMETER_GAME_ID));
		long gameId = parseGameId(gameIdString);
		GameState gameState = getServer().getGameCache().getGameStateById(gameId);
		JsonObject jsonObject = new JsonObject();

		if (gameState == null) {
			jsonObject.add("message", "Game '" + gameId + "' not found");
			pResponse.setStatus(404);
		} else {
			SkillFactory skillFactory = gameState.getGame().getFactory(FactoryType.Factory.SKILL);
			skillFactory.getSkills().stream().filter(skill -> Objects.nonNull(skill.getSkillBehaviour())).forEach(skill -> jsonObject.add(skill.getClass().getCanonicalName(), skill.getSkillBehaviour().getClass().getCanonicalName()));
		}

		return jsonObject.toString();
	}

	private void handleChallenge(HttpServletResponse pResponse) throws IOException {
		boolean isOk;
		pResponse.setContentType("text/xml; charset=UTF-8");
		TransformerHandler handler = UtilXml.createTransformerHandler(pResponse.getWriter(), true);

		try {
			handler.startDocument();
		} catch (SAXException pSaxException) {
			throw new FantasyFootballException(pSaxException);
		}
		UtilXml.startElement(handler, _XML_TAG_ADMIN);

		isOk = handleChallenge(handler);

		UtilXml.addValueElement(handler, _XML_TAG_STATUS, isOk ? _STATUS_OK : _STATUS_FAIL);

		UtilXml.endElement(handler, _XML_TAG_ADMIN);

		try {
			handler.endDocument();
		} catch (SAXException pSaxException) {
			throw new FantasyFootballException(pSaxException);
		}
	}

	private boolean handleChallenge(TransformerHandler pHandler) {
		boolean isOk = true;
		String challenge = fServer.getProperty(IServerProperty.ADMIN_SALT) +
			System.currentTimeMillis();
		try {
			fLastChallenge = PasswordChallenge.toHexString(PasswordChallenge.md5Encode(challenge.getBytes()));
		} catch (NoSuchAlgorithmException pE) {
			fLastChallenge = null;
		}
		if (fLastChallenge != null) {
			UtilXml.addValueElement(pHandler, _XML_TAG_CHALLENGE, fLastChallenge);
		} else {
			isOk = false;
		}
		return isOk;
	}

	private long parseGameId(String pGameStateId) {
		if (StringTool.isProvided(pGameStateId)) {
			try {
				return Long.parseLong(pGameStateId);
			} catch (NumberFormatException pNfe) {
				// continue and return 0
			}
		}
		return 0;
	}

	private boolean checkResponse(String pResonse) {
		boolean isOk = (fLastChallenge != null);
		if (isOk) {
			byte[] md5Password = PasswordChallenge.fromHexString(fServer.getProperty(IServerProperty.ADMIN_PASSWORD));
			try {
				String response = PasswordChallenge.createResponse(fLastChallenge, md5Password);
				isOk = response.equals(pResonse);
			} catch (NoSuchAlgorithmException | IOException pE) {
				isOk = false;
			}
		}
		fLastChallenge = null;
		return isOk;
	}

}
