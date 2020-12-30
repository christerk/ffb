package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandReceiveChoice;
import com.balancedbytes.games.ffb.report.ReportReceiveChoice;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in kickoff sequence to determine receive choice.
 * 
 * Expects stepParameter CHOOSING_TEAM_ID to be set by a preceding step.
 * 
 * @author Kalimar
 */
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
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case CHOOSING_TEAM_ID:
				fChoosingTeamId = (String) pParameter.getValue();
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
	public StepReceiveChoice initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fChoosingTeamId = IServerJsonOption.CHOOSING_TEAM_ID.getFrom(game, jsonObject);
		fReceiveChoice = IServerJsonOption.RECEIVE_CHOICE.getFrom(game, jsonObject);
		return this;
	}

}
