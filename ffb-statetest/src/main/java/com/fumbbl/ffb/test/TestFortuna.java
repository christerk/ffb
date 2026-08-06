package com.fumbbl.ffb.test;

import com.fumbbl.ffb.server.util.rng.Fortuna;

public class TestFortuna extends Fortuna {

	private static volatile boolean testRollsExhausted;

	public static void markTestRollsQueued() {
		testRollsExhausted = true;
	}

	public static void clearTestRollsFlag() {
		testRollsExhausted = false;
	}

	public static boolean isTestRollsExhausted() {
		return testRollsExhausted;
	}

	@Override
	public int getDieRoll(int sides) {
		if (testRollsExhausted) {
			throw new AssertionError("Test roll queue exhausted, no pre-declared roll available for a d" + sides + " roll");
		}
		return super.getDieRoll(sides);
	}
}
