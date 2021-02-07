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
public class KickTeamMate extends SequenceGenerator<KickTeamMate.SequenceParams>{

	public KickTeamMate() {
		super(Type.KickTeamMate);
	}

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push kickTeamMateSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.INIT_KICK_TEAM_MATE, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_KICK_TEAM_MATE),
			from(StepParameterKey.KICKED_PLAYER_ID, params.kickedPlayerId), from(StepParameterKey.NR_OF_DICE, params.numDice));
		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_KICK_TEAM_MATE));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_KICK_TEAM_MATE));
		sequence.add(StepId.TAKE_ROOT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_KICK_TEAM_MATE));
		sequence.add(StepId.WILD_ANIMAL, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_KICK_TEAM_MATE));
		sequence.add(StepId.BLOOD_LUST);
		sequence.add(StepId.KICK_TEAM_MATE,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.KICK_TM_DOUBLE_ROLLED));
		// insert scatterPlayerSequence at this point
		sequence.jump(IStepLabel.RIGHT_STUFF);
		sequence.add(StepId.KICK_TM_DOUBLE_ROLLED, IStepLabel.KICK_TM_DOUBLE_ROLLED);
		sequence.jump(IStepLabel.APOTHECARY_KICKED_PLAYER);
		sequence.add(StepId.RIGHT_STUFF, IStepLabel.RIGHT_STUFF);
		sequence.jump(IStepLabel.APOTHECARY_KICKED_PLAYER);
		sequence.add(StepId.APOTHECARY, IStepLabel.APOTHECARY_KICKED_PLAYER,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.THROWN_PLAYER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_KICK_TEAM_MATE, IStepLabel.END_KICK_TEAM_MATE);

		gameState.getStepStack().push(sequence.getSequence());
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final int numDice;
		private final String kickedPlayerId;

		public SequenceParams(GameState gameState, int numDice, String kickedPlayerId) {
			super(gameState);
			this.numDice = numDice;
			this.kickedPlayerId = kickedPlayerId;
		}

		public SequenceParams(GameState gameState) {
			this(gameState, 0, null);
		}
	}
}
