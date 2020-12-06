package com.balancedbytes.games.ffb.server.step.action.foul;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilBox;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in foul sequence to handle ejecting a spotted fouler.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 * 
 * Expects stepParameter FOULER_HAS_BALL to be set by a preceding step. Expects
 * stepParameter ARGUE_THE_CALL_SUCCESFUL to be set by a preceding step.
 * 
 * Sets stepParameter CATCH_SCATTER_THROW_IN_MODE for all steps on the stack.
 * Sets stepParameter END_TURN for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepEjectPlayer extends AbstractStep {

	public class StepState {
		public String gotoLabelOnEnd;
		public Boolean foulerHasBall;
		public Boolean argueTheCallSuccessful;
	}

	private StepState state;

	public StepEjectPlayer(GameState pGameState) {
		super(pGameState);

		state = new StepState();
	}

	public StepId getId() {
		return StepId.EJECT_PLAYER;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
				case GOTO_LABEL_ON_END:
					state.gotoLabelOnEnd = (String) parameter.getValue();
					break;
				default:
					break;
				}
			}
		}
		if (!StringTool.isProvided(state.gotoLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case FOULER_HAS_BALL:
				state.foulerHasBall = (Boolean) pParameter.getValue();
				return true;
			case ARGUE_THE_CALL_SUCCESSFUL:
				state.argueTheCallSuccessful = (Boolean) pParameter.getValue();
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
		getGameState().executeStepHooks(this, state);
		Game game = getGameState().getGame();

		ActingPlayer actingPlayer = game.getActingPlayer();

		UtilBox.putPlayerIntoBox(game, actingPlayer.getPlayer());
		UtilBox.refreshBoxes(game);
		UtilServerGame.updateLeaderReRolls(this);
		publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
		if ((state.foulerHasBall != null) && state.foulerHasBall) {
			publishParameter(
					new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			getResult().setNextAction(StepAction.GOTO_LABEL, state.gotoLabelOnEnd);
		}

	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, state.gotoLabelOnEnd);
		IServerJsonOption.FOULER_HAS_BALL.addTo(jsonObject, state.foulerHasBall);
		IServerJsonOption.ARGUE_THE_CALL_SUCCESSFUL.addTo(jsonObject, state.argueTheCallSuccessful);
		return jsonObject;
	}

	@Override
	public StepEjectPlayer initFrom(JsonValue pJsonValue) {
		super.initFrom(pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		state.gotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(jsonObject);
		state.foulerHasBall = IServerJsonOption.FOULER_HAS_BALL.getFrom(jsonObject);
		state.argueTheCallSuccessful = IServerJsonOption.ARGUE_THE_CALL_SUCCESSFUL.getFrom(jsonObject);
		return this;
	}

}
