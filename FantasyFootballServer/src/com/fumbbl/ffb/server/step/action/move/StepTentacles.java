package com.fumbbl.ffb.server.step.action.move;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.util.StringTool;

/**
 * Step in move sequence to handle skill TENTACLES.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_SUCCESS.
 * 
 * Expects stepParameter COORDINATE_FROM to be set by a preceding step.
 * 
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepTentacles extends AbstractStepWithReRoll {

	public class StepState {
		public String goToLabelOnSuccess;
		public FieldCoordinate coordinateFrom;
		public Boolean usingTentacles;
	}

	private StepState state;

	public StepTentacles(GameState pGameState) {
		super(pGameState);

		state = new StepState();
	}

	public StepId getId() {
		return StepId.TENTACLES;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
				// mandatory
				case GOTO_LABEL_ON_SUCCESS:
					state.goToLabelOnSuccess = (String) parameter.getValue();
					break;
				default:
					break;
				}
			}
		}
		if (state.goToLabelOnSuccess == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_SUCCESS + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case COORDINATE_FROM:
				state.coordinateFrom = (FieldCoordinate) pParameter.getValue();
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

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
			case CLIENT_PLAYER_CHOICE:
				ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
				if (playerChoiceCommand.getPlayerChoiceMode() == PlayerChoiceMode.TENTACLES) {
					state.usingTentacles = StringTool.isProvided(playerChoiceCommand.getPlayerId());
					getGameState().getGame().setDefenderId(playerChoiceCommand.getPlayerId());
					commandStatus = StepCommandStatus.EXECUTE_STEP;
				}
				break;
			default:
				break;
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		getGameState().executeStepHooks(this, state);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_SUCCESS.addTo(jsonObject, state.goToLabelOnSuccess);
		IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, state.coordinateFrom);
		IServerJsonOption.USING_TENTACLES.addTo(jsonObject, state.usingTentacles);
		return jsonObject;
	}

	@Override
	public StepTentacles initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		state.goToLabelOnSuccess = IServerJsonOption.GOTO_LABEL_ON_SUCCESS.getFrom(game, jsonObject);
		state.coordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(game, jsonObject);
		state.usingTentacles = IServerJsonOption.USING_TENTACLES.getFrom(game, jsonObject);
		return this;
	}

}
