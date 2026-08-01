package com.fumbbl.ffb.server.step.bb2025.command;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.step.DeferredCommand;
import com.fumbbl.ffb.server.step.DeferredCommandId;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerInjury;

@RulesCollection(RulesCollection.Rules.BB2025)
public class DropPlayerFromBombCommand extends DeferredCommand {

	private boolean suppressEndTurn, eligibleForSafePairOfHands, wasActive, causesTurnover;
	private String playerId;
	private ApothecaryMode apothecaryMode;

	@SuppressWarnings("unused")
	public DropPlayerFromBombCommand() {
		// for json deserialization
	}

	public DropPlayerFromBombCommand(String playerId, ApothecaryMode apothecaryMode, boolean eligibleForSafePairOfHands, boolean wasActive, boolean suppressEndTurn, boolean causesTurnover) {
		this.suppressEndTurn = suppressEndTurn;
		this.causesTurnover = causesTurnover;
		this.eligibleForSafePairOfHands = eligibleForSafePairOfHands;
		this.wasActive = wasActive;
		this.playerId = playerId;
		this.apothecaryMode = apothecaryMode;
	}

	@Override
	public void execute(IStep step) {
		Game game = step.getGameState().getGame();
		Player<?> player = game.getPlayerById(playerId);

		StepParameterSet parameterSet = UtilServerInjury.dropPlayer(step, player, apothecaryMode, eligibleForSafePairOfHands);
		PlayerState newState = game.getFieldModel().getPlayerState(player);
		if (!player.getId().equalsIgnoreCase(step.getGameState().getPassState().getOriginalBombardier()) && newState.isProneOrStunned()) {
			game.getFieldModel().setPlayerState(player, newState.changeActive(wasActive));
		}
		boolean droppedBall = parameterSet.remove(StepParameterKey.END_TURN);
		step.publishParameters(parameterSet);
		if (causesTurnover || (droppedBall && !suppressEndTurn)) {
			// this command only runs when the player actually goes down, so the turnover of this
			// player is final and must not be revoked by another player hit by the same bomb
			step.publishParameter(new StepParameter(StepParameterKey.TURNOVER_PLAYER_ID, playerId));
		}
	}

	@Override
	public DeferredCommandId getId() {
		return DeferredCommandId.DROP_PLAYER_FROM_BOMB;
	}
}
