package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.GameTitle;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandGameTime;

public class ClientCommandHandlerGameTime extends ClientCommandHandler {

	protected ClientCommandHandlerGameTime(FantasyFootballClient pClient) {
		super(pClient);
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_GAME_TIME;
	}

	public boolean handleNetCommand(NetCommand netCommand, ClientCommandHandlerMode mode) {

		ServerCommandGameTime gameTimeCommand = (ServerCommandGameTime) netCommand;

		GameTitle gameTitle = new GameTitle();
		gameTitle.setGameTime(gameTimeCommand.getGameTime());
		gameTitle.setTurnTime(gameTimeCommand.getTurnTime());
		updateGameTitle(gameTitle);

		return true;

	}

}
