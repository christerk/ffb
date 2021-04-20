package com.fumbbl.ffb.server.step.generator.common;

import static com.fumbbl.ffb.server.step.StepParameter.from;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;

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
		private final com.fumbbl.ffb.inducement.Card card;
		private final boolean homeTeam;

		public SequenceParams(GameState gameState, com.fumbbl.ffb.inducement.Card card, boolean homeTeam) {
			super(gameState);
			this.card = card;
			this.homeTeam = homeTeam;
		}
	}
}
