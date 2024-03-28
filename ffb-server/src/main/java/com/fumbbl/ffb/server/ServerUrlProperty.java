package com.fumbbl.ffb.server;

import com.fumbbl.ffb.util.StringTool;

import java.util.Properties;

import static com.fumbbl.ffb.server.IServerProperty.*;

public enum ServerUrlProperty {

	ADMIN_URL_BACKUP("admin.url.backup"),
	ADMIN_URL_BLOCK("admin.url.block"),
	ADMIN_URL_CACHE("admin.url.cache"),
	ADMIN_URL_CHALLENGE("admin.url.challenge"),
	ADMIN_URL_CLOSE("admin.url.close"),
	ADMIN_URL_CONCEDE("admin.url.concede"),
	ADMIN_URL_DELETE("admin.url.delete"),
	ADMIN_URL_FORCELOG("admin.url.forcelog"),
	ADMIN_URL_LIST_ID("admin.url.list.id"),
	ADMIN_URL_LIST_STATUS("admin.url.list.status"),
	ADMIN_URL_LOGLEVEL("admin.url.loglevel"),
	ADMIN_URL_MESSAGE("admin.url.message"),
	ADMIN_URL_REFRESH("admin.url.refresh"),
	ADMIN_URL_SCHEDULE("admin.url.schedule"),
	ADMIN_URL_SHUTDOWN("admin.url.shutdown"),
	ADMIN_URL_STATS("admin.url.stats"),
	ADMIN_URL_UNBLOCK("admin.url.unblock"),
	ADMIN_URL_UPLOAD("admin.url.upload"),
	ADMIN_URL_PORTRAIT("admin.url.portrait"),
	ADMIN_URL_PURGE_TEST("admin.url.purgetest"),
	GAMESTATE_URL_CHALLENGE("gamestate.url.challenge"),
	GAMESTATE_URL_BEHAVIORS("gamestate.url.behaviours"),
	GAMESTATE_URL_GET("gamestate.url.get"),
	GAMESTATE_URL_RESULT("gamestate.url.result"),
	GAMESTATE_URL_SET("gamestate.url.set"),

	BACKUP_URL_CHALLENGE("backup.url.challenge"),
	BACKUP_URL_LOAD("backup.url.load"),
	BACKUP_URL_SAVE("backup.url.save"),


	FUMBBL_AUTH_CHALLENGE(FUMBBL_BASE, FUMBBL_PORT, "fumbbl.auth.challenge"),
	FUMBBL_AUTH_RESPONSE(FUMBBL_BASE, FUMBBL_PORT, "fumbbl.auth.response"),
	FUMBBL_TEAMS(FUMBBL_BASE, FUMBBL_PORT, "fumbbl.teams"),
	FUMBBL_TEAM(FUMBBL_BASE, FUMBBL_PORT, "fumbbl.team"),
	FUMBBL_ROSTER(FUMBBL_BASE, FUMBBL_PORT, "fumbbl.roster"),
	FUMBBL_ROSTER_TEAM(FUMBBL_BASE, FUMBBL_PORT, "fumbbl.roster.team"),
	FUMBBL_GAMESTATE_CHECK(FUMBBL_BASE, FUMBBL_PORT, "fumbbl.gamestate.check"),
	FUMBBL_GAMESTATE_CREATE(FUMBBL_BASE, FUMBBL_PORT, "fumbbl.gamestate.create"),
	FUMBBL_GAMESTATE_RESUME(FUMBBL_BASE, FUMBBL_PORT, "fumbbl.gamestate.resume"),
	FUMBBL_GAMESTATE_UPDATE(FUMBBL_BASE, FUMBBL_PORT, "fumbbl.gamestate.update"),
	FUMBBL_GAMESTATE_REMOVE(FUMBBL_BASE, FUMBBL_PORT, "fumbbl.gamestate.remove"),
	FUMBBL_GAMESTATE_OPTIONS(FUMBBL_BASE, FUMBBL_PORT, "fumbbl.gamestate.options"),
	FUMBBL_RESULT(FUMBBL_BASE, FUMBBL_PORT, "fumbbl.result"),
	FUMBBL_TALK(FUMBBL_BASE, FUMBBL_PORT, "fumbbl.talk"),
	FUMBBL_NAMEGENERATOR_BASE(FUMBBL_BASE, FUMBBL_PORT, "fumbbl.namegenerator.base"),
	FUMBBL_PLAYER_MARKINGS(FUMBBL_BASE, FUMBBL_PORT, "fumbbl.playermarkings");

	private final String baseKey;
	private final String portKey;
	private final String pathKey;

	ServerUrlProperty(String pathKey) {
		this(SERVER_BASE, SERVER_PORT, pathKey);
	}

	ServerUrlProperty(String baseKey, String portKey, String pathKey) {
		this.baseKey = baseKey;
		this.portKey = portKey;
		this.pathKey = pathKey;
	}

	public String url(Properties properties) {
		String path = get(properties, this.getPathKey());
		if (path.startsWith("http")) {
			return path;
		}

		path = stripLeadingSlash(path);

		StringBuilder url = new StringBuilder();

		String base = stripTrailingSlashes(get(properties, this.getBaseKey()));

		url.append(base);

		String port = get(properties, this.getPortKey());

		if (base.split(":").length < 3 && StringTool.isProvided(port)) {
			url.append(":").append(port);
		}

		appendIfNotEmpty(url, path);


		return url.toString();
	}

	private static void appendIfNotEmpty(StringBuilder url, String prefix) {
		if (StringTool.isProvided(prefix)) {
			url.append("/").append(prefix);
		}
	}

	private static String stripTrailingSlashes(String input) {
		while (input.endsWith("/")) {
			if (input.length() == 1) {
				input = "";
			} else {
				input = input.substring(0, input.length() - 1);
			}
		}
		return input;
	}

	private static String stripLeadingSlash(String input) {
		while (input.startsWith("/")) {
			if (input.length() == 1) {
				return "";
			}
			input = input.substring(1);
		}
		return input;
	}

	public String getBaseKey() {
		return baseKey;
	}

	public String getPortKey() {
		return portKey;
	}

	public String getPathKey() {
		return pathKey;
	}

	private String get(Properties props, String key) {
		String value = props.getProperty(key);
		return StringTool.isProvided(value) ? value : "";
	}
}
