package com.balancedbytes.games.ffb.server.step.bb2020.multiblock;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.dialog.DialogOpponentBlockSelectionParameter;
import com.balancedbytes.games.ffb.dialog.DialogReRollBlockForTargetsParameter;
import com.balancedbytes.games.ffb.factory.BlockResultFactory;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.BlockRoll;
import com.balancedbytes.games.ffb.model.BlockTarget;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandBlockOrReRollChoiceForTarget;
import com.balancedbytes.games.ffb.report.ReportBlock;
import com.balancedbytes.games.ffb.report.ReportBlockRoll;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.IStepLabel;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.generator.Sequence;
import com.balancedbytes.games.ffb.server.util.ServerUtilBlock;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.balancedbytes.games.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepBlockRollMultiple extends AbstractStep {

	private State state = new State();
	private final Set<StepParameterKey> parameterToConsume = new HashSet<>();

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
				switch (parameter.getKey()) {
					case BLOCK_TARGETS:
						//noinspection unchecked
						List<BlockTarget> targets = (List<BlockTarget>) parameter.getValue();
						state.blockRolls.addAll(targets.stream().map(target ->
							new BlockRoll(target.getPlayerId(), target.getOriginalPlayerState(), targets.indexOf(target))
						).collect(Collectors.toList()));
						break;
					case CONSUME_PARAMETER:
						//noinspection unchecked
						parameterToConsume.addAll((Collection<? extends StepParameterKey>) parameter.getValue());
						break;
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
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus stepCommandStatus = super.handleCommand(pReceivedCommand);
		if (stepCommandStatus == StepCommandStatus.UNHANDLED_COMMAND && pReceivedCommand.getId() == NetCommandId.CLIENT_BLOCK_OR_RE_ROLL_CHOICE_FOR_TARGET) {
			ClientCommandBlockOrReRollChoiceForTarget command = (ClientCommandBlockOrReRollChoiceForTarget) pReceivedCommand.getCommand();
			state.reRollSource = command.getReRollSource();
			state.selectedTarget = command.getTargetId();
			state.blockRolls.stream().filter(roll -> roll.getTargetId().equals(command.getTargetId()))
				.findFirst().ifPresent(roll -> roll.setSelectedIndex(command.getSelectedIndex()));
			state.reRollAvailableAgainst.remove(state.selectedTarget);
			stepCommandStatus = StepCommandStatus.EXECUTE_STEP;
		}
		if (stepCommandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return stepCommandStatus;
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

			state.blockRolls.forEach(roll -> {
				Player<?> defender = game.getPlayerById(roll.getTargetId());
				int nrOfDice = ServerUtilBlock.findNrOfBlockDice(game, actingPlayer.getPlayer(), defender, true, roll.isSuccessFulDauntless());
				roll.setNrOfDice(Math.abs(nrOfDice));
				roll.setOwnChoice(nrOfDice > 0);
				roll(roll, false);
			});
			state.reRollAvailableAgainst.addAll(state.blockRolls.stream().map(BlockRoll::getTargetId).collect(Collectors.toList()));
			decideNextStep(game);

		} else {
			if (StringTool.isProvided(state.selectedTarget) && state.reRollSource != null) {
				if (UtilServerReRoll.useReRoll(this, state.reRollSource, actingPlayer.getPlayer())) {
					state.blockRolls.stream()
						.filter(filteredRoll -> filteredRoll.getTargetId().equals(state.selectedTarget))
						.findFirst().ifPresent(roll -> roll(roll, true));
				}
			}
			decideNextStep(game);
		}
	}

	private void decideNextStep(Game game) {
		List<BlockRoll> unselected = state.blockRolls.stream().filter(BlockRoll::needsSelection).collect(Collectors.toList());

		if (unselected.isEmpty()) {
			nextStep();
			return;
		}

		state.teamReRollAvailable = UtilServerReRoll.isTeamReRollAvailable(getGameState(), game.getActingPlayer().getPlayer());
		state.proReRollAvailable = UtilServerReRoll.isProReRollAvailable(game.getActingPlayer().getPlayer(), game);

		if (!state.proReRollAvailable && !state.teamReRollAvailable) {
			state.reRollAvailableAgainst.clear();
		}

		if (state.attackerTeamSelects) {
			if (unselected.stream().anyMatch(BlockRoll::isOwnChoice) || !state.reRollAvailableAgainst.isEmpty()) {
				UtilServerDialog.showDialog(getGameState(), createAttackerDialogParameter(game.getActingPlayer().getPlayer(), state.blockRolls), false);
			} else {
				state.attackerTeamSelects = false;
			}
 		}
		if (!state.attackerTeamSelects) {
			List<BlockRoll> defender = state.blockRolls.stream().filter(roll -> !roll.isOwnChoice()).collect(Collectors.toList());
			if (unselected.stream().anyMatch(roll -> !roll.isOwnChoice())) {
				Team otherTeam = game.getOtherTeam(game.getActingTeam());
				UtilServerDialog.showDialog(getGameState(), createDefenderDialogParameter(otherTeam, defender), true);
			} else {
				nextStep();
			}
		}
	}

	private void roll(BlockRoll roll, boolean reRolling) {
		Game game = getGameState().getGame();
		Player<?> defender = game.getPlayerById(roll.getTargetId());
		roll.setBlockRoll(getGameState().getDiceRoller().rollBlockDice(roll.getNrOfDice()));
		if (!reRolling) {
			getResult().addReport(new ReportBlock(game.getDefenderId()));
		}
		getResult().addReport(new ReportBlockRoll(defender.getTeam().getId(), roll.getBlockRoll(), roll.getTargetId()));
		getResult().setSound(SoundId.BLOCK);
	}

	private DialogOpponentBlockSelectionParameter createDefenderDialogParameter(Team team, List<BlockRoll> blockRolls){
		return new DialogOpponentBlockSelectionParameter(team.getId(), blockRolls);
	}

	private DialogReRollBlockForTargetsParameter createAttackerDialogParameter(Player<?> player, List<BlockRoll> blockRolls) {

		return new DialogReRollBlockForTargetsParameter(player.getId(), blockRolls,
			state.reRollAvailableAgainst, state.proReRollAvailable, state.teamReRollAvailable);
	}

	private void nextStep() {
		Collections.reverse(state.blockRolls);
		state.blockRolls.forEach(this::generateBlockEvaluationSequence);

		getResult().setNextAction(StepAction.NEXT_STEP);
	}


	private void generateBlockEvaluationSequence(BlockRoll blockRoll) {
		Sequence sequence = new Sequence(getGameState());
		sequence.add(StepId.SET_DEFENDER, new StepParameter(StepParameterKey.BLOCK_DEFENDER_ID, blockRoll.getTargetId()));

		sequence.add(StepId.BLOCK_CHOICE, from(StepParameterKey.GOTO_LABEL_ON_DODGE, IStepLabel.DODGE_BLOCK),
			from(StepParameterKey.GOTO_LABEL_ON_JUGGERNAUT, IStepLabel.BOTH_DOWN),
			from(StepParameterKey.GOTO_LABEL_ON_PUSHBACK, IStepLabel.PUSHBACK),
			from(StepParameterKey.SUPPRESS_EXTRA_EFFECT_HANDLING, true),
			from(StepParameterKey.BLOCK_ROLL_ID, blockRoll.getId()),
			from(StepParameterKey.SHOW_NAME_IN_REPORT, true));
		sequence.jump(IStepLabel.DROP_FALLING_PLAYERS);

		sequence.add(StepId.BOTH_DOWN, IStepLabel.BOTH_DOWN);
		sequence.add(StepId.WRESTLE);
		sequence.jump(IStepLabel.DROP_FALLING_PLAYERS);

		sequence.add(StepId.BLOCK_DODGE, IStepLabel.DODGE_BLOCK);
		sequence.add(StepId.PUSHBACK, IStepLabel.PUSHBACK);

		sequence.add(StepId.DROP_FALLING_PLAYERS, IStepLabel.DROP_FALLING_PLAYERS);
		sequence.add(StepId.CONSUME_PARAMETER, from(StepParameterKey.CONSUME_PARAMETER, parameterToConsume));

		getGameState().getStepStack().push(sequence.getSequence());

		BlockResultFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.BLOCK_RESULT);

		publishParameter(new StepParameter(StepParameterKey.OLD_DEFENDER_STATE, blockRoll.getOldPlayerState()));
		publishParameter(new StepParameter(StepParameterKey.NR_OF_DICE, blockRoll.getNrOfDice()));
		publishParameter(new StepParameter(StepParameterKey.BLOCK_ROLL, blockRoll.getBlockRoll()));
		publishParameter(new StepParameter(StepParameterKey.DICE_INDEX, blockRoll.getSelectedIndex()));
		publishParameter(new StepParameter(StepParameterKey.BLOCK_RESULT, factory.forRoll(blockRoll.getBlockRoll()[blockRoll.getSelectedIndex()])));
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.STEP_STATE.addTo(jsonObject, state.toJsonValue());
		String[] keys = parameterToConsume.stream().map(StepParameterKey::name).collect(Collectors.toList()).toArray(new String[] {});
		IJsonOption.STEP_PARAMETER_KEYS.addTo(jsonObject, keys);
		return jsonObject;
	}

	@Override
	public StepBlockRollMultiple initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		state = new State().initFrom(source, IJsonOption.STEP_STATE.getFrom(source, jsonObject));
		parameterToConsume.addAll(Arrays.stream(IJsonOption.STEP_PARAMETER_KEYS.getFrom(source, UtilJson.toJsonObject(pJsonValue)))
			.map(StepParameterKey::valueOf).collect(Collectors.toSet()));
		return this;
	}

	private static class State implements IJsonSerializable {
		private List<String> reRollAvailableAgainst = new ArrayList<>();
		private List<BlockRoll> blockRolls = new ArrayList<>();
		private boolean firstRun = true, teamReRollAvailable, proReRollAvailable, attackerTeamSelects = true;
		private ReRollSource reRollSource;
		private String selectedTarget;

		@Override
		public JsonObject toJsonValue() {
			JsonObject jsonObject = new JsonObject();
			JsonArray jsonArray = new JsonArray();
			blockRolls.stream().map(BlockRoll::toJsonValue).forEach(jsonArray::add);
			IJsonOption.BLOCK_ROLLS.addTo(jsonObject, jsonArray);
			IJsonOption.PLAYER_ID.addTo(jsonObject, selectedTarget);
			IJsonOption.FIRST_RUN.addTo(jsonObject, firstRun);
			IJsonOption.PRO_RE_ROLL_OPTION.addTo(jsonObject, proReRollAvailable);
			IJsonOption.TEAM_RE_ROLL_OPTION.addTo(jsonObject, teamReRollAvailable);
			IJsonOption.RE_ROLL_AVAILABLE_AGAINST.addTo(jsonObject, reRollAvailableAgainst);
			IJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, reRollSource);
			IJsonOption.ATTACKER_SELECTS.addTo(jsonObject, attackerTeamSelects);
			return jsonObject;
		}

		@Override
		public State initFrom(IFactorySource game, JsonValue pJsonValue) {
			JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
			JsonArray jsonArray = IJsonOption.BLOCK_ROLLS.getFrom(game, jsonObject);
			blockRolls = jsonArray.values().stream().map(value -> new BlockRoll().initFrom(game, value)).collect(Collectors.toList());
			selectedTarget = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
			firstRun = IJsonOption.FIRST_RUN.getFrom(game, jsonObject);
			proReRollAvailable = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(game, jsonObject);
			teamReRollAvailable = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(game, jsonObject);
			reRollAvailableAgainst = Arrays.asList(IJsonOption.RE_ROLL_AVAILABLE_AGAINST.getFrom(game, jsonObject));
			reRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE.getFrom(game, jsonObject);
			attackerTeamSelects = IJsonOption.ATTACKER_SELECTS.getFrom(game, jsonObject);
			return this;
		}
	}
}
