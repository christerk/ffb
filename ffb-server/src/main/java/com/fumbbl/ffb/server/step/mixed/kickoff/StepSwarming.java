package com.fumbbl.ffb.server.step.mixed.kickoff;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.commands.ClientCommandEndTurn;
import com.fumbbl.ffb.net.commands.ClientCommandSetupPlayer;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerSetup;

@RulesCollection(RulesCollection.Rules.BB2016)
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepSwarming extends AbstractStep {

	public static class StepState {
		public ActionStatus status;
		public boolean endTurn;
		public boolean handleReceivingTeam;
		public int allowedAmount, rolledAmount = -1, limitingAmount  = -1;
		public String teamId;
	}

	private final StepState state;

	public StepSwarming(GameState pGameState) {
		super(pGameState);

		state = new StepState();
	}

	@Override
	public void start() {
		executeStep();
	}

	@Override
	public StepId getId() {
		return StepId.SWARMING;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		super.init(pParameterSet);
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.HANDLE_RECEIVING_TEAM) {
					state.handleReceivingTeam = (boolean) parameter.getValue();
				}
			}
		}
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);

		switch (pReceivedCommand.getId()) {
		case CLIENT_END_TURN:
			setPlayerCoordinates(((ClientCommandEndTurn) pReceivedCommand.getCommand()).getPlayerCoordinates());
			state.endTurn = true;
			executeStep();
			break;

		case CLIENT_SETUP_PLAYER:
			ClientCommandSetupPlayer setupPlayerCommand = (ClientCommandSetupPlayer) pReceivedCommand.getCommand();
			UtilServerSetup.setupPlayer(getGameState(), setupPlayerCommand.getPlayerId(), setupPlayerCommand.getCoordinate());
			break;
		default:
			break;
		}
		return commandStatus;
	}

	private void executeStep() {
		getGameState().executeStepHooks(this, state);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, state.endTurn);
		IServerJsonOption.HANDLE_RECEIVING_TEAM.addTo(jsonObject, state.handleReceivingTeam);
		IServerJsonOption.SWARMING_PLAYER_AMOUNT.addTo(jsonObject, state.allowedAmount);
		IServerJsonOption.SWARMING_PLAYER_LIMIT.addTo(jsonObject, state.limitingAmount);
		IServerJsonOption.SWARMING_PLAYER_ROLL.addTo(jsonObject, state.rolledAmount);
		IServerJsonOption.TEAM_ID.addTo(jsonObject, state.teamId);
		return jsonObject;
	}

	@Override
	public StepSwarming initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state.endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		state.handleReceivingTeam = IServerJsonOption.HANDLE_RECEIVING_TEAM.getFrom(source, jsonObject);
		state.allowedAmount = IServerJsonOption.SWARMING_PLAYER_AMOUNT.getFrom(source, jsonObject);
		if (IServerJsonOption.SWARMING_PLAYER_ROLL.isDefinedIn(jsonObject)) {
			state.rolledAmount = IServerJsonOption.SWARMING_PLAYER_ROLL.getFrom(source, jsonObject);
		}
		if (IServerJsonOption.SWARMING_PLAYER_LIMIT.isDefinedIn(jsonObject)) {
			state.limitingAmount = IServerJsonOption.SWARMING_PLAYER_LIMIT.getFrom(source, jsonObject);
		}
		state.teamId = IServerJsonOption.TEAM_ID.getFrom(source, jsonObject);
		return this;
	}

}
