package com.fumbbl.ffb.server.model.change;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.model.CarriedPlayer;
import com.fumbbl.ffb.util.StringTool;

@RulesCollection(RulesCollection.Rules.BB2025)
public class CarriedPlayerCoordinateObserver implements ConditionalModelChangeObserver {

	@Override
	public void next(GameState gameState, ModelChange modelChange) {

		String key = modelChange.getKey();
		if (!StringTool.isProvided(key)) {
			return;
		}
		CarriedPlayer carriedPlayer = gameState.getCarriedPlayer();

		if (carriedPlayer == null || !gameState.getGame().getActingPlayer().getPlayerId().equals(modelChange.getKey())) {
			return;
		}

		switch (modelChange.getChangeId()) {
			case FIELD_MODEL_SET_PLAYER_COORDINATE:
			case FIELD_MODEL_REMOVE_PLAYER:
				FieldCoordinate coordinate = (FieldCoordinate) modelChange.getValue();
				if (coordinate != null && !coordinate.isBoxCoordinate()) {
					carriedPlayer.setCarrierCoordinate(coordinate);
				}
				break;
			default:
				break;
		}
	}
}
