package com.balancedbytes.games.ffb.server.step.generator;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.InducementPhase;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;

import static com.balancedbytes.games.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.COMMON)
public class Inducement extends SequenceGenerator<Inducement.SequenceParams> {

	protected Inducement() {
		super(Type.Inducement);
	}

	@Override
	public void pushSequence(SequenceParams params) {

		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push inducementSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.INIT_INDUCEMENT, from(StepParameterKey.INDUCEMENT_PHASE, params.inducementPhase),
			from(StepParameterKey.HOME_TEAM, params.homeTeam));
		// may insert wizard or card sequence at this point
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ATTACKER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_INDUCEMENT);
		// may insert endTurn or inducement sequence at this point

		gameState.getStepStack().push(sequence.getSequence());
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final InducementPhase inducementPhase;
		private final boolean homeTeam;

		public SequenceParams(GameState gameState, InducementPhase inducementPhase, boolean homeTeam) {
			super(gameState);
			this.inducementPhase = inducementPhase;
			this.homeTeam = homeTeam;
		}
	}
}
