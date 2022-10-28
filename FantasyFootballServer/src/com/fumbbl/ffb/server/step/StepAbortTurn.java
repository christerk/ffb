package com.fumbbl.ffb.server.step;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;

@RulesCollection(RulesCollection.Rules.COMMON)
public class StepAbortTurn extends AbstractStep {

	public StepAbortTurn(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.ABORT_TURN;
	}

	@Override
	public void start() {
		executeStep();
	}

	private void executeStep() {
		getGameState().getStepStack().clear();
		if (getGameState().getGame().getLastTurnMode() != null) {
			getGameState().getGame().setTurnMode(getGameState().getGame().getLastTurnMode());
		} else {
			getGameState().getGame().setTurnMode(TurnMode.REGULAR);
		}

		EndPlayerAction endActionGenerator = (EndPlayerAction) getGameState().getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR).forName(SequenceGenerator.Type.EndPlayerAction.name());
		endActionGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), false, true, true));

		getResult().setNextAction(StepAction.NEXT_STEP);
	}
}
