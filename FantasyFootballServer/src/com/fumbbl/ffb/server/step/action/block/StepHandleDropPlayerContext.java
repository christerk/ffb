package com.fumbbl.ffb.server.step.action.block;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.model.DropPlayerContext;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.util.StringTool;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepHandleDropPlayerContext extends AbstractStepWithReRoll {

	private DropPlayerContext dropPlayerContext;

	public StepHandleDropPlayerContext(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.HANDLE_DROP_PLAYER_CONTEXT;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null && parameter.getKey() == StepParameterKey.DROP_PLAYER_CONTEXT) {
			dropPlayerContext = (DropPlayerContext) parameter.getValue();
			consume(parameter);
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
		getResult().setNextAction(StepAction.NEXT_STEP);
		if (dropPlayerContext != null && dropPlayerContext.getInjuryResult() != null) {

			if (dropPlayerContext.getInjuryResult().injuryContext().isArmorBroken()) {
				Game game = getGameState().getGame();
				publishParameters(UtilServerInjury.dropPlayer(this, game.getPlayerById(dropPlayerContext.getPlayerId()),
					dropPlayerContext.getApothecaryMode(), dropPlayerContext.isEligibleForSafePairOfHands()));
				if (dropPlayerContext.isEndTurn()) {
					publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
				}
			}
			publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, dropPlayerContext.getInjuryResult()));
			if (StringTool.isProvided(dropPlayerContext.getLabel())) {
				getResult().setNextAction(StepAction.GOTO_LABEL, dropPlayerContext.getLabel());
			}
		}
	}


	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		if (dropPlayerContext != null) {
			IServerJsonOption.DROP_PLAYER_CONTEXT.addTo(jsonObject, dropPlayerContext.toJsonValue());
		}
		return jsonObject;
	}

	@Override
	public StepHandleDropPlayerContext initFrom(IFactorySource source, JsonValue pJsonValue) {
		StepHandleDropPlayerContext step = (StepHandleDropPlayerContext) super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		if (IServerJsonOption.DROP_PLAYER_CONTEXT.isDefinedIn(jsonObject)) {
			dropPlayerContext = new DropPlayerContext().initFrom(source, IServerJsonOption.DROP_PLAYER_CONTEXT.getFrom(source, jsonObject));
		}
		return step;
	}
}
