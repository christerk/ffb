package com.fumbbl.ffb.server.step.bb2016.ttm;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.util.UtilServerDialog;

/**
 * Final step of the throw team mate sequence. Consumes all expected
 * stepParameters.
 *
 * Expects stepParameter END_PLAYER_ACTION to be set by a preceding step.
 * Expects stepParameter END_TURN to be set by a preceding step. Expects
 * stepParameter THROWN_PLAYER_COORDINATE to be set by a preceding step. Expects
 * stepParameter THROWN_PLAYER_HAS_BALL to be set by a preceding step. Expects
 * stepParameter THROWN_PLAYER_ID to be set by a preceding step. Expects
 * stepParameter THROWN_PLAYER_STATE to be set by a preceding step.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public final class StepEndThrowTeamMate extends AbstractStep {

	private boolean fEndTurn;
	private boolean fEndPlayerAction;
	private FieldCoordinate fThrownPlayerCoordinate;
	private boolean fThrownPlayerHasBall;
	private String fThrownPlayerId;
	private PlayerState fThrownPlayerState;

	public StepEndThrowTeamMate(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_THROW_TEAM_MATE;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
			case END_TURN:
				fEndTurn = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
				consume(parameter);
				return true;
			case THROWN_PLAYER_COORDINATE:
				fThrownPlayerCoordinate = (FieldCoordinate) parameter.getValue();
				consume(parameter);
				return true;
			case THROWN_PLAYER_HAS_BALL:
				fThrownPlayerHasBall = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
				consume(parameter);
				return true;
			case THROWN_PLAYER_ID:
				fThrownPlayerId = (String) parameter.getValue();
				consume(parameter);
				return true;
			case THROWN_PLAYER_STATE:
				fThrownPlayerState = (PlayerState) parameter.getValue();
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

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
			case CLIENT_ACTING_PLAYER:
				SequenceGeneratorFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
				((Select)factory.forName(SequenceGenerator.Type.Select.name()))
					.pushSequence(new Select.SequenceParams(getGameState(), false));
				getResult().setNextAction(StepAction.NEXT_STEP_AND_REPEAT);
				commandStatus = StepCommandStatus.SKIP_STEP;
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
		UtilServerDialog.hideDialog(getGameState());
		Game game = getGameState().getGame();
		game.setPassCoordinate(null);
		game.getFieldModel().setRangeRuler(null);
		game.setDefenderId(null);
		// reset thrown player (e.g. failed confusion roll, successful escape roll)
		Player<?> thrownPlayer = game.getPlayerById(fThrownPlayerId);
		if ((thrownPlayer != null) && (fThrownPlayerCoordinate != null) && (fThrownPlayerState != null)
				&& (fThrownPlayerState.getId() > 0)) {
			game.getFieldModel().setPlayerCoordinate(thrownPlayer, fThrownPlayerCoordinate);
			game.getFieldModel().setPlayerState(thrownPlayer, fThrownPlayerState);
			if (fThrownPlayerHasBall) {
				game.getFieldModel().setBallCoordinate(fThrownPlayerCoordinate);
			}
		}
		SequenceGeneratorFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		((EndPlayerAction)factory.forName(SequenceGenerator.Type.EndPlayerAction.name()))
			.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), true, true, fEndTurn));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
		IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, fThrownPlayerId);
		IServerJsonOption.THROWN_PLAYER_STATE.addTo(jsonObject, fThrownPlayerState);
		IServerJsonOption.THROWN_PLAYER_HAS_BALL.addTo(jsonObject, fThrownPlayerHasBall);
		IServerJsonOption.THROWN_PLAYER_COORDINATE.addTo(jsonObject, fThrownPlayerCoordinate);
		return jsonObject;
	}

	@Override
	public StepEndThrowTeamMate initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		fThrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(source, jsonObject);
		fThrownPlayerState = IServerJsonOption.THROWN_PLAYER_STATE.getFrom(source, jsonObject);
		fThrownPlayerHasBall = IServerJsonOption.THROWN_PLAYER_HAS_BALL.getFrom(source, jsonObject);
		fThrownPlayerCoordinate = IServerJsonOption.THROWN_PLAYER_COORDINATE.getFrom(source, jsonObject);
		return this;
	}

}
