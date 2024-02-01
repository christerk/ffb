package com.fumbbl.ffb.server.step.phase.kickoff;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.InducementPhase;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandKickoff;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.Inducement;
import com.fumbbl.ffb.server.util.UtilServerDialog;

/**
 * Step in the kickoff sequence to place the kickoff.
 * 
 * Sets stepParameter KICKOFF_START_COORDINATE for all steps on the stack.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public final class StepKickoff extends AbstractStep {

	private FieldCoordinate fKickoffStartCoordinate;

	public StepKickoff(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.KICKOFF;
	}

	@Override
	public void start() {
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			Game game = getGameState().getGame();
			switch (pReceivedCommand.getId()) {
			case CLIENT_KICKOFF:
				ClientCommandKickoff kickoffCommand = (ClientCommandKickoff) pReceivedCommand.getCommand();
				if (game.isHomePlaying()) {
					fKickoffStartCoordinate = kickoffCommand.getBallCoordinate();
				} else {
					fKickoffStartCoordinate = kickoffCommand.getBallCoordinate().transform();
				}
				commandStatus = StepCommandStatus.EXECUTE_STEP;
				break;
			default:
				break;
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		if (fKickoffStartCoordinate != null) {
			Game game = getGameState().getGame();
			UtilServerDialog.hideDialog(getGameState());
			publishParameter(new StepParameter(StepParameterKey.KICKOFF_START_COORDINATE, fKickoffStartCoordinate));
			SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
			Inducement generator = (Inducement) factory.forName(SequenceGenerator.Type.Inducement.name());
			generator.pushSequence(new Inducement.SequenceParams(getGameState(), InducementPhase.BEFORE_KICKOFF_SCATTER,
					game.isHomePlaying()));
			generator.pushSequence(new Inducement.SequenceParams(getGameState(), InducementPhase.BEFORE_KICKOFF_SCATTER,
					!game.isHomePlaying()));
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.KICKOFF_START_COORDINATE.addTo(jsonObject, fKickoffStartCoordinate);
		return jsonObject;
	}

	@Override
	public StepKickoff initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fKickoffStartCoordinate = IServerJsonOption.KICKOFF_START_COORDINATE.getFrom(source, jsonObject);
		return this;
	}

}
