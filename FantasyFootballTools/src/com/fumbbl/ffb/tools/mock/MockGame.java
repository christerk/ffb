package com.fumbbl.ffb.tools.mock;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameRules;

public class MockGame extends Game {

	private MockGameRules rules = new MockGameRules(null, null);

	public MockGame() {
		super(null, null);
	}

	@Override
	public GameRules getRules() {
		return rules;
	}
}

