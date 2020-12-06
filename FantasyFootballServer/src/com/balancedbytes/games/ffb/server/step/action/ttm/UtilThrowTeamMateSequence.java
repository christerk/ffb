package com.balancedbytes.games.ffb.server.step.action.ttm;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.report.ReportScatterPlayer;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.util.UtilServerCatchScatterThrowIn;

/**
 * 
 * @author Kalimar
 */
public class UtilThrowTeamMateSequence {

	public static class ScatterResult {

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

	}

	public static ScatterResult scatterPlayer(IStep pStep, FieldCoordinate pStartCoordinate, boolean pThrowScatter) {

		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();

		FieldCoordinate endCoordinate = null;
		FieldCoordinate lastValidCoordinate = null;
		FieldCoordinate startCoordinate = pStartCoordinate;
		List<Integer> rollList = new ArrayList<Integer>();
		List<Direction> directionList = new ArrayList<Direction>();

		boolean inBounds = true;
		while (inBounds) {
			if ((pThrowScatter && (rollList.size() >= 3))
					|| (!pThrowScatter && (rollList.size() >= 1) && (game.getFieldModel().getPlayer(startCoordinate) == null))) {
				break;
			}
			int roll = gameState.getDiceRoller().rollScatterDirection();
			rollList.add(roll);
			Direction direction = DiceInterpreter.getInstance().interpretScatterDirectionRoll(roll);
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
		Direction[] directions = directionList.toArray(new Direction[directionList.size()]);
		pStep.getResult().addReport(new ReportScatterPlayer(pStartCoordinate, endCoordinate, directions, rolls));

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
