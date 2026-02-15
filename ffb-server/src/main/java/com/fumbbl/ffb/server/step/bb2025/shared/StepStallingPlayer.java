package com.fumbbl.ffb.server.step.bb2025.shared;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.mixed.ReportPlayerEvent;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.util.UtilPlayer;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepStallingPlayer extends AbstractStep {

	private final StallingExtension stallingExtension = new StallingExtension();

	public StepStallingPlayer(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.STALLING_PLAYER;
	}

	@Override
	public void start() {
		getResult().setNextAction(StepAction.NEXT_STEP);

		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Player<?> player = actingPlayer.getPlayer();

		if (player == null || !UtilGameOption.isOptionEnabled(game, GameOptionId.ENABLE_STALLING_CHECK)) {
			// This is part of EndPlayerAction which is also triggered when ending your turn
			getGameState().resetStalling();
			return;
		}

		boolean gotRid = getGameState().isStalling() && !UtilPlayer.hasBall(game, player);
		boolean scored = UtilServerSteps.checkTouchdown(getGameState());
		boolean noStalling = !getGameState().isStalling() || gotRid || scored;


		getGameState().resetStalling();

		if (noStalling || game.getFieldModel().getPlayerState(player).isProneOrStunned()) {
			if (gotRid || scored) {
				getResult().addReport(new ReportPlayerEvent(player.getId(), "did not stall after all"));
			}
			return;
		}

		stallingExtension.handleStaller(this, player);

	}
}
