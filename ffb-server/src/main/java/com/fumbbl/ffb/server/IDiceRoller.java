package com.fumbbl.ffb.server;

import com.fumbbl.ffb.DiceCategory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;

import java.util.List;
import java.util.Map;

public interface IDiceRoller {

	int rollDice(int pType);

	int rollDice(DiceCategory category);

	int rollFanFactor();

	int[] rollWeather();

	int rollSkill();

	int rollCardEffect();

	int[] rollTentaclesEscape();

	int[] rollShadowingEscape();

	int[] rollSecretWeapon();

	int rollWinnings();

	int[] rollFanFactorPostMatch(boolean pWinningTeam);

	int rollApothecary();

	int rollBribes();

	int rollArgueTheCall();

	int rollGoingForIt();

	int rollDauntless();

	int rollChainsaw();

	int rollPenaltyShootout();

	int rollWeepingDagger();

	boolean throwCoin();

	int[] rollBlockDice(int pNrOfDice);

	int[] rollArmour();

	int[] rollSpectators();

	int rollExtraReRoll();

	int rollRiot();

	int rollThrowARock();

	int rollPitchInvasion();

	int rollWizardSpell();

	int[] rollInjury();

	int rollKnockoutRecovery();

	int[] rollCasualtyRenamed();

	int rollScatterDirection();

	int rollThrowInDirection();

	int rollCornerThrowInDirection();

	int rollScatterDistance();

	int rollKickScatterDistance();

	int[] rollThrowInDistance();

	int[] rollKickoff();

	int rollPlayerLoss();

	int[] rollMasterChef();

	int rollXCoordinate();

	int[] rollRiotousRookies();

	int rollGender();

	int rollSwarmingPlayers();

	Player<?> randomPlayer(Player<?>[] pPlayers);

	String randomPlayerId(String[] playerIds);

	Card drawCard(CardDeck pDeck);

	void addTestRoll(int roll);

	void addTestRoll(DiceCategory category);

	void addTestRoll(String command, Game game, Team team);

	Map<String, List<DiceCategory>> getTestRolls();

	void clearTestRolls();

}
