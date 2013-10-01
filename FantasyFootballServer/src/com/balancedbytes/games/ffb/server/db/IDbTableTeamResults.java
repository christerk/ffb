package com.balancedbytes.games.ffb.server.db;

/**
 * 
 * @author Kalimar
 */
public interface IDbTableTeamResults {
	
	String TABLE_NAME = "ffb_team_results";
  
  String COLUMN_GAME_STATE_ID = "game_state_id";
  String COLUMN_TEAM_ID = "team_id";
  String COLUMN_SCORE = "score";
  String COLUMN_CONCEDED = "conceded";
  String COLUMN_FAME = "fame";
  String COLUMN_SPECTATORS = "spectators";
  String COLUMN_WINNINGS = "winnings";
  String COLUMN_SPIRALLING_EXPENSES = "spiralling_expenses";
  String COLUMN_FAN_FACTOR_MODIFIER = "fan_factor_modifier";
  String COLUMN_SUFFERED_BH = "suffered_bh";
  String COLUMN_SUFFERED_SI = "suffered_si";
  String COLUMN_SUFFERED_RIP = "suffered_rip";
  String COLUMN_RAISED_DEAD = "raised_dead";
  String COLUMN_PETTY_CASH_TRANSFERRED = "petty_cash_transferred";
  String COLUMN_PETTY_CASH_USED = "petty_cash_used";
  String COLUMN_TEAM_VALUE = "team_value";

}