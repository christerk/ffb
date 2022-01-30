package com.fumbbl.ffb.server.step.bb2020;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;

@RulesCollection(RulesCollection.Rules.BB2020)
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
		Player<?> player = getGameState().getGame().getActingPlayer().getPlayer();
		PlayerState playerState = getGameState().getGame().getFieldModel().getPlayerState(player);
		TargetSelectionState targetSelectionState = getGameState().getGame().getFieldModel().getTargetSelectionState();
		if (targetSelectionState != null) {
			targetSelectionState.setOldPlayerState(playerState);
		}
		getGameState().getGame().getFieldModel().setPlayerState(player, playerState.recoverTacklezones());
		getResult().setNextAction(StepAction.NEXT_STEP);
	}
}
