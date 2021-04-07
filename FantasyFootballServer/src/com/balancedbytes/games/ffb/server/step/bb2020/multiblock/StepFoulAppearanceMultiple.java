package com.balancedbytes.games.ffb.server.step.bb2020.multiblock;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.BlockTarget;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseReRollForTarget;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepFoulAppearanceMultiple extends AbstractStep {

	public static class StepState {
		public String goToLabelOnFailure;
		public List<String> reRollAvailableAgainst = new ArrayList<>();
		public List<String> blockTargets = new ArrayList<>();
		public boolean firstRun = true, teamReRollAvailable, proReRollAvailable;
		public ReRollSource reRollSource;
		public String reRollTarget;
		public Map<String, Integer> minimumRolls = new HashMap<>();
	}

	private final StepState state;

	public StepFoulAppearanceMultiple(GameState pGameState) {
		super(pGameState);
		state = new StepState();
	}

	public StepId getId() {
		return StepId.FOUL_APPEARANCE_MULTIPLE;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					case GOTO_LABEL_ON_FAILURE:
						state.goToLabelOnFailure = (String) parameter.getValue();
						break;
					case BLOCK_TARGETS:
						//noinspection unchecked
						state.blockTargets.addAll(((List<BlockTarget>) parameter.getValue()).stream().map(BlockTarget::getPlayerId).collect(Collectors.toList()));
						break;
					default:
						break;
				}
			}
		}
		if (state.goToLabelOnFailure == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
		}
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
			if (pReceivedCommand.getId() == NetCommandId.CLIENT_USE_RE_ROLL_FOR_TARGET) {
				ClientCommandUseReRollForTarget command = (ClientCommandUseReRollForTarget) pReceivedCommand.getCommand();
				if (command.getReRolledAction() == ReRolledActions.FOUL_APPEARANCE) {
					state.reRollSource = command.getReRollSource();
					state.reRollTarget = command.getTargetId();
					commandStatus = StepCommandStatus.EXECUTE_STEP;
				}
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		getGameState().executeStepHooks(this, state);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, state.goToLabelOnFailure);
		IJsonOption.PLAYER_IDS.addTo(jsonObject, state.blockTargets);
		IJsonOption.PLAYER_ID.addTo(jsonObject, state.reRollTarget);
		IJsonOption.FIRST_RUN.addTo(jsonObject, state.firstRun);
		IJsonOption.PRO_RE_ROLL_OPTION.addTo(jsonObject, state.proReRollAvailable);
		IJsonOption.TEAM_RE_ROLL_OPTION.addTo(jsonObject, state.teamReRollAvailable);
		IJsonOption.TEAM_RE_ROLL_AVAILABLE_AGAINST.addTo(jsonObject, state.reRollAvailableAgainst);
		IJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, state.reRollSource);
		IJsonOption.MINIMUM_ROLLS.addTo(jsonObject, state.minimumRolls);
		return jsonObject;
	}

	@Override
	public StepFoulAppearanceMultiple initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		state.goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(game, jsonObject);
		state.blockTargets = Arrays.asList(IJsonOption.PLAYER_IDS.getFrom(game, jsonObject));
		state.reRollTarget = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		state.firstRun = IJsonOption.FIRST_RUN.getFrom(game, jsonObject);
		state.proReRollAvailable = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(game, jsonObject);
		state.teamReRollAvailable = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(game, jsonObject);
		state.reRollAvailableAgainst = Arrays.asList(IJsonOption.TEAM_RE_ROLL_AVAILABLE_AGAINST.getFrom(game, jsonObject));
		state.reRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE.getFrom(game, jsonObject);
		state.minimumRolls = IJsonOption.MINIMUM_ROLLS.getFrom(game, jsonObject);
		return this;
	}

}
