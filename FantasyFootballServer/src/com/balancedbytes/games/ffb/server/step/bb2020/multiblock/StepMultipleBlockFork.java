package com.balancedbytes.games.ffb.server.step.bb2020.multiblock;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.BlockTarget;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.generator.Sequence;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepMultipleBlockFork extends AbstractStep {
	private List<BlockTarget> targets = new ArrayList<>();
	private List<String> successfulDauntless = new ArrayList<>();

	public StepMultipleBlockFork(GameState pGameState) {
		super(pGameState);
	}

	public StepMultipleBlockFork(GameState pGameState, StepAction defaultStepResult) {
		super(pGameState, defaultStepResult);
	}

	@Override
	public StepId getId() {
		return StepId.MULTI_BLOCK_FORK;
	}

	@Override
	public void init(StepParameterSet parameterSet) {
		if (parameterSet != null) {
			for (StepParameter parameter: parameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.BLOCK_TARGETS) {//noinspection unchecked
					targets.addAll((Collection<? extends BlockTarget>) parameter.getValue());
				}
			}
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			switch (parameter.getKey()) {
				case PLAYER_ID_TO_REMOVE:
					targets.stream().filter(target -> target.getPlayerId().equals((String) parameter.getValue()))
						.findFirst().ifPresent(targets::remove);
					consume(parameter);
					return true;
				case PLAYER_ID_DAUNTLESS_SUCCESS:
					successfulDauntless.add((String) parameter.getValue());
					consume(parameter);
					return true;
			}
		}
		return super.setParameter(parameter);
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		Sequence sequence = new Sequence(getGameState());


		getGameState().getStepStack().push(sequence.getSequence());
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		JsonArray jsonArray = new JsonArray();
		targets.stream().map(BlockTarget::toJsonValue).forEach(jsonArray::add);
		IJsonOption.SELECTED_BLOCK_TARGETS.addTo(jsonObject, jsonArray);
		return jsonObject;
	}

	@Override
	public StepMultipleBlockFork initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		JsonArray jsonArray = IJsonOption.SELECTED_BLOCK_TARGETS.getFrom(game, jsonObject);
		jsonArray.values().stream()
			.map(value -> new BlockTarget().initFrom(game, value))
			.forEach(value -> targets.add(value));
		successfulDauntless = Arrays.asList(IJsonOption.PLAYER_IDS.getFrom(game, jsonObject));
		return this;
	}
}
