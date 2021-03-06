package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.MoveSquare;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.server.GameState;

public class UtilServerPlayerSwoop {
	public static void updateSwoopSquares(GameState pGameState, Player<?> swoopingPlayer) {
		Game game = pGameState.getGame();
		FieldModel fieldModel = game.getFieldModel();
		if (swoopingPlayer != null) {
			fieldModel.clearMoveSquares();
			FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(swoopingPlayer);
			if (FieldCoordinateBounds.FIELD.isInBounds(playerCoordinate)) {
				if (swoopingPlayer.hasSkillWithProperty(NamedProperties.ttmScattersInSingleDirection)) {
					for (int x = -1; x < 2; x += 2) {
						FieldCoordinate moveCoordinate = playerCoordinate.add(x, 0);
						if (FieldCoordinateBounds.FIELD.isInBounds(moveCoordinate)) {
							addSwoopSquare(pGameState, moveCoordinate);
						}
					}
					for (int y = -1; y < 2; y += 2) {
						FieldCoordinate moveCoordinate = playerCoordinate.add(0, y);
						if (FieldCoordinateBounds.FIELD.isInBounds(moveCoordinate)) {
							addSwoopSquare(pGameState, moveCoordinate);
						}
					}
				}
			}
		}
	}

	private static void addSwoopSquare(GameState pGameState, FieldCoordinate pCoordinate) {
		Game game = pGameState.getGame();
		FieldModel fieldModel = game.getFieldModel();
		MoveSquare moveSquare = new MoveSquare(pCoordinate, 0, 0);
		fieldModel.add(moveSquare);
	}
}
