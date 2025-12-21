package com.fumbbl.ffb.server.admin;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.mixed.pass.state.PassState;
import com.fumbbl.ffb.util.UtilActingPlayer;

public class GameStateService {

	public void resetStepStack(GameState gameState) {
		Game game = gameState.getGame();
		UtilActingPlayer.changeActingPlayer(game, null, null, false);

		gameState.getStepStack().clear();

		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);

		Select generator = (Select) factory.forName(SequenceGenerator.Type.Select.name());

		generator.pushSequence(new Select.SequenceParams(gameState, true));
		gameState.startNextStep();

		game.setTurnMode(TurnMode.REGULAR);
		game.setLastTurnMode(null);
		game.setTimeoutEnforced(false);
		gameState.setPassState(new PassState());
		gameState.setBlitzTurnState(null);

		TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
		if (targetSelectionState != null) {
			String playerId = targetSelectionState.getSelectedPlayerId();
			if (playerId != null) {
				Player<?> player = game.getPlayerById(playerId);
				if (player != null) {
					PlayerState playerState = game.getFieldModel().getPlayerState(player);
					if (playerState != null) {
						game.getFieldModel().setPlayerState(player, playerState.removeAllTargetSelections());
					}
				}
			}
			game.getFieldModel().setTargetSelectionState(null);
		}
	}
}
