package com.fumbbl.ffb.client;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.factory.ClientModeFactory;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class ClientParameters {

	public static final String USAGE = "java -jar FantasyFootballClient.jar -player -coach <coach>\n"
			+ "java -jar FantasyFootballClient.jar -player -coach <coach> -gameId <gameId>\n"
			+ "java -jar FantasyFootballClient.jar -player -coach <coach> -gameId <gameId> -teamHome <teamName> -teamAway <teamName>\n"
			+ "java -jar FantasyFootballClient.jar -player -coach <coach> -teamId <teamId> -teamName <teamName>\n"
			+ "java -jar FantasyFootballClient.jar -spectator -coach <coach>\n"
			+ "java -jar FantasyFootballClient.jar -spectator -coach <coach> -gameId <gameId>\n"
			+ "java -jar FantasyFootballClient.jar -replay -gameId <gameId>";

	private static final String _ARGUMENT_COACH = "-coach";
	private static final String _ARGUMENT_GAME_ID = "-gameId";
	private static final String _ARGUMENT_TEAM_ID = "-teamId";
	private static final String _ARGUMENT_TEAM_NAME = "-teamName";
	private static final String _ARGUMENT_TEAM_HOME = "-teamHome";
	private static final String _ARGUMENT_TEAM_AWAY = "-teamAway";
	private static final String _ARGUMENT_AUTHENTICATION = "-auth";
	private static final String _ARGUMENT_PORT = "-port";
	private static final String _ARGUMENT_SERVER = "-server";
	private static final String _ARGUMENT_BUILD = "-build";

	private static final String _ARGUMENT_LAYOUT = "-layout";

	private ClientMode fMode;
	private String fCoach;
	private long fGameId;
	private String fTeamId;
	private String fTeamName;
	private String fTeamHome;
	private String fTeamAway;
	private String fAuthentication;
	private int fPort;
	private String fServer;
	private String fBuild;
	private ClientLayout layout = ClientLayout.LANDSCAPE;

	public ClientMode getMode() {
		return fMode;
	}

	public String getCoach() {
		return fCoach;
	}

	private void setCoach(String pCoach) {
		fCoach = pCoach;
	}

	public long getGameId() {
		return fGameId;
	}

	public String getTeamName() {
		return fTeamName;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public String getTeamHome() {
		return fTeamHome;
	}

	public String getTeamAway() {
		return fTeamAway;
	}

	public String getAuthentication() {
		return fAuthentication;
	}

	public int getPort() {
		return fPort;
	}

	public String getServer() {
		return fServer;
	}

	public String getBuild() {
		return fBuild;
	}

	public ClientLayout getLayout() {
		return layout;
	}

	public void initFrom(String[] pArguments) {
		if (ArrayTool.isProvided(pArguments)) {
			ClientModeFactory clientModeFactory = new ClientModeFactory();
			int pos = 0;
			while (pos < pArguments.length) {
				String argument = fetchArgument(pArguments, pos++);
				if (clientModeFactory.forArgument(argument) != null) {
					fMode = clientModeFactory.forArgument(argument);
				} else if (_ARGUMENT_COACH.equalsIgnoreCase(argument)) {
					setCoach(fetchArgument(pArguments, pos++));
				} else if (_ARGUMENT_GAME_ID.equalsIgnoreCase(argument)) {
					try {
						fGameId = Long.parseLong(fetchArgument(pArguments, pos++));
					} catch (NumberFormatException pNfe) {
						throw new FantasyFootballException("GameId must be numeric.");
					}
				} else if (_ARGUMENT_TEAM_ID.equalsIgnoreCase(argument)) {
					fTeamId = fetchArgument(pArguments, pos++);
				} else if (_ARGUMENT_TEAM_NAME.equalsIgnoreCase(argument)) {
					fTeamName = fetchArgument(pArguments, pos++);
				} else if (_ARGUMENT_TEAM_HOME.equalsIgnoreCase(argument)) {
					fTeamHome = fetchArgument(pArguments, pos++);
				} else if (_ARGUMENT_TEAM_AWAY.equalsIgnoreCase(argument)) {
					fTeamAway = fetchArgument(pArguments, pos++);
				} else if (_ARGUMENT_AUTHENTICATION.equalsIgnoreCase(argument)) {
					fAuthentication = fetchArgument(pArguments, pos++);
				} else if (_ARGUMENT_PORT.equalsIgnoreCase(argument)) {
					try {
						fPort = Integer.parseInt(fetchArgument(pArguments, pos++));
					} catch (NumberFormatException pNfe) {
						throw new FantasyFootballException("Port must be numeric.");
					}
				} else if (_ARGUMENT_SERVER.equalsIgnoreCase(argument)) {
					fServer = fetchArgument(pArguments, pos++);
				} else if (_ARGUMENT_BUILD.equalsIgnoreCase(argument)) {
					fBuild = fetchArgument(pArguments, pos++);
				} else if (_ARGUMENT_LAYOUT.equalsIgnoreCase(argument)) {
					layout = ClientLayout.valueOf(fetchArgument(pArguments, pos++));
				} else {
					throw new FantasyFootballException("Unknown argument " + argument);
				}
			}
		}
	}

	public boolean validate() {
		if (getMode() == null) {
			return false;
		}
		switch (getMode()) {
		case PLAYER:
			if (!StringTool.isProvided(getCoach())) {
				return false;
			}
			if (getGameId() > 0) {
				if (StringTool.isProvided(getTeamHome())) {
					return StringTool.isProvided(getTeamAway());
				}
				if (StringTool.isProvided(getTeamAway())) {
					return StringTool.isProvided(getTeamHome());
				}
			} else {
				if (StringTool.isProvided(getTeamId())) {
					return StringTool.isProvided(getTeamName());
				}
				if (StringTool.isProvided(getTeamName())) {
					return StringTool.isProvided(getTeamId());
				}
			}
			return true;
			case SPECTATOR:
				return StringTool.isProvided(getCoach());
			case REPLAY:
				return (getGameId() > 0);
			default:
				return false;
		}
	}

	private String fetchArgument(String[] pArguments, int pPosition) {
		if (pPosition < pArguments.length) {
			return pArguments[pPosition];
		} else {
			throw new FantasyFootballException("Argument list too short");
		}
	}

}
