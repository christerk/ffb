package com.balancedbytes.games.ffb.server.db;

/**
 * 
 * @author Kalimar
 */
public enum DbStatementId {
  
  PASSWORD_FOR_COACH_QUERY,
  COACHES_INSERT,
  
  PLAYER_MARKERS_QUERY,
  PLAYER_MARKERS_DELETE,
  PLAYER_MARKERS_INSERT,
  
  TEAM_SETUPS_QUERY_ALL_FOR_A_TEAM,
  TEAM_SETUPS_QUERY,
  TEAM_SETUPS_DELETE,
  TEAM_SETUPS_INSERT,
  
  SERVER_STEPS_DELETE,
  SERVER_STEPS_FOR_GAME_STATE_QUERY,
    
  GAME_STATES_DELETE,
  GAME_STATES_QUERY,
  GAME_STATES_QUERY_FINISHED_GAMES,
  
  GAMES_INFO_INSERT,
  GAMES_INFO_UPDATE,
  GAMES_INFO_DELETE,

  GAMES_SERIALIZED_INSERT,
  GAMES_SERIALIZED_UPDATE,
  GAMES_SERIALIZED_DELETE,
  GAMES_SERIALIZED_QUERY,
  GAMES_SERIALIZED_QUERY_MAX_ID,
  
  FIELD_MODELS_DELETE,
  FIELD_MODELS_QUERY,
  
  USER_SETTINGS_DELETE,
  USER_SETTINGS_INSERT,
  USER_SETTINGS_QUERY,
  
  DIALOGS_DELETE,
  DIALOGS_FOR_GAME_STATE_QUERY,
  
  ACTING_PLAYERS_DELETE,
  ACTING_PLAYERS_FOR_GAME_STATE_QUERY,
  
  TURN_DATA_DELETE,
  TURN_DATA_FOR_GAME_STATE_QUERY,
  
  INDUCEMENTS_DELETE,
  INDUCEMENTS_FOR_GAME_STATE_QUERY,
  
  TEAMS_DELETE,
  TEAMS_FOR_GAME_STATE_QUERY,
  
  TEAM_RESULTS_DELETE,
  TEAM_RESULTS_FOR_GAME_STATE_QUERY,
  
  PLAYER_DELETE,
  
  PLAYERS_DELETE,
  PLAYERS_FOR_GAME_STATE_QUERY,
  
  PLAYER_ICONS_DELETE,
  PLAYER_ICONS_FOR_GAME_STATE_QUERY,
  
  PLAYER_SKILLS_DELETE,
  PLAYER_SKILLS_FOR_GAME_STATE_QUERY,

  PLAYER_INJURIES_DELETE,
  PLAYER_INJURIES_FOR_GAME_STATE_QUERY,

  PLAYER_RESULTS_DELETE,
  PLAYER_RESULTS_FOR_GAME_STATE_QUERY,

  GAME_LOGS_DELETE,
  GAME_LOGS_FOR_GAME_STATE_QUERY,
  
  STEP_STACK_DELETE,
  STEP_STACK_FOR_GAME_STATE_QUERY,
  
  GAME_OPTIONS_DELETE,
  GAME_OPTIONS_FOR_GAME_STATE_QUERY,

  GAME_LIST_QUERY_OPEN_GAMES_BY_COACH,
  GAME_LIST_QUERY_OPEN_GAMES_BY_COACH_OLD,
  ADMIN_LIST_BY_STATUS_QUERY,
  ADMIN_LIST_BY_STATUS_QUERY_OLD;

}
