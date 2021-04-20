package com.fumbbl.ffb.server.step.bb2020.multiblock;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.BlockTarget;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRollForTarget;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.skillbehaviour.bb2020.StepStateMultipleRolls;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;

import java.util.List;
import java.util.stream.Collectors;


@RulesCollection(RulesCollection.Rules.BB2020)
public class StepBlockChainsawMultiple extends AbstractStepWithReRoll {

	private StepStateMultipleRolls state;

	public StepBlockChainsawMultiple(GameState pGameState) {
		super(pGameState);
		state = new StepStateMultipleRolls();
	}

	public StepId getId() {
		return StepId.BLOCK_CHAINSAW_MULTIPLE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.BLOCK_TARGETS) {
					//noinspection unchecked
					state.blockTargets.addAll(((List<BlockTarget>) parameter.getValue()).stream().map(BlockTarget::getPlayerId).collect(Collectors.toList()));
				}
			}
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
				if (command.getReRolledAction() == ReRolledActions.CHAINSAW) {
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

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null && parameter.getKey() == StepParameterKey.PLAYER_ID_TO_REMOVE) {
			state.blockTargets.remove((String) parameter.getValue());
			return true;
		}

		return super.setParameter(parameter);
	}

	private void executeStep() {
		getGameState().executeStepHooks(this, state);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.STEP_STATE.addTo(jsonObject, state.toJsonValue());
		return jsonObject;
	}

	@Override
	public StepBlockChainsawMultiple initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		state = new StepStateMultipleRolls().initFrom(source, IJsonOption.STEP_STATE.getFrom(source, jsonObject));
		return this;
	}

}
