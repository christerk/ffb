package com.fumbbl.ffb.server.step.bb2025.command;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.model.TurnoverUpdate;
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
		boolean endTurn = causesTurnover || (droppedBall && !suppressEndTurn);
		step.publishParameters(parameterSet);
		// track the turnover per player as several players may be hit by the same bomb
		// and each of them may avoid being knocked down on its own
		step.publishParameter(new StepParameter(StepParameterKey.TURNOVER_UPDATE, new TurnoverUpdate(playerId, endTurn)));
	}

	@Override
	public DeferredCommandId getId() {
		return DeferredCommandId.DROP_PLAYER_FROM_BOMB;
	}
}
