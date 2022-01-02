package com.fumbbl.ffb.server.step.bb2020.blitz;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepRemoveTargetSelectionState extends AbstractStep {

	public StepRemoveTargetSelectionState(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.REMOVE_TARGET_SELECTION_STATE;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
		if (targetSelectionState != null) {
			game.getFieldModel().setTargetSelectionState(null);
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
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}
}
