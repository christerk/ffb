package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.server.DiceRoller;

/**
 * Dice roller used to evaluate a node: armour and injury dice are the node's dice, casualty dice come from the pool.
 */
public class PooledDiceRoller extends DiceRoller {

	private final InjuryDicePool pool;
	private final DiceRoller diceRoller;
	private final int[] armourDice;
	private final int[] injuryDice;
	private int casualtyIndex;

	public PooledDiceRoller(DiceRoller diceRoller, InjuryDicePool pool, int[] armourDice, int[] injuryDice) {
		super(diceRoller.getGameState());
		this.diceRoller = diceRoller;
		this.pool = pool;
		this.armourDice = armourDice;
		this.injuryDice = injuryDice;
	}

	@Override
	public int[] rollArmour() {
		return armourDice != null ? armourDice.clone() : pool.armour(diceRoller);
	}

	@Override
	public int[] rollInjury() {
		return injuryDice != null ? injuryDice.clone() : pool.injury(diceRoller);
	}

	@Override
	public int rollDice(int type) {
		return pool.casualtyDie(casualtyIndex++, type, diceRoller);
	}
}
