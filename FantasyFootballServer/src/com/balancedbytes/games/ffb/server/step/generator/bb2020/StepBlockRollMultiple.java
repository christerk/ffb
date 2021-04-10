package com.balancedbytes.games.ffb.server.step.generator.bb2020;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.dialog.DialogReRollForTargetsParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.BlockTarget;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportBlock;
import com.balancedbytes.games.ffb.report.ReportBlockRoll;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.ServerUtilBlock;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepBlockRollMultiple extends AbstractStep {

	private State state = new State();

	public StepBlockRollMultiple(GameState pGameState) {
		super(pGameState);
	}

	public StepBlockRollMultiple(GameState pGameState, StepAction defaultStepResult) {
		super(pGameState, defaultStepResult);
	}

	@Override
	public StepId getId() {
		return StepId.BLOCK_ROLL_MULTIPLE;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.BLOCK_TARGETS) {
					//noinspection unchecked
					state.blockRolls.addAll(((List<BlockTarget>) parameter.getValue()).stream().map(target -> new BlockRoll(target.getPlayerId())).collect(Collectors.toList()));
				}
			}
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null && parameter.getKey() == StepParameterKey.PLAYER_ID_TO_REMOVE) {
			state.blockRolls.stream().filter(roll -> roll.getTargetId().equals(parameter.getValue())).findFirst()
				.ifPresent(roll -> state.blockRolls.remove(roll));
			return true;
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
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (state.firstRun) {
			state.firstRun = false;
			game.getFieldModel().clearDiceDecorations();

			state.blockRolls.forEach(roll -> roll(actingPlayer, roll, false));
			state.reRollAvailableAgainst.addAll(state.blockRolls.stream().map(BlockRoll::getTargetId).collect(Collectors.toList()));
			decideNextStep(game);

		} else {
			if (!StringTool.isProvided(state.reRollTarget) || state.reRollSource == null) {
				nextStep();
			} else {
				if (UtilServerReRoll.useReRoll(this, state.reRollSource, actingPlayer.getPlayer())) {
					state.blockRolls.stream()
						.filter(filteredRoll -> filteredRoll.getTargetId().equals(state.reRollTarget))
						.findFirst().ifPresent(roll -> roll(actingPlayer, roll, true));
				}
				state.reRollAvailableAgainst.remove(state.reRollTarget);
				decideNextStep(game);
			}
		}
	}

	private void decideNextStep(Game game) {
		state.teamReRollAvailable = UtilServerReRoll.isTeamReRollAvailable(getGameState(), game.getActingPlayer().getPlayer());
		state.proReRollAvailable = UtilServerReRoll.isProReRollAvailable(game.getActingPlayer().getPlayer(), game);
		if (state.reRollAvailableAgainst.isEmpty() || (!state.teamReRollAvailable && !state.proReRollAvailable)) {
			nextStep();
		} else {
			UtilServerDialog.showDialog(getGameState(), createDialogParameter(game.getActingPlayer().getPlayer()), false);
		}
	}

	private void roll(ActingPlayer actingPlayer, BlockRoll roll, boolean reRolling) {
		Game game = getGameState().getGame();
		Player<?> defender = game.getPlayerById(roll.getTargetId());
		roll.setNrOfDice(ServerUtilBlock.findNrOfBlockDice(game, actingPlayer.getPlayer(), defender, true, roll.isSuccessFulDauntless()));
		roll.setBlockRoll(getGameState().getDiceRoller().rollBlockDice(roll.getNrOfDice()));
		if (!reRolling) {
			getResult().addReport(new ReportBlock(game.getDefenderId()));
		}
		getResult().addReport(new ReportBlockRoll(defender.getTeam().getId(), roll.getBlockRoll(), reRolling ? roll.getTargetId() : null));
		getResult().setSound(SoundId.BLOCK);
	}

	private DialogReRollForTargetsParameter createDialogParameter(Player<?> player) {
		// TODO dialog
		return null;
	}

	private void nextStep() {
		//TODO progress to next step
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.STEP_STATE.addTo(jsonObject, state.toJsonValue());
		return jsonObject;
	}

	@Override
	public StepBlockRollMultiple initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		state = new State().initFrom(source, IJsonOption.STEP_STATE.getFrom(source, jsonObject));
		return this;
	}

	public static class State implements IJsonSerializable {
		public List<String> reRollAvailableAgainst = new ArrayList<>();
		public List<BlockRoll> blockRolls = new ArrayList<>();
		public boolean firstRun = true, teamReRollAvailable, proReRollAvailable;
		public ReRollSource reRollSource;
		public String reRollTarget;

		@Override
		public JsonObject toJsonValue() {
			JsonObject jsonObject = new JsonObject();
			JsonArray jsonArray = new JsonArray();
			blockRolls.stream().map(BlockRoll::toJsonValue).forEach(jsonArray::add);
			IJsonOption.BLOCK_ROLLS.addTo(jsonObject, jsonArray);
			IJsonOption.PLAYER_ID.addTo(jsonObject, reRollTarget);
			IJsonOption.FIRST_RUN.addTo(jsonObject, firstRun);
			IJsonOption.PRO_RE_ROLL_OPTION.addTo(jsonObject, proReRollAvailable);
			IJsonOption.TEAM_RE_ROLL_OPTION.addTo(jsonObject, teamReRollAvailable);
			IJsonOption.TEAM_RE_ROLL_AVAILABLE_AGAINST.addTo(jsonObject, reRollAvailableAgainst);
			IJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, reRollSource);
			return jsonObject;
		}

		@Override
		public State initFrom(IFactorySource game, JsonValue pJsonValue) {
			JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
			JsonArray jsonArray = IJsonOption.BLOCK_ROLLS.getFrom(game, jsonObject);
			blockRolls = jsonArray.values().stream().map(value -> new BlockRoll().initFrom(game, value)).collect(Collectors.toList());
			reRollTarget = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
			firstRun = IJsonOption.FIRST_RUN.getFrom(game, jsonObject);
			proReRollAvailable = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(game, jsonObject);
			teamReRollAvailable = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(game, jsonObject);
			reRollAvailableAgainst = Arrays.asList(IJsonOption.TEAM_RE_ROLL_AVAILABLE_AGAINST.getFrom(game, jsonObject));
			reRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE.getFrom(game, jsonObject);
			return this;
		}
	}
}
