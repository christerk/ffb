package com.fumbbl.ffb.server.step.phase.kickoff;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandReceiveChoice;
import com.fumbbl.ffb.report.ReportReceiveChoice;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.util.UtilServerDialog;

/**
 * Step in kickoff sequence to determine receive choice.
 * 
 * Expects stepParameter CHOOSING_TEAM_ID to be set by a preceding step.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public final class StepReceiveChoice extends AbstractStep {

	private String fChoosingTeamId;
	private Boolean fReceiveChoice;

	public StepReceiveChoice(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.RECEIVE_CHOICE;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
			case CHOOSING_TEAM_ID:
				fChoosingTeamId = (String) parameter.getValue();
				return true;
			default:
				break;
			}
		}
		return false;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
			case CLIENT_RECEIVE_CHOICE:
				ClientCommandReceiveChoice receiveChoiceCommand = (ClientCommandReceiveChoice) pReceivedCommand.getCommand();
				fReceiveChoice = receiveChoiceCommand.isChoiceReceive();
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
		Game game = getGameState().getGame();
		if (fReceiveChoice != null) {
			UtilServerDialog.hideDialog(getGameState());
			if (game.getTeamHome().getId().equals(fChoosingTeamId)) {
				game.setHomePlaying(!fReceiveChoice);
				getResult().addReport(new ReportReceiveChoice(game.getTeamHome().getId(), fReceiveChoice));
			} else {
				game.setHomePlaying(fReceiveChoice);
				getResult().addReport(new ReportReceiveChoice(game.getTeamAway().getId(), fReceiveChoice));
			}
			game.setHomeFirstOffense(!game.isHomePlaying());
			game.setSetupOffense(false);
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.CHOOSING_TEAM_ID.addTo(jsonObject, fChoosingTeamId);
		IServerJsonOption.RECEIVE_CHOICE.addTo(jsonObject, fReceiveChoice);
		return jsonObject;
	}

	@Override
	public StepReceiveChoice initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fChoosingTeamId = IServerJsonOption.CHOOSING_TEAM_ID.getFrom(source, jsonObject);
		fReceiveChoice = IServerJsonOption.RECEIVE_CHOICE.getFrom(source, jsonObject);
		return this;
	}

}
