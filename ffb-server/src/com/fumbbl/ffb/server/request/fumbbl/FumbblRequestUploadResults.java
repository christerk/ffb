package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.GameStatus;
import com.fumbbl.ffb.report.ReportFumbblResultUpload;
import com.fumbbl.ffb.report.ReportList;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.request.ServerRequest;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import com.fumbbl.ffb.server.request.ServerRequestSaveReplay;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerHttpClient;
import com.fumbbl.ffb.util.StringTool;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kalimar
 */
public class FumbblRequestUploadResults extends ServerRequest {

	private static final Pattern _PATTERN_RESULT = Pattern.compile("<result>([^<]+)</result>");
	private static final Pattern _PATTERN_DESCRIPTION = Pattern.compile("<description>([^<]+)</description>");

	private final GameState fGameState;
	private boolean fUploadSuccessful;
	private String fUploadStatus;

	public FumbblRequestUploadResults(GameState pGameState) {
		fGameState = pGameState;
	}

	public GameState getGameState() {
		return fGameState;
	}

	public boolean isUploadSuccessful() {
		return fUploadSuccessful;
	}

	public String getUploadStatus() {
		return fUploadStatus;
	}

	@Override
	public void process(ServerRequestProcessor pRequestProcessor) {

		FantasyFootballServer server = pRequestProcessor.getServer();
		String challengeResponse = UtilFumbblRequest.getFumbblAuthChallengeResponseForFumbblUser(server);
		FumbblResult fumbblResult = new FumbblResult(getGameState().getGame());
		String resultXml = fumbblResult.toXml(true);
		server.getDebugLog().log(IServerLogLevel.DEBUG, getGameState().getGame().getId(), resultXml);
		setRequestUrl(server.getProperty(IServerProperty.FUMBBL_RESULT));
		server.getDebugLog().log(IServerLogLevel.DEBUG, getGameState().getGame().getId(), DebugLog.FUMBBL_REQUEST, getRequestUrl());
		try {

			String responseXml = UtilServerHttpClient.postMultipartXml(getRequestUrl(), challengeResponse, resultXml);
			server.getDebugLog().log(IServerLogLevel.DEBUG, getGameState().getGame().getId(), DebugLog.FUMBBL_RESPONSE, responseXml);

			if (StringTool.isProvided(responseXml)) {
				try (BufferedReader xmlReader = new BufferedReader(new StringReader(responseXml))) {
					String line = null;
					while ((line = xmlReader.readLine()) != null) {
						Matcher resultMatcher = _PATTERN_RESULT.matcher(line);
						if (resultMatcher.find()) {
							fUploadSuccessful = "success".equalsIgnoreCase(resultMatcher.group(1));
						}
						Matcher descriptionMatcher = _PATTERN_DESCRIPTION.matcher(line);
						if (descriptionMatcher.find()) {
							fUploadStatus = descriptionMatcher.group(1);
						}
					}
				}
			}

			if (isUploadSuccessful()) {
				getGameState().setStatus(GameStatus.UPLOADED);
				server.getGameCache().queueDbUpdate(getGameState(), false);
				server.getDebugLog().log(IServerLogLevel.WARN, getGameState().getId(), "GAME UPLOADED");
				server.getRequestProcessor().add(new ServerRequestSaveReplay(getGameState().getId()));
				server.getRequestProcessor().add(new FumbblRequestRemoveGamestate(getGameState()));
			}

			ReportList reportList = new ReportList();
			reportList.add(new ReportFumbblResultUpload(isUploadSuccessful(), getUploadStatus()));
			UtilServerGame.syncGameModel(getGameState(), reportList, null, null);

		} catch (Exception ex) {
			throw new FantasyFootballException(ex);
		}

	}

}
