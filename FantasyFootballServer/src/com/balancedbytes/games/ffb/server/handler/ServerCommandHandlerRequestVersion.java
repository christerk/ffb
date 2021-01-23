package com.balancedbytes.games.ffb.server.handler;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.FantasyFootballConstants;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerRequestVersion extends ServerCommandHandler {

	protected ServerCommandHandlerRequestVersion(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_REQUEST_VERSION;
	}

	public boolean handleCommand(ReceivedCommand pReceivedCommand) {
		String[] properties = getServer().getProperties();
		List<String> clientProperties = new ArrayList<>();
		List<String> clientPropertyValues = new ArrayList<>();
		for (String property : properties) {
			if (property.startsWith("client.")) {
				clientProperties.add(property);
				clientPropertyValues.add(getServer().getProperty(property));
			}
		}
		getServer().getCommunication().sendVersion(pReceivedCommand.getSession(), FantasyFootballConstants.SERVER_VERSION,
				FantasyFootballConstants.CLIENT_VERSION, clientProperties.toArray(new String[clientProperties.size()]),
				clientPropertyValues.toArray(new String[clientPropertyValues.size()]));
		return true;
	}

}
