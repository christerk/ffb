package com.fumbbl.ffb.test;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IDiceRoller;

public class TestGameState extends GameState {

	public TestGameState(FantasyFootballServer pServer) {
		super(pServer);
	}

	@Override
	protected IDiceRoller createDiceRoller() {
		return new TestDiceRoller();
	}
}
