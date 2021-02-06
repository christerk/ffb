package com.balancedbytes.games.ffb.server.step.generator;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;

import static com.balancedbytes.games.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.COMMON)
public class SpikedBallApo extends SequenceGenerator<SequenceGenerator.SequenceParams> {

	protected SpikedBallApo() {
		super(Type.SpikedBallApo);
	}

	public void pushSequence(SequenceGenerator.SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push spikedBallApoSequence onto stack");

		Sequence sequence = new Sequence(gameState);
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CATCHER));
		gameState.getStepStack().push(sequence.getSequence());
	}

}
