package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.inducement.InducementPhase;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.factory.SequenceGeneratorFactory;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.generator.common.EndTurn;
import com.balancedbytes.games.ffb.server.step.generator.common.Inducement;
import com.balancedbytes.games.ffb.server.step.generator.SequenceGenerator;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step to end kickoff sequence.
 * 
 * Pushes endTurnSequence and selectSequence on stack when finishing.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
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
		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		((EndTurn)factory.forName(SequenceGenerator.Type.EndTurn.name()))
			.pushSequence(new SequenceGenerator.SequenceParams(getGameState()));
		((Inducement)factory.forName(SequenceGenerator.Type.Inducement.name()))
			.pushSequence(new Inducement.SequenceParams(getGameState(), InducementPhase.AFTER_KICKOFF_TO_OPPONENT,
			game.isHomePlaying()));
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
