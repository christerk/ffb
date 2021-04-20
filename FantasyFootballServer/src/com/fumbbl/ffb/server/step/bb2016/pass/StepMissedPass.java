package com.fumbbl.ffb.server.step.bb2016.pass;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.report.ReportPassDeviate;
import com.fumbbl.ffb.report.ReportScatterBall;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.UtilServerCatchScatterThrowIn;
import com.fumbbl.ffb.server.util.UtilServerGame;

import java.util.ArrayList;
import java.util.List;

/**
 * Step of the pass sequence to handle a missed pass.
 * <p>
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * Sets stepParameter THROWIN_COORDINATE for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public class StepMissedPass extends AbstractStep {

	private boolean passDeviates;

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
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			if (pParameter.getKey() == StepParameterKey.PASS_DEVIATES) {
				passDeviates = (boolean) pParameter.getValue();
				return true;
			}
		}
		return false;
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
		List<Integer> rollList = new ArrayList<>();
		List<Direction> directionList = new ArrayList<>();
		FieldCoordinate coordinateStart;
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
		if (passDeviates) {
			coordinateStart = throwerCoordinate;
			int directionRoll = getGameState().getDiceRoller().rollScatterDirection();
			int distanceRoll = getGameState().getDiceRoller().rollScatterDistance();
			Direction direction = DiceInterpreter.getInstance().interpretScatterDirectionRoll(game, directionRoll);
			coordinateEnd = UtilServerCatchScatterThrowIn.findScatterCoordinate(coordinateStart, direction, distanceRoll);
			lastValidCoordinate = coordinateEnd;
			int validDistance = distanceRoll;
			while (!FieldCoordinateBounds.FIELD.isInBounds(lastValidCoordinate) && validDistance > 0) {
				validDistance--;
				lastValidCoordinate = UtilServerCatchScatterThrowIn.findScatterCoordinate(coordinateStart, direction, validDistance);
			}
			getResult().addReport(new ReportPassDeviate(coordinateEnd, direction, directionRoll, distanceRoll));
		} else {
			coordinateStart = game.getPassCoordinate();
			while (FieldCoordinateBounds.FIELD.isInBounds(coordinateStart) && (rollList.size() < 3)) {
				int roll = getGameState().getDiceRoller().rollScatterDirection();
				rollList.add(roll);
				Direction direction = DiceInterpreter.getInstance().interpretScatterDirectionRoll(game, roll);
				directionList.add(direction);
				coordinateEnd = UtilServerCatchScatterThrowIn.findScatterCoordinate(coordinateStart, direction, 1);
				lastValidCoordinate = FieldCoordinateBounds.FIELD.isInBounds(coordinateEnd) ? coordinateEnd : coordinateStart;
				coordinateStart = coordinateEnd;
			}
			int[] rolls = new int[rollList.size()];
			for (int i = 0; i < rolls.length; i++) {
				rolls[i] = rollList.get(i);
			}

			Direction[] directions = directionList.toArray(new Direction[0]);
			getResult().addReport(new ReportScatterBall(directions, rolls, false));
		}

		game.getFieldModel().setRangeRuler(null);
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
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PASS_DEVIATES.addTo(jsonObject, passDeviates);
		return jsonObject;
	}

	@Override
	public StepMissedPass initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		passDeviates = IJsonOption.PASS_DEVIATES.getFrom(game, jsonObject);
		return this;
	}

}
