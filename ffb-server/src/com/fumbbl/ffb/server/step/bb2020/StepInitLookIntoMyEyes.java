package com.fumbbl.ffb.server.step.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.Optional;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepInitLookIntoMyEyes extends AbstractStepWithReRoll {

	public StepInitLookIntoMyEyes(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.INIT_LOOK_INTO_MY_EYES;
	}


	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {

		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		game.setDefenderId(null);
		target(game, actingPlayer).ifPresent(game::setDefenderId);
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private Optional<String> target(Game game, ActingPlayer actingPlayer) {
		return Arrays.stream(UtilPlayer.findAdjacentBlockablePlayers(game, game.getOtherTeam(game.getActingTeam()), game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer())))
			.filter(adjacentPlayer -> UtilPlayer.hasBall(game, adjacentPlayer)).map(Player::getId).findFirst();
	}
}
