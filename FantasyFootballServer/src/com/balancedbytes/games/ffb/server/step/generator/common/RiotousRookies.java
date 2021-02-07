package com.balancedbytes.games.ffb.server.step.generator.common;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.step.generator.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.phase.inducement.StepRiotousRookies;

@RulesCollection(RulesCollection.Rules.COMMON)
public class RiotousRookies extends SequenceGenerator<SequenceGenerator.SequenceParams> {

	public RiotousRookies() {
		super(Type.RiotousRookies);
	}

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push riotous rookies step onto stack");

		gameState.getStepStack().push(new StepRiotousRookies(gameState));
	}
}
