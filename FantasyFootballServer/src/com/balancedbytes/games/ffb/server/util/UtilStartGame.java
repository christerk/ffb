package com.balancedbytes.games.ffb.server.util;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.GameOption;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbStatementFactory;
import com.balancedbytes.games.ffb.server.db.query.DbUserSettingsQuery;
import com.balancedbytes.games.ffb.server.net.ChannelManager;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;


/**
 * 
 * @author Kalimar
 */
public class UtilStartGame {

  public static boolean joinGameAsPlayerAndCheckIfReadyToStart(GameState pGameState, SocketChannel pSender, String pCoach, boolean pHomeTeam) {
    Game game = pGameState.getGame();
    FantasyFootballServer server = pGameState.getServer();
    if ((game.getTeamAway() != null) && (game.getTeamHome() != null) && StringTool.isProvided(game.getTeamAway().getId()) && game.getTeamAway().getId().equals(game.getTeamHome().getId())) {
      server.getCommunication().sendStatus(pSender, ServerStatus.ERROR_SAME_TEAM, null);
    } else {
      if (UtilStartGame.sendServerJoin(pGameState, pSender, pCoach, pHomeTeam, ClientMode.PLAYER) > 1) {
        return true;
      }
    }
    return false;
  }
  
  public static int sendServerJoin(GameState pGameState, SocketChannel pSender, String pCoach, boolean pHomeTeam, ClientMode pMode) {
    
    FantasyFootballServer server = pGameState.getServer();
    ChannelManager channelManager = server.getChannelManager();
    channelManager.addChannel(pSender, pGameState, pCoach, pMode, pHomeTeam);
    
    List<String> playerList = new ArrayList<String>();
    
    SocketChannel[] receivers = channelManager.getChannelsForGameId(pGameState.getId());
    for (int i = 0; i < receivers.length; i++) {
      String coach = channelManager.getCoachForChannel(receivers[i]);
      ClientMode mode = channelManager.getModeForChannel(receivers[i]);
      if (mode == ClientMode.PLAYER) {
	      if (receivers[i] == channelManager.getChannelOfHomeCoach(pGameState)) {
          playerList.add(0, coach);
	      } else {
          playerList.add(coach);
        }
      }
    }
    String[] players = playerList.toArray(new String[playerList.size()]);
    
    server.getCommunication().sendJoin(receivers, pCoach, pMode, players, receivers.length - playerList.size());
    
    sendUserSettings(pGameState, pCoach, pSender);
    
    return players.length;
  
  }
  
  public static void sendUserSettings(GameState pGameState, String pCoach, SocketChannel pReceiver) {
    FantasyFootballServer server = pGameState.getServer();
    IDbStatementFactory statementFactory = server.getDbQueryFactory();
    DbUserSettingsQuery userSettingsQuery = (DbUserSettingsQuery) statementFactory.getStatement(DbStatementId.USER_SETTINGS_QUERY);
    userSettingsQuery.execute(pCoach);
    String[] settingNames = userSettingsQuery.getSettingNames();
    String[] settingValues = userSettingsQuery.getSettingValues();
    if (ArrayTool.isProvided(settingNames) && ArrayTool.isProvided(settingValues)) {
      server.getCommunication().sendUserSettings(pReceiver, settingNames, settingValues);
    }
  }
  
  public static boolean startGame(GameState pGameState) {
    Game game = pGameState.getGame();
    FantasyFootballServer server = pGameState.getServer();
    boolean ownershipOk = true;
    if (!game.isTesting() && game.getOptions().getOptionValue(GameOption.CHECK_OWNERSHIP).isEnabled()) {
    	if (!server.getChannelManager().isHomeCoach(pGameState, game.getTeamHome().getCoach())) {
    		ownershipOk = false;
        server.getCommunication().sendStatus(server.getChannelManager().getChannelOfHomeCoach(pGameState), ServerStatus.ERROR_NOT_YOUR_TEAM, null);
    	}
    	if (!server.getChannelManager().isAwayCoach(pGameState, game.getTeamAway().getCoach())) {
    		ownershipOk = false;
        server.getCommunication().sendStatus(server.getChannelManager().getChannelOfAwayCoach(pGameState), ServerStatus.ERROR_NOT_YOUR_TEAM, null);
    	}
    }
    if (ownershipOk) {
    	if (game.getFinished() == null) {
    		if (pGameState.getStepStack().size() > 0) {
      		pGameState.findNextStep(null);
    		} else {
      		SequenceGenerator.getInstance().pushStartGameSequence(pGameState);
    		}
    	}
	    server.getCommunication().sendGameState(pGameState);
	    pGameState.getGame().fetchChanges();  // clear changes after sending the whole model
	    return true;
    } else {
    	return false;
    }
  }
  
}
