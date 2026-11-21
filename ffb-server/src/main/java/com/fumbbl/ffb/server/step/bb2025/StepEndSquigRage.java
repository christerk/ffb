package com.fumbbl.ffb.server.step.bb2025;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;

import java.util.Arrays;

/**
 * Final step of the Squig Rage sequence. Continues with the select sequence unless the turn ended, in which case the
 * end turn parameter (not consumed here) is handled by the end selecting step.
 * <p>
 * Expects stepParameter GOTO_LABEL_ON_FAILURE to be set by a preceding step.
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepEndSquigRage extends AbstractStep {

	private String goToLabelOnFailure;
	private boolean endTurn;

	public StepEndSquigRage(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.END_SQUIG_RAGE;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		super.init(pParameterSet);
		if (pParameterSet != null) {
			Arrays.stream(pParameterSet.values()).forEach(parameter -> {
				if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_FAILURE) {
					goToLabelOnFailure = (String) parameter.getValue();
				}
			});
		}
		if (goToLabelOnFailure == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null && parameter.getKey() == StepParameterKey.END_TURN) {
			endTurn = toPrimitive((Boolean) parameter.getValue());
			return true;
		}
		return super.setParameter(parameter);
	}

	@Override
	public void start() {
		super.start();
		if (endTurn) {
			getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnFailure);
		} else {
			// only now the player is offered to declare an action, intermediate states of the sequence must not do so
			ActingPlayer actingPlayer = getGameState().getGame().getActingPlayer();
			if (actingPlayer.getPlayerAction() == PlayerAction.SQUIG_RAGE) {
				actingPlayer.setAwaitingActionDeclaration(true);
			}
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, goToLabelOnFailure);
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		return jsonObject;
	}

	@Override
	public StepEndSquigRage initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		endTurn = toPrimitive(IServerJsonOption.END_TURN.getFrom(source, jsonObject));
		return this;
	}
}
