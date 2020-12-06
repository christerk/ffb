package com.balancedbytes.games.ffb.server.request.fumbbl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;

import org.eclipse.jetty.websocket.api.Session;
import org.xml.sax.InputSource;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.TeamList;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.request.ServerRequest;
import com.balancedbytes.games.ffb.server.request.ServerRequestProcessor;
import com.balancedbytes.games.ffb.server.util.UtilServerHttpClient;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.XmlHandler;

/**
 * 
 * @author Kalimar
 */
public class FumbblRequestLoadTeamList extends ServerRequest {

	private String fCoach;
	private GameState fGameState;
	private Session fSession;

	public FumbblRequestLoadTeamList(GameState pGameState, String pCoach, Session pSession) {
		fGameState = pGameState;
		fCoach = pCoach;
		fSession = pSession;
	}

	public GameState getGameState() {
		return fGameState;
	}

	public String getCoach() {
		return fCoach;
	}

	public Session getSession() {
		return fSession;
	}

	@Override
	public void process(ServerRequestProcessor pRequestProcessor) {
		FantasyFootballServer server = pRequestProcessor.getServer();
		TeamList teamList = null;
		try {
			setRequestUrl(StringTool.bind(server.getProperty(IServerProperty.FUMBBL_TEAMS),
					URLEncoder.encode(getCoach(), UtilFumbblRequest.CHARACTER_ENCODING)));
			String teamsXml = UtilServerHttpClient.fetchPage(getRequestUrl());
			if (StringTool.isProvided(teamsXml)) {
				try (BufferedReader xmlReader = new BufferedReader(new StringReader(teamsXml))) {
					InputSource xmlSource = new InputSource(xmlReader);
					teamList = new TeamList();
					XmlHandler.parse(xmlSource, teamList);
				}
			}
		} catch (IOException ioe) {
			throw new FantasyFootballException(ioe);
		}
		if (teamList != null) {
			server.getCommunication().sendTeamList(getSession(), teamList);
		}
	}

}
