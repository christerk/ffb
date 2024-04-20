package com.fumbbl.ffb.server.step.phase.kickoff;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;

/**
 * Step to end kickoff sequence.
 * 
 * Expects stepParameter KICKING_PLAYER_COORDINATE to be set by a preceding
 * step. Expects stepParameter TOUCHBACK to be set by a preceding step.
 * 
 * Sets stepParameter CATCH_SCATTER_THROW_IN_MODE for all steps on the stack.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public final class StepKickoffAnimation extends AbstractStep {

	private FieldCoordinate fKickingPlayerCoordinate;
	private boolean fTouchback;

	public StepKickoffAnimation(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.KICKOFF_ANIMATION;
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

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
			case KICKED_PLAYER_COORDINATE:
				fKickingPlayerCoordinate = (FieldCoordinate) parameter.getValue();
				return true;
			case TOUCHBACK:
				fTouchback = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
				return true;
			default:
				break;
			}
		}
		return false;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		if (fKickingPlayerCoordinate == null) {
			if (game.isHomePlaying()) {
				fKickingPlayerCoordinate = new FieldCoordinate(2, 8);
			} else {
				fKickingPlayerCoordinate = new FieldCoordinate(27, 8);
			}
		}
		game.getFieldModel().setBallInPlay(true);
		FieldCoordinate ballCoordinate = game.getFieldModel().getBallCoordinate();
		getResult().setAnimation(new Animation(AnimationType.KICK, fKickingPlayerCoordinate, ballCoordinate));
		if (!fTouchback) {
			publishParameter(
					new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.CATCH_KICKOFF));
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.KICKING_PLAYER_COORDINATE.addTo(jsonObject, fKickingPlayerCoordinate);
		IServerJsonOption.TOUCHBACK.addTo(jsonObject, fTouchback);
		return jsonObject;
	}

	@Override
	public StepKickoffAnimation initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fKickingPlayerCoordinate = IServerJsonOption.KICKING_PLAYER_COORDINATE.getFrom(source, jsonObject);
		fTouchback = IServerJsonOption.TOUCHBACK.getFrom(source, jsonObject);
		return this;
	}

}
