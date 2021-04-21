package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogPileDriverParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.UtilServerDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepPileDriver extends AbstractStep {

	private final List<String> knockedDownPlayers = new ArrayList<>();
	private Phase phase = Phase.SELECT_TARGET;

	public StepPileDriver(GameState pGameState) {
		super(pGameState);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(StepParameterSet parameterSet) {
		if (parameterSet != null) {
			for (StepParameter parameter: parameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.PLAYER_IDS && parameter.getValue() != null) {
					knockedDownPlayers.addAll((Collection<? extends String>) parameter.getValue());
				}
			}
		}
		super.init(parameterSet);
	}

	@Override
	public StepId getId() {
		return StepId.PILE_DRIVER;
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND && UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
			switch (pReceivedCommand.getId()) {
				default:
					break;
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		switch (phase) {
			case INIT:
				UtilServerDialog.showDialog(getGameState(), new DialogPileDriverParameter(getGameState().getGame().getActingTeam().getId(), knockedDownPlayers), false);
				break;
			case SELECT_TARGET:
				break;
			case SELECT_CHAINSAW:
				break;
		}

	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.PLAYER_IDS.addTo(jsonObject, knockedDownPlayers);
		IServerJsonOption.STEP_PHASE.addTo(jsonObject, phase.name());
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		knockedDownPlayers.addAll(Arrays.stream(IServerJsonOption.PLAYER_IDS.getFrom(source, jsonObject)).collect(Collectors.toList()));
		phase = Phase.valueOf(IServerJsonOption.STEP_PHASE.getFrom(source, jsonObject));
		return this;
	}

	private enum Phase {
		INIT, SELECT_TARGET, SELECT_CHAINSAW
	}
}
