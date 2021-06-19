package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.bb2020.PrayerFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.bb2020.ReportPrayerRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.PrayerHandlerFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterSet;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepPrayer extends AbstractStep {
	private int roll;
	private String teamId;
	private boolean firstRun = true;

	public StepPrayer(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.PRAYER;
	}

	@Override
	public void init(StepParameterSet parameterSet) {
		if (parameterSet != null) {
			for (StepParameter parameter : parameterSet.values()) {
				switch (parameter.getKey()) {
					case PRAYER_ROLL:
						roll = (int) parameter.getValue();
						break;
					case TEAM_ID:
						teamId = (String) parameter.getValue();
						break;
					default:
						break;
				}
			}
		}
		super.init(parameterSet);
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus status = super.handleCommand(pReceivedCommand);
		if (status.equals(StepCommandStatus.UNHANDLED_COMMAND)) {
			switch (pReceivedCommand.getId()) {

				default:
					break;
			}
		}

		if (status.equals(StepCommandStatus.EXECUTE_STEP)) {
			executeStep();
		}

		return status;
	}

	@Override
	public void start() {
		executeStep();
	}

	private void executeStep() {
		if (firstRun) {
			firstRun = false;
			getResult().addReport(new ReportPrayerRoll(roll));
		}

		PrayerFactory prayerFactory = getGameState().getGame().getFactory(FactoryType.Factory.PRAYER);
		PrayerHandlerFactory handlerFactory = getGameState().getGame().getFactory(FactoryType.Factory.PRAYER_HANDLER);

		handlerFactory.forPrayer(prayerFactory.forRoll(roll)).ifPresent(handler ->
			handler.initEffect(this, getGameState(), teamId));

		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.ROLL.addTo(jsonObject, roll);
		IServerJsonOption.FIRST_RUN.addTo(jsonObject, firstRun);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		roll = IServerJsonOption.ROLL.getFrom(source, jsonObject);
		firstRun = IServerJsonOption.FIRST_RUN.getFrom(source, jsonObject);
		return this;
	}
}
