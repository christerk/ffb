package com.balancedbytes.games.ffb.server.step.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.dialog.DialogSelectBlitzTargetParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepSelectBlitzTarget extends AbstractStep {

	private String gotoLabelOnEnd;
	private String selectedPlayerId;

	public StepSelectBlitzTarget(GameState pGameState) {
		super(pGameState);
	}

	public StepSelectBlitzTarget(GameState pGameState, StepAction defaultStepResult) {
		super(pGameState, defaultStepResult);
	}

	@Override
	public StepId getId() {
		return StepId.SELECT_BLITZ_TARGET;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			super.init(pParameterSet);
			for (StepParameter parameter: pParameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_END) {
					gotoLabelOnEnd = (String) parameter.getValue();
				}
			}
		}
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		if (selectedPlayerId == null) {
			UtilServerDialog.showDialog(getGameState(), new DialogSelectBlitzTargetParameter(), true);
		} else if (selectedPlayerId.equals(getGameState().getGame().getActingPlayer().getPlayerId())) {
			
		} else {

		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, gotoLabelOnEnd);
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, selectedPlayerId);
		return jsonObject;
	}

	@Override
	public StepSelectBlitzTarget initFrom(IFactorySource source, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		super.initFrom(source, jsonObject);
		gotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		selectedPlayerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}
}
