package com.balancedbytes.games.ffb.server;

/**
 * 
 * @author Kalimar
 */
public interface IServerProperty {
  
  String SERVER_PORT = "server.port";
  String SERVER_BASE_DIR = "server.base.dir";

  String SERVER_LOG_FILE = "server.log.file";
  String SERVER_LOG_LEVEL = "server.log.level";
  String SERVER_PING_INTERVAL = "server.ping.interval";
  String SERVER_PING_MAX_DELAY = "server.ping.maxDelay";
  String SERVER_SPECTATOR_COOLDOWN = "server.spectator.cooldown";
  String SERVER_DEBUG_COMPRESSION = "server.debug.compression";
  String SERVER_COMMAND_COMPRESSION = "server.command.compression";
  String SERVER_TEST = "server.test";
  
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
  String FUMBBL_GAMESTATE_UPDATE = "fumbbl.gamestate.update";
  String FUMBBL_GAMESTATE_REMOVE = "fumbbl.gamestate.remove";
  String FUMBBL_RESULT = "fumbbl.result";
  
  String ADMIN_SALT = "admin.salt";
  String ADMIN_PASSWORD = "admin.password";
  String ADMIN_URL_CHALLENGE = "admin.url.challenge";
  String ADMIN_URL_LIST_ID = "admin.url.list.id";
  String ADMIN_URL_LIST_STATUS = "admin.url.list.status";
  String ADMIN_URL_BACKUP = "admin.url.backup";
  String ADMIN_URL_CLOSE = "admin.url.close";
  String ADMIN_URL_CONCEDE = "admin.url.concede";
  String ADMIN_URL_SHUTDOWN = "admin.url.shutdown";
  String ADMIN_URL_REFRESH = "admin.url.refresh";
  String ADMIN_URL_UPLOAD = "admin.url.upload";
  String ADMIN_URL_DELETE = "admin.url.delete";
  String ADMIN_URL_MESSAGE = "admin.url.message";
  String ADMIN_URL_SCHEDULE = "admin.url.schedule";
  String ADMIN_URL_BLOCK = "admin.url.block";
  String ADMIN_URL_UNBLOCK = "admin.url.unblock";
  String ADMIN_URL_LOGLEVEL = "admin.url.loglevel";

  String BACKUP_DIR = "backup.dir";
  String BACKUP_EXTENSION = "backup.extension";
  String BACKUP_SALT = "backup.salt";
  String BACKUP_PASSWORD = "backup.password";
  String BACKUP_URL_CHALLENGE = "backup.url.challenge";
  String BACKUP_URL_LOAD = "backup.url.load";
  String BACKUP_URL_SAVE = "backup.url.save";
  
  String DB_DRIVER = "db.driver";
  String DB_URL = "db.url";
  String DB_USER = "db.user";
  String DB_PASSWORD = "db.password";
  String DB_KEEP_ALIVE = "db.keepAlive";

}
