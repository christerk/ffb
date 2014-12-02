package com.balancedbytes.games.ffb.server.db.old;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.admin.UtilBackup;
import com.balancedbytes.games.ffb.server.db.DbConnectionManager;
import com.balancedbytes.games.ffb.server.db.DbQueryFactory;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbTransaction;
import com.balancedbytes.games.ffb.server.db.insert.DbGamesInfoInsertParameter;
import com.balancedbytes.games.ffb.server.request.fumbbl.UtilFumbblRequest;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Georg Seipler
 */
public class DbConversion {

  private FantasyFootballServer fServer;
  private DbQueryFactory fDbOldQueryFactory;
  
  public DbConversion(FantasyFootballServer pServer) {
    fServer = pServer;
    DbConnectionManager dbConnectionManagerOld = new DbConnectionManager(fServer);
    dbConnectionManagerOld.setDbUrl(fServer.getProperty(IServerProperty.DB_OLD_URL));
    dbConnectionManagerOld.setDbUser(fServer.getProperty(IServerProperty.DB_OLD_USER));
    dbConnectionManagerOld.setDbPassword(fServer.getProperty(IServerProperty.DB_OLD_PASSWORD));
    fDbOldQueryFactory = new DbQueryFactory(dbConnectionManagerOld);
  }
  
  public FantasyFootballServer getServer() {
    return fServer;
  }
  
  public void convert(long pStartGameId, long pEndGameId) throws SQLException {
    fDbOldQueryFactory.prepareStatements();
    long startTime = System.currentTimeMillis();
    SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    System.out.println("Conversion started at " + timestampFormat.format(new Date(startTime)));
    DbGameStatesQueryFinishedGames finishedGamesQuery = (DbGameStatesQueryFinishedGames) fDbOldQueryFactory.getStatement(DbStatementId.GAME_STATES_QUERY_FINISHED_GAMES);
    long[] finishedGameStateIds = finishedGamesQuery.execute(pStartGameId, pEndGameId);
    List<Long> unconvertedGames = new ArrayList<Long>();
    for (long gameStateId : finishedGameStateIds) {
      System.out.print("load old game " + gameStateId + " from db");
      GameState gameState = DbQueryScript.readGameState(fServer, gameStateId);
      if (gameState != null) {
        Game game = gameState.getGame();
        System.out.print(", load rosters from fumbbl");
        Roster homeRoster = UtilFumbblRequest.loadFumbblRosterForTeam(getServer(), game.getTeamHome().getId());
        Roster awayRoster = UtilFumbblRequest.loadFumbblRosterForTeam(getServer(), game.getTeamAway().getId());
        if ((homeRoster != null) && (awayRoster != null)) {
          game.getTeamHome().updateRoster(homeRoster);
          game.getTeamAway().updateRoster(awayRoster);
        } else {
          gameState = null;
        }
      }
      if (gameState != null) {
        System.out.print(", save info to db");
        try {
          DbTransaction insertTransaction = new DbTransaction();
          insertTransaction.add(new DbGamesInfoInsertParameter(gameState));
          insertTransaction.executeUpdate(fServer);
        } catch (Exception pAny) {
          fServer.getDebugLog().log(gameStateId, pAny);
          gameState = null;
        }
      }
      if (gameState != null) {
        System.out.print(", back game up");
        try {
          if (UtilBackup.save(gameState)) {
            gameState = UtilBackup.load(fServer, gameStateId);
          } else {
            gameState = null;
          }
        } catch (Exception pAny) {
          fServer.getDebugLog().log(gameStateId, pAny);
          gameState = null;
        }
      }
//      if (gameState != null) {
//        System.out.print(" and delete old entry");
//        try {
//          DbTransaction deleteTransaction = DbUpdateScript.createDeleteGameStateTransaction(fServer, gameStateId);
//          deleteTransaction.executeUpdate(fServer);
//          System.out.println(".");
//        } catch (Exception pAny) {
//          fServer.getDebugLog().log(gameStateId, pAny);
//          gameState = null;
//        }
//      }
      if (gameState != null) {
        System.out.println(".");
      } else {
        System.out.println(" -> unable to convert!");
        unconvertedGames.add(gameStateId);
      }
    }
    long endTime = System.currentTimeMillis();
    System.out.println("Conversion finished at " + timestampFormat.format(new Date(endTime)));
    System.out.println((finishedGameStateIds.length - unconvertedGames.size()) + " games converted in " + StringTool.formatThousands(((endTime - startTime) / 1000)) + " seconds.");
    if (unconvertedGames.size() > 0) {
      System.out.println("Unable to convert " + unconvertedGames.size() + " games:");
      boolean firstElement = true;
      for (long gameStateId : unconvertedGames) {
        if (firstElement) {
          firstElement = false;
        } else {
          System.out.print(", ");
        }
        System.out.print(gameStateId);
      }
      System.out.println();
    }
    fDbOldQueryFactory.closeDbConnection();
  } 
    
}
