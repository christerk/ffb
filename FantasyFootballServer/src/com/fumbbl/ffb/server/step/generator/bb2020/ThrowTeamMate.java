package com.fumbbl.ffb.server.step.generator.bb2020;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ThrowTeamMate extends com.fumbbl.ffb.server.step.generator.ThrowTeamMate {

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push throwTeamMateSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.INIT_THROW_TEAM_MATE, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_THROW_TEAM_MATE),
			from(StepParameterKey.THROWN_PLAYER_ID, params.getThrownPlayerId()),
			from(StepParameterKey.IS_KICKED_PLAYER, params.isKicked()),
			from(StepParameterKey.TARGET_COORDINATE, params.getTargetCoordinate()));
		sequence.add(StepId.INIT_ACTIVATION);
		sequence.add(StepId.ANIMAL_SAVAGERY,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.DROP_FALLING_PLAYERS);
		sequence.add(StepId.PLACE_BALL);
		sequence.add(StepId.APOTHECARY,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ANIMAL_SAVAGERY));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.SET_DEFENDER, from(StepParameterKey.BLOCK_DEFENDER_ID, params.getThrownPlayerId()));
		sequence.add(StepId.GOTO_LABEL, from(StepParameterKey.GOTO_LABEL, IStepLabel.NEXT), from(StepParameterKey.ALTERNATE_GOTO_LABEL, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.BONE_HEAD, IStepLabel.NEXT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.TAKE_ROOT);
		sequence.add(StepId.UNCHANNELLED_FURY, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.ALWAYS_HUNGRY,
			from(StepParameterKey.IS_KICKED_PLAYER, params.isKicked()),
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.EAT_TEAM_MATE),
			from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.RESOLVE_PASS));
		sequence.add(StepId.THROW_TEAM_MATE,
			from(StepParameterKey.IS_KICKED_PLAYER, params.isKicked()));
		sequence.add(StepId.DISPATCH_SCATTER_PLAYER, IStepLabel.RESOLVE_PASS, from(StepParameterKey.IS_KICKED_PLAYER, params.isKicked()));
		// may insert scatterPlayerSequence at this point
		sequence.add(StepId.RIGHT_STUFF, IStepLabel.RIGHT_STUFF,
			from(StepParameterKey.IS_KICKED_PLAYER, params.isKicked()),
			from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.END_SCATTER_PLAYER));
		sequence.jump(IStepLabel.APOTHECARY_THROWN_PLAYER);
		sequence.add(StepId.PICK_UP, IStepLabel.END_SCATTER_PLAYER,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.SCATTER_BALL),
			from(StepParameterKey.THROWN_PLAYER_ID, params.getThrownPlayerId()));
		sequence.jump(IStepLabel.END_THROW_TEAM_MATE);
		sequence.add(StepId.EAT_TEAM_MATE, IStepLabel.EAT_TEAM_MATE);
		sequence.add(StepId.APOTHECARY, IStepLabel.APOTHECARY_THROWN_PLAYER,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.THROWN_PLAYER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN, IStepLabel.SCATTER_BALL);
		sequence.add(StepId.END_THROW_TEAM_MATE, IStepLabel.END_THROW_TEAM_MATE);

		gameState.getStepStack().push(sequence.getSequence());

	}
}
