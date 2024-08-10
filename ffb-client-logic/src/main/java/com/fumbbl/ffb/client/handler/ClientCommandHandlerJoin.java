package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.GameTitle;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandJoin;
import com.fumbbl.ffb.util.ArrayTool;

public class ClientCommandHandlerJoin extends ClientCommandHandler {

	protected ClientCommandHandlerJoin(FantasyFootballClient pClient) {
		super(pClient);
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_JOIN;
	}

	public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {

		if (pMode == ClientCommandHandlerMode.QUEUING) {
			return true;
		}

		ServerCommandJoin joinCommand = (ServerCommandJoin) pNetCommand;
		UserInterface userInterface = getClient().getUserInterface();

		if (ClientMode.PLAYER == joinCommand.getClientMode()) {
			getClient().getClientData().setTurnTimerStopped(false);
		}

		String coachName = getClient().getParameters().getCoach();
		String[] players = joinCommand.getPlayerNames();
		if (ArrayTool.isProvided(players) && (players.length > 1)) {

			String homeCoach;
			String awayCoach;
			if (players[1].equals(coachName)) {
				homeCoach = players[1];
				awayCoach = players[0];
			} else {
				homeCoach = players[0];
				awayCoach = players[1];
			}

			GameTitle gameTitle = new GameTitle();
			gameTitle.setClientMode(getClient().getMode());
			gameTitle.setHomeCoach(homeCoach);
			gameTitle.setAwayCoach(awayCoach);
			updateGameTitle(gameTitle);

		}

		getClient().getClientData().setSpectatorCount(joinCommand.getSpectatorCount());
		getClient().getClientData().setSpectators(joinCommand.getSpectators());

		if (pMode != ClientCommandHandlerMode.REPLAYING && coachName != null && !coachName.equals(joinCommand.getCoach())) {

			userInterface.getLog().markCommandBegin(joinCommand.getCommandNr());
			userInterface.getStatusReport().reportJoin(joinCommand);
			userInterface.getLog().markCommandEnd(joinCommand.getCommandNr());
			refreshSideBars();
		}

		return true;

	}

}
