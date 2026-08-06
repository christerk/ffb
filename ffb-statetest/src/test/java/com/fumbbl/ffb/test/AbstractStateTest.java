package com.fumbbl.ffb.test;

import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IDiceRoller;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractStateTest {

	protected TestServer testServer;
	protected GameState gameState;

	@BeforeEach
	void setUp() throws Exception {
		testServer = new TestServer();
		gameState = null;
	}

	@AfterEach
	void verifyAllRollsConsumed() {
		if (gameState != null) {
			IDiceRoller roller = gameState.getDiceRoller();
			if (roller instanceof TestDiceRoller) {
				TestDiceRoller testRoller = (TestDiceRoller) roller;
				if (!testRoller.allRollsConsumed()) {
					StringBuilder sb = new StringBuilder("Unconsumed test rolls:\n");
					for (TestDiceRoller.RollDescriptor desc : testRoller.getUnconsumedRolls()) {
						sb.append("  ").append(desc).append("\n");
					}
					throw new AssertionError(sb.toString());
				}
			}
		}
	}
}
