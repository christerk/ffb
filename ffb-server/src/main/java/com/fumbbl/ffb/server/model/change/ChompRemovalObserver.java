package com.fumbbl.ffb.server.model.change;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.report.bb2025.ReportChompRemoved;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.util.StringTool;

import java.util.HashMap;
import java.util.Map;

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

		Map<String, Boolean> chompUpdates = new HashMap<>();

		switch (modelChange.getChangeId()) {
			case FIELD_MODEL_SET_PLAYER_COORDINATE:
				FieldCoordinate coordinate = (FieldCoordinate) modelChange.getValue();

				if (coordinate.isBoxCoordinate()) {
					chompUpdates.putAll(fieldModel.removeChomps(player));
				} else {
					chompUpdates.putAll(fieldModel.updateChomps(player));
				}
				break;
			case FIELD_MODEL_SET_PLAYER_STATE:
				PlayerState playerState = (PlayerState) modelChange.getValue();

				if (!playerState.hasTacklezones()) {
					chompUpdates.putAll(fieldModel.removeChomps(player));
				}

				break;
			default:
				break;
		}

		IStep currentStep = gameState.getCurrentStep();

		if (currentStep != null) {
			chompUpdates.entrySet().stream().filter(entry -> entry.getValue() != null)
				.forEach(entry -> currentStep.getResult().addReport(new ReportChompRemoved(entry.getKey(), entry.getValue())));
		}
	}
}
