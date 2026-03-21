package com.fumbbl.ffb.server.step.action.ttm;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.TtmMechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.report.ReportScatterPlayer;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.util.UtilServerCatchScatterThrowIn;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class UtilThrowTeamMateSequence {

	public static class ScatterResult implements IJsonSerializable {

		private FieldCoordinate fLastValidCoordinate;
		private boolean fInBounds;

		public ScatterResult(FieldCoordinate pLastValidCoordinate, boolean pInBounds) {
			fLastValidCoordinate = pLastValidCoordinate;
			fInBounds = pInBounds;
		}

		public FieldCoordinate getLastValidCoordinate() {
			return fLastValidCoordinate;
		}

		public boolean isInBounds() {
			return fInBounds;
		}

		@Override
		public ScatterResult initFrom(IFactorySource source, JsonValue jsonValue) {
			JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
			if (IServerJsonOption.FIELD_COORDINATE.isDefinedIn(jsonObject)) {
				fLastValidCoordinate =
					new FieldCoordinate().initFrom(source, IServerJsonOption.FIELD_COORDINATE.getFrom(source, jsonObject));
			}
			fInBounds = !IServerJsonOption.OUT_OF_BOUNDS.getFrom(source, jsonObject);
			return null;
		}

		@Override
		public JsonObject toJsonValue() {
			JsonObject jsonObject = new JsonObject();
			if (fLastValidCoordinate != null) {
				IServerJsonOption.FIELD_COORDINATE.addTo(jsonObject, fLastValidCoordinate.toJsonValue());
			}
			IServerJsonOption.OUT_OF_BOUNDS.addTo(jsonObject, !fInBounds);
			return jsonObject;
		}
	}

	public static ScatterResult scatterPlayer(IStep pStep, FieldCoordinate pStartCoordinate, boolean pThrowScatter) {

		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		TtmMechanic mechanic =
			(TtmMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());

		FieldCoordinate endCoordinate = null;
		FieldCoordinate lastValidCoordinate = null;
		FieldCoordinate startCoordinate = pStartCoordinate;
		List<Integer> rollList = new ArrayList<>();
		List<Direction> directionList = new ArrayList<>();

		boolean inBounds = true;
		while (inBounds) {
			if ((pThrowScatter && (rollList.size() >= 3))
				|| (!pThrowScatter && (!rollList.isEmpty()) && mechanic.isValidEndScatterCoordinate(game, startCoordinate))) {
				break;
			}
			int roll = gameState.getDiceRoller().rollScatterDirection();
			rollList.add(roll);
			Direction direction = DiceInterpreter.getInstance().interpretScatterDirectionRoll(game, roll);
			directionList.add(direction);
			endCoordinate = UtilServerCatchScatterThrowIn.findScatterCoordinate(startCoordinate, direction, 1);
			if (FieldCoordinateBounds.FIELD.isInBounds(endCoordinate)) {
				lastValidCoordinate = endCoordinate;
			} else {
				lastValidCoordinate = startCoordinate;
				inBounds = false;
			}
			startCoordinate = endCoordinate;
		}
		int[] rolls = new int[rollList.size()];
		for (int i = 0; i < rolls.length; i++) {
			rolls[i] = rollList.get(i);
		}
		Direction[] directions = directionList.toArray(new Direction[0]);
		pStep.getResult()
			.addReport(new ReportScatterPlayer(pStartCoordinate, endCoordinate, directions, rolls, pThrowScatter));

		return new ScatterResult(lastValidCoordinate, inBounds);

	}

	public static ScatterResult kickPlayer(IStep pStep, FieldCoordinate pKickedPlayerCoordinate,
		FieldCoordinate pTargetCoordinate) {
		FieldCoordinate lastValidCoordinate = pKickedPlayerCoordinate;
		FieldCoordinate currentCoordinate = pKickedPlayerCoordinate;

		boolean inBounds = true;
		int dx = Integer.signum(pTargetCoordinate.getX() - pKickedPlayerCoordinate.getX());
		int dy = Integer.signum(pTargetCoordinate.getY() - pKickedPlayerCoordinate.getY());
		while (inBounds && !currentCoordinate.equals(pTargetCoordinate)) {
			currentCoordinate = currentCoordinate.add(dx, dy);
			if (FieldCoordinateBounds.FIELD.isInBounds(currentCoordinate)) {
				lastValidCoordinate = currentCoordinate;
			} else {
				inBounds = false;
			}
		}

		if (inBounds) {
			return scatterPlayer(pStep, lastValidCoordinate, true);
		}

		return new ScatterResult(lastValidCoordinate, inBounds);
	}

}
