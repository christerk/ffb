package com.balancedbytes.games.ffb.server.step.bb2020.multiblock;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.BlockTarget;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.generator.Sequence;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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
