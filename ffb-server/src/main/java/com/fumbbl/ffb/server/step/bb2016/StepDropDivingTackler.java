package com.fumbbl.ffb.server.step.bb2016;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.util.StringTool;

/**
 * Step in move sequence to drop a player using the DIVING_TACKLE skill.
 * <p>
 * Expects stepParameter COORDINATE_FROM to be set by a preceding step. Expects
 * stepParameter USING_DIVING_TACKLE to be set by a preceding step.
 * <p>
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public class StepDropDivingTackler extends AbstractStep {

	private boolean fUsingDivingTackle;
	private FieldCoordinate fCoordinateFrom;

	public StepDropDivingTackler(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.DROP_DIVING_TACKLER;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
			case COORDINATE_FROM:
				fCoordinateFrom = (FieldCoordinate) parameter.getValue();
				return true;
			case USING_DIVING_TACKLE:
				fUsingDivingTackle = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
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
		if (fUsingDivingTackle && StringTool.isProvided(game.getDefenderId())) {
			game.getFieldModel().updatePlayerAndBallPosition(game.getDefender(), fCoordinateFrom);
			publishParameters(UtilServerInjury.dropPlayer(this, game.getDefender(), ApothecaryMode.DEFENDER));
			UtilServerPlayerMove.updateMoveSquares(getGameState(), game.getActingPlayer().isJumping());
		}
		// reset DivingTackle & Shadowing attributes
		game.setDefenderId(null);
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.USING_DIVING_TACKLE.addTo(jsonObject, fUsingDivingTackle);
		IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, fCoordinateFrom);
		return jsonObject;
	}

	@Override
	public StepDropDivingTackler initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fUsingDivingTackle = IServerJsonOption.USING_DIVING_TACKLE.getFrom(source, jsonObject);
		fCoordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(source, jsonObject);
		return this;
	}

}
