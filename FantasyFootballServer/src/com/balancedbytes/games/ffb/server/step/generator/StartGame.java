package com.balancedbytes.games.ffb.server.step.generator;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.step.StepId;

@RulesCollection(RulesCollection.Rules.COMMON)
public class StartGame extends SequenceGenerator<SequenceGenerator.SequenceParams> {

	protected StartGame() {
		super(Type.StartGame);
	}

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push startGameSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.INIT_START_GAME);
		sequence.add(StepId.WEATHER);
		sequence.add(StepId.PETTY_CASH);
		sequence.add(StepId.BUY_CARDS);
		sequence.add(StepId.BUY_INDUCEMENTS);
		// inserts inducement sequence at this point
		sequence.add(StepId.SPECTATORS);
		// continues with kickoffSequence after that

		gameState.getStepStack().push(sequence.getSequence());

	}
}
