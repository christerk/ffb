package com.fumbbl.ffb.server.step.bb2016;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.InducementPhase;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.bb2020.StepCheckStalling;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.EndTurn;
import com.fumbbl.ffb.server.step.generator.common.Inducement;
import com.fumbbl.ffb.server.util.UtilServerDialog;

/**
 * Last step in the inducement sequence. Consumes all expected stepParameters.
 * 
 * Expects stepParameter END_INDUCEMENT_PHASE to be set by a preceding step.
 * Expects stepParameter END_TURN to be set by a preceding step. Expects
 * stepParameter HOME_TEAM to be set by a preceding step. Expects stepParameter
 * INDUCEMENT_PHASE to be set by a preceding step.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public final class StepEndInducement extends AbstractStep {

	private boolean fEndInducementPhase;
	private boolean fEndTurn;
	private InducementPhase fInducementPhase;
	private boolean fHomeTeam;

	public StepEndInducement(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_INDUCEMENT;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
			case HOME_TEAM:
				fHomeTeam = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
				consume(parameter);
				return true;
			case INDUCEMENT_PHASE:
				fInducementPhase = (InducementPhase) parameter.getValue();
				consume(parameter);
				return true;
			case END_INDUCEMENT_PHASE:
				fEndInducementPhase = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
				consume(parameter);
				return true;
			case END_TURN:
				fEndTurn = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
				consume(parameter);
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
		UtilServerDialog.hideDialog(getGameState());
		if (fInducementPhase == null) {
			return;
		}
		fEndTurn |= UtilServerSteps.checkTouchdown(getGameState());
		SequenceGeneratorFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		EndTurn endTurnGenerator = ((EndTurn)factory.forName(SequenceGenerator.Type.EndTurn.name()));
		SequenceGenerator.SequenceParams endTurnParams = new SequenceGenerator.SequenceParams(getGameState());
		if (fEndTurn) {
			endTurnGenerator.pushSequence(endTurnParams);
		} else if (fEndInducementPhase) {
			switch (fInducementPhase) {
				case END_OF_OWN_TURN:
					endTurnGenerator.pushSequence(endTurnParams);
					break;
				case START_OF_OWN_TURN:
					((Select) factory.forName(SequenceGenerator.Type.Select.name())).pushSequence(new Select.SequenceParams(getGameState(), true));
					getGameState().getStepStack().push(new StepCheckStalling(getGameState()));
					break;
				default:
					break;
			}
		} else {
			((Inducement)factory.forName(SequenceGenerator.Type.Inducement.name())).pushSequence(new Inducement.SequenceParams(getGameState(), fInducementPhase, fHomeTeam));
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.INDUCEMENT_PHASE.addTo(jsonObject, fInducementPhase);
		IServerJsonOption.HOME_TEAM.addTo(jsonObject, fHomeTeam);
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		IServerJsonOption.END_INDUCEMENT_PHASE.addTo(jsonObject, fEndInducementPhase);
		return jsonObject;
	}

	@Override
	public StepEndInducement initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fInducementPhase = (InducementPhase) IServerJsonOption.INDUCEMENT_PHASE.getFrom(source, jsonObject);
		fHomeTeam = IServerJsonOption.HOME_TEAM.getFrom(source, jsonObject);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		fEndInducementPhase = IServerJsonOption.END_INDUCEMENT_PHASE.getFrom(source, jsonObject);
		return this;
	}

}
