package com.balancedbytes.games.ffb.server;

/**
 * 
 * @author Kalimar
 */
public interface IServerProperty {
  
  String SERVER_PORT = "server.port";
  String SERVER_DIR_LOG = "server.dir.log";
  String SERVER_PING_INTERVAL = "server.ping.interval";
  String SERVER_PING_MAX_DELAY = "server.ping.maxDelay";
  String SERVER_DEBUG_COMPRESSION = "server.debug.compression";
  String SERVER_LOG_LEVEL = "server.log.level";
  String SERVER_SPECTATOR_COOLDOWN = "server.spectator.cooldown";
  
  String HTTP_PORT = "http.port";
  String HTTP_DIR = "http.dir";

  String FUMBBL_USER = "fumbbl.user";
  String FUMBBL_PASSWORD = "fumbbl.password";
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
  String ADMIN_CHALLENGE = "admin.challenge";
  String ADMIN_LIST_STATUS = "admin.list.status";
  String ADMIN_CLOSE = "admin.close";
  String ADMIN_CONCEDE = "admin.concede";
  String ADMIN_SHUTDOWN = "admin.shutdown";
  String ADMIN_REFRESH = "admin.refresh";
  String ADMIN_UPLOAD = "admin.upload";
  String ADMIN_DELETE = "admin.delete";
  String ADMIN_MESSAGE = "admin.message";
  String ADMIN_SCHEDULE = "admin.schedule";
  String ADMIN_BLOCK = "admin.block";
  String ADMIN_UNBLOCK = "admin.unblock";
  
  String DB_DRIVER = "db.driver";
  String DB_URL = "db.url";
  String DB_USER = "db.user";
  String DB_PASSWORD = "db.password";
  String DB_KEEP_ALIVE = "db.keepAlive";

  String DB_OLD_URL = "db.old.url";
  String DB_OLD_USER = "db.old.user";
  String DB_OLD_PASSWORD = "db.old.password";

}
