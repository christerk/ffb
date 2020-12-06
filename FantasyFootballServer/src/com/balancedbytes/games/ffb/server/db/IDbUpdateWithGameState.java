package com.balancedbytes.games.ffb.server.db;

import com.balancedbytes.games.ffb.server.GameState;

/**
 * 
 * @author Kalimar
 */
public interface IDbUpdateWithGameState {

	public long getId();

	public GameState getGameState();

}
