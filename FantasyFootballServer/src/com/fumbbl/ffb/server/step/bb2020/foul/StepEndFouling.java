package com.fumbbl.ffb.server.step.bb2020.foul;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.Select;

/**
 * Final step of the foul sequence. Consumes all expected stepParameters.
 * 
 * Expects stepParameter END_PLAYER_ACTION to be set by a preceding step.
 * Expects stepParameter END_TURN to be set by a preceding step.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepEndFouling extends AbstractStep {

	private boolean fEndTurn;
	private boolean fEndPlayerAction;

	public StepEndFouling(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_FOULING;
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case END_PLAYER_ACTION:
				fEndPlayerAction = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				consume(pParameter);
				return true;
			case END_TURN:
				fEndTurn = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				consume(pParameter);
				return true;
			default:
				break;
			}
		}
		return false;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		ActingPlayer actingPlayer = getGameState().getGame().getActingPlayer();
		Player<?> player = actingPlayer.getPlayer();
		boolean isOnPitch = FieldCoordinateBounds.FIELD.isInBounds(getGameState().getGame().getFieldModel().getPlayerCoordinate(player));
		SequenceGeneratorFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);

		if (isOnPitch && player.hasSkillProperty(NamedProperties.canMoveAfterFoul)) {
			((Select) factory.forName(SequenceGenerator.Type.Select.name()))
				.pushSequence(new Select.SequenceParams(getGameState(), true));
			UtilServerSteps.changePlayerAction(this, player.getId(),
				PlayerAction.MOVE, false);
		} else {
			((EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name()))
				.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), true, true, fEndTurn));
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
		return jsonObject;
	}

	@Override
	public StepEndFouling initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(game, jsonObject);
		fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(game, jsonObject);
		return this;
	}

}
