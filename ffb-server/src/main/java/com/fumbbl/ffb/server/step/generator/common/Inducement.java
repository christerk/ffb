package com.fumbbl.ffb.server.step.generator.common;

import static com.fumbbl.ffb.server.step.StepParameter.from;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.InducementPhase;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;

@RulesCollection(RulesCollection.Rules.COMMON)
public class Inducement extends SequenceGenerator<Inducement.SequenceParams> {

	public Inducement() {
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
		sequence.add(StepId.END_INDUCEMENT, from(StepParameterKey.CHECK_FORGO, params.checkForgo));
		// may insert endTurn or inducement sequence at this point

		gameState.getStepStack().push(sequence.getSequence());
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final InducementPhase inducementPhase;
		private final boolean homeTeam, checkForgo;

		public SequenceParams(GameState gameState, InducementPhase inducementPhase, boolean homeTeam) {
			this(gameState, inducementPhase, homeTeam, false);
		}

		public SequenceParams(GameState gameState, InducementPhase inducementPhase, boolean homeTeam, boolean checkForgo) {
			super(gameState);
			this.inducementPhase = inducementPhase;
			this.homeTeam = homeTeam;
			this.checkForgo = checkForgo;
		}
	}
}
