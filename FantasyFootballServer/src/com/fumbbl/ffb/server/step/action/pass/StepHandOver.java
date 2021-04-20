package com.fumbbl.ffb.server.step.action.pass;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportHandOver;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;

/**
 * Step in the pass sequence to handle a hand over of the ball.
 *
 * Expects stepParameter CATCHER_ID to be set by a preceding step.
 *
 * Sets stepParameter CATCH_SCATTER_THROW_IN_MODE for all steps on the stack.
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public final class StepHandOver extends AbstractStepWithReRoll {

	private String fCatcherId;

	public StepHandOver(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.HAND_OVER;
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case CATCHER_ID:
				fCatcherId = (String) pParameter.getValue();
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
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		game.getFieldModel().setBallMoving(true);
		game.setPassCoordinate(null);
		Player<?> thrower = game.getActingPlayer().getPlayer();
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(thrower);
		Player<?> catcher = game.getPlayerById(fCatcherId);
		FieldCoordinate catcherCoordinate = game.getFieldModel().getPlayerCoordinate(catcher);
		if ((throwerCoordinate != null) && throwerCoordinate.isAdjacent(catcherCoordinate)) {
			game.getFieldModel().setBallCoordinate(catcherCoordinate);
			getResult().addReport(new ReportHandOver(fCatcherId));
			publishParameter(
					new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.CATCH_HAND_OFF));
		}
		publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
		return jsonObject;
	}

	@Override
	public StepHandOver initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fCatcherId = IServerJsonOption.CATCHER_ID.getFrom(game, jsonObject);
		return this;
	}

}
