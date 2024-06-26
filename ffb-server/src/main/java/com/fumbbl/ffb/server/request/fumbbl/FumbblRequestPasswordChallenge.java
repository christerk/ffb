package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.ServerUrlProperty;
import com.fumbbl.ffb.server.request.ServerRequest;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import com.fumbbl.ffb.server.util.UtilServerHttpClient;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kalimar
 */
public class FumbblRequestPasswordChallenge extends ServerRequest {

	private static final Pattern _PATTERN_CHALLENGE = Pattern.compile("<challenge>([^<]+)</challenge>");

	private final String fCoach;
	private final Session fSession;

	public FumbblRequestPasswordChallenge(String pCoach, Session pSession) {
		fCoach = pCoach;
		fSession = pSession;
	}

	public String getCoach() {
		return fCoach;
	}

	public Session getSession() {
		return fSession;
	}

	@Override
	public void process(ServerRequestProcessor pRequestProcessor) {
		String challenge = null;
		FantasyFootballServer server = pRequestProcessor.getServer();
		try {
			setRequestUrl(StringTool.bind(ServerUrlProperty.FUMBBL_AUTH_CHALLENGE.url(server.getProperties()),
				URLEncoder.encode(getCoach(), UtilFumbblRequest.CHARACTER_ENCODING)));
			server.getDebugLog().logWithOutGameId(IServerLogLevel.DEBUG, DebugLog.FUMBBL_REQUEST, getRequestUrl());
			String responseXml = UtilServerHttpClient.fetchPage(getRequestUrl());
			if (StringTool.isProvided(responseXml)) {
				server.getDebugLog().logWithOutGameId(IServerLogLevel.DEBUG, DebugLog.FUMBBL_RESPONSE, responseXml);
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
		} catch (IOException pIoException) {
			throw new FantasyFootballException(pIoException);
		}
		server.getCommunication().sendPasswordChallenge(getSession(), challenge);
	}

}
