package com.balancedbytes.games.ffb.server;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.fumbbl.rng.Fortuna;


/**
 * 
 * @author Kalimar
 */
public class DiceRoller {

  private GameState fGameState;
  private List<Integer> fTestRolls;

  public DiceRoller(GameState pGameState) {
    fGameState = pGameState;
    fTestRolls = new ArrayList<Integer>();
  }
  
  public GameState getGameState() {
    return fGameState;
  }

  private int rollDice(int pType) {
    Fortuna fortuna = getGameState().getServer().getFortuna(); 
    while (fTestRolls.size() > 0) {
      int testRoll = fTestRolls.remove(0).intValue();
      if (testRoll <= pType) {
        return testRoll;
      }
    }
    return fortuna.getDieRoll(pType);
  }

  private int[] rollDice(int pNumber, int pType) {
    int[] result = new int[pNumber];
    for (int i = 0; i < pNumber; i++) {
      result[i] = rollDice(pType);
    }
    return result;
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
  
  public int[] rollFanFactor(boolean pWinningTeam) {
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
  
  public boolean throwCoin() {
    return (rollDice(2) == 1);
  }

  public int[] rollBlockDice(int pNrOfDice) {
    return rollDice(Math.abs(pNrOfDice), 6);
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
  
  public int[] rollCasualty() {
    return new int[] { rollDice(6), rollDice(8) };
  }
  
  public int rollScatterDirection() {
    return rollDice(8);
  }
  
  public int rollThrowInDirection() {
    return rollDice(6);
  }

  public int rollScatterDistance() {
    return rollDice(6);
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
  
  public Player randomPlayer(Player[] pPlayers) {
    Player randomPlayer = null;
    if (ArrayTool.isProvided(pPlayers)) {
      randomPlayer = pPlayers[rollDice(pPlayers.length) - 1];
    }
    return randomPlayer;
  }
  
  public Card drawCard(CardDeck pDeck) {
  	return pDeck.draw(rollDice(pDeck.size()) - 1);
  }

  public void addTestRoll(int pRoll) {
    fTestRolls.add(pRoll);
  }
  
  public int[] getTestRolls() {
    int[] testRolls = new int[fTestRolls.size()];
    for (int i = 0; i < fTestRolls.size(); i++) {
      testRolls[i] = fTestRolls.get(i).intValue();
    }
    return testRolls;
  }
  
  public void clearTestRolls() {
    fTestRolls.clear();
  }
  
}
