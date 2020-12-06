package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandCloseGame extends InternalServerCommand {

	public InternalServerCommandCloseGame(long pGameId) {
		super(pGameId);
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_CLOSE_GAME;
	}

}
