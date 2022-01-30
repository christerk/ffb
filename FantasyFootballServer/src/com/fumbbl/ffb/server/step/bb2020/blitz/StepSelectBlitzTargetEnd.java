package com.fumbbl.ffb.server.step.bb2020.blitz;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepSelectBlitzTargetEnd extends AbstractStep {

	private boolean endTurn;

	public StepSelectBlitzTargetEnd(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.SELECT_BLITZ_TARGET_END;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null && parameter.getKey() == StepParameterKey.END_TURN) {
			endTurn = parameter.getValue() != null && (boolean) parameter.getValue();
			return true;
		}

		return super.setParameter(parameter);
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
		if (endTurn) {
			EndPlayerAction endGenerator = (EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name());
			game.setDefenderId(null); // clear defender for next multi block
			endGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), true, true, endTurn));
		} else if (targetSelectionState != null) {
			if (targetSelectionState.isCanceled()) {
				UtilServerSteps.changePlayerAction(this, null, null, false);
				game.getFieldModel().setTargetSelectionState(null);
				((Select) factory.forName(SequenceGenerator.Type.Select.name()))
					.pushSequence(new Select.SequenceParams(getGameState(), false));
			} else if (targetSelectionState.isSelected()) {
				UtilServerSteps.changePlayerAction(this, game.getActingPlayer().getPlayerId(), PlayerAction.BLITZ_MOVE, false);
				((Select) factory.forName(SequenceGenerator.Type.Select.name()))
					.pushSequence(new Select.SequenceParams(getGameState(), false));
				game.getTurnData().setBlitzUsed(true);
				game.getActingPlayer().setHasMoved(true);
			} else if (targetSelectionState.isSkipped()) {
				UtilServerSteps.changePlayerAction(this, game.getActingPlayer().getPlayerId(), PlayerAction.BLITZ_MOVE, false);
				((Select) factory.forName(SequenceGenerator.Type.Select.name()))
					.pushSequence(new Select.SequenceParams(getGameState(), false));
				game.getTurnData().setBlitzUsed(true);
				game.getActingPlayer().setHasMoved(true);
				getResult().setSound(SoundId.CLICK);
			} else if (targetSelectionState.isFailed()) {
				Sequence sequence = new Sequence(getGameState());
				sequence.add(StepId.END_MOVING, StepParameter.from(StepParameterKey.END_PLAYER_ACTION, true));
				getGameState().getStepStack().push(sequence.getSequence());
			}
		}

		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		Boolean endTurnObject = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		endTurn = endTurnObject != null && endTurnObject;
		return this;
	}
}
