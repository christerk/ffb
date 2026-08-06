package com.fumbbl.ffb.test;

import com.fumbbl.ffb.DiceCategory;
import com.fumbbl.ffb.DiceCategoryFactory;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IDiceRoller;

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

	private TestDiceRoller getTestDiceRoller() {
		IDiceRoller roller = gameState.getDiceRoller();
		if (roller instanceof TestDiceRoller) {
			return (TestDiceRoller) roller;
		}
		throw new IllegalStateException("TestRolls requires a TestDiceRoller. Use TestServer.getGameState() to create the GameState.");
	}

	private int parseCommand(String value) {
		DiceCategory cat = DiceCategoryFactory.forCommandString(value, gameState.getGame(), team);
		if (cat == null) {
			throw new IllegalArgumentException("Unknown dice roll command: '" + value + "'");
		}
		return cat.testRoll();
	}

	public TestRolls general(String label, int... values) {
		for (int value : values) {
			TestFortuna.markTestRollsQueued();
			getTestDiceRoller().registerRoll("general", value);
		}
		return this;
	}

	public TestRolls block(String... values) {
		TestFortuna.markTestRollsQueued();
		int[] parsed = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			parsed[i] = parseCommand(values[i]);
		}
		getTestDiceRoller().registerRoll("block", parsed);
		return this;
	}

	public TestRolls armour(int d1, int d2) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("armour", d1, d2);
		return this;
	}

	public TestRolls injury(int d1, int d2) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("injury", d1, d2);
		return this;
	}

	public TestRolls casualty(int d1, int d2) {
		TestFortuna.markTestRollsQueued();
		String rulesVersion = gameState.getGame().getOptions().getOptionWithDefault(GameOptionId.RULESVERSION).getValueAsString();
		if ("BB2016".equals(rulesVersion)) {
			getTestDiceRoller().registerRoll("casualtyRenamed", d1, d2);
		} else {
			getTestDiceRoller().registerRoll("general", d1);
			getTestDiceRoller().registerRoll("general", d2);
		}
		return this;
	}

	public TestRolls skill(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("skill", value);
		return this;
	}

	public TestRolls goingForIt(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("goingForIt", value);
		return this;
	}

	public TestRolls fanFactor(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("fanFactor", value);
		return this;
	}

	public TestRolls cardEffect(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("cardEffect", value);
		return this;
	}

	public TestRolls winnings(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("winnings", value);
		return this;
	}

	public TestRolls apothecary(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("apothecary", value);
		return this;
	}

	public TestRolls bribes(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("bribes", value);
		return this;
	}

	public TestRolls argueTheCall(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("argueTheCall", value);
		return this;
	}

	public TestRolls dauntless(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("dauntless", value);
		return this;
	}

	public TestRolls chainsaw(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("chainsaw", value);
		return this;
	}

	public TestRolls penaltyShootout(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("penaltyShootout", value);
		return this;
	}

	public TestRolls weepingDagger(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("weepingDagger", value);
		return this;
	}

	public TestRolls throwCoin(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("throwCoin", value);
		return this;
	}

	public TestRolls extraReRoll(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("extraReRoll", value);
		return this;
	}

	public TestRolls riot(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("riot", value);
		return this;
	}

	public TestRolls throwARock(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("throwARock", value);
		return this;
	}

	public TestRolls pitchInvasion(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("pitchInvasion", value);
		return this;
	}

	public TestRolls wizardSpell(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("wizardSpell", value);
		return this;
	}

	public TestRolls knockoutRecovery(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("knockoutRecovery", value);
		return this;
	}

	public TestRolls scatterDirection(String... values) {
		TestFortuna.markTestRollsQueued();
		for (String value : values) {
			getTestDiceRoller().registerRoll("scatterDirection", parseCommand(value));
		}
		return this;
	}

	public TestRolls scatterDirection(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("scatterDirection", value);
		return this;
	}

	public TestRolls scatterDistance(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("scatterDistance", value);
		return this;
	}

	public TestRolls throwInDirection(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("throwInDirection", value);
		return this;
	}

	public TestRolls cornerThrowInDirection(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("cornerThrowInDirection", value);
		return this;
	}

	public TestRolls kickScatterDistance(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("kickScatterDistance", value);
		return this;
	}

	public TestRolls playerLoss(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("playerLoss", value);
		return this;
	}

	public TestRolls xCoordinate(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("xCoordinate", value);
		return this;
	}

	public TestRolls gender(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("gender", value);
		return this;
	}

	public TestRolls swarmingPlayers(int value) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("swarmingPlayers", value);
		return this;
	}

	public TestRolls weather(int d1, int d2) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("weather", d1, d2);
		return this;
	}

	public TestRolls kickoff(int d1, int d2) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("kickoff", d1, d2);
		return this;
	}

	public TestRolls secretWeapon(int d1, int d2) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("secretWeapon", d1, d2);
		return this;
	}

	public TestRolls tentaclesEscape(int d1, int d2) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("tentaclesEscape", d1, d2);
		return this;
	}

	public TestRolls shadowingEscape(int d1, int d2) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("shadowingEscape", d1, d2);
		return this;
	}

	public TestRolls spectators(int d1, int d2) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("spectators", d1, d2);
		return this;
	}

	public TestRolls throwInDistance(int d1, int d2) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("throwInDistance", d1, d2);
		return this;
	}

	public TestRolls fanFactorPostMatch(int... values) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("fanFactorPostMatch", values);
		return this;
	}

	public TestRolls masterChef(int d1, int d2, int d3) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("masterChef", d1, d2, d3);
		return this;
	}

	public TestRolls riotousRookies(int d1, int d2) {
		TestFortuna.markTestRollsQueued();
		getTestDiceRoller().registerRoll("riotousRookies", d1, d2);
		return this;
	}
}
