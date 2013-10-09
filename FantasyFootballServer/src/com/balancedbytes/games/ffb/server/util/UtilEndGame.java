package com.balancedbytes.games.ffb.server.util;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.dialog.DialogWinningsReRollParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.report.ReportDefectingPlayers;
import com.balancedbytes.games.ffb.report.ReportFanFactorRoll;
import com.balancedbytes.games.ffb.report.ReportMostValuablePlayers;
import com.balancedbytes.games.ffb.report.ReportWinningsRoll;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public class UtilEndGame {

  public static void adjustScore(GameState pGameState) {
    Game game = pGameState.getGame();
    GameResult gameResult = game.getGameResult();
    if (gameResult.getTeamResultHome().hasConceded()) {
      int scoreDiffAway = gameResult.getTeamResultAway().getScore() - gameResult.getTeamResultHome().getScore();
      if (scoreDiffAway <= 0) {
        gameResult.getTeamResultAway().setScore(gameResult.getTeamResultAway().getScore() + Math.abs(scoreDiffAway) + 1);
      }
    }
    if (gameResult.getTeamResultAway().hasConceded()) {
      int scoreDiffHome = gameResult.getTeamResultHome().getScore() - gameResult.getTeamResultAway().getScore();
      if (scoreDiffHome <= 0) {
        gameResult.getTeamResultHome().setScore(gameResult.getTeamResultHome().getScore() + Math.abs(scoreDiffHome) + 1);
      }
    }
  }
  
  public static ReportMostValuablePlayers findMostValuablePlayers(GameState pGameState) {
    Game game = pGameState.getGame();
    GameResult gameResult = game.getGameResult();
    ReportMostValuablePlayers mvpReport = new ReportMostValuablePlayers();
    int nrOfHomeMvps = 1;
    int nrOfAwayMvps = 1;
    if (gameResult.getTeamResultHome().hasConceded() && (UtilPlayer.findPlayersInReserveOrField(game, game.getTeamHome()).length > 2)) {
      nrOfHomeMvps = 0;
      nrOfAwayMvps = 2;
    }
    if (gameResult.getTeamResultAway().hasConceded() && (UtilPlayer.findPlayersInReserveOrField(game, game.getTeamAway()).length > 2)) {
      nrOfHomeMvps = 2;
      nrOfAwayMvps = 0;
    }
    for (int i = 0; i < nrOfHomeMvps; i++) {
      Player[] playersForMvp = null;
      Player mvpHome = null;
      PlayerResult playerResultHome = null;
      try {
        playersForMvp = findPlayersForMvp(game, game.getTeamHome());
        mvpHome = pGameState.getDiceRoller().randomPlayer(playersForMvp);
        playerResultHome = gameResult.getPlayerResult(mvpHome);
        playerResultHome.setPlayerAwards(playerResultHome.getPlayerAwards() + 1);
        mvpReport.addPlayerIdHome(mvpHome.getId());
      } catch (Exception pAnyException) {
        debugMvp(pGameState, pAnyException, game.getTeamHome(), playersForMvp, mvpHome, playerResultHome);
      }
    }
    for (int i = 0; i < nrOfAwayMvps; i++) {
      Player[] playersForMvp = null;
      Player mvpAway = null;
      PlayerResult playerResultAway = null;
      try {
        playersForMvp = findPlayersForMvp(game, game.getTeamAway());
        mvpAway = pGameState.getDiceRoller().randomPlayer(playersForMvp);
        playerResultAway = gameResult.getPlayerResult(mvpAway);
        playerResultAway.setPlayerAwards(playerResultAway.getPlayerAwards() + 1);
        mvpReport.addPlayerIdAway(mvpAway.getId());
      } catch (Exception pAnyException) {
        debugMvp(pGameState, pAnyException, game.getTeamHome(), playersForMvp, mvpAway, playerResultAway);
      }
    }
    return mvpReport;
  }
  
  public static ReportWinningsRoll rollWinnings(GameState pGameState, boolean pReRolled) {
    Game game = pGameState.getGame();
    GameResult gameResult = game.getGameResult(); 
    int scoreDiffHome = gameResult.getTeamResultHome().getScore() - gameResult.getTeamResultAway().getScore();
    int winningsHome = 0;
    int rollHome = 0;
    if (!pReRolled || (scoreDiffHome > 0)) {
      rollHome = pGameState.getDiceRoller().rollWinnings();
      winningsHome = rollHome + gameResult.getTeamResultHome().getFame();
      if (scoreDiffHome >= 0) {
        winningsHome++;
      }
      gameResult.getTeamResultHome().setWinnings(winningsHome * 10000);
    }
    int winningsAway = 0;
    int rollAway = 0;
    if (!pReRolled || (scoreDiffHome < 0)) {
      rollAway = pGameState.getDiceRoller().rollWinnings();
      winningsAway = rollAway + gameResult.getTeamResultAway().getFame();
      if (scoreDiffHome <= 0) {
        winningsAway++;
      }
      gameResult.getTeamResultAway().setWinnings(winningsAway * 10000);
    }
    if (!pReRolled) {
      if (scoreDiffHome > 0) {
        UtilDialog.showDialog(pGameState, new DialogWinningsReRollParameter(game.getTeamHome().getId(), rollHome));
      }
      if (scoreDiffHome < 0) {
        UtilDialog.showDialog(pGameState, new DialogWinningsReRollParameter(game.getTeamAway().getId(), rollAway));
      }
    }
    return new ReportWinningsRoll(rollHome, gameResult.getTeamResultHome().getWinnings(), rollAway, gameResult.getTeamResultAway().getWinnings());
  }
  
  public static ReportWinningsRoll concedeWinnings(GameState pGameState) {
    ReportWinningsRoll report = null;
    Game game = pGameState.getGame();
    GameResult gameResult = game.getGameResult(); 
    if (gameResult.getTeamResultHome().hasConceded() && (UtilPlayer.findPlayersInReserveOrField(game, game.getTeamHome()).length > 2)) {
      gameResult.getTeamResultAway().setWinnings(gameResult.getTeamResultAway().getWinnings() + gameResult.getTeamResultHome().getWinnings());
      gameResult.getTeamResultHome().setWinnings(0);
      report = new ReportWinningsRoll(0, gameResult.getTeamResultHome().getWinnings(), 0, gameResult.getTeamResultAway().getWinnings());
    }
    if (gameResult.getTeamResultAway().hasConceded() && (UtilPlayer.findPlayersInReserveOrField(game, game.getTeamAway()).length > 2)) {
      gameResult.getTeamResultHome().setWinnings(gameResult.getTeamResultHome().getWinnings() + gameResult.getTeamResultAway().getWinnings());
      gameResult.getTeamResultAway().setWinnings(0);
      report = new ReportWinningsRoll(0, gameResult.getTeamResultHome().getWinnings(), 0, gameResult.getTeamResultAway().getWinnings());
    }
    return report;
  }
  
  public static ReportFanFactorRoll rollFanFactor(GameState pGameState) {
    Game game = pGameState.getGame();
    GameResult gameResult = game.getGameResult();
    int scoreDiffHome = gameResult.getTeamResultHome().getScore() - gameResult.getTeamResultAway().getScore();
    int[] fanFactorRollHome = null;
    int fanFactorModifierHome = -1;
    if (!gameResult.getTeamResultHome().hasConceded()) {
      fanFactorRollHome = pGameState.getDiceRoller().rollFanFactor(scoreDiffHome > 0);
      fanFactorModifierHome = DiceInterpreter.getInstance().interpretFanFactorRoll(fanFactorRollHome, game.getTeamHome().getFanFactor(), scoreDiffHome);
    }
    gameResult.getTeamResultHome().setFanFactorModifier(fanFactorModifierHome);
    int[] fanFactorRollAway = null;
    int fanFactorModifierAway = -1;
    if (!gameResult.getTeamResultAway().hasConceded()) {
      fanFactorRollAway = pGameState.getDiceRoller().rollFanFactor(scoreDiffHome < 0);
      fanFactorModifierAway = DiceInterpreter.getInstance().interpretFanFactorRoll(fanFactorRollAway, game.getTeamAway().getFanFactor(), -scoreDiffHome);
    }
    gameResult.getTeamResultAway().setFanFactorModifier(fanFactorModifierAway);
    return new ReportFanFactorRoll(fanFactorRollHome, fanFactorModifierHome, fanFactorRollAway, fanFactorModifierAway);
  }

  public static ReportDefectingPlayers rollPlayerLoss(GameState pGameState) {
    ReportDefectingPlayers report = null;
    Game game = pGameState.getGame();
    GameResult gameResult = game.getGameResult();
    Team team = null;
    if (gameResult.getTeamResultHome().hasConceded() && (UtilPlayer.findPlayersInReserveOrField(game, game.getTeamHome()).length > 2)) {
      team = game.getTeamHome();
    }
    if (gameResult.getTeamResultAway().hasConceded() && (UtilPlayer.findPlayersInReserveOrField(game, game.getTeamAway()).length > 2)) {
      team = game.getTeamAway();
    }
    if (team != null) {
      List<String> defectingPlayerIds = new ArrayList<String>();
      List<Integer> defectingRolls = new ArrayList<Integer>();
      List<Boolean> defectingFlags = new ArrayList<Boolean>();
      for (Player player : team.getPlayers()) {
        PlayerResult playerResult = gameResult.getPlayerResult(player);
        if (playerResult.getCurrentSpps() >= 51) {
          defectingPlayerIds.add(player.getId());
          int defectingRoll = pGameState.getDiceRoller().rollPlayerLoss();
          defectingRolls.add(defectingRoll);
          boolean playerDefecting = DiceInterpreter.getInstance().isPlayerDefecting(defectingRoll); 
          defectingFlags.add(playerDefecting);
          playerResult.setDefecting(playerDefecting);
        }
      }
      if (defectingPlayerIds.size() > 0) {
        report = new ReportDefectingPlayers(defectingPlayerIds.toArray(new String[defectingPlayerIds.size()]), ArrayTool.toIntArray(defectingRolls), ArrayTool.toBooleanArray(defectingFlags));
      }
    }
    return report;
  }
  
  /* 
  public static ReportSpirallingExpenses findSpirallingExpenses(GameState pGameState) {
    GameResult gameResult = game.getGameResult();
    gameResult.getTeamResultHome().setSpirallingExpenses(findSpirallingExpenses(game.getTeamHome()));
    gameResult.getTeamResultAway().setSpirallingExpenses(findSpirallingExpenses(game.getTeamAway()));
    return new ReportSpirallingExpenses(gameResult.getTeamResultHome().getSpirallingExpenses(), gameResult.getTeamResultAway().getSpirallingExpenses());
  }
 
  private static int findSpirallingExpenses(Team pTeam) {
    int spirallingExpenses = 0;
    if (pTeam != null) {
      int teamValue = pTeam.getTeamValue();
      if (teamValue >= 1750000) {
        if (teamValue <= 1890000) {
          spirallingExpenses = 10000;
        } else if (teamValue <= 2040000){
          spirallingExpenses = 20000;
        } else if (teamValue <= 2190000){
          spirallingExpenses = 30000;
        } else if (teamValue <= 2340000){
          spirallingExpenses = 40000;
        } else if (teamValue <= 2490000){
          spirallingExpenses = 50000;
        } else if (teamValue <= 2640000){
          spirallingExpenses = 60000;
        } else {
          spirallingExpenses = 70000 + (((teamValue - 2650000) / 150000) * 10000);
        }
      }
    }
    return spirallingExpenses;
  }
  */
  
  private static Player[] findPlayersForMvp(Game pGame, Team pTeam) {
    List<Player> players = new ArrayList<Player>();
    GameResult gameResult = pGame.getGameResult();
    for (Player player : pTeam.getPlayers()) {
      PlayerResult playerResult = gameResult.getPlayerResult(player);
      if (SendToBoxReason.MNG != playerResult.getSendToBoxReason() &&  SendToBoxReason.NURGLES_ROT != playerResult.getSendToBoxReason() ) {
        players.add(player);
      }
    }
    return players.toArray(new Player[players.size()]);
  }
  
  // TODO: delete this if bug has been found & fixed
  private static void debugMvp(GameState pGameState, Exception pException, Team pTeam, Player[] pAvailablePlayers, Player pChosenPlayer, PlayerResult pPlayerResult) {
    DebugLog debugLog = pGameState.getServer().getDebugLog();
    StringBuilder message = new StringBuilder();
    message.append("MVP BUG");
    message.append("\nTeam = ").append(pGameState.getGame().getTeamHome());
    message.append("\nAvailable Players for Mvp = ");
    if (ArrayTool.isProvided(pAvailablePlayers)) {
      for (int j = 0; j < pAvailablePlayers.length; j++) {
        if (j > 0) {
          message.append(", ");
        }
        if (pAvailablePlayers[j] != null) {
          message.append(pAvailablePlayers[j].getNr()).append(" ").append(pAvailablePlayers[j].getName());
        } else {
          message.append("null");
        }
      }
    } else {
      message.append("none");
    }
    message.append("\nChosen Player = ");
    if (pChosenPlayer != null) {
      message.append(pChosenPlayer.getNr()).append(" ").append(pChosenPlayer.getName());
    } else {
      message.append("null");
    }
    if (pPlayerResult != null) {
      message.append("\nPlayerResult available.");
    } else {
      message.append("\nPlayerResult not available.");
    }
    debugLog.log(IServerLogLevel.ERROR, message.toString());
    debugLog.log(pException);
  }
  
}
