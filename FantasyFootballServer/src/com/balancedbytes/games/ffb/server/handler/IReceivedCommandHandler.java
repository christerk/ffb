package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.server.net.ReceivedCommand;

/**
 * 
 * @author Kalimar
 */
public interface IReceivedCommandHandler {

	public boolean handleCommand(ReceivedCommand receivedCommand);

}
