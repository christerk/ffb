package com.fumbbl.ffb.server.model.change;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.util.StringTool;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ChompRemovalObserver implements ConditionalModelChangeObserver {
	@Override
	public void next(GameState gameState, ModelChange modelChange) {
		String key = modelChange.getKey();
		if (!StringTool.isProvided(key)) {
			return;
		}
		Game game = gameState.getGame();
		FieldModel fieldModel = game.getFieldModel();
		Player<?> player = game.getPlayerById(key);

		switch (modelChange.getChangeId()) {
			case FIELD_MODEL_SET_PLAYER_COORDINATE:
				FieldCoordinate coordinate = (FieldCoordinate) modelChange.getValue();

				if (coordinate.isBoxCoordinate()) {
					fieldModel.removeChomps(player);
				} else {
					fieldModel.updateChomps(player);
				}
				break;
			case FIELD_MODEL_SET_PLAYER_STATE:
				PlayerState playerState = (PlayerState) modelChange.getValue();

				if (!playerState.hasTacklezones()) {
					fieldModel.removeChomps(player);
				}

				break;
			default:
				break;
		}
	}
}
