package com.fumbbl.ffb.server.step.bb2025.kickoff;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.inducement.InducementPhase;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.*;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.mechanic.SetupMechanic;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.*;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.Inducement;
import com.fumbbl.ffb.server.util.UtilServerSetup;
import com.fumbbl.ffb.util.UtilBox;

@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepSetup extends AbstractStep {

	private boolean fEndSetup;

	public StepSetup(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.SETUP;
	}

	@Override
	public void start() {
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_TEAM_SETUP_LOAD:
					ClientCommandTeamSetupLoad loadSetupCommand = (ClientCommandTeamSetupLoad) pReceivedCommand.getCommand();
					UtilServerSetup.loadTeamSetup(getGameState(), loadSetupCommand.getSetupName());
					commandStatus = StepCommandStatus.SKIP_STEP;
					break;
				case CLIENT_TEAM_SETUP_SAVE:
					ClientCommandTeamSetupSave saveSetupCommand = (ClientCommandTeamSetupSave) pReceivedCommand.getCommand();
					UtilServerSetup.saveTeamSetup(getGameState(), saveSetupCommand.getSetupName(),
						saveSetupCommand.getPlayerNumbers(), saveSetupCommand.getPlayerCoordinates());
					commandStatus = StepCommandStatus.SKIP_STEP;
					break;
				case CLIENT_TEAM_SETUP_DELETE:
					ClientCommandTeamSetupDelete deleteSetupCommand = (ClientCommandTeamSetupDelete) pReceivedCommand.getCommand();
					UtilServerSetup.deleteTeamSetup(getGameState(), deleteSetupCommand.getSetupName());
					commandStatus = StepCommandStatus.SKIP_STEP;
					break;
				case CLIENT_SETUP_PLAYER:
					ClientCommandSetupPlayer setupPlayerCommand = (ClientCommandSetupPlayer) pReceivedCommand.getCommand();
					UtilServerSetup.setupPlayer(getGameState(), setupPlayerCommand.getPlayerId(),
						setupPlayerCommand.getCoordinate());
					commandStatus = StepCommandStatus.SKIP_STEP;
					break;
				case CLIENT_END_TURN:
					if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
						setPlayerCoordinates(((ClientCommandEndTurn) pReceivedCommand.getCommand()).getPlayerCoordinates());
						fEndSetup = true;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
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
		Game game = getGameState().getGame();
		if (fEndSetup) {
			getResult().setSound(SoundId.DING);
			MechanicsFactory mechanicsFactory = game.getFactory(FactoryType.Factory.MECHANIC);
			SetupMechanic mechanic = (SetupMechanic) mechanicsFactory.forName(Mechanic.Type.SETUP.name());
			if (mechanic.checkSetup(getGameState(), game.isHomePlaying())) {
				game.setHomePlaying(!game.isHomePlaying());
				game.getTurnData().setTurnStarted(false);
				game.getTurnData().setFirstTurnAfterKickoff(false);
				UtilBox.refreshBoxes(game);
				if (!game.isSetupOffense()) {
					game.setSetupOffense(true);
					SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
					((Inducement) factory.forName(SequenceGenerator.Type.Inducement.name()))
						.pushSequence(new Inducement.SequenceParams(getGameState(), InducementPhase.BEFORE_SETUP, game.isHomePlaying()));
					((Inducement) factory.forName(SequenceGenerator.Type.Inducement.name()))
						.pushSequence(new Inducement.SequenceParams(getGameState(), InducementPhase.BEFORE_SETUP, !game.isHomePlaying()));
				}
				getResult().setNextAction(StepAction.NEXT_STEP);
			} else {
				fEndSetup = false;
			}
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_KICKOFF.addTo(jsonObject, fEndSetup);
		return jsonObject;
	}

	@Override
	public StepSetup initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fEndSetup = IServerJsonOption.END_KICKOFF.getFrom(source, jsonObject);
		return this;
	}

}
