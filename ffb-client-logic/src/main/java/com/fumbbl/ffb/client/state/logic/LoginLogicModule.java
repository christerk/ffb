package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FantasyFootballConstants;
import com.fumbbl.ffb.GameListEntry;
import com.fumbbl.ffb.PasswordChallenge;
import com.fumbbl.ffb.TeamListEntry;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.commands.ServerCommandPasswordChallenge;
import com.fumbbl.ffb.net.commands.ServerCommandVersion;
import com.fumbbl.ffb.util.StringTool;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginLogicModule extends LogicModule {
	private static final Pattern _PATTERN_VERSION = Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)");

	private String gameName;
	private byte[] encodedPassword;
	private int passwordLength;
	private boolean listGames;
	private String teamHomeId;
	private String teamHomeName;
	private String teamAwayName;
	private long gameId;

	public LoginLogicModule(FantasyFootballClient client) {
		super(client);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.LOGIN;
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.emptySet();
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		// no actions in this state
	}

	public String getTeamHomeName() {
		return teamHomeName;
	}

	public String getTeamAwayName() {
		return teamAwayName;
	}

	public String getGameName() {
		return gameName;
	}

	public byte[] getEncodedPassword() {
		return encodedPassword;
	}

	public int getPasswordLength() {
		return passwordLength;
	}

	public void setPasswordLength(int passwordLength) {
		this.passwordLength = passwordLength;
	}

	public void init() {
		if (StringTool.isProvided(client.getParameters().getTeamId())) {
			teamHomeId = client.getParameters().getTeamId();
			teamHomeName = client.getParameters().getTeamName();
			teamAwayName = null;
		} else {
			teamHomeId = null;
			teamHomeName = client.getParameters().getTeamHome();
			teamAwayName = client.getParameters().getTeamAway();
		}
		client.getCommunication().sendRequestVersion();
	}

	public void sendChallenge(LoginData loginData) {
		encodedPassword = loginData.encodedPassword;
		gameName = loginData.gameName;
		listGames = loginData.listGames;
		passwordLength = loginData.passwordLength;
		sendChallenge();
	}

	public void sendChallenge(TeamListEntry teamListEntry) {
		teamHomeId = teamListEntry.getTeamId();
		teamHomeName = teamListEntry.getTeamName();
		sendChallenge();
	}

	public void sendChallenge(GameListEntry gameListEntry) {
		gameId = gameListEntry.getGameId();
		listGames = false;
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
		if (listGames) {
			client.getCommunication().sendJoin(client.getParameters().getCoach(), pResponse, 0, null, null, null);

		} else {
			client.getCommunication().sendJoin(client.getParameters().getCoach(), pResponse,
				(gameId > 0L) ? gameId : client.getParameters().getGameId(), gameName, teamHomeId, teamHomeName);
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
			response = PasswordChallenge.createResponse(pChallenge, encodedPassword);
		} catch (IOException | NoSuchAlgorithmException ioe) {
			response = null;
		}
		return response;
	}

	public boolean idAndNameProvided() {
		return (client.getParameters().getGameId() == 0) && StringTool.isProvided(getGameName());
	}

	public VersionCheck handleVersionCommand(ServerCommandVersion pNetCommand) {
		VersionCheck versionCheck = checkVersion(pNetCommand.getServerVersion(), pNetCommand.getClientVersion());
		if (pNetCommand.isTestServer() || versionCheck == VersionCheck.SUCCESS) {
			String[] properties = pNetCommand.getClientProperties();
			for (String property : properties) {
				client.setProperty(property, pNetCommand.getClientPropertyValue(property));
			}
		}
		return versionCheck;
	}

	private VersionCheck checkVersion(String pServerVersion, String pClientVersion) {

		if (checkVersionConflict(pClientVersion, FantasyFootballConstants.VERSION)) {
				return VersionCheck.CLIENT_FAIL;
		}

		if (checkVersionConflict(FantasyFootballConstants.VERSION, pServerVersion)) {
			return VersionCheck.SERVER_FAIL;
		}

		return VersionCheck.SUCCESS;
	}

	public enum VersionCheck {
		SUCCESS, CLIENT_FAIL, SERVER_FAIL
	}

		public static class LoginData {
		private final String gameName;
		private final byte[] encodedPassword;
		private final int passwordLength;
		private final boolean listGames;

		public LoginData(String gameName, byte[] encodedPassword, int passwordLength, boolean listGames) {
			this.gameName = gameName;
			this.encodedPassword = encodedPassword;
			this.passwordLength = passwordLength;
			this.listGames = listGames;
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
