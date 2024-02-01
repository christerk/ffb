package com.fumbbl.ffb.server.step.bb2016;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.util.StringTool;

/**
 * Step in any sequence to handle skill SHADOWING.
 * 
 * Expects stepParameter COORDINATE_FROM to be set by a preceding step. Expects
 * stepParameter DEFENDER_POSITION to be set by a preceding step. Expects
 * stepParameter USING_DIVING_TACKLE to be set by a preceding step.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public class StepShadowing extends AbstractStepWithReRoll {

	public class StepState {
		public ActionStatus status;

		public FieldCoordinate defenderPosition;
		public FieldCoordinate coordinateFrom;
		public boolean usingDivingTackle;
		public Boolean usingShadowing;
	}

	private final StepState state;

	public StepShadowing(GameState pGameState) {
		super(pGameState);

		state = new StepState();

	}

	public StepId getId() {
		return StepId.SHADOWING;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
			case COORDINATE_FROM:
				state.coordinateFrom = (FieldCoordinate) parameter.getValue();
				return true;
			case DEFENDER_POSITION:
				state.defenderPosition = (FieldCoordinate) parameter.getValue();
				return true;
			case USING_DIVING_TACKLE:
				state.usingDivingTackle = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
				return true;
			default:
				break;
			}
		}
		return false;
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			Game game = getGameState().getGame();
			switch (pReceivedCommand.getId()) {
			case CLIENT_PLAYER_CHOICE:
				ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
				if (PlayerChoiceMode.SHADOWING == playerChoiceCommand.getPlayerChoiceMode()) {
					state.usingShadowing = StringTool.isProvided(playerChoiceCommand.getPlayerId());
					game.setDefenderId(playerChoiceCommand.getPlayerId());
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
		IServerJsonOption.DEFENDER_POSITION.addTo(jsonObject, state.defenderPosition);
		IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, state.coordinateFrom);
		IServerJsonOption.USING_DIVING_TACKLE.addTo(jsonObject, state.usingDivingTackle);
		IServerJsonOption.USING_SHADOWING.addTo(jsonObject, state.usingDivingTackle);
		return jsonObject;
	}

	@Override
	public StepShadowing initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state.defenderPosition = IServerJsonOption.DEFENDER_POSITION.getFrom(source, jsonObject);
		state.coordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(source, jsonObject);
		state.usingDivingTackle = IServerJsonOption.USING_DIVING_TACKLE.getFrom(source, jsonObject);
		state.usingDivingTackle = IServerJsonOption.USING_SHADOWING.getFrom(source, jsonObject);
		return this;
	}

}
