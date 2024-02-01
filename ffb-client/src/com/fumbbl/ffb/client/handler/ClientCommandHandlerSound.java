package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandSound;

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
