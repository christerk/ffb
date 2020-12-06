package com.balancedbytes.games.ffb.client.handler;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.GameTitle;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandGameTime;

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
