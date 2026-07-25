package com.fumbbl.ffb.test;

import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;

public class TestRolls {

	private final GameState gameState;
	private final Team team;

	private TestRolls(GameState gameState) {
		this.gameState = gameState;
		this.team = gameState.getGame().getTeamHome();
	}

	public static TestRolls on(GameState gameState) {
		return new TestRolls(gameState);
	}

	public TestRolls block(String... values) {
		for (String value : values) {
			gameState.getDiceRoller().addTestRoll(value, gameState.getGame(), team);
		}
		return this;
	}

	public TestRolls armor(int d1, int d2) {
		general(d1, d2);
		return this;
	}

	public TestRolls injury(int d1, int d2) {
		general(d1, d2);
		return this;
	}

	public TestRolls casualty(int value) {
		gameState.getDiceRoller().addTestRoll(String.valueOf(value), gameState.getGame(), team);
		return this;
	}

	public TestRolls general(int... values) {
		for (int value : values) {
			gameState.getDiceRoller().addTestRoll(String.valueOf(value), gameState.getGame(), team);
		}
		return this;
	}
}