package com.fumbbl.ffb.server.step;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.util.StringTool;

/**
 * Step in any sequence to jump to a given label.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepGotoLabel extends AbstractStep {

	private String fGotoLabel, alternateLabel;
	private boolean useAlternateLabel;

	public StepGotoLabel(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.GOTO_LABEL;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					case GOTO_LABEL:
						fGotoLabel = (String) parameter.getValue();
						break;
					case ALTERNATE_GOTO_LABEL:
						alternateLabel = (String) parameter.getValue();
						break;
					default:
						break;
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabel)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null && parameter.getKey() == StepParameterKey.USE_ALTERNATE_LABEL) {
			useAlternateLabel = (boolean) parameter.getValue();
			consume(parameter);
			return true;
		}
		return super.setParameter(parameter);
	}

	@Override
	public void start() {
		super.start();
		getResult().setNextAction(StepAction.GOTO_LABEL, useAlternateLabel ? alternateLabel : fGotoLabel);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL.addTo(jsonObject, fGotoLabel);
		IServerJsonOption.ALTERNATE_GOTO_LABEL.addTo(jsonObject, alternateLabel);
		IServerJsonOption.USE_ALTERNATE_LABEL.addTo(jsonObject, useAlternateLabel);
		return jsonObject;
	}

	@Override
	public StepGotoLabel initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabel = IServerJsonOption.GOTO_LABEL.getFrom(game, jsonObject);
		alternateLabel = IServerJsonOption.ALTERNATE_GOTO_LABEL.getFrom(game, jsonObject);
		Boolean alternateBoolean = IServerJsonOption.USE_ALTERNATE_LABEL.getFrom(game, jsonObject);
		useAlternateLabel = alternateBoolean != null && alternateBoolean;
		return this;
	}

}
