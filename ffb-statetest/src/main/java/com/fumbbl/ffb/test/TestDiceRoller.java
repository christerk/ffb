package com.fumbbl.ffb.test;

import com.fumbbl.ffb.DiceCategory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.CardDeck;
import com.fumbbl.ffb.server.IDiceRoller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDiceRoller implements IDiceRoller {

	private final List<RollDescriptor> expectedRolls = new ArrayList<>();
	private int rollIndex = 0;

	public void registerRoll(String type, int... values) {
		expectedRolls.add(new RollDescriptor(type, values));
		TestFortuna.markTestRollsQueued();
	}

	private int[] consumeNext(String expectedType) {
		if (rollIndex >= expectedRolls.size()) {
			throw new AssertionError("Test roll queue exhausted: no registered roll available for " + expectedType);
		}
		RollDescriptor desc = expectedRolls.get(rollIndex);
		if (!desc.type.equals(expectedType)) {
			throw new AssertionError("Roll type mismatch: expected [" + desc.type + "] at queue position " + rollIndex
				+ " but [" + expectedType + "] was requested");
		}
		rollIndex++;
		return desc.values;
	}

	public boolean allRollsConsumed() {
		return rollIndex >= expectedRolls.size();
	}

	public List<RollDescriptor> getUnconsumedRolls() {
		if (rollIndex >= expectedRolls.size()) {
			return Collections.emptyList();
		}
		return expectedRolls.subList(rollIndex, expectedRolls.size());
	}

	static class RollDescriptor {
		final String type;
		final int[] values;

		RollDescriptor(String type, int[] values) {
			this.type = type;
			this.values = values;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(type);
			sb.append(" [");
			for (int i = 0; i < values.length; i++) {
				if (i > 0) sb.append(", ");
				sb.append(values[i]);
			}
			sb.append("]");
			return sb.toString();
		}
	}

	@Override
	public int rollDice(int pType) {
		return consumeNext("general")[0];
	}

	@Override
	public int rollDice(DiceCategory category) {
		return consumeNext("general")[0];
	}

	@Override
	public int rollFanFactor() {
		return consumeNext("fanFactor")[0];
	}

	@Override
	public int[] rollWeather() {
		return consumeNext("weather");
	}

	@Override
	public int rollSkill() {
		return consumeNext("skill")[0];
	}

	@Override
	public int rollCardEffect() {
		return consumeNext("cardEffect")[0];
	}

	@Override
	public int[] rollTentaclesEscape() {
		return consumeNext("tentaclesEscape");
	}

	@Override
	public int[] rollShadowingEscape() {
		return consumeNext("shadowingEscape");
	}

	@Override
	public int[] rollSecretWeapon() {
		return consumeNext("secretWeapon");
	}

	@Override
	public int rollWinnings() {
		return consumeNext("winnings")[0];
	}

	@Override
	public int[] rollFanFactorPostMatch(boolean pWinningTeam) {
		return consumeNext("fanFactorPostMatch");
	}

	@Override
	public int rollApothecary() {
		return consumeNext("apothecary")[0];
	}

	@Override
	public int rollBribes() {
		return consumeNext("bribes")[0];
	}

	@Override
	public int rollArgueTheCall() {
		return consumeNext("argueTheCall")[0];
	}

	@Override
	public int rollGoingForIt() {
		return consumeNext("goingForIt")[0];
	}

	@Override
	public int rollDauntless() {
		return consumeNext("dauntless")[0];
	}

	@Override
	public int rollChainsaw() {
		return consumeNext("chainsaw")[0];
	}

	@Override
	public int rollPenaltyShootout() {
		return consumeNext("penaltyShootout")[0];
	}

	@Override
	public int rollWeepingDagger() {
		return consumeNext("weepingDagger")[0];
	}

	@Override
	public boolean throwCoin() {
		return consumeNext("throwCoin")[0] == 1;
	}

	@Override
	public int[] rollBlockDice(int pNrOfDice) {
		return consumeNext("block");
	}

	@Override
	public int[] rollArmour() {
		return consumeNext("armour");
	}

	@Override
	public int[] rollSpectators() {
		return consumeNext("spectators");
	}

	@Override
	public int rollExtraReRoll() {
		return consumeNext("extraReRoll")[0];
	}

	@Override
	public int rollRiot() {
		return consumeNext("riot")[0];
	}

	@Override
	public int rollThrowARock() {
		return consumeNext("throwARock")[0];
	}

	@Override
	public int rollPitchInvasion() {
		return consumeNext("pitchInvasion")[0];
	}

	@Override
	public int rollWizardSpell() {
		return consumeNext("wizardSpell")[0];
	}

	@Override
	public int[] rollInjury() {
		return consumeNext("injury");
	}

	@Override
	public int rollKnockoutRecovery() {
		return consumeNext("knockoutRecovery")[0];
	}

	@Override
	public int[] rollCasualtyRenamed() {
		return consumeNext("casualtyRenamed");
	}

	@Override
	public int rollScatterDirection() {
		return consumeNext("scatterDirection")[0];
	}

	@Override
	public int rollThrowInDirection() {
		return consumeNext("throwInDirection")[0];
	}

	@Override
	public int rollCornerThrowInDirection() {
		return consumeNext("cornerThrowInDirection")[0];
	}

	@Override
	public int rollScatterDistance() {
		return consumeNext("scatterDistance")[0];
	}

	@Override
	public int rollKickScatterDistance() {
		return consumeNext("kickScatterDistance")[0];
	}

	@Override
	public int[] rollThrowInDistance() {
		return consumeNext("throwInDistance");
	}

	@Override
	public int[] rollKickoff() {
		return consumeNext("kickoff");
	}

	@Override
	public int rollPlayerLoss() {
		return consumeNext("playerLoss")[0];
	}

	@Override
	public int[] rollMasterChef() {
		return consumeNext("masterChef");
	}

	@Override
	public int rollXCoordinate() {
		return consumeNext("xCoordinate")[0];
	}

	@Override
	public int[] rollRiotousRookies() {
		return consumeNext("riotousRookies");
	}

	@Override
	public int rollGender() {
		return consumeNext("gender")[0];
	}

	@Override
	public int rollSwarmingPlayers() {
		return consumeNext("swarmingPlayers")[0];
	}

	@Override
	public Player<?> randomPlayer(Player<?>[] pPlayers) {
		int value = consumeNext("general")[0];
		if (pPlayers == null || pPlayers.length == 0) {
			return null;
		}
		return pPlayers[value - 1];
	}

	@Override
	public String randomPlayerId(String[] playerIds) {
		int value = consumeNext("general")[0];
		if (playerIds == null || playerIds.length == 0) {
			return null;
		}
		return playerIds[value - 1];
	}

	@Override
	public Card drawCard(CardDeck pDeck) {
		int value = consumeNext("general")[0];
		return pDeck.draw(value - 1);
	}

	@Override
	public void addTestRoll(int roll) {
	}

	@Override
	public void addTestRoll(DiceCategory category) {
	}

	@Override
	public void addTestRoll(String command, Game game, Team team) {
	}

	@Override
	public Map<String, List<DiceCategory>> getTestRolls() {
		return new HashMap<>();
	}

	@Override
	public void clearTestRolls() {
	}
}
