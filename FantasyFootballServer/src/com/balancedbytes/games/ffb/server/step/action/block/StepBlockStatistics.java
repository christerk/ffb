package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to track if acting player has blocked, turn is started
 * etc.
 * 
 * @author Kalimar
 */
public class StepBlockStatistics extends AbstractStep {

	public StepBlockStatistics(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.BLOCK_STATISTICS;
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
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (!actingPlayer.hasBlocked()) {
			actingPlayer.setHasBlocked(true);
			game.getTurnData().setTurnStarted(true);
			game.setConcessionPossible(false);
			PlayerResult playerResult = game.getGameResult().getPlayerResult(actingPlayer.getPlayer());
			playerResult.setBlocks(playerResult.getBlocks() + 1);
		}
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
	public StepBlockStatistics initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		return this;
	}

}
