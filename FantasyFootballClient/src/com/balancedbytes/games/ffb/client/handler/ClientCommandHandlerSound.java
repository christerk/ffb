package com.balancedbytes.games.ffb.client.handler;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandSound;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandHandlerSound extends ClientCommandHandler {

	protected ClientCommandHandlerSound(FantasyFootballClient pClient) {
		super(pClient);
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_SOUND;
	}

	public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {
		ServerCommandSound soundCommand = (ServerCommandSound) pNetCommand;
		playSound(soundCommand.getSound(), pMode, false);
		return true;
	}

}
