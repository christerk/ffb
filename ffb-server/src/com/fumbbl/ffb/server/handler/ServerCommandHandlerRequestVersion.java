package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.FantasyFootballConstants;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.net.ReceivedCommand;

import java.util.ArrayList;
import java.util.List;

/**
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
			FantasyFootballConstants.CLIENT_VERSION, clientProperties.toArray(new String[0]),
			clientPropertyValues.toArray(new String[0]), isServerInTestMode());
		return true;
	}

}
