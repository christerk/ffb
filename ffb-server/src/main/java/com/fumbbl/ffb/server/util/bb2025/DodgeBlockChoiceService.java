package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.CommonPropertyValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PushbackMode;
import com.fumbbl.ffb.PushbackSquare;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.util.UtilServerPushback;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

/**
 * Decides whether the defending coach is asked about using Dodge (or Watch Out!) against a Stumble,
 * based on the coach's SETTING_DODGE_BLOCK value.
 */
public class DodgeBlockChoiceService {

	private static final FieldCoordinateBounds[] SIDELINE_ROWS = {
		FieldCoordinateBounds.SIDELINE_UPPER, FieldCoordinateBounds.SIDELINE_LOWER
	};

	private static final FieldCoordinateBounds[] WIDE_ZONE_ROWS = {
		FieldCoordinateBounds.UPPER_WIDE_ZONE_HOME, FieldCoordinateBounds.UPPER_WIDE_ZONE_AWAY,
		FieldCoordinateBounds.LOWER_WIDE_ZONE_HOME, FieldCoordinateBounds.LOWER_WIDE_ZONE_AWAY
	};

	/**
	 * @param setting the defending coach's setting value, missing or unknown values count as "always"
	 */
	public boolean askForDodge(Game game, String setting) {
		String mode = StringTool.isProvided(setting) ? setting : CommonPropertyValue.SETTING_DODGE_BLOCK_ALWAYS;
		switch (mode) {
			case CommonPropertyValue.SETTING_DODGE_BLOCK_NEVER:
				return false;
			case CommonPropertyValue.SETTING_DODGE_BLOCK_SITUATIONAL:
				return isCriticalPush(game, SIDELINE_ROWS);
			case CommonPropertyValue.SETTING_DODGE_BLOCK_WIDE_ZONE:
				return isCriticalPush(game, WIDE_ZONE_ROWS);
			default:
				return true;
		}
	}

	/**
	 * A push is critical when:
	 * 1: it is a potential chain push, i.e. one of the pushback squares is occupied.
	 * 2: it is the first turn after kick-off and the defender could be pushed over the line of scrimmage into the
	 * attackers half.
	 * 3: the defender could be pushed into one of the given sideline rows or into an end zone.
	 */
	private boolean isCriticalPush(Game game, FieldCoordinateBounds[] sidelineRows) {

		boolean chainPush = false;
		boolean sidelinePush = false;
		boolean attackerHalfPush = false;
		ActingPlayer actingPlayer = game.getActingPlayer();

		Player<?> attacker = actingPlayer.getPlayer();
		FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(attacker);
		FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
		PushbackSquare startingSquare = UtilServerPushback.findStartingSquare(attackerCoordinate, defenderCoordinate,
			game.isHomePlaying());

		if (startingSquare != null) {
			PushbackSquare[] regularPushbackSquares = UtilServerPushback.findPushbackSquares(game, startingSquare,
				PushbackMode.REGULAR);
			if (ArrayTool.isProvided(regularPushbackSquares)) {
				for (PushbackSquare pushbackSquare : regularPushbackSquares) {
					FieldCoordinate coordinate = pushbackSquare.getCoordinate();
					if (game.getFieldModel().getPlayer(coordinate) != null) {
						chainPush = true;
					}
				}
			}

			PushbackSquare[] grabPushbackSquares = regularPushbackSquares;
			if ((actingPlayer.getPlayerAction().isBlockOrSpecialAction())
				&& attacker.hasSkillProperty(NamedProperties.canPushBackToAnySquare)
				&& !game.getDefender().hasSkillProperty(NamedProperties.canChooseOwnPushedBackSquare)) {
				grabPushbackSquares = UtilServerPushback.findPushbackSquares(game, startingSquare, PushbackMode.GRAB);
			}
			if (ArrayTool.isProvided(regularPushbackSquares)) {
				for (PushbackSquare pushbackSquare : grabPushbackSquares) {
					FieldCoordinate coordinate = pushbackSquare.getCoordinate();
					if (isInAny(coordinate, sidelineRows)
						|| FieldCoordinateBounds.ENDZONE_HOME.isInBounds(coordinate)
						|| FieldCoordinateBounds.ENDZONE_AWAY.isInBounds(coordinate)) {
						sidelinePush = true;
					}
					if ((game.getTeamHome().hasPlayer(attacker) && FieldCoordinateBounds.HALF_HOME.isInBounds(coordinate)
						&& game.getTurnDataHome().isFirstTurnAfterKickoff())
						|| (game.getTeamAway().hasPlayer(attacker) && FieldCoordinateBounds.HALF_AWAY.isInBounds(coordinate)
						&& game.getTurnDataAway().isFirstTurnAfterKickoff())) {
						attackerHalfPush = true;
					}
				}
			}
		}
		return chainPush || sidelinePush || attackerHalfPush;
	}

	private boolean isInAny(FieldCoordinate coordinate, FieldCoordinateBounds[] bounds) {
		for (FieldCoordinateBounds bound : bounds) {
			if (bound.isInBounds(coordinate)) {
				return true;
			}
		}
		return false;
	}
}
