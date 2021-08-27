package com.fumbbl.ffb.server.step.generator.common;

import static com.fumbbl.ffb.server.step.StepParameter.from;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;

@RulesCollection(RulesCollection.Rules.COMMON)
public class SpikedBallApo extends SequenceGenerator<SequenceGenerator.SequenceParams> {

	public SpikedBallApo() {
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
