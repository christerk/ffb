package com.balancedbytes.games.ffb.server.step.action.pass;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.AnimationType;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.report.ReportScatterBall;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.util.UtilServerCatchScatterThrowIn;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step of the pass sequence to handle a missed pass.
 * 
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * Sets stepParameter THROWIN_COORDINATE for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepMissedPass extends AbstractStep {

	public StepMissedPass(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.MISSED_PASS;
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
		FieldCoordinate coordinateEnd = null;
		FieldCoordinate lastValidCoordinate = null;
		List<Integer> rollList = new ArrayList<Integer>();
		List<Direction> directionList = new ArrayList<Direction>();

		FieldCoordinate coordinateStart = game.getPassCoordinate();
		while (FieldCoordinateBounds.FIELD.isInBounds(coordinateStart) && (rollList.size() < 3)) {
			int roll = getGameState().getDiceRoller().rollScatterDirection();
			rollList.add(roll);
			Direction direction = DiceInterpreter.getInstance().interpretScatterDirectionRoll(roll);
			directionList.add(direction);
			coordinateEnd = UtilServerCatchScatterThrowIn.findScatterCoordinate(coordinateStart, direction, 1);
			lastValidCoordinate = FieldCoordinateBounds.FIELD.isInBounds(coordinateEnd) ? coordinateEnd : coordinateStart;
			coordinateStart = coordinateEnd;
		}
		int[] rolls = new int[rollList.size()];
		for (int i = 0; i < rolls.length; i++) {
			rolls[i] = rollList.get(i);
		}

		Direction[] directions = directionList.toArray(new Direction[directionList.size()]);
		getResult().addReport(new ReportScatterBall(directions, rolls, false));

		game.getFieldModel().setRangeRuler(null);
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
		if (PlayerAction.HAIL_MARY_PASS == game.getThrowerAction()) {
			getResult()
					.setAnimation(new Animation(AnimationType.HAIL_MARY_PASS, throwerCoordinate, lastValidCoordinate, null));
		} else if (PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction()) {
			getResult()
					.setAnimation(new Animation(AnimationType.HAIL_MARY_BOMB, throwerCoordinate, lastValidCoordinate, null));
		} else if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
			getResult().setAnimation(new Animation(AnimationType.THROW_BOMB, throwerCoordinate, lastValidCoordinate, null));
		} else {
			getResult().setAnimation(new Animation(AnimationType.PASS, throwerCoordinate, lastValidCoordinate, null));
		}
		UtilServerGame.syncGameModel(this);
		if (!FieldCoordinateBounds.FIELD.isInBounds(coordinateEnd)) {
			if ((PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction())
					|| (PlayerAction.THROW_BOMB == game.getThrowerAction())) {
				game.getFieldModel().setBombCoordinate(null);
				publishParameter(new StepParameter(StepParameterKey.BOMB_OUT_OF_BOUNDS, true));
			} else {
				publishParameter(
						new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.THROW_IN));
				publishParameter(new StepParameter(StepParameterKey.THROW_IN_COORDINATE, lastValidCoordinate));
				game.getFieldModel().setBallMoving(true);
			}
		} else {
			if ((PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction())
					|| (PlayerAction.THROW_BOMB == game.getThrowerAction())) {
				publishParameter(
						new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.CATCH_BOMB));
				game.getFieldModel().setBombCoordinate(coordinateEnd);
				game.getFieldModel().setBombMoving(true);
			} else {
				publishParameter(
						new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.CATCH_MISSED_PASS));
				game.getFieldModel().setBallCoordinate(coordinateEnd);
				game.getFieldModel().setBallMoving(true);
			}
		}

		getResult().setNextAction(StepAction.NEXT_STEP);

	}

	// ByteArray serialization

	public int getByteArraySerializationVersion() {
		return 1;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		return super.toJsonValue();
	}

	@Override
	public StepMissedPass initFrom(JsonValue pJsonValue) {
		super.initFrom(pJsonValue);
		return this;
	}

}
