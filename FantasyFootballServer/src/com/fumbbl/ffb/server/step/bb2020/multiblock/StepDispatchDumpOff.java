package com.fumbbl.ffb.server.step.bb2020.multiblock;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.BlockTarget;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepDispatchDumpOff extends AbstractStep {
	private List<String> targets = new ArrayList<>();

	public StepDispatchDumpOff(GameState pGameState) {
		super(pGameState);
	}

	public StepDispatchDumpOff(GameState pGameState, StepAction defaultStepResult) {
		super(pGameState, defaultStepResult);
	}

	@Override
	public StepId getId() {
		return StepId.DISPATCH_DUMP_OFF;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(StepParameterSet parameterSet) {
		if (parameterSet != null) {
			for (StepParameter parameter: parameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.BLOCK_TARGETS) {
					//noinspection unchecked
					targets.addAll(((Collection<? extends BlockTarget>) parameter.getValue()).stream().map(BlockTarget::getPlayerId).collect(Collectors.toList()));
				}
			}
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			if (parameter.getKey() == StepParameterKey.PLAYER_ID_TO_REMOVE) {
				targets.stream().filter(target -> target.equals(parameter.getValue()))
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
		Game game = getGameState().getGame();
		targets.stream().filter(target -> {
			Player<?> player = game.getPlayerById(target);
			return UtilPlayer.hasBall(game, player);
		})
			.findFirst().ifPresent(target -> {
				Player<?> player = game.getPlayerById(target);
				game.setDefenderId(target);
				Sequence sequence = new Sequence(getGameState());
				sequence.add(StepId.DUMP_OFF);
				getGameState().getStepStack().push(sequence.getSequence());
				publishParameter(new StepParameter(StepParameterKey.DEFENDER_POSITION, game.getFieldModel().getPlayerCoordinate(player)));
		});
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_IDS.addTo(jsonObject, targets);
		return jsonObject;
	}

	@Override
	public StepDispatchDumpOff initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		targets = Arrays.asList(IJsonOption.PLAYER_IDS.getFrom(game, jsonObject));

		return this;
	}
}
