package com.fumbbl.ffb.server.step.phase.inducement;

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
@RulesCollection(RulesCollection.Rules.COMMON)
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
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case HOME_TEAM:
				fHomeTeam = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				consume(pParameter);
				return true;
			case INDUCEMENT_PHASE:
				fInducementPhase = (InducementPhase) pParameter.getValue();
				consume(pParameter);
				return true;
			case END_INDUCEMENT_PHASE:
				fEndInducementPhase = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
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
	public StepEndInducement initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fInducementPhase = (InducementPhase) IServerJsonOption.INDUCEMENT_PHASE.getFrom(game, jsonObject);
		fHomeTeam = IServerJsonOption.HOME_TEAM.getFrom(game, jsonObject);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(game, jsonObject);
		fEndInducementPhase = IServerJsonOption.END_INDUCEMENT_PHASE.getFrom(game, jsonObject);
		return this;
	}

}
