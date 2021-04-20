package com.fumbbl.ffb.server.step.generator.common;

import static com.fumbbl.ffb.server.step.StepParameter.from;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;

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
		private final com.fumbbl.ffb.SpecialEffect specialEffect;
		private final String playerId;
		private final boolean rollForEffect;

		public SequenceParams(GameState gameState, com.fumbbl.ffb.SpecialEffect specialEffect, String playerId, boolean rollForEffect) {
			super(gameState);
			this.specialEffect = specialEffect;
			this.playerId = playerId;
			this.rollForEffect = rollForEffect;
		}
	}
}
