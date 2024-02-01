package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.GameState;

public class UtilServerPlayerSwoop {
	public static void updateSwoopSquares(GameState pGameState, Player<?> swoopingPlayer) {
		Game game = pGameState.getGame();
		FieldModel fieldModel = game.getFieldModel();
		if (swoopingPlayer != null) {
			fieldModel.clearMoveSquares();
			FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(swoopingPlayer);
			if (FieldCoordinateBounds.FIELD.isInBounds(playerCoordinate)) {
				if (swoopingPlayer.hasSkillProperty(NamedProperties.ttmScattersInSingleDirection)) {
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
