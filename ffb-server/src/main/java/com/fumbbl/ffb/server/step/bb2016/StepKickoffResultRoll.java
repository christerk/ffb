package com.fumbbl.ffb.server.step.bb2016;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.kickoff.KickoffResult;
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
 * 
 * Sets stepParameter KICKOFF_RESULT for all steps on the stack.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
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
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {

		UtilServerDialog.hideDialog(getGameState());

		int[] rollKickoff = getGameState().getDiceRoller().rollKickoff();
		fKickoffResult = DiceInterpreter.getInstance().interpretRollKickoff(getGameState().getGame(), rollKickoff);
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
