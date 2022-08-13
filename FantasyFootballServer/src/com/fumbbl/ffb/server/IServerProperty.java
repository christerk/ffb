package com.fumbbl.ffb.server;

/**
 * 
 * @author Kalimar
 */
public interface IServerProperty {

	String SERVER_PORT = "server.port";
	String SERVER_BASE_DIR = "server.base.dir";

	String SERVER_LOG_FILE = "server.log.file";
	String SERVER_LOG_LEVEL = "server.log.level";
	String SERVER_SPECTATOR_COOLDOWN = "server.spectator.cooldown";
	String SERVER_DEBUG_COMPRESSION = "server.debug.compression";
	String SERVER_COMMAND_COMPRESSION = "server.command.compression";
	String SERVER_TEST = "server.test";
	String SERVER_REDEPLOY_EXIT_CODE = "server.redeploy.exitCode";
	String SERVER_REDEPLOY_DEFAULT_BRANCH = "server.redeploy.defaultBranch";
	String SERVER_REDEPLOY_FILE = "server.redeploy.file";

	String FUMBBL_USER = "fumbbl.user";
	String FUMBBL_PASSWORD = "fumbbl.password";
	String FUMBBL_BACKUP_SERVICE = "fumbbl.backup.service";
	String FUMBBL_AUTH_CHALLENGE = "fumbbl.auth.challenge";
	String FUMBBL_AUTH_RESPONSE = "fumbbl.auth.response";
	String FUMBBL_TEAMS = "fumbbl.teams";
	String FUMBBL_TEAM = "fumbbl.team";
	String FUMBBL_ROSTER = "fumbbl.roster";
	String FUMBBL_ROSTER_TEAM = "fumbbl.roster.team";
	String FUMBBL_GAMESTATE_CHECK = "fumbbl.gamestate.check";
	String FUMBBL_GAMESTATE_CREATE = "fumbbl.gamestate.create";
	String FUMBBL_GAMESTATE_RESUME = "fumbbl.gamestate.resume";
	String FUMBBL_GAMESTATE_UPDATE = "fumbbl.gamestate.update";
	String FUMBBL_GAMESTATE_REMOVE = "fumbbl.gamestate.remove";
	String FUMBBL_GAMESTATE_OPTIONS = "fumbbl.gamestate.options";
	String FUMBBL_RESULT = "fumbbl.result";
	String FUMBBL_TALK = "fumbbl.talk";
	String FUMBBL_NAMEGENERATOR_BASE = "fumbbl.namegenerator.base";

	String ADMIN_SALT = "admin.salt";
	String ADMIN_PASSWORD = "admin.password";
	String ADMIN_URL_BACKUP = "admin.url.backup";
	String ADMIN_URL_BLOCK = "admin.url.block";
	String ADMIN_URL_CACHE = "admin.url.cache";
	String ADMIN_URL_CHALLENGE = "admin.url.challenge";
	String ADMIN_URL_CLOSE = "admin.url.close";
	String ADMIN_URL_CONCEDE = "admin.url.concede";
	String ADMIN_URL_DELETE = "admin.url.delete";
	String ADMIN_URL_FORCELOG = "admin.url.forcelog";
	String ADMIN_URL_LIST_ID = "admin.url.list.id";
	String ADMIN_URL_LIST_STATUS = "admin.url.list.status";
	String ADMIN_URL_LOGLEVEL = "admin.url.loglevel";
	String ADMIN_URL_MESSAGE = "admin.url.message";
	String ADMIN_URL_REFRESH = "admin.url.refresh";
	String ADMIN_URL_SCHEDULE = "admin.url.schedule";
	String ADMIN_URL_SHUTDOWN = "admin.url.shutdown";
	String ADMIN_URL_STATS = "admin.url.stats";
	String ADMIN_URL_UNBLOCK = "admin.url.unblock";
	String ADMIN_URL_UPLOAD = "admin.url.upload";

	String GAMESTATE_URL_CHALLENGE = "gamestate.url.challenge";
	String GAMESTATE_URL_BEHAVIORS = "gamestate.url.behaviours";
	String GAMESTATE_URL_GET = "gamestate.url.get";
	String GAMESTATE_URL_RESULT = "gamestate.url.result";
	String GAMESTATE_URL_SET = "gamestate.url.set";

	String BACKUP_DIR = "backup.dir";
	String BACKUP_EXTENSION = "backup.extension";
	String BACKUP_SALT = "backup.salt";
	String BACKUP_PASSWORD = "backup.password";
	String BACKUP_URL_CHALLENGE = "backup.url.challenge";
	String BACKUP_URL_LOAD = "backup.url.load";
	String BACKUP_URL_SAVE = "backup.url.save";
	String BACKUP_S3_PROFILE = "backup.s3.profile";
	String BACKUP_S3_REGION = "backup.s3.region";
	String BACKUP_S3_BUCKET = "backup.s3.bucket";
	String BACKUP_S3_BASE_PATH = "backup.s3.basePath";

	String DB_DRIVER = "db.driver";
	String DB_URL = "db.url";
	String DB_USER = "db.user";
	String DB_PASSWORD = "db.password";
	String DB_TYPE = "db.type";

	String TIMER_DB_KEEP_ALIVE = "timer.dbKeepAlive";
	String TIMER_NETWORK_ENTROPY = "timer.networkEntropy";

	String TIMER_SESSION_TIMEOUT_ENABLED = "timer.sessionTimeoutEnabled";
	String TIMER_SESSION_TIMEOUT_SCHEDULE = "timer.sessionTimeoutSchedule";
	String SESSION_TIMEOUT_VALUE = "session.timeoutValue";
}
