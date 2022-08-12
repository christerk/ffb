package com.fumbbl.ffb.server.admin;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.PasswordChallenge;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.request.ServerRequestSaveReplay;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.xml.UtilXml;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.sax.TransformerHandler;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @author Kalimar
 */
public class BackupServlet extends HttpServlet {

	public static final String CHALLENGE = "challenge";
	public static final String LOAD = "load";
	public static final String SAVE = "save";

	private static final String _STATUS_OK = "ok";
	private static final String _STATUS_FAIL = "fail";

	private static final String _PARAMETER_RESPONSE = "response";
	private static final String _PARAMETER_GAME_ID = "gameId";

	private static final String _XML_TAG_BACKUP = "backup";
	private static final String _XML_TAG_CHALLENGE = "challenge";
	private static final String _XML_TAG_SAVE = "save";
	private static final String _XML_TAG_ERROR = "error";
	private static final String _XML_TAG_STATUS = "status";

	private static final String _XML_ATTRIBUTE_GAME_ID = "gameId";

	private final FantasyFootballServer fServer;
	private String fLastChallenge;

	public BackupServlet(FantasyFootballServer pServer) {
		fServer = pServer;
	}

	public FantasyFootballServer getServer() {
		return fServer;
	}

	@Override
	protected void doGet(HttpServletRequest pRequest, HttpServletResponse pResponse)
		throws IOException {

		String command = pRequest.getPathInfo();
		if ((command != null) && (command.length() > 1) && command.startsWith("/")) {
			command = command.substring(1);
		}

		if (CHALLENGE.equals(command)) {
			executeChallenge(pResponse);
		}

		if (LOAD.equals(command)) {
			executeLoad(pRequest, pResponse);
		}

		if (SAVE.equals(command)) {
			executeSave(pRequest, pResponse);
		}

	}

	private void executeChallenge(HttpServletResponse pResponse) throws IOException {

		pResponse.setContentType("text/xml;charset=UTF-8");

		TransformerHandler handler = UtilXml.createTransformerHandler(pResponse.getWriter(), true);
		try {
			handler.startDocument();
		} catch (SAXException pSaxException) {
			throw new FantasyFootballException(pSaxException);
		}

		UtilXml.startElement(handler, _XML_TAG_BACKUP);

		boolean isOk = true;
		String challenge = fServer.getProperty(IServerProperty.BACKUP_SALT) +
			System.currentTimeMillis();
		try {
			fLastChallenge = PasswordChallenge.toHexString(PasswordChallenge.md5Encode(challenge.getBytes()));
		} catch (NoSuchAlgorithmException pE) {
			fLastChallenge = null;
		}
		if (fLastChallenge != null) {
			UtilXml.addValueElement(handler, _XML_TAG_CHALLENGE, fLastChallenge);
		} else {
			isOk = false;
		}

		UtilXml.addValueElement(handler, _XML_TAG_STATUS, isOk ? _STATUS_OK : _STATUS_FAIL);

		UtilXml.endElement(handler, _XML_TAG_BACKUP);

		try {
			handler.endDocument();
		} catch (SAXException pSaxException) {
			throw new FantasyFootballException(pSaxException);
		}

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

	private void executeSave(HttpServletRequest pRequest, HttpServletResponse pResponse) throws IOException {

		pResponse.setContentType("text/xml;charset=UTF-8");
		Map<String, String[]> parameters = pRequest.getParameterMap();
		String gameIdString = ArrayTool.firstElement(parameters.get(_PARAMETER_GAME_ID));
		String response = ArrayTool.firstElement(parameters.get(_PARAMETER_RESPONSE));

		TransformerHandler handler = UtilXml.createTransformerHandler(pResponse.getWriter(), true);
		try {
			handler.startDocument();
		} catch (SAXException pSaxException) {
			throw new FantasyFootballException(pSaxException);
		}

		UtilXml.startElement(handler, _XML_TAG_BACKUP);

		boolean isOk = checkResponse(response);

		if (isOk) {
			AttributesImpl attributes = new AttributesImpl();
			UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GAME_ID, gameIdString);
			UtilXml.addEmptyElement(handler, _XML_TAG_SAVE, attributes);
			long gameId = parseGameId(gameIdString);
			if (gameId > 0) {
				getServer().getRequestProcessor().add(new ServerRequestSaveReplay(gameId));
			} else {
				UtilXml.addValueElement(handler, _XML_TAG_ERROR, "Invalid or missing gameId parameter");
				isOk = false;
			}
		}

		UtilXml.addValueElement(handler, _XML_TAG_STATUS, isOk ? _STATUS_OK : _STATUS_FAIL);

		UtilXml.endElement(handler, _XML_TAG_BACKUP);

		try {
			handler.endDocument();
		} catch (SAXException pSaxException) {
			throw new FantasyFootballException(pSaxException);
		}

	}

	private void executeLoad(HttpServletRequest pRequest, HttpServletResponse pResponse) throws IOException {

		Map<String, String[]> parameters = pRequest.getParameterMap();
		long gameId = parseGameId(ArrayTool.firstElement(parameters.get(_PARAMETER_GAME_ID)));

		String acceptEncoding = pRequest.getHeader("Accept-Encoding");
		boolean doGzip = StringTool.isProvided(acceptEncoding) && acceptEncoding.contains("gzip");

		// fServer.getDebugLog().log(IServerLogLevel.WARN, gameId, doGzip ? "Requesting
		// gzipped replay." : "Requesting plain replay.");

		pResponse.setContentType("application/json;charset=UTF-8");

		Closeable out = null;
		try {

			GameState gameState = loadGameState(gameId);
			if (gameState == null) {
				return;
			}

			if (doGzip) {

				pResponse.addHeader("Content-Encoding", "gzip");

				out = new BufferedOutputStream(pResponse.getOutputStream());

				byte[] gzippedJson = UtilJson.gzip(gameState.toJsonValue());

				String logMessage = "Compressing json " + gameState.toJsonValue().toString().length() + " --> "
					+ gzippedJson.length;
				fServer.getDebugLog().log(IServerLogLevel.DEBUG, gameId, logMessage);

				((BufferedOutputStream) out).write(gzippedJson, 0, gzippedJson.length);
				((BufferedOutputStream) out).flush();

			} else {

				out = new BufferedWriter(pResponse.getWriter());
				String jsonString = gameState.toJsonValue().toString();
				((BufferedWriter) out).write(jsonString);
				((BufferedWriter) out).flush();

			}

		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	private GameState loadGameState(long gameId) {
		GameState gameState = UtilBackup.load(getServer(), gameId);
		if (gameState != null) {
			fServer.getDebugLog().log(IServerLogLevel.WARN, gameId, "Replay loaded from file system.");
		}
		if (gameState == null) {
			// fallback: try to load gameState from db
			gameState = fServer.getGameCache().queryFromDb(gameId);
			if (gameState != null) {
				fServer.getDebugLog().log(IServerLogLevel.WARN, gameId, "Replay loaded from database.");
			}
		}
		if ((gameState == null)) {
			gameState = loadFromS3(gameId);
			if (gameState != null) {
				fServer.getDebugLog().log(IServerLogLevel.WARN, gameId, "Replay loaded from s3 bucket.");
			}
		}
		return gameState;
	}


	private GameState loadFromS3(long gameId) {
		String basePath = fServer.getProperty(IServerProperty.BACKUP_S3_BASE_PATH);
		if (!basePath.endsWith("/")) {
			basePath += "/";
		}
		String fileName = basePath + UtilBackup.calculateFolderPathForGame(fServer, String.valueOf(gameId));
		AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(fServer.getProperty(IServerProperty.BACKUP_S3_REGION))
			.withCredentials(new ProfileCredentialsProvider(fServer.getProperty(IServerProperty.BACKUP_S3_PROFILE))).build();

		byte[] buffer = new byte[1024];
		int buffer_size;

		try (S3Object s3Replay = s3.getObject(fServer.getProperty(IServerProperty.BACKUP_S3_BUCKET), fileName);
				 S3ObjectInputStream s3Stream = s3Replay.getObjectContent();
				 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			while ((buffer_size = s3Stream.read(buffer)) > 0) {
				byteArrayOutputStream.write(buffer, 0, buffer_size);
			}
			GameState gameState = new GameState(fServer);
			gameState.initFrom(gameState.getGame().getRules(), UtilJson.gunzip(byteArrayOutputStream.toByteArray()));
			return gameState;
		} catch (Exception e) {
			fServer.getDebugLog().log(gameId, e);
		}
		return null;
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

}
