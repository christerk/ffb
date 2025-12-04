package com.fumbbl.ffb.server.step.bb2025.shared;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepInitActivation extends AbstractStep {
	public StepInitActivation(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.INIT_ACTIVATION;
	}

	@Override
	public void start() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Player<?> player = actingPlayer.getPlayer();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
		if (targetSelectionState != null) {
			targetSelectionState.setOldActingPlayerState(playerState);
		}
		game.getFieldModel().setPlayerState(player, playerState.recoverTacklezones().clearEyeGouge());
		getResult().setNextAction(StepAction.NEXT_STEP);
	}
}
