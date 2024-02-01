package com.fumbbl.ffb.server.step.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepSetActingTeam extends AbstractStep {

	private String teamId;

	public StepSetActingTeam(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.SET_ACTING_TEAM;
	}

	@Override
	public void init(StepParameterSet parameterSet) {
		if (parameterSet != null) {
			Arrays.stream(parameterSet.values()).filter(parameter ->
				parameter.getKey() == StepParameterKey.TEAM_ID
			).findFirst().ifPresent(parameter ->
				teamId = (String) parameter.getValue()
			);
		}
	}

	@Override
	public void start() {
		Game game = getGameState().getGame();
		game.getActingPlayer().setPlayer(null);

		Team team = game.getTeamById(teamId);

		if (team != game.getActingTeam()) {
			game.setHomePlaying(!game.isHomePlaying());
		}

		getResult().setNextAction(StepAction.NEXT_STEP);
	}
}
