package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.net.ServerStatus;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandJoinApproved;
import com.fumbbl.ffb.server.request.ServerRequest;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import com.fumbbl.ffb.server.util.UtilServerHttpClient;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
public class FumbblRequestCheckAuthorization extends ServerRequest {

	private static final Pattern _PATTERN_RESPONSE = Pattern.compile("<response>([^<]+)</response>");

	private final Session fSession;
	private final String fCoach;
	private final String fPassword;
	private final long fGameId;
	private final String fGameName;
	private final String fTeamId;
	private final ClientMode fMode;

	public FumbblRequestCheckAuthorization(Session pSession, String pCoach, String pPassword, long pGameId,
	                                       String pGameName, String pTeamId, ClientMode pMode) {
		fSession = pSession;
		fCoach = pCoach;
		fPassword = pPassword;
		fGameId = pGameId;
		fGameName = pGameName;
		fTeamId = pTeamId;
		fMode = pMode;
	}

	public Session getSession() {
		return fSession;
	}

	public String getCoach() {
		return fCoach;
	}

	public String getPassword() {
		return fPassword;
	}

	public long getGameId() {
		return fGameId;
	}

	public String getGameName() {
		return fGameName;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public ClientMode getMode() {
		return fMode;
	}

	@Override
	public void process(ServerRequestProcessor pRequestProcessor) {
		boolean passwordOk = false;
		List<String> accountProperties = new ArrayList<String>();

		FantasyFootballServer server = pRequestProcessor.getServer();
		try {
			if (getCoach() != null && getPassword() != null) {
				setRequestUrl(StringTool.bind(server.getProperty(IServerProperty.FUMBBL_AUTH_RESPONSE),
					new String[]{URLEncoder.encode(getCoach(), UtilFumbblRequest.CHARACTER_ENCODING),
						URLEncoder.encode(getPassword(), UtilFumbblRequest.CHARACTER_ENCODING)}));
				server.getDebugLog().log(IServerLogLevel.DEBUG, getGameId(), DebugLog.FUMBBL_REQUEST, getRequestUrl());
				String responseXml = UtilServerHttpClient.fetchPage(getRequestUrl());
				server.getDebugLog().log(IServerLogLevel.DEBUG, getGameId(), DebugLog.FUMBBL_RESPONSE, responseXml);
				if (StringTool.isProvided(responseXml)) {
					try (BufferedReader xmlReader = new BufferedReader(new StringReader(responseXml))) {
						String line;
						String response = null;
						while ((line = xmlReader.readLine()) != null) {
							Matcher responseMatcher = _PATTERN_RESPONSE.matcher(line);
							if (responseMatcher.find()) {
								response = responseMatcher.group(1);
							}
						}
						passwordOk = (StringTool.isProvided(response) && response.startsWith("OK"));
						String[] segments = response.split(" ");
						accountProperties = Arrays.stream(segments).skip(1).collect(Collectors.toList());
					}
				}
			}
		} catch (IOException ioe) {
			throw new FantasyFootballException(ioe);
		}
		if (passwordOk) {
			InternalServerCommandJoinApproved joinApprovedCommand = new InternalServerCommandJoinApproved(getGameId(),
					getGameName(), getCoach(), getTeamId(), getMode(), accountProperties);
			server.getCommunication().handleCommand(new ReceivedCommand(joinApprovedCommand, getSession()));
		} else {
			server.getCommunication().sendStatus(getSession(), ServerStatus.ERROR_WRONG_PASSWORD, null);
		}
	}

}
