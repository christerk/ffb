package com.fumbbl.ffb.server.db;

import com.fumbbl.ffb.server.GameState;

/**
 * 
 * @author Kalimar
 */
public interface IDbUpdateWithGameState {

	public long getId();

	public GameState getGameState();

}
