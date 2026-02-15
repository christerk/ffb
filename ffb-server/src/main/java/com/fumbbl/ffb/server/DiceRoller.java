package com.fumbbl.ffb.server;

import com.fumbbl.ffb.BlockDiceCategory;
import com.fumbbl.ffb.DiceCategory;
import com.fumbbl.ffb.DiceCategoryFactory;
import com.fumbbl.ffb.DirectionDiceCategory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.util.rng.Fortuna;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Kalimar
 */
public class DiceRoller {

	private final GameState fGameState;
	private final Map<String, List<DiceCategory>> testRolls;



	public DiceRoller(GameState pGameState) {
		fGameState = pGameState;
		testRolls = new HashMap<>();
	}

	public GameState getGameState() {
		return fGameState;
	}

	public int rollDice(int pType) {
		Fortuna fortuna = getGameState().getServer().getFortuna();
		List<DiceCategory> testRollList = testRolls.get("General");
		if(testRollList != null){
			while (testRollList.size() > 0) {
				DiceCategory testRoll = testRollList.remove(0);
				if (testRoll.testRoll() <= pType) {
					return testRoll.testRoll();
				}
			}
		}
		return fortuna.getDieRoll(pType);
	}
	
	public int rollDice(DiceCategory category) {	
		
		List<DiceCategory> testRollList = testRolls.get(category.name());
		if(testRollList != null){
			while (testRollList.size() > 0) {
				DiceCategory testRoll = testRollList.remove(0);
				if (testRoll.testRoll() <= category.diceType()) {
					return testRoll.testRoll();
				}
			}
		}
		return rollDice(category.diceType());
	}

	private int[] rollDice(int pNumber, DiceCategory diceCat) {
		int[] result = new int[pNumber];
		for (int i = 0; i < pNumber; i++) {
			result[i] = rollDice(diceCat);
		}
		return result;
	}
	
	private int[] rollDice(int pNumber, int pType) {
		int[] result = new int[pNumber];
		for (int i = 0; i < pNumber; i++) {
			result[i] = rollDice(pType);
		}
		return result;
	}

	public int rollFanFactor() {
		return rollDice(3);
	}

	public int[] rollWeather() {
		return rollDice(2, 6);
	}

	public int rollSkill() {
		return rollDice(6);
	}

	public int rollCardEffect() {
		return rollDice(6);
	}

	public int[] rollTentaclesEscape() {
		return rollDice(2, 6);
	}

	public int[] rollShadowingEscape() {
		return rollDice(2, 6);
	}

	public int[] rollSecretWeapon() {
		return rollDice(2, 6);
	}

	public int rollWinnings() {
		return rollDice(6);
	}

	public int[] rollFanFactorPostMatch(boolean pWinningTeam) {
		if (pWinningTeam) {
			return rollDice(3, 6);
		} else {
			return rollDice(2, 6);
		}
	}

	public int rollApothecary() {
		return rollDice(6);
	}

	public int rollBribes() {
		return rollDice(6);
	}

	public int rollArgueTheCall() {
		return rollDice(6);
	}

	public int rollGoingForIt() {
		return rollDice(6);
	}

	public int rollDauntless() {
		return rollDice(6);
	}

	public int rollChainsaw() {
		return rollDice(6);
	}

	public int rollPenaltyShootout() {
		return rollDice(6);
	}

	public int rollWeepingDagger() {
		return rollDice(6);
	}

	public boolean throwCoin() {
		return (rollDice(2) == 1);
	}

	public int[] rollBlockDice(int pNrOfDice) {
		return rollDice(Math.abs(pNrOfDice), new BlockDiceCategory());
	}

	public int[] rollArmour() {
		return rollDice(2, 6);
	}

	public int[] rollSpectators() {
		return rollDice(2, 6);
	}

	public int rollExtraReRoll() {
		return rollDice(3);
	}

	public int rollRiot() {
		return rollDice(6);
	}

	public int rollThrowARock() {
		return rollDice(6);
	}

	public int rollPitchInvasion() {
		return rollDice(6);
	}

	public int rollWizardSpell() {
		return rollDice(6);
	}

	public int[] rollInjury() {
		return rollDice(2, 6);
	}

	public int rollKnockoutRecovery() {
		return rollDice(6);
	}

	public int[] rollCasualtyRenamed() {
		return new int[] { rollDice(6), rollDice(8) };
	}

	public int rollScatterDirection() {
		return rollDice(new DirectionDiceCategory());
	}

	public int rollThrowInDirection() {
		return rollDice(6);
	}

	public int rollCornerThrowInDirection() {
		return rollDice(3);
	}

	public int rollScatterDistance() {
		return rollDice(6);
	}

	public int rollKickScatterDistance() {
		return rollDice(3);
	}

	public int[] rollThrowInDistance() {
		return rollDice(2, 6);
	}

	public int[] rollKickoff() {
		return rollDice(2, 6);
	}

	public int rollPlayerLoss() {
		return rollDice(6);
	}

	public int[] rollMasterChef() {
		return rollDice(3, 6);
	}

	public int rollXCoordinate() {
		return rollDice(26) - 1;
	}

	public int[] rollRiotousRookies() {
		return new int[] { rollDice(3), rollDice(3) };
	}

	public int rollGender() {
		return rollDice(4);
	}

	public int rollSwarmingPlayers() {
		return rollDice(3);
	}

	public Player<?> randomPlayer(Player<?>[] pPlayers) {
		Player<?> randomPlayer = null;
		if (ArrayTool.isProvided(pPlayers)) {
			randomPlayer = pPlayers[rollDice(pPlayers.length) - 1];
		}
		return randomPlayer;
	}

	public String randomPlayerId(String[] playerIds) {
		String randomPlayerId = null;
		if (ArrayTool.isProvided(playerIds)) {
			randomPlayerId = playerIds[rollDice(playerIds.length) - 1];
		}
		return randomPlayerId;
	}

	public Card drawCard(CardDeck pDeck) {
		return pDeck.draw(rollDice(DiceCategoryFactory.forDiceSize(pDeck.size())) - 1);
	}
	
	public void addTestRoll(int roll) {
		List<DiceCategory> testRollList = testRolls.computeIfAbsent("General", s ->  new ArrayList<DiceCategory>());		
		testRollList.add(new DiceCategory(roll));
		testRolls.putIfAbsent("General", testRollList);
	}

	public void addTestRoll(DiceCategory category) {
		List<DiceCategory> testRollList = testRolls.computeIfAbsent(category.name(), s ->  new ArrayList<DiceCategory>());
		testRollList.add(category);
		testRolls.putIfAbsent(category.name(), testRollList);
	}
	
	public void addTestRoll(String command, Game game, Team team) {
		DiceCategory category = DiceCategoryFactory.forCommandString(command, game, team);
		if(category != null) {
			addTestRoll(category);
		}
	}

	public Map<String, List<DiceCategory>> getTestRolls() {
		return testRolls;
	}

	public void clearTestRolls() {
		testRolls.clear();
	}

}
