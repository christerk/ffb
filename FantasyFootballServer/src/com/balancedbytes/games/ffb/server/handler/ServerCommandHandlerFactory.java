package com.balancedbytes.games.ffb.server.handler;

import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerFactory {
  
  private Map<NetCommandId,ServerCommandHandler> fCommandHandlerById;
  
  public ServerCommandHandlerFactory(FantasyFootballServer pServer) {
    fCommandHandlerById = new HashMap<NetCommandId,ServerCommandHandler>();
    register(new ServerCommandHandlerCloseGame(pServer));
    register(new ServerCommandHandlerDeleteGame(pServer));
    register(new ServerCommandHandlerFumbblGameChecked(pServer));
    register(new ServerCommandHandlerFumbblTeamLoaded(pServer));
    register(new ServerCommandHandlerJoin(pServer));
    register(new ServerCommandHandlerJoinApproved(pServer));
    register(new ServerCommandHandlerPasswordChallenge(pServer));
    register(new ServerCommandHandlerPing(pServer));
    register(new ServerCommandHandlerReplay(pServer));
    register(new ServerCommandHandlerReplayLoaded(pServer));
    register(new ServerCommandHandlerRequestVersion(pServer));
    register(new ServerCommandHandlerScheduleGame(pServer));
    register(new ServerCommandHandlerSetMarker(pServer));
    register(new ServerCommandHandlerSocketClosed(pServer));
    register(new ServerCommandHandlerTalk(pServer));
    register(new ServerCommandHandlerTimeout(pServer));
    register(new ServerCommandHandlerUploadGame(pServer));
    register(new ServerCommandHandlerUserSettings(pServer));
    register(new ServerCommandHandlerCloseSession(pServer));
  }
  
  public void handleCommand(ReceivedCommand pReceivedCommand) {
    ServerCommandHandler commandHandler = getCommandHandler(pReceivedCommand.getId());
    if (commandHandler != null) {
      commandHandler.handleCommand(pReceivedCommand);
    }
  }
  
  public ServerCommandHandler getCommandHandler(NetCommandId pType) {
    return fCommandHandlerById.get(pType);
  }

  private void register(ServerCommandHandler pCommandHandler) {
    fCommandHandlerById.put(pCommandHandler.getId(), pCommandHandler);
  }
  
}
