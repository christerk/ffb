package com.fumbbl.ffb.server.step.bb2025.start;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;

/**
 * Step in start game sequence that persists the game state as a recovery point.
 * <p>
 * The next action is set before the game state is persisted, so a game restored from this snapshot
 * resumes with the step following this one (see {@link GameState#resumeFinishedStep()}).
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepCheckpoint extends AbstractStep {

	public StepCheckpoint(GameState gameState) {
		super(gameState);
	}

	public StepId getId() {
		return StepId.CHECKPOINT;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		// both coaches have already started the game, a restored game must not ask them again
		if ((game.getDialogParameter() != null) && (game.getDialogParameter().getId() == DialogId.START_GAME)) {
			game.setDialogParameter(null);
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
		getGameState().getServer().getGameCache().queueDbUpdate(getGameState(), true);
	}

}
