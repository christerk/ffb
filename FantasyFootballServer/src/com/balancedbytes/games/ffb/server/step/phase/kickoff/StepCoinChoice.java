package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.dialog.DialogCoinChoiceParameter;
import com.balancedbytes.games.ffb.dialog.DialogReceiveChoiceParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.commands.ClientCommandCoinChoice;
import com.balancedbytes.games.ffb.report.ReportCoinThrow;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in kickoff sequence to choose coin.
 * 
 * Sets stepParameter CHOOSING_TEAM_ID for all steps on the stack.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public final class StepCoinChoice extends AbstractStep {

	protected Boolean fCoinChoiceHeads;

	public StepCoinChoice(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.COIN_CHOICE;
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
			case CLIENT_COIN_CHOICE:
				ClientCommandCoinChoice coinChoiceCommand = (ClientCommandCoinChoice) pReceivedCommand.getCommand();
				fCoinChoiceHeads = coinChoiceCommand.isChoiceHeads();
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
		if (fCoinChoiceHeads == null) {
			UtilServerDialog.showDialog(getGameState(), new DialogCoinChoiceParameter(), false);
		} else {
			boolean coinThrowHeads = getGameState().getDiceRoller().throwCoin();
			Team choosingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
			getResult().addReport(new ReportCoinThrow(coinThrowHeads, choosingTeam.getCoach(), fCoinChoiceHeads));
			if ((game.isHomePlaying() && (coinThrowHeads != fCoinChoiceHeads)
					|| (!game.isHomePlaying() && (coinThrowHeads == fCoinChoiceHeads)))) {
				choosingTeam = game.getTeamAway();
			} else {
				choosingTeam = game.getTeamHome();
			}
			publishParameter(new StepParameter(StepParameterKey.CHOOSING_TEAM_ID, choosingTeam.getId()));
			UtilServerDialog.showDialog(getGameState(), new DialogReceiveChoiceParameter(choosingTeam.getId()), false);
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.COIN_CHOICE_HEADS.addTo(jsonObject, fCoinChoiceHeads);
		return jsonObject;
	}

	@Override
	public StepCoinChoice initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fCoinChoiceHeads = IServerJsonOption.COIN_CHOICE_HEADS.getFrom(game, jsonObject);
		return this;
	}

}