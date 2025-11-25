package com.fumbbl.ffb.server.step.generator.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.BlockTarget;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;

import java.util.List;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2025)
public class MultiBlock extends SequenceGenerator<MultiBlock.SequenceParams> {
	public MultiBlock() {
		super(Type.MultiBlock);
	}

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push multiBlockSequence onto stack");

		Sequence sequence = new Sequence(gameState);
		ActivationSequenceBuilder.create().withFailureLabel(IStepLabel.END_BLOCKING).addTo(sequence);

		sequence.add(StepId.FOUL_APPEARANCE_MULTIPLE, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING),
			from(StepParameterKey.BLOCK_TARGETS, params.blockTargets));
		sequence.add(StepId.DISPATCH_DUMP_OFF, from(StepParameterKey.BLOCK_TARGETS, params.blockTargets));
		// might insert dump off here with game#defenderId set
		sequence.add(StepId.BLOCK_STATISTICS, from(StepParameterKey.INCREMENT, params.blockTargets.size()));
		sequence.add(StepId.MULTI_BLOCK_FORK, from(StepParameterKey.BLOCK_TARGETS, params.blockTargets));
		// inserts different sequences depending on the kind of blocks thrown
		sequence.add(StepId.PLACE_BALL);
		sequence.add(StepId.APOTHECARY_MULTIPLE, from(StepParameterKey.ACTING_TEAM, false));
		sequence.add(StepId.APOTHECARY_MULTIPLE, from(StepParameterKey.ACTING_TEAM, true));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN, IStepLabel.SCATTER_BALL);
		sequence.add(StepId.END_BLOCKING, IStepLabel.END_BLOCKING);
		// may insert endTurn sequence add this point

		gameState.getStepStack().push(sequence.getSequence());
	}


	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final List<BlockTarget> blockTargets;

		public SequenceParams(GameState gameState, List<BlockTarget> blockTargets) {
			super(gameState);
			this.blockTargets = blockTargets;
		}
	}
}
