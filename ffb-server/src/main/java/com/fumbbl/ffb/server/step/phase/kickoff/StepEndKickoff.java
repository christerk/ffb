package com.fumbbl.ffb.server.step.phase.kickoff;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.InducementPhase;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.EndTurn;
import com.fumbbl.ffb.server.step.generator.common.Inducement;

/**
 * Step to end kickoff sequence.
 * <p>
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
			.pushSequence(new EndTurn.SequenceParams(getGameState(), false));
		((Inducement)factory.forName(SequenceGenerator.Type.Inducement.name()))
			.pushSequence(new Inducement.SequenceParams(getGameState(), InducementPhase.AFTER_KICKOFF_TO_OPPONENT,
			game.isHomePlaying()));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}


	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		return super.toJsonValue();
	}

	@Override
	public StepEndKickoff initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		return this;
	}

}
