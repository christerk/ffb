package com.balancedbytes.games.ffb.server.step.generator;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.step.IStepLabel;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;

import static com.balancedbytes.games.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.COMMON)
public class SpecialEffect extends SequenceGenerator<SpecialEffect.SequenceParams> {

	public SpecialEffect() {
		super(Type.SpecialEffect);
	}

	@Override
	public void pushSequence(SequenceParams params) {
			GameState gameState = params.getGameState();
			gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
				"push specialEffectSequence onto stack (player " + params.playerId + ")");

			Sequence sequence = new Sequence(gameState);

			sequence.add(StepId.SPECIAL_EFFECT, from(StepParameterKey.SPECIAL_EFFECT, params.specialEffect),
				from(StepParameterKey.PLAYER_ID, params.playerId), from(StepParameterKey.ROLL_FOR_EFFECT, params.rollForEffect),
				from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SPECIAL_EFFECT));
			sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.SPECIAL_EFFECT));
			sequence.add(StepId.NEXT_STEP, IStepLabel.END_SPECIAL_EFFECT);

			gameState.getStepStack().push(sequence.getSequence());

		}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final com.balancedbytes.games.ffb.SpecialEffect specialEffect;
		private final String playerId;
		private final boolean rollForEffect;

		public SequenceParams(GameState gameState, com.balancedbytes.games.ffb.SpecialEffect specialEffect, String playerId, boolean rollForEffect) {
			super(gameState);
			this.specialEffect = specialEffect;
			this.playerId = playerId;
			this.rollForEffect = rollForEffect;
		}
	}
}
