package com.balancedbytes.games.ffb.server.db.update;

import java.sql.Timestamp;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbUpdateStatement;
import com.balancedbytes.games.ffb.server.db.DefaultDbUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbGamesInfoUpdateParameter extends DefaultDbUpdateParameter {

  private long fId;
  private Timestamp fScheduled;
  private Timestamp fStarted;
  private Timestamp fFinished;
  private String fCoachHome;
  private String fTeamHomeId;
  private String fTeamHomeName;
  private String fCoachAway;
  private String fTeamAwayId;
  private String fTeamAwayName;
  private byte fHalf;
  private byte fTurn;
  private boolean fHomePlaying;
  private String fStatus;
  private boolean fTesting;

  public DbGamesInfoUpdateParameter(GameState pGameState) {
    if (pGameState != null) {
      Game game = pGameState.getGame();
      fId = pGameState.getId();
      fScheduled = (game.getScheduled() != null) ? new Timestamp(game.getScheduled().getTime()) : null;
      fStarted = (game.getStarted() != null) ? new Timestamp(game.getStarted().getTime()) : null;
      fFinished = (game.getFinished() != null) ? new Timestamp(game.getFinished().getTime()) : null;
      fCoachHome = (game.getTeamHome() != null) ? game.getTeamHome().getCoach() : null;
      fTeamHomeId = (game.getTeamHome() != null) ? game.getTeamHome().getId() : null;
      fTeamHomeName = (game.getTeamHome() != null) ? game.getTeamHome().getName() : null;
      fCoachAway = (game.getTeamAway() != null) ? game.getTeamAway().getCoach() : null;
      fTeamAwayId = (game.getTeamAway() != null) ? game.getTeamAway().getId() : null;
      fTeamAwayName = (game.getTeamAway() != null) ? game.getTeamAway().getName() : null;
      fHalf = ((byte) game.getHalf());
      fTurn = ((byte) Math.min(game.getTurnDataHome().getTurnNr(), game.getTurnDataAway().getTurnNr()));
      fHomePlaying = game.isHomePlaying();
      fStatus = (pGameState.getStatus() != null) ? pGameState.getStatus().getTypeString() : " ";
      fTesting = game.isTesting();
    }
  }
  
  public long getId() {
    return fId;
  }
  
  public Timestamp getScheduled() {
    return fScheduled;
  }
  
  public Timestamp getStarted() {
    return fStarted;
  }
  
  public Timestamp getFinished() {
    return fFinished;
  }
  
  public String getCoachHome() {
	  return fCoachHome;
  }

  public String getTeamHomeId() {
	  return fTeamHomeId;
  }
  
  public String getTeamHomeName() {
	  return fTeamHomeName;
  }

  public String getCoachAway() {
	  return fCoachAway;
  }

  public String getTeamAwayId() {
	  return fTeamAwayId;
  }
  
  public String getTeamAwayName() {
	  return fTeamAwayName;
  }

  public byte getHalf() {
    return fHalf;
  }
  
  public byte getTurn() {
	  return fTurn;
  }

  public boolean isHomePlaying() {
    return fHomePlaying;
  }

  public String getStatus() {
    return fStatus;
  }
  
  public boolean isTesting() {
    return fTesting;
  }
  
  public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
    return (DbUpdateStatement) pServer.getDbUpdateFactory().getStatement(DbStatementId.GAMES_INFO_UPDATE);
  }

}
