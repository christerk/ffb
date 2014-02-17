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
import com.balancedbytes.games.ffb.server.db.DbQueryFactory;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbTransaction;
import com.balancedbytes.games.ffb.server.db.delete.DbGamesInfoDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbGamesSerializedDeleteParameter;
import com.balancedbytes.games.ffb.server.db.insert.DbGamesInfoInsertParameter;
import com.balancedbytes.games.ffb.server.db.insert.DbGamesSerializedInsertParameter;
import com.balancedbytes.games.ffb.server.request.fumbbl.UtilFumbblRequest;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Georg Seipler
 */
public class DbConversion {

  private FantasyFootballServer fServer;
  private DbQueryFactory fDbQueryFactory;
  
  public DbConversion(FantasyFootballServer pServer) {
    fServer = pServer;
    fDbQueryFactory = new DbQueryFactory(pServer);
  }
  
  public FantasyFootballServer getServer() {
    return fServer;
  }
  
  public void convert(long pStartGameId, long pEndGameId) throws SQLException {
    fDbQueryFactory.prepareStatements();
    long startTime = System.currentTimeMillis();
    SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    System.out.println("Conversion started at " + timestampFormat.format(new Date(startTime)));
    DbGameStatesQueryFinishedGames finishedGamesQuery = (DbGameStatesQueryFinishedGames) fDbQueryFactory.getStatement(DbStatementId.GAME_STATES_QUERY_FINISHED_GAMES);
    long[] finishedGameStateIds = finishedGamesQuery.execute(pStartGameId, pEndGameId);
    List<Long> unconvertedGames = new ArrayList<Long>();
    for (long gameStateId : finishedGameStateIds) {
      System.out.print("load game " + gameStateId + " in old format");
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
        System.out.print(" and save game in new format");
        try {
          saveToDb(gameState);
          System.out.println(".");
        } catch (Exception pAny) {
          fServer.getDebugLog().log(gameStateId, pAny);
          gameState = null;
        }
      }
      if (gameState == null) {
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
    fDbQueryFactory.closeDbConnection();
  } 
  
  public void saveToDb(GameState pGameState) {
    DbTransaction transaction = new DbTransaction();
    transaction.add(new DbGamesInfoDeleteParameter(pGameState.getId()));
    transaction.add(new DbGamesInfoInsertParameter(pGameState));
    transaction.add(new DbGamesSerializedDeleteParameter(pGameState.getId()));
    transaction.add(new DbGamesSerializedInsertParameter(pGameState));
    transaction.executeUpdate(fServer);
  }
  
}
