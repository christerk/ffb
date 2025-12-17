package com.fumbbl.ffb.server.step.bb2025.shared;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.mixed.ReportPlayerEvent;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepStallingPlayer extends AbstractStep {

	private static final Set<PlayerAction> PREVENT_STALLING_ACTION = new HashSet<PlayerAction>() {{
		add(PlayerAction.PASS_MOVE);
		add(PlayerAction.HAND_OVER_MOVE);
	}};

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
		PlayerAction playerAction = actingPlayer.getPlayerAction();
		boolean gotRid = gotRidOfBall(playerAction, game);
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

	private boolean gotRidOfBall(PlayerAction playerAction, Game game) {
		return PREVENT_STALLING_ACTION.contains(playerAction) &&
			!UtilPlayer.hasBall(game, game.getActingPlayer().getPlayer());
	}
}
