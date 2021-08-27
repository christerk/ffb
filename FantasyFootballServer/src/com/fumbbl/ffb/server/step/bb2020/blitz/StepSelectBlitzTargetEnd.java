package com.fumbbl.ffb.server.step.bb2020.blitz;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.model.BlitzState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.Select;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepSelectBlitzTargetEnd extends AbstractStep {
	public StepSelectBlitzTargetEnd(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.SELECT_BLITZ_TARGET_END;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		BlitzState blitzState = game.getFieldModel().getBlitzState();
		if (blitzState != null) {
			if (blitzState.isCanceled()) {
				UtilServerSteps.changePlayerAction(this, null, null, false);
				game.getFieldModel().setBlitzState(null);
				SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
				((Select) factory.forName(SequenceGenerator.Type.Select.name()))
					.pushSequence(new Select.SequenceParams(getGameState(), false));
			} else if (blitzState.isSelected()) {
				UtilServerSteps.changePlayerAction(this, game.getActingPlayer().getPlayerId(), PlayerAction.BLITZ_MOVE, false);
				SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
				((Select) factory.forName(SequenceGenerator.Type.Select.name()))
					.pushSequence(new Select.SequenceParams(getGameState(), false));
				game.getTurnData().setBlitzUsed(true);
				game.getActingPlayer().setHasMoved(true);
			} else if (blitzState.isSkipped()) {
				UtilServerSteps.changePlayerAction(this, game.getActingPlayer().getPlayerId(), PlayerAction.BLITZ_MOVE, false);
				SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
				((Select) factory.forName(SequenceGenerator.Type.Select.name()))
					.pushSequence(new Select.SequenceParams(getGameState(), false));
				getResult().setSound(SoundId.CLICK);
			} else if (blitzState.isFailed()) {
				Sequence sequence = new Sequence(getGameState());
				sequence.add(StepId.END_MOVING, StepParameter.from(StepParameterKey.END_PLAYER_ACTION, true));
				getGameState().getStepStack().push(sequence.getSequence());
			}
		}

		getResult().setNextAction(StepAction.NEXT_STEP);
	}
}
