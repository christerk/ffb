package com.fumbbl.ffb.server.step.bb2020.multiblock;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.BlockKind;
import com.fumbbl.ffb.model.BlockTarget;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.generator.Sequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepMultipleBlockFork extends AbstractStep {
	private final List<BlockTarget> targets = new ArrayList<>();
	private final Set<StepParameterKey> parameterToConsume = new HashSet<StepParameterKey>() {{
		add(StepParameterKey.BLOCK_ROLL);
		add(StepParameterKey.BLOCK_RESULT);
		add(StepParameterKey.DICE_INDEX);
		add(StepParameterKey.NR_OF_DICE);
		add(StepParameterKey.STARTING_PUSHBACK_SQUARE);
		add(StepParameterKey.DEFENDER_PUSHED);
		add(StepParameterKey.FOLLOWUP_CHOICE);
		add(StepParameterKey.USING_STAB);
		add(StepParameterKey.OLD_DEFENDER_STATE);
	}};

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

	@SuppressWarnings("unchecked")
	@Override
	public void init(StepParameterSet parameterSet) {
		if (parameterSet != null) {
			for (StepParameter parameter : parameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.BLOCK_TARGETS) {
					targets.addAll((Collection<? extends BlockTarget>) parameter.getValue());
				}
			}
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			if (parameter.getKey() == StepParameterKey.PLAYER_ID_TO_REMOVE) {
				targets.stream().filter(target -> target.getPlayerId().equals(parameter.getValue()))
					.findFirst().ifPresent(targets::remove);
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
		Map<BlockKind, List<BlockTarget>> groupedTargets = targets.stream().collect(Collectors.groupingBy(BlockTarget::getKind));

		List<BlockTarget> blockGroup = groupedTargets.get(BlockKind.BLOCK);
		if (blockGroup != null && !blockGroup.isEmpty()) {
			Sequence sequence = new Sequence(getGameState());
			sequence.add(StepId.DAUNTLESS_MULTIPLE, StepParameter.from(StepParameterKey.BLOCK_TARGETS, blockGroup));
			sequence.add(StepId.DOUBLE_STRENGTH);
			blockGroup.forEach(target -> {
				sequence.add(StepId.SET_DEFENDER, StepParameter.from(StepParameterKey.BLOCK_DEFENDER_ID, target.getPlayerId()));
				sequence.add(StepId.TRICKSTER);
				sequence.add(StepId.CATCH_SCATTER_THROW_IN);
			});
			sequence.add(StepId.BLOCK_ROLL_MULTIPLE, StepParameter.from(StepParameterKey.BLOCK_TARGETS, blockGroup),
				StepParameter.from(StepParameterKey.CONSUME_PARAMETER, parameterToConsume));
			getGameState().getStepStack().push(sequence.getSequence());
		}

		List<BlockTarget> stabGroup = groupedTargets.get(BlockKind.STAB);
		if (stabGroup != null && !stabGroup.isEmpty()) {
			Collections.reverse(stabGroup);
			stabGroup.forEach(
				target -> {
					Sequence sequence = new Sequence(getGameState());
					sequence.add(StepId.SET_DEFENDER, StepParameter.from(StepParameterKey.BLOCK_DEFENDER_ID, target.getPlayerId()));
					sequence.add(StepId.TRICKSTER);
					sequence.add(StepId.CATCH_SCATTER_THROW_IN);
					sequence.add(StepId.STAB, StepParameter.from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.NEXT));
					sequence.add(StepId.HANDLE_DROP_PLAYER_CONTEXT);
					sequence.add(StepId.REPORT_STAB_INJURY, IStepLabel.NEXT, StepParameter.from(StepParameterKey.PLAYER_ID, target.getPlayerId()));
					sequence.add(StepId.CONSUME_PARAMETER, StepParameter.from(StepParameterKey.CONSUME_PARAMETER, parameterToConsume));
					getGameState().getStepStack().push(sequence.getSequence());
					publishParameter(StepParameter.from(StepParameterKey.OLD_DEFENDER_STATE, target.getOriginalPlayerState()));
					publishParameter(StepParameter.from(StepParameterKey.USING_STAB, true));

				}
			);
		}


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
	public StepMultipleBlockFork initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		JsonArray jsonArray = IJsonOption.SELECTED_BLOCK_TARGETS.getFrom(source, jsonObject);
		jsonArray.values().stream()
			.map(value -> new BlockTarget().initFrom(source, value))
			.forEach(targets::add);
		return this;
	}
}
