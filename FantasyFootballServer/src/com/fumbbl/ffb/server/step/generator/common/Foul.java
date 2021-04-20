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
public class Foul extends SequenceGenerator<Foul.SequenceParams> {

	public Foul() {
		super(Type.Foul);
	}

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(), "push foulSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.INIT_FOULING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FOULING),
			from(StepParameterKey.FOUL_DEFENDER_ID, params.fouldDefenderId), from(StepParameterKey.USING_CHAINSAW, params.usingChainsaw));
		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_FOULING));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_FOULING));
		sequence.add(StepId.TAKE_ROOT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_FOULING));
		sequence.add(StepId.WILD_ANIMAL, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_FOULING));
		sequence.add(StepId.BLOOD_LUST);
		sequence.add(StepId.FOUL_CHAINSAW, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.APOTHECARY_ATTACKER));
		sequence.add(StepId.FOUL);
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		sequence.add(StepId.REFEREE, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FOULING));
		sequence.add(StepId.BRIBES, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FOULING));
		sequence.add(StepId.EJECT_PLAYER, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FOULING));
		sequence.jump(IStepLabel.END_FOULING);
		sequence.add(StepId.APOTHECARY, IStepLabel.APOTHECARY_ATTACKER,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ATTACKER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN, IStepLabel.END_FOULING);
		sequence.add(StepId.END_FOULING);

		gameState.getStepStack().push(sequence.getSequence());
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final String fouldDefenderId;
		private final boolean usingChainsaw;

		public SequenceParams(GameState gameState, String fouldDefenderId, boolean usingChainsaw) {
			super(gameState);
			this.fouldDefenderId = fouldDefenderId;
			this.usingChainsaw = usingChainsaw;
		}

		public SequenceParams(GameState gameState) {
			this(gameState, null, false);
		}
	}
}