package com.fumbbl.ffb.server;

/**
 * 
 * @author Kalimar
 */
public interface IServerProperty {

	String SERVER_PORT = "server.port";
	String SERVER_BASE = "server.base";
	String SERVER_BASE_DIR = "server.base.dir";

	String SERVER_LOG_FILE = "server.log.file";
	String SERVER_LOG_FOLDER = "server.log.folder";
	String SERVER_LOG_LEVEL = "server.log.level";
	String SERVER_LOG_FILE_SPLIT = "server.log.file.split";
	String SERVER_SPECTATOR_COOLDOWN = "server.spectator.cooldown";
	String SERVER_COMMAND_COMPRESSION = "server.command.compression";
	String SERVER_TEST = "server.test";
	String SERVER_REDEPLOY_EXIT_CODE = "server.redeploy.exitCode";
	String SERVER_REDEPLOY_DEFAULT_BRANCH = "server.redeploy.defaultBranch";
	String SERVER_REDEPLOY_FILE = "server.redeploy.file";

	String FUMBBL_USER = "fumbbl.user";
	String FUMBBL_PASSWORD = "fumbbl.password";
	String FUMBBL_BASE = "fumbbl.base";
	String FUMBBL_PORT = "fumbbl.port";

	String ADMIN_SALT = "admin.salt";
	String ADMIN_PASSWORD = "admin.password";

	String BACKUP_DIR = "backup.dir";
	String BACKUP_EXTENSION = "backup.extension";
	String BACKUP_SALT = "backup.salt";
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
