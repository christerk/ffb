package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 * @author Kalimar
 */
public class UtilClientActionKeys {

	public static FieldCoordinate findMoveCoordinate(FieldCoordinate pStartCoordinate,
	                                                 ActionKey pActionKey) {
		FieldCoordinate moveCoordinate = null;
		Direction moveDirection = findMoveDirection(pActionKey);
		if (moveDirection != null) {
			switch (moveDirection) {
				case NORTH:
					moveCoordinate = pStartCoordinate.add(0, -1);
					break;
				case NORTHEAST:
					moveCoordinate = pStartCoordinate.add(1, -1);
					break;
			case EAST:
				moveCoordinate = pStartCoordinate.add(1, 0);
				break;
			case SOUTHEAST:
				moveCoordinate = pStartCoordinate.add(1, 1);
				break;
			case SOUTH:
				moveCoordinate = pStartCoordinate.add(0, 1);
				break;
			case SOUTHWEST:
				moveCoordinate = pStartCoordinate.add(-1, 1);
				break;
			case WEST:
				moveCoordinate = pStartCoordinate.add(-1, 0);
				break;
			case NORTHWEST:
				moveCoordinate = pStartCoordinate.add(-1, -1);
				break;
			}
		}
		return moveCoordinate;
	}

	public static Direction findMoveDirection(ActionKey pActionKey) {
		Direction moveDirection = null;
		switch (pActionKey) {
			case PLAYER_MOVE_NORTH:
				moveDirection = Direction.NORTH;
				break;
			case PLAYER_MOVE_NORTHEAST:
				moveDirection = Direction.NORTHEAST;
				break;
			case PLAYER_MOVE_EAST:
				moveDirection = Direction.EAST;
				break;
		case PLAYER_MOVE_SOUTHEAST:
			moveDirection = Direction.SOUTHEAST;
			break;
		case PLAYER_MOVE_SOUTH:
			moveDirection = Direction.SOUTH;
			break;
		case PLAYER_MOVE_SOUTHWEST:
			moveDirection = Direction.SOUTHWEST;
			break;
		case PLAYER_MOVE_WEST:
			moveDirection = Direction.WEST;
			break;
		case PLAYER_MOVE_NORTHWEST:
			moveDirection = Direction.NORTHWEST;
			break;
		default:
			break;
		}
		return moveDirection;
	}

	public static Player<?> cyclePlayer(Game pGame, Player<?> pStartPlayer, boolean pRight) {
		Player<?> nextPlayer = null;
		if (pStartPlayer != null) {
			FieldCoordinate startPlayerPosition = pGame.getFieldModel().getPlayerCoordinate(pStartPlayer);
			if (pRight) {
				for (int y = 0; (nextPlayer == null) && (y < FieldCoordinate.FIELD_HEIGHT - startPlayerPosition.getY()); y++) {
					for (int x = 0; (nextPlayer == null)
							&& (x < FieldCoordinate.FIELD_WIDTH - startPlayerPosition.getX() - 2); x++) {
						if ((x != 0) || (y != 0)) {
							nextPlayer = findSelectableHomePlayer(pGame, startPlayerPosition.add(x, y));
						}
					}
					if (y > 0) {
						for (int x = -1; (nextPlayer == null) && (x > 1 - startPlayerPosition.getX()); x--) {
							nextPlayer = findSelectableHomePlayer(pGame, startPlayerPosition.add(x, y));
						}
					}
				}
			} else {
				for (int y = 0; (nextPlayer == null) && (y > -startPlayerPosition.getY()); y--) {
					for (int x = 0; (nextPlayer == null) && (x > 1 - startPlayerPosition.getX()); x--) {
						if ((x != 0) || (y != 0)) {
							nextPlayer = findSelectableHomePlayer(pGame, startPlayerPosition.add(x, y));
						}
					}
					if (y < 0) {
						for (int x = 1; (nextPlayer == null)
								&& (x < FieldCoordinate.FIELD_WIDTH - startPlayerPosition.getX() - 2); x++) {
							nextPlayer = findSelectableHomePlayer(pGame, startPlayerPosition.add(x, y));
						}
					}
				}
			}
		} else {
			Player<?>[] players = pGame.getTeamHome().getPlayers();
			for (int i = 0; i < players.length; i++) {
				PlayerState playerState = pGame.getFieldModel().getPlayerState(players[i]);
				if (playerState.isActive()) {
					if (nextPlayer == null) {
						nextPlayer = players[i];
					} else {
						FieldCoordinate playerCoordinate = pGame.getFieldModel().getPlayerCoordinate(players[i]);
						FieldCoordinate nextPlayerCoordinate = pGame.getFieldModel().getPlayerCoordinate(nextPlayer);
						if (pRight) {
							if (playerCoordinate.getX() > nextPlayerCoordinate.getX()) {
								nextPlayer = players[i];
							}
						} else {
							if (playerCoordinate.getX() < nextPlayerCoordinate.getX()) {
								nextPlayer = players[i];
							}
						}
					}
				}
			}
		}
		if (nextPlayer == null) {
			nextPlayer = pStartPlayer;
		}
		return nextPlayer;
	}

	private static Player<?> findSelectableHomePlayer(Game pGame, FieldCoordinate pCoordinate) {
		Player<?> player = pGame.getFieldModel().getPlayer(pCoordinate);
		if (player != null) {
			if (pGame.getTeamHome().hasPlayer(player)) {
				PlayerState playerState = pGame.getFieldModel().getPlayerState(player);
				if (playerState.isActive()) {
					return player;
				}
			}
		}
		return null;
	}

}
