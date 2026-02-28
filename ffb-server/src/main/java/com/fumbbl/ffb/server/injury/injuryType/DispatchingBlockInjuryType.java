package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;

public abstract class DispatchingBlockInjuryType<T extends InjuryType> extends ModificationAwareInjuryTypeServer<T> {

	protected final BlockInjuryEvaluator evaluator;

	public DispatchingBlockInjuryType(T injuryType, BlockInjuryEvaluator.Mode mode, boolean allowAttackerChainsaw) {
		super(injuryType);
		evaluator = new BlockInjuryEvaluator(mode, allowAttackerChainsaw);
	}

	@Override
	protected void injuryRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker,
		Player<?> pDefender, InjuryContext injuryContext) {
		evaluator.injuryRoll(game, gameState, diceRoller, pAttacker, pDefender, injuryContext, this);
	}

	@Override
	protected void armourRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker,
		Player<?> pDefender, DiceInterpreter diceInterpreter, InjuryContext injuryContext, boolean roll) {

		evaluator.armourRoll(game, gameState, diceRoller, pAttacker, pDefender, diceInterpreter, injuryContext, roll, this);

	}
}
