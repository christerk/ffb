package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.GameListEntry;
import com.fumbbl.ffb.PasswordChallenge;
import com.fumbbl.ffb.TeamListEntry;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.net.commands.ServerCommandPasswordChallenge;
import com.fumbbl.ffb.util.StringTool;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginLogicModule {
	private static final Pattern _PATTERN_VERSION = Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)");

	private String fGameName;
	private byte[] fEncodedPassword;
	private int fPasswordLength;
	private boolean fListGames;
	private String fTeamHomeId;
	private String fTeamHomeName;
	private String fTeamAwayName;
	private long fGameId;

	public LoginLogicModule(FantasyFootballClient client) {
		this.client = client;
	}

	private final FantasyFootballClient client;

	public void setfTeamHomeId(String fTeamHomeId) {
		this.fTeamHomeId = fTeamHomeId;
	}

	public String getfTeamHomeName() {
		return fTeamHomeName;
	}

	public void setfTeamHomeName(String fTeamHomeName) {
		this.fTeamHomeName = fTeamHomeName;
	}

	public String getfTeamAwayName() {
		return fTeamAwayName;
	}

	public void setfTeamAwayName(String fTeamAwayName) {
		this.fTeamAwayName = fTeamAwayName;
	}


	public String getfGameName() {
		return fGameName;
	}

	public byte[] getfEncodedPassword() {
		return fEncodedPassword;
	}

	public int getfPasswordLength() {
		return fPasswordLength;
	}

	public void setfPasswordLength(int fPasswordLength) {
		this.fPasswordLength = fPasswordLength;
	}

	public void sendChallenge(LoginData loginData) {
		fEncodedPassword = loginData.fEncodedPassword;
		fGameName = loginData.fGameName;
		fListGames = loginData.fListGames;
		fPasswordLength = loginData.fPasswordLength;
		sendChallenge();
	}

	public void sendChallenge(TeamListEntry teamListEntry) {
		fTeamHomeId = teamListEntry.getTeamId();
		fTeamHomeName = teamListEntry.getTeamName();
		sendChallenge();
	}

	public void sendChallenge(GameListEntry gameListEntry) {
		fGameId = gameListEntry.getGameId();
		fListGames = false;
		sendChallenge();
	}

	private void sendChallenge() {
		String authentication = client.getParameters().getAuthentication();
		if (StringTool.isProvided(authentication)) {
			sendJoin(authentication);
		} else {
			client.getCommunication().sendPasswordChallenge();
		}
	}

	public void sendJoin(String pResponse) {
		if (fListGames) {
			client.getCommunication().sendJoin(client.getParameters().getCoach(), pResponse, 0, null, null, null);

		} else {
			client.getCommunication().sendJoin(client.getParameters().getCoach(), pResponse,
				(fGameId > 0L) ? fGameId : client.getParameters().getGameId(), fGameName, fTeamHomeId, fTeamHomeName);
		}
	}

	public void handlePasswordChallenge(ServerCommandPasswordChallenge pNetCommand) {
		String response = createResponse(pNetCommand.getChallenge());
		sendJoin(response);
	}

	public boolean checkVersionConflict(String pVersionExpected, String pVersionIs) {
		Version expectedVersion = new Version(pVersionExpected);
		Version actualVersion = new Version(pVersionIs);

		return ((actualVersion.major < expectedVersion.major) || (actualVersion.minor < expectedVersion.minor)
			|| (actualVersion.release < expectedVersion.release));
	}

	private String createResponse(String pChallenge) {
		String response;
		try {
			response = PasswordChallenge.createResponse(pChallenge, fEncodedPassword);
		} catch (IOException | NoSuchAlgorithmException ioe) {
			response = null;
		}
		return response;
	}

	public boolean idAndNameProvided() {
		return (client.getParameters().getGameId() == 0) && StringTool.isProvided(getfGameName());
	}

	public static class LoginData {
		private final String fGameName;
		private final byte[] fEncodedPassword;
		private final int fPasswordLength;
		private final boolean fListGames;

		public LoginData(String fGameName, byte[] fEncodedPassword, int fPasswordLength, boolean fListGames) {
			this.fGameName = fGameName;
			this.fEncodedPassword = fEncodedPassword;
			this.fPasswordLength = fPasswordLength;
			this.fListGames = fListGames;
		}
	}

	private static class Version {
		int major;
		int minor;
		int release;

		public Version(String version) {
			Matcher versionExpectedMatcher = _PATTERN_VERSION.matcher(version);
			if (versionExpectedMatcher.matches()) {
				major = Integer.parseInt(versionExpectedMatcher.group(1));
				minor = Integer.parseInt(versionExpectedMatcher.group(2));
				release = Integer.parseInt(versionExpectedMatcher.group(3));
			}
		}
	}
}
