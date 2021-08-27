package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.server.net.ReceivedCommand;

/**
 * 
 * @author Kalimar
 */
public interface IReceivedCommandHandler {

	public boolean handleCommand(ReceivedCommand receivedCommand);

}
