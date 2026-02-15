package com.fumbbl.ffb.server.step.mixed;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.util.UtilServerInjury;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepDropActingPlayer extends AbstractStep {

	public StepDropActingPlayer(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.DROP_ACTING_PLAYER;
	}

	@Override
	public void start() {
		super.start();

		Game game = getGameState().getGame();
		Player<?> player = game.getActingPlayer().getPlayer();
		FieldCoordinate coordinate = game.getFieldModel().getPlayerCoordinate(player);
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		if (FieldCoordinateBounds.FIELD.isInBounds(coordinate) && playerState.getBase() != PlayerState.STUNNED) {
			publishParameters(UtilServerInjury.dropPlayer(this, player, ApothecaryMode.DROPPED_BY_OWN_SKILL, true));
		}

		getResult().setNextAction(StepAction.NEXT_STEP);
	}
}
