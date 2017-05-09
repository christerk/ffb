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
  
  public ServerCommandHandlerFactory(FantasyFootballServer server) {
    fCommandHandlerById = new HashMap<NetCommandId,ServerCommandHandler>();
    register(new ServerCommandHandlerCloseGame(server));
    register(new ServerCommandHandlerDeleteGame(server));
    register(new ServerCommandHandlerFumbblGameChecked(server));
    register(new ServerCommandHandlerFumbblTeamLoaded(server));
    register(new ServerCommandHandlerJoin(server));
    register(new ServerCommandHandlerJoinApproved(server));
    register(new ServerCommandHandlerPasswordChallenge(server));
    register(new ServerCommandHandlerReplay(server));
    register(new ServerCommandHandlerReplayLoaded(server));
    register(new ServerCommandHandlerRequestVersion(server));
    register(new ServerCommandHandlerScheduleGame(server));
    register(new ServerCommandHandlerSetMarker(server));
    register(new ServerCommandHandlerSocketClosed(server));
    register(new ServerCommandHandlerTalk(server));
    register(new ServerCommandHandlerUploadGame(server));
    register(new ServerCommandHandlerUserSettings(server));
    register(new ServerCommandHandlerCloseSession(server));
    register(new ServerCommandHandlerPing(server));
  }
  
  public void handleCommand(ReceivedCommand receivedCommand) {
    ServerCommandHandler commandHandler = getCommandHandler(receivedCommand.getId());
    if (commandHandler != null) {
      commandHandler.handleCommand(receivedCommand);
    }
  }
  
  public ServerCommandHandler getCommandHandler(NetCommandId commandId) {
    return fCommandHandlerById.get(commandId);
  }

  private void register(ServerCommandHandler commandHandler) {
    fCommandHandlerById.put(commandHandler.getId(), commandHandler);
  }
  
}
