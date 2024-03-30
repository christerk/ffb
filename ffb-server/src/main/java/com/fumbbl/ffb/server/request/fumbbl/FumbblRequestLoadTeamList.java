package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.TeamList;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.ServerUrlProperty;
import com.fumbbl.ffb.server.request.ServerRequest;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import com.fumbbl.ffb.server.util.UtilServerHttpClient;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.xml.XmlHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;

/**
 * 
 * @author Kalimar
 */
public class FumbblRequestLoadTeamList extends ServerRequest {

	private final String fCoach;
	private final GameState fGameState;
	private final Session fSession;

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
			setRequestUrl(StringTool.bind(ServerUrlProperty.FUMBBL_TEAMS.url(server.getProperties()),
					URLEncoder.encode(getCoach(), UtilFumbblRequest.CHARACTER_ENCODING)));
			String teamsXml = UtilServerHttpClient.fetchPage(getRequestUrl());
			if (StringTool.isProvided(teamsXml)) {
				try (BufferedReader xmlReader = new BufferedReader(new StringReader(teamsXml))) {
					InputSource xmlSource = new InputSource(xmlReader);
					teamList = new TeamList();
					XmlHandler.parse(null, xmlSource, teamList);
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
