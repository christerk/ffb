package com.balancedbytes.games.ffb.server.step.generator.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.model.BlockTarget;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.step.IStepLabel;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.generator.Sequence;
import com.balancedbytes.games.ffb.server.step.generator.SequenceGenerator;

import java.util.List;

import static com.balancedbytes.games.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2020)
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

		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.TAKE_ROOT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.WILD_ANIMAL, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.BLOOD_LUST, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.GO_FOR_IT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		sequence.add(StepId.FOUL_APPEARANCE_MULTIPLE, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING),
			from(StepParameterKey.BLOCK_TARGETS, params.blockTargets));
		sequence.add(StepId.DISPATCH_DUMP_OFF, from(StepParameterKey.BLOCK_TARGETS, params.blockTargets));
		// might insert dump off here with game#defenderId set
		sequence.add(StepId.BLOCK_STATISTICS, from(StepParameterKey.INCREMENT, params.blockTargets.size()));
		sequence.add(StepId.DAUNTLESS_MULTIPLE, from(StepParameterKey.BLOCK_TARGETS, params.blockTargets));
		sequence.add(StepId.MULTI_BLOCK_FORK, from(StepParameterKey.BLOCK_TARGETS, params.blockTargets));
		/*sequence.add(StepId.DUMP_OFF);
		sequence.add(StepId.STAB, from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.APOTHECARY_DEFENDER));
		sequence.add(StepId.BLOCK_CHAINSAW, from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.APOTHECARY_DEFENDER),
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.APOTHECARY_ATTACKER));

		sequence.add(StepId.BLOCK_ROLL);
		sequence.add(StepId.BLOCK_CHOICE, from(StepParameterKey.GOTO_LABEL_ON_DODGE, IStepLabel.DODGE_BLOCK),
			from(StepParameterKey.GOTO_LABEL_ON_PUSHBACK, IStepLabel.PUSHBACK));
		sequence.jump(IStepLabel.DROP_FALLING_PLAYERS);

		sequence.add(StepId.BOTH_DOWN);
		sequence.add(StepId.WRESTLE);
		sequence.jump(IStepLabel.DROP_FALLING_PLAYERS);

		// on blockChoice = POW_PUSHBACK
		sequence.add(StepId.BLOCK_DODGE, IStepLabel.DODGE_BLOCK);

		// on blockChoice = POW or PUSHBACK
		sequence.add(StepId.PUSHBACK, IStepLabel.PUSHBACK);
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CROWD_PUSH));
		sequence.jump(IStepLabel.DROP_FALLING_PLAYERS);
		sequence.add(StepId.FALL_DOWN, IStepLabel.FALL_DOWN);
		sequence.jump(IStepLabel.APOTHECARY_ATTACKER);

		// on blockChoice = SKULL
		sequence.add(StepId.DROP_FALLING_PLAYERS, IStepLabel.DROP_FALLING_PLAYERS);
		sequence.add(StepId.APOTHECARY, IStepLabel.APOTHECARY_DEFENDER,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));

		sequence.jump(IStepLabel.APOTHECARY_ATTACKER);
		sequence.add(StepId.DROP_FALLING_PLAYERS, IStepLabel.DROP_FALLING_PLAYERS);
		sequence.add(StepId.FALL_DOWN);

		sequence.add(StepId.APOTHECARY, IStepLabel.APOTHECARY_ATTACKER,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ATTACKER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN, IStepLabel.SCATTER_BALL);*/
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
