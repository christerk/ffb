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
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.util.StringTool;

/**
 * Step in move sequence to handle the DIVING_TACKLE skill.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL.
 * 
 * Expects stepParameter COORDINATE_FROM to be set by a preceding step. Expects
 * stepParameter COORDINATE_TO to be set by a preceding step. Expects
 * stepParameter DODGE_ROLL to be set by a preceding step.
 * 
 * Sets stepParameter USING_DIVING_TACKLE for all steps on the stack.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepDivingTackle extends AbstractStep {

	public class StepState {
		public String goToLabelOnSuccess;
		public FieldCoordinate coordinateFrom;
		public FieldCoordinate coordinateTo;
		public int dodgeRoll;
		public Boolean usingDivingTackle;
		public boolean usingBreakTackle;
	}

	private final StepState state;

	public StepDivingTackle(GameState pGameState) {
		super(pGameState);
		state = new StepState();
	}

	public StepId getId() {
		return StepId.DIVING_TACKLE;
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
		if (!StringTool.isProvided(state.goToLabelOnSuccess)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_SUCCESS + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
			case COORDINATE_FROM:
				state.coordinateFrom = (FieldCoordinate) parameter.getValue();
				return true;
			case COORDINATE_TO:
				state.coordinateTo = (FieldCoordinate) parameter.getValue();
				return true;
			case DODGE_ROLL:
				state.dodgeRoll = (Integer) parameter.getValue();
				return true;
			case USING_BREAK_TACKLE:
				state.usingBreakTackle = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
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
				if (playerChoiceCommand.getPlayerChoiceMode() == PlayerChoiceMode.DIVING_TACKLE) {
					state.usingDivingTackle = StringTool.isProvided(playerChoiceCommand.getPlayerId());
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
		IServerJsonOption.COORDINATE_TO.addTo(jsonObject, state.coordinateTo);
		IServerJsonOption.DODGE_ROLL.addTo(jsonObject, state.dodgeRoll);
		IServerJsonOption.USING_DIVING_TACKLE.addTo(jsonObject, state.usingDivingTackle);
		IServerJsonOption.USING_BREAK_TACKLE.addTo(jsonObject, state.usingBreakTackle);
		return jsonObject;
	}

	@Override
	public StepDivingTackle initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state.goToLabelOnSuccess = IServerJsonOption.GOTO_LABEL_ON_SUCCESS.getFrom(source, jsonObject);
		state.coordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(source, jsonObject);
		state.coordinateTo = IServerJsonOption.COORDINATE_TO.getFrom(source, jsonObject);
		state.dodgeRoll = IServerJsonOption.DODGE_ROLL.getFrom(source, jsonObject);
		state.usingDivingTackle = IServerJsonOption.USING_DIVING_TACKLE.getFrom(source, jsonObject);
		state.usingBreakTackle = IServerJsonOption.USING_BREAK_TACKLE.getFrom(source, jsonObject);
		return this;
	}

}
