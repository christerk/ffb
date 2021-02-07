package com.balancedbytes.games.ffb.server.step.generator;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.step.IStepLabel;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;

import static com.balancedbytes.games.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ThrowTeamMate extends SequenceGenerator<ThrowTeamMate.SequenceParams> {

	protected ThrowTeamMate() {
		super(Type.ThrowTeamMate);
	}

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push throwTeamMateSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.INIT_THROW_TEAM_MATE, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_THROW_TEAM_MATE),
			from(StepParameterKey.THROWN_PLAYER_ID, params.thrownPlayerId),
			from(StepParameterKey.TARGET_COORDINATE, params.targetCoordinate));
		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.TAKE_ROOT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.WILD_ANIMAL, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_THROW_TEAM_MATE));
		sequence.add(StepId.BLOOD_LUST);
		sequence.add(StepId.ALWAYS_HUNGRY,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.EAT_TEAM_MATE),
			from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.FUMBLE_TTM_PASS));
		sequence.add(StepId.THROW_TEAM_MATE, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FUMBLE_TTM_PASS));
		// insert scatterPlayerSequence at this point
		sequence.jump(IStepLabel.RIGHT_STUFF);
		sequence.add(StepId.FUMBLE_TTM_PASS, IStepLabel.FUMBLE_TTM_PASS);
		sequence.add(StepId.RIGHT_STUFF, IStepLabel.RIGHT_STUFF);
		sequence.jump(IStepLabel.APOTHECARY_THROWN_PLAYER);
		sequence.add(StepId.EAT_TEAM_MATE, IStepLabel.EAT_TEAM_MATE);
		sequence.add(StepId.APOTHECARY, IStepLabel.APOTHECARY_THROWN_PLAYER,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.THROWN_PLAYER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_THROW_TEAM_MATE, IStepLabel.END_THROW_TEAM_MATE);

		gameState.getStepStack().push(sequence.getSequence());

	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final String thrownPlayerId;
		private final FieldCoordinate targetCoordinate;

		public SequenceParams(GameState gameState, String thrownPlayerId, FieldCoordinate targetCoordinate) {
			super(gameState);
			this.thrownPlayerId = thrownPlayerId;
			this.targetCoordinate = targetCoordinate;
		}
	}
}
