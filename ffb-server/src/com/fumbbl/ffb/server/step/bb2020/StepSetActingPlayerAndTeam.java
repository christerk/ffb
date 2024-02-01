package com.fumbbl.ffb.server.step.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepSetActingPlayerAndTeam extends AbstractStep {

	private String playerId;

	public StepSetActingPlayerAndTeam(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.SET_ACTING_PLAYER_AND_TEAM;
	}

	@Override
	public void init(StepParameterSet parameterSet) {
		if (parameterSet != null) {
			Arrays.stream(parameterSet.values()).filter(parameter ->
				parameter.getKey() == StepParameterKey.PLAYER_ID
			).findFirst().ifPresent(parameter ->
				playerId = (String) parameter.getValue()
			);
		}
	}

	@Override
	public void start() {
		Game game = getGameState().getGame();
		Player<?> player = game.getPlayerById(playerId);
		game.getActingPlayer().setPlayer(player);
		if (player.getTeam() != game.getActingTeam()) {
			game.setHomePlaying(!game.isHomePlaying());
		}

		getResult().setNextAction(StepAction.NEXT_STEP);
	}
}
