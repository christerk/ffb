package com.fumbbl.ffb.server.admin;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.PasswordChallenge;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.xml.UtilXml;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.sax.TransformerHandler;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Objects;

/**
 * @author Kalimar
 */
public class GameStateServlet extends HttpServlet {

	public static final String BEHAVIOURS = "behaviours";
	public static final String CHALLENGE = "challenge";

	private static final String _STATUS_OK = "ok";
	private static final String _STATUS_FAIL = "fail";

	private static final String _PARAMETER_RESPONSE = "response";
	private static final String _PARAMETER_GAME_ID = "gameId";
	private static final String _PARAMETER_TEAM_ID = "teamId";
	private static final String _PARAMETER_STATUS = "status";
	private static final String _PARAMETER_MESSSAGE = "message";
	private static final String _PARAMETER_TEAM_HOME_ID = "teamHomeId";
	private static final String _PARAMETER_TEAM_AWAY_ID = "teamAwayId";
	private static final String _PARAMETER_VALUE = "value";

	private static final String _XML_TAG_ADMIN = "admin";
	private static final String _XML_TAG_CHALLENGE = "challenge";
	private static final String _XML_TAG_STATUS = "status";

	private static final String _XML_ATTRIBUTE_INITIATED = "initiated";
	private static final String _XML_ATTRIBUTE_GAME_ID = "gameId";
	private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
	private static final String _XML_ATTRIBUTE_TEAM_HOME_ID = "teamHomeId";
	private static final String _XML_ATTRIBUTE_TEAM_AWAY_ID = "teamAwayId";
	private static final String _XML_ATTRIBUTE_GAME_STATUS = "gameStatus";
	private static final String _XML_ATTRIBUTE_SIZE = "size";
	private static final String _XML_ATTRIBUTE_VALUE = "value";

	private static final DateFormat _TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // 2001-07-04T12:08:56.235

	private final FantasyFootballServer fServer;
	private String fLastChallenge;

	public GameStateServlet(FantasyFootballServer pServer) {
		fServer = pServer;
	}

	public FantasyFootballServer getServer() {
		return fServer;
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
			isOk = checkResponse(ArrayTool.firstElement(parameters.get(_PARAMETER_RESPONSE)));
			if (isOk) {
				String response;
				if (BEHAVIOURS.equals(command)) {
					response = handleBehaviours(parameters);
				} else {
					JsonObject someObject = new JsonObject();
					someObject.add("key", "value");
					response = someObject.toString();
				}
				pResponse.getWriter().write(response);
				pResponse.getWriter().flush();
				pResponse.getWriter().close();
			}
		}

	}

	private String handleBehaviours(Map<String, String[]> pParameters) {
		String gameIdString = ArrayTool.firstElement(pParameters.get(_PARAMETER_GAME_ID));
		long gameId = parseGameId(gameIdString);
		GameState gameState = getServer().getGameCache().getGameStateById(gameId);
		JsonObject jsonObject = new JsonObject();

		if (gameState == null) {
			jsonObject.add("status", 404);
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
