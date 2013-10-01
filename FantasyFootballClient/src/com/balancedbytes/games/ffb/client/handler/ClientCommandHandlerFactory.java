package com.balancedbytes.games.ffb.client.handler;

import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.state.ClientState;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandHandlerFactory {
  
  private FantasyFootballClient fClient;
  
  private Map<NetCommandId,ClientCommandHandler> fCommandHandlerById;
  
  public ClientCommandHandlerFactory(FantasyFootballClient pClient) {
    fClient = pClient;
    fCommandHandlerById = new HashMap<NetCommandId,ClientCommandHandler>();
    register(new ClientCommandHandlerJoin(getClient()));
    register(new ClientCommandHandlerLeave(getClient()));
    register(new ClientCommandHandlerTalk(getClient()));
    register(new ClientCommandHandlerGameState(getClient()));
    register(new ClientCommandHandlerSound(getClient()));
    register(new ClientCommandHandlerPing(getClient()));
    register(new ClientCommandHandlerUserSettings(getClient()));
    register(new ClientCommandHandlerAdminMessage(getClient()));
    register(new ClientCommandHandlerModelSync(getClient()));
    register(new ClientCommandHandlerSocketClosed(getClient()));
    register(new ClientCommandHandlerAddPlayer(getClient()));
    register(new ClientCommandHandlerRemovePlayer(getClient()));
  }
  
  public void handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {
    if (pNetCommand != null) {
      ClientCommandHandler commandHandler = getCommandHandler(pNetCommand.getId());
      if (commandHandler != null) {
        if (NetCommandId.SERVER_PING != pNetCommand.getId()) {
          boolean completed = commandHandler.handleNetCommand(pNetCommand, pMode);
          if (completed) {
            updateClientState(pNetCommand, pMode, false);
          } else {
          	if (pMode == ClientCommandHandlerMode.PLAYING) {
              synchronized (this) {
                try {
                  wait();
                } catch (InterruptedException ie) {
                }
              }
          	}
          }
        }
      } else {
        updateClientState(pNetCommand, pMode, false);
      }
    }
  }

  public void updateClientState(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {
    updateClientState(pNetCommand, pMode, true);
  }
  
  private void updateClientState(NetCommand pNetCommand, ClientCommandHandlerMode pMode, boolean pNotify) {
    ClientState clientState = getClient().updateClientState();
    if (clientState != null) {
      clientState.handleNetCommand(pNetCommand);
    }
    if (pNotify) {
      synchronized (this) {
        notifyAll();
      }
    }
  }
  
  public ClientCommandHandler getCommandHandler(NetCommandId pType) {
    return fCommandHandlerById.get(pType);
  }

  private void register(ClientCommandHandler pCommandHandler) {
    fCommandHandlerById.put(pCommandHandler.getId(), pCommandHandler);
  }
  
  public FantasyFootballClient getClient() {
    return fClient;
  }
  
}
