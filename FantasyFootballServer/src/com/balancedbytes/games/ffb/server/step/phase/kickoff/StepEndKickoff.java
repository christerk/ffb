package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.InducementPhase;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step to end kickoff sequence.
 * 
 * Pushes endTurnSequence and selectSequence on stack when finishing.
 * 
 * @author Kalimar
 */
public final class StepEndKickoff extends AbstractStep {

	public StepEndKickoff(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_KICKOFF;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		SequenceGenerator.getInstance().pushEndTurnSequence(getGameState());
		SequenceGenerator.getInstance().pushInducementSequence(getGameState(), InducementPhase.AFTER_KICKOFF_TO_OPPONENT,
				game.isHomePlaying());
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// ByteArray serialization

	public int getByteArraySerializationVersion() {
		return 1;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		return super.toJsonValue();
	}

	@Override
	public StepEndKickoff initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		return this;
	}

}
