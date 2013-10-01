package com.balancedbytes.games.ffb.server.handler;

import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.net.INetCommandHandler;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerFactory implements INetCommandHandler {
  
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
  }
  
  public void handleNetCommand(NetCommand pNetCommand) {
    ServerCommandHandler commandHandler = getCommandHandler(pNetCommand.getId());
    if (commandHandler != null) {
      commandHandler.handleNetCommand(pNetCommand);
    }
  }
  
  public ServerCommandHandler getCommandHandler(NetCommandId pType) {
    return fCommandHandlerById.get(pType);
  }

  private void register(ServerCommandHandler pUiCommandHandler) {
    fCommandHandlerById.put(pUiCommandHandler.getId(), pUiCommandHandler);
  }
  
}
