package com.fumbbl.ffb.server.step.generator.bb2016;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2016)
public class BlitzBlock extends com.fumbbl.ffb.server.step.generator.BlitzBlock {

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push blitzBlockSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.INIT_BLOCKING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_BLOCKING),
			from(StepParameterKey.BLOCK_DEFENDER_ID, params.getBlockDefenderId()), from(StepParameterKey.USING_STAB, params.isUsingStab()),
			from(StepParameterKey.MULTI_BLOCK_DEFENDER_ID, params.getMultiBlockDefenderId()));
		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.TAKE_ROOT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.WILD_ANIMAL, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.BLOOD_LUST, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.GO_FOR_IT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		sequence.add(StepId.FOUL_APPEARANCE, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLOCKING));
		sequence.add(StepId.HORNS);
		sequence.add(StepId.BLOCK_STATISTICS);
		sequence.add(StepId.DAUNTLESS);
		sequence.add(StepId.DUMP_OFF);
		sequence.add(StepId.STAB, from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.APOTHECARY_DEFENDER));
		sequence.add(StepId.BLOCK_CHAINSAW, from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.APOTHECARY_DEFENDER),
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.APOTHECARY_ATTACKER));
		sequence.add(StepId.BLOCK_BALL_AND_CHAIN, from(StepParameterKey.GOTO_LABEL_ON_PUSHBACK, IStepLabel.PUSHBACK));
		sequence.add(StepId.BLOCK_ROLL);
		sequence.add(StepId.BLOCK_CHOICE, from(StepParameterKey.GOTO_LABEL_ON_DODGE, IStepLabel.DODGE_BLOCK),
			from(StepParameterKey.GOTO_LABEL_ON_JUGGERNAUT, IStepLabel.JUGGERNAUT),
			from(StepParameterKey.GOTO_LABEL_ON_PUSHBACK, IStepLabel.PUSHBACK));
		sequence.jump(IStepLabel.DROP_FALLING_PLAYERS);

		// on blockChoice = BOTH_DOWN
		sequence.add(StepId.JUGGERNAUT, IStepLabel.JUGGERNAUT,
			from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.PUSHBACK));
		sequence.add(StepId.BOTH_DOWN);
		sequence.add(StepId.WRESTLE);
		sequence.jump(IStepLabel.DROP_FALLING_PLAYERS);

		// on blockChoice = POW_PUSHBACK
		sequence.add(StepId.BLOCK_DODGE, IStepLabel.DODGE_BLOCK);

		// on blockChoice = POW or PUSHBACK
		sequence.add(StepId.PUSHBACK, IStepLabel.PUSHBACK);
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.CROWD_PUSH));
		sequence.add(StepId.FOLLOWUP);
		sequence.add(StepId.SHADOWING);
		sequence.add(StepId.PICK_UP, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.DROP_FALLING_PLAYERS));
		sequence.jump(IStepLabel.DROP_FALLING_PLAYERS);
		sequence.add(StepId.FALL_DOWN, IStepLabel.FALL_DOWN);
		sequence.jump(IStepLabel.APOTHECARY_ATTACKER);

		// on blockChoice = SKULL
		sequence.add(StepId.DROP_FALLING_PLAYERS, IStepLabel.DROP_FALLING_PLAYERS);
		sequence.add(StepId.APOTHECARY, IStepLabel.APOTHECARY_DEFENDER,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));

		// GFI for ball & chain should go here.
		sequence.add(StepId.GO_FOR_IT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.DROP_FALLING_PLAYERS),
			from(StepParameterKey.BALL_AND_CHAIN_GFI, true));
		sequence.jump(IStepLabel.APOTHECARY_ATTACKER);
		sequence.add(StepId.DROP_FALLING_PLAYERS, IStepLabel.DROP_FALLING_PLAYERS);
		sequence.add(StepId.FALL_DOWN);

		sequence.add(StepId.APOTHECARY, IStepLabel.APOTHECARY_ATTACKER,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ATTACKER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN, IStepLabel.SCATTER_BALL);
		sequence.add(StepId.END_BLOCKING, IStepLabel.END_BLOCKING);
		// may insert endTurn sequence add this point

		gameState.getStepStack().push(sequence.getSequence());

	}
}
