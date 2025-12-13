package com.fumbbl.ffb.server.step.generator.bb2025;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ThrowARock extends SequenceGenerator<ThrowARock.SequenceParams> {
	public ThrowARock() {
		super(Type.ThrowARock);
	}

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push throw a rock onto stack");

		Sequence sequence = new Sequence(gameState);
		sequence.add(StepId.THROW_A_ROCK, StepParameter.from(StepParameterKey.HOME_TEAM, params.homeTeam));
		sequence.add(StepId.STEADY_FOOTING, StepParameter.from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		sequence.add(StepId.HANDLE_DROP_PLAYER_CONTEXT);
		sequence.add(StepId.PLACE_BALL);
		sequence.add(StepId.APOTHECARY, StepParameter.from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);

		gameState.getStepStack().push(sequence.getSequence());

	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {

		private final boolean homeTeam;

		public SequenceParams(GameState gameState, boolean homeTeam) {
			super(gameState);
			this.homeTeam = homeTeam;
		}
	}
}
