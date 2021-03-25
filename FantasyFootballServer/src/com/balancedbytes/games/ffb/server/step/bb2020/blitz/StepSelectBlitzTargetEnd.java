package com.balancedbytes.games.ffb.server.step.bb2020.blitz;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.model.BlitzState;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.factory.SequenceGeneratorFactory;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.step.generator.Sequence;
import com.balancedbytes.games.ffb.server.step.generator.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.generator.common.Select;

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
