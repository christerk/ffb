package com.fumbbl.ffb.server.step.bb2025.shared;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.report.mixed.ReportPlayerEvent;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepForgoneStalling extends AbstractStep {


	private final StallingExtension stallingExtension = new StallingExtension();

	public StepForgoneStalling(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.FORGONE_STALLING;
	}

	@Override
	public void start() {
		getResult().setNextAction(StepAction.NEXT_STEP);
		Game game = getGameState().getGame();
		Arrays.stream(game.getActingTeam().getPlayers()).filter(pl -> UtilPlayer.hasBall(game, pl) &&
				game.getFieldModel().getPlayerState(pl).isActive()).findFirst().ifPresent(
				player -> {
					if (stallingExtension.isConsideredStalling(game, player)) {
						getResult().addReport(new ReportPlayerEvent(player.getId(), "is stalling"));
						stallingExtension.handleStaller(this, player);
					}
				}
		);
	}
}
