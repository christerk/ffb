package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;

public class ModificationParams {
	private final GameState gameState;
	private final DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();
	private final ModifiedInjuryContext newContext;

	private final InjuryType injuryType;

	public ModificationParams(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType) {
		this.gameState = gameState;
		this.newContext = newContext;
		this.injuryType = injuryType;
	}

	public GameState getGameState() {
		return gameState;
	}

	public DiceInterpreter getDiceInterpreter() {
		return diceInterpreter;
	}

	public ModifiedInjuryContext getNewContext() {
		return newContext;
	}

	public InjuryType getInjuryType() {
		return injuryType;
	}
}
