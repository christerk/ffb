package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandSocketClosed extends InternalServerCommand {

  public InternalServerCommandSocketClosed() {
    super();
  }

  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_SOCKET_CLOSED;
  }

}
