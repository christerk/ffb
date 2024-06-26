package com.fumbbl.ffb.util;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Kalimar
 */
public class UtilBox {

	public static void putAllPlayersIntoBox(Game pGame) {
		refreshBoxes(pGame);
		if (pGame != null) {
			for (Player<?> player : pGame.getPlayers()) {
				PlayerState playerState = pGame.getFieldModel().getPlayerState(player);
				if (playerState.canBeSetUpNextDrive()) {
					pGame.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.RESERVE));
					putPlayerIntoBox(pGame, player);
				}
			}
		}
	}

	public static void putPlayerIntoBox(Game pGame, Player<?> pPlayer) {
		if ((pGame != null) && (pPlayer != null)) {
			int boxX = 0;
			boolean homePlayer = pGame.getTeamHome().hasPlayer(pPlayer);
			PlayerState playerState = pGame.getFieldModel().getPlayerState(pPlayer);
			switch (playerState.getBase()) {
				case PlayerState.RESERVE:
				case PlayerState.EXHAUSTED:
				case PlayerState.SETUP_PREVENTED:
					boxX = homePlayer ? FieldCoordinate.RSV_HOME_X : FieldCoordinate.RSV_AWAY_X;
					break;
				case PlayerState.KNOCKED_OUT:
					boxX = homePlayer ? FieldCoordinate.KO_HOME_X : FieldCoordinate.KO_AWAY_X;
					break;
				case PlayerState.BADLY_HURT:
					boxX = homePlayer ? FieldCoordinate.BH_HOME_X : FieldCoordinate.BH_AWAY_X;
					break;
				case PlayerState.SERIOUS_INJURY:
					boxX = homePlayer ? FieldCoordinate.SI_HOME_X : FieldCoordinate.SI_AWAY_X;
					break;
				case PlayerState.RIP:
					boxX = homePlayer ? FieldCoordinate.RIP_HOME_X : FieldCoordinate.RIP_AWAY_X;
					break;
				case PlayerState.BANNED:
					boxX = homePlayer ? FieldCoordinate.BAN_HOME_X : FieldCoordinate.BAN_AWAY_X;
					break;
				case PlayerState.MISSING:
					boxX = homePlayer ? FieldCoordinate.MNG_HOME_X : FieldCoordinate.MNG_AWAY_X;
					break;
				default:
					break;
			}
			if (boxX != 0) {
				pGame.getFieldModel().remove(pPlayer);
				int y = 0;
				FieldCoordinate freeCoordinate = new FieldCoordinate(boxX, y);
				while (pGame.getFieldModel().getPlayer(freeCoordinate) != null) {
					freeCoordinate = new FieldCoordinate(boxX, ++y);
				}
				pGame.getFieldModel().setPlayerCoordinate(pPlayer, freeCoordinate);
			}
		}
	}

	public static void refreshBoxes(Game pGame) {
		refreshBox(pGame, FieldCoordinate.RSV_HOME_X);
		refreshBox(pGame, FieldCoordinate.RSV_AWAY_X);
		refreshBox(pGame, FieldCoordinate.KO_HOME_X);
		refreshBox(pGame, FieldCoordinate.KO_AWAY_X);
		refreshBox(pGame, FieldCoordinate.BH_HOME_X);
		refreshBox(pGame, FieldCoordinate.BH_AWAY_X);
		refreshBox(pGame, FieldCoordinate.SI_HOME_X);
		refreshBox(pGame, FieldCoordinate.SI_AWAY_X);
		refreshBox(pGame, FieldCoordinate.RIP_HOME_X);
		refreshBox(pGame, FieldCoordinate.RIP_AWAY_X);
		refreshBox(pGame, FieldCoordinate.BAN_HOME_X);
		refreshBox(pGame, FieldCoordinate.BAN_AWAY_X);
		refreshBox(pGame, FieldCoordinate.MNG_HOME_X);
		refreshBox(pGame, FieldCoordinate.MNG_AWAY_X);
	}

	private static void refreshBox(Game pGame, int pBoxX) {
		List<FieldCoordinate> coordinates = new ArrayList<>();
		for (FieldCoordinate coordinate : pGame.getFieldModel().getPlayerCoordinates()) {
			if (coordinate.getX() == pBoxX) {
				coordinates.add(coordinate);
			}
		}
		coordinates.sort(Comparator.comparingInt(FieldCoordinate::getY));
		for (int y = 0; y < coordinates.size(); y++) {
			Player<?> player = pGame.getFieldModel().getPlayer(coordinates.get(y));
			pGame.getFieldModel().setPlayerCoordinate(player, new FieldCoordinate(pBoxX, y));
		}
	}

}
