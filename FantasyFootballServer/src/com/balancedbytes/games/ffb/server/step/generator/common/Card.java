package com.balancedbytes.games.ffb.server.step.generator.common;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.generator.Sequence;
import com.balancedbytes.games.ffb.server.step.generator.SequenceGenerator;

import static com.balancedbytes.games.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.COMMON)
public class Card extends SequenceGenerator<Card.SequenceParams> {

	public Card() {
		super(Type.Card);
	}

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();

		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(), "push cardSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.PLAY_CARD, from(StepParameterKey.CARD, params.card), from(StepParameterKey.HOME_TEAM, params.homeTeam));

		gameState.getStepStack().push(sequence.getSequence());
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final com.balancedbytes.games.ffb.Card card;
		private final boolean homeTeam;

		public SequenceParams(GameState gameState, com.balancedbytes.games.ffb.Card card, boolean homeTeam) {
			super(gameState);
			this.card = card;
			this.homeTeam = homeTeam;
		}
	}
}
