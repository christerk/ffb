package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandLoadAutomaticPlayerMarkings;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestLoadPlayerMarkingsForGameVersion;

public class ServerCommandHandlerLoadAutomaticPlayerMarkings extends ServerCommandHandler {

	protected ServerCommandHandlerLoadAutomaticPlayerMarkings(FantasyFootballServer pServer) {
		super(pServer);
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_LOAD_AUTOMATIC_PLAYER_MARKINGS;
	}

	@Override
	public boolean handleCommand(ReceivedCommand receivedCommand) {
		ClientCommandLoadAutomaticPlayerMarkings clientCommandLoadAutomaticPlayerMarkings = (ClientCommandLoadAutomaticPlayerMarkings) receivedCommand.getCommand();
		getServer().getRequestProcessor().add(new FumbblRequestLoadPlayerMarkingsForGameVersion(clientCommandLoadAutomaticPlayerMarkings.getGame(),
			clientCommandLoadAutomaticPlayerMarkings.getIndex(), clientCommandLoadAutomaticPlayerMarkings.getCoach(), receivedCommand.getSession()));
		return true;
	}
}
