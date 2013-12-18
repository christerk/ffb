package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.GameList;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.net.commands.ClientCommandJoin;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.db.DbQueryFactory;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.query.DbPasswordForCoachQuery;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestCheckAuthorization;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.ServerCommunication;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandJoinApproved;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerJoin extends ServerCommandHandler {
  
  protected ServerCommandHandlerJoin(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_JOIN;
  }

  public void handleCommand(ReceivedCommand pReceivedCommand) {
    
    ClientCommandJoin joinCommand = (ClientCommandJoin) pReceivedCommand.getCommand();
    ServerCommunication communication = getServer().getCommunication();
    
    if ((joinCommand.getGameId() > 0) || StringTool.isProvided(joinCommand.getGameName())) {
    
      if (ServerMode.FUMBBL == getServer().getMode()) {
      	
        getServer().getFumbblRequestProcessor().add(
          new FumbblRequestCheckAuthorization(
            pReceivedCommand.getSender(),
            joinCommand.getCoach(),
            joinCommand.getPassword(),
            joinCommand.getGameId(),
            joinCommand.getGameName(),
            joinCommand.getTeamId(),
            joinCommand.getClientMode()
          )
        );
  
      } else {
        
        DbQueryFactory statementFactory = getServer().getDbQueryFactory();
        DbPasswordForCoachQuery passwordQuery = (DbPasswordForCoachQuery) statementFactory.getStatement(DbStatementId.PASSWORD_FOR_COACH_QUERY);
        String password = passwordQuery.execute(joinCommand.getCoach());
        
        if (joinCommand.getPassword().equals(password)) {
          InternalServerCommandJoinApproved joinApprovedCommand = new InternalServerCommandJoinApproved(
            joinCommand.getGameId(),
            joinCommand.getGameName(),
            joinCommand.getCoach(),
            joinCommand.getTeamId(),
            joinCommand.getClientMode()
          );
          ReceivedCommand receivedJoinApproved = new ReceivedCommand(joinApprovedCommand);
          receivedJoinApproved.setSender(pReceivedCommand.getSender());
          communication.handleCommand(receivedJoinApproved);
          
        } else {
          communication.sendStatus(pReceivedCommand.getSender(), ServerStatus.ERROR_WRONG_PASSWORD, null);
        }
        
      }
    
    } else {
  	
      GameList gameList = null;
      GameCache gameCache = getServer().getGameCache();
      if (ClientMode.PLAYER == joinCommand.getClientMode()) {
        gameList = gameCache.findOpenGamesForCoach(joinCommand.getCoach());
      } else {
        gameList = gameCache.findActiveGames();
      }
      communication.sendGameList(pReceivedCommand.getSender(), gameList);
  	
    }
    
  }
    
}
