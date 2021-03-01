package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.inducement.InducementPhase;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandKickoff;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.factory.SequenceGeneratorFactory;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.generator.common.Inducement;
import com.balancedbytes.games.ffb.server.step.generator.SequenceGenerator;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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
	public StepKickoff initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fKickoffStartCoordinate = IServerJsonOption.KICKOFF_START_COORDINATE.getFrom(game, jsonObject);
		return this;
	}

}
