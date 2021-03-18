package com.balancedbytes.games.ffb.server.step.bb2020;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepRemoveBlitzState extends AbstractStep {

	public StepRemoveBlitzState(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.REMOVE_BLITZ_STATE;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		BlitzState blitzState = getGameState().getBlitzState();
		if (blitzState != null) {
			getGameState().setBlitzState(null);
			Game game = getGameState().getGame();
			String playerId = blitzState.getSelectedPlayerId();
			if (playerId != null) {
				Player<?> player = game.getPlayerById(playerId);
				if (player != null) {
					PlayerState playerState = game.getFieldModel().getPlayerState(player);
					if (playerState != null) {
						game.getFieldModel().setPlayerState(player, playerState.removeSelectedBlitzTarget());
					}
				}
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}
}
