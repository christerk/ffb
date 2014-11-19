package com.balancedbytes.games.ffb.server.util;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.TeamSetup;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbTransaction;
import com.balancedbytes.games.ffb.server.db.IDbTableTeamSetups;
import com.balancedbytes.games.ffb.server.db.delete.DbTeamSetupsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.insert.DbTeamSetupsInsertParameter;
import com.balancedbytes.games.ffb.server.db.query.DbTeamSetupsForTeamQuery;
import com.balancedbytes.games.ffb.server.db.query.DbTeamSetupsQuery;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilBox;

/**
 * 
 * @author Kalimar
 */
public class UtilServerSetup {
  
  public static void loadTeamSetup(GameState pGameState, String pSetupName) {
    
    if (pGameState != null) {
    
      FantasyFootballServer server = pGameState.getServer();
      Game game = pGameState.getGame();
      Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
  
      if (StringTool.isProvided(pSetupName)) {
        DbTeamSetupsQuery teamSetupQuery = (DbTeamSetupsQuery) server.getDbQueryFactory().getStatement(DbStatementId.TEAM_SETUPS_QUERY);
        TeamSetup teamSetup = teamSetupQuery.execute(team.getId(), pSetupName);
        if (teamSetup != null) {
          teamSetup.applyTo(game);
          UtilBox.refreshBoxes(game);
        }
        
      } else {
        DbTeamSetupsForTeamQuery allSetupNamesQuery = (DbTeamSetupsForTeamQuery) server.getDbQueryFactory().getStatement(DbStatementId.TEAM_SETUPS_QUERY_ALL_FOR_A_TEAM);
        String[] setupNames = allSetupNamesQuery.execute(team);
        Session session = game.isHomePlaying() ? server.getSessionManager().getSessionOfHomeCoach(pGameState) : server.getSessionManager().getSessionOfAwayCoach(pGameState);
        server.getCommunication().sendTeamSetupList(session, setupNames);
      }
      
    }
            
  }
  
  public static void saveTeamSetup(GameState pGameState, String pSetupName, int[] pPlayerNumbers, FieldCoordinate[] pPlayerCoordinates) {
    
    if ((pGameState != null) && StringTool.isProvided(pSetupName)) {
    
      FantasyFootballServer server = pGameState.getServer();
      Game game = pGameState.getGame();
      Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
  
      TeamSetup teamSetup = new TeamSetup();
      if (pSetupName.length() <= IDbTableTeamSetups.LENGTH_NAME) {
        teamSetup.setName(pSetupName);
      } else {
        teamSetup.setName(pSetupName.substring(0, IDbTableTeamSetups.LENGTH_NAME));
      }
      teamSetup.setTeamId(team.getId());
      for (int i = 0; i < pPlayerNumbers.length; i++) {
        teamSetup.addCoordinate(pPlayerCoordinates[i], pPlayerNumbers[i]);
      }
      
      // System.out.println(teamSetup.toXml(0));
      
      DbTransaction dbTransaction = new DbTransaction();
      dbTransaction.add(new DbTeamSetupsDeleteParameter(teamSetup.getTeamId(), teamSetup.getName()));
      dbTransaction.add(new DbTeamSetupsInsertParameter(teamSetup));
      server.getDbUpdater().add(dbTransaction);
        
    }
            
  }
  
  public static void deleteTeamSetup(GameState pGameState, String pSetupName) {
    
    if (pGameState != null) {

      FantasyFootballServer server = pGameState.getServer();
      Game game = pGameState.getGame();
      Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();

      if (StringTool.isProvided(pSetupName)) {
        DbTransaction dbTransaction = new DbTransaction();
        dbTransaction.add(new DbTeamSetupsDeleteParameter(team.getId(), pSetupName));
        server.getDbUpdater().add(dbTransaction);
      }
  
      DbTeamSetupsForTeamQuery allSetupNamesQuery = (DbTeamSetupsForTeamQuery) server.getDbQueryFactory().getStatement(DbStatementId.TEAM_SETUPS_QUERY_ALL_FOR_A_TEAM);
      String[] setupNames = allSetupNamesQuery.execute(team);
      Session session = game.isHomePlaying() ? server.getSessionManager().getSessionOfHomeCoach(pGameState) : server.getSessionManager().getSessionOfAwayCoach(pGameState);
      server.getCommunication().sendTeamSetupList(session, setupNames);
      
    }

  }
  
  public static void setupPlayer(GameState pGameState, String pPlayerId, FieldCoordinate pCoordinate) {
    
    if ((pGameState != null) && StringTool.isProvided(pPlayerId) && (pCoordinate != null)) {
      
      Game game = pGameState.getGame();
      Player player = game.getPlayerById(pPlayerId);
      if (player == null) {
        return;
      }
        
      boolean homeTeam = game.getTeamHome().hasPlayer(player);
      if (homeTeam != game.isHomePlaying()) {
        return;
      }
        
      FieldModel fieldModel = game.getFieldModel();
      FieldCoordinate coordinate = homeTeam ? pCoordinate : pCoordinate.transform();
      FieldCoordinate oldCoordinate = fieldModel.getPlayerCoordinate(player);
      PlayerState playerState = fieldModel.getPlayerState(player);       
      
      if (coordinate.isBoxCoordinate()) {
        fieldModel.setPlayerState(player, playerState.changeBase(PlayerState.RESERVE));
      } else {
        if ((game.getTurnMode() == TurnMode.QUICK_SNAP) && !coordinate.equals(oldCoordinate)) {
          fieldModel.setPlayerState(player, playerState.changeBase(PlayerState.STANDING).changeActive(false));
        } else {
          fieldModel.setPlayerState(player, playerState.changeBase(PlayerState.STANDING).changeActive(true));
        }
      }
      fieldModel.setPlayerCoordinate(player, coordinate);
  
    }
    
  }
  
}
