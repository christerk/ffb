package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogKickOffResultParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.kickoff.KickoffResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandKickOffResultChoice;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionString;
import com.fumbbl.ffb.report.ReportKickoffResult;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.UtilServerDialog;

/**
 * Step in kickoff sequence to roll kickoff result.
 * <p>
 * Sets stepParameter KICKOFF_RESULT for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepKickoffResultRoll extends AbstractStep {

	private KickoffResult fKickoffResult;

	public StepKickoffResultRoll(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.KICKOFF_RESULT_ROLL;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);

		if (NetCommandId.CLIENT_KICK_OFF_RESULT_CHOICE.equals(pReceivedCommand.getCommand().getId())) {
			ClientCommandKickOffResultChoice command = (ClientCommandKickOffResultChoice) pReceivedCommand.getCommand();
			fKickoffResult = command.getKickoffResult();
			commandStatus = StepCommandStatus.EXECUTE_STEP;
		}

		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {

		UtilServerDialog.hideDialog(getGameState());

		int[] rollKickoff = new int[0];

		if (fKickoffResult == null) {
			Game game = getGameState().getGame();
			GameOptionString overTimeKickOffs = (GameOptionString) game.getOptions().getOptionWithDefault(GameOptionId.OVERTIME_KICK_OFF_RESULTS);
			if (game.getHalf() < 3 || GameOptionString.OVERTIME_KICK_OFF_ALL.equals(overTimeKickOffs.getValue())) {
				rollKickoff = getGameState().getDiceRoller().rollKickoff();
				fKickoffResult = DiceInterpreter.getInstance().interpretRollKickoff(getGameState().getGame(), rollKickoff);
			} else if (GameOptionString.OVERTIME_KICK_OFF_RANDOM_BLITZ_OR_SOLID_DEFENCE.equals(overTimeKickOffs.getValue())) {
				int[][] validRolls = {{1, 3}, {2, 2}, {3, 1}, {6, 4}, {5, 5}, {4, 6}};
				int index = getGameState().getDiceRoller().rollDice(6) - 1;
				rollKickoff = validRolls[index];
				fKickoffResult = DiceInterpreter.getInstance().interpretRollKickoff(getGameState().getGame(), rollKickoff);
			} else if (GameOptionString.OVERTIME_KICK_OFF_BLITZ.equals(overTimeKickOffs.getValue())) {
				fKickoffResult = com.fumbbl.ffb.kickoff.bb2020.KickoffResult.BLITZ;
			} else if (GameOptionString.OVERTIME_KICK_OFF_SOLID_DEFENCE.equals(overTimeKickOffs.getValue())) {
				fKickoffResult = com.fumbbl.ffb.kickoff.bb2020.KickoffResult.SOLID_DEFENCE;
			} else {
				UtilServerDialog.showDialog(getGameState(), new DialogKickOffResultParameter(game.getActingTeam().getId()), false);
				getResult().setNextAction(StepAction.CONTINUE);
				return;
			}

		}

		getResult().addReport(new ReportKickoffResult(fKickoffResult, rollKickoff));
		publishParameter(new StepParameter(StepParameterKey.KICKOFF_RESULT, fKickoffResult));
		getResult().setNextAction(StepAction.NEXT_STEP);


	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.KICKOFF_RESULT.addTo(jsonObject, fKickoffResult);
		return jsonObject;
	}

	@Override
	public StepKickoffResultRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fKickoffResult = (KickoffResult) IServerJsonOption.KICKOFF_RESULT.getFrom(source, jsonObject);
		return this;
	}

}
