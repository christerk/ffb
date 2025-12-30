package com.fumbbl.ffb.server.step.bb2025.block;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.BlockResult;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.dialog.DialogOpponentBlockSelectionPropertiesParameter;
import com.fumbbl.ffb.dialog.DialogReRollBlockForTargetsPropertiesParameter;
import com.fumbbl.ffb.factory.BlockResultFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.BlockRollProperties;
import com.fumbbl.ffb.model.BlockTarget;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandBlockOrReRollChoiceForTarget;
import com.fumbbl.ffb.net.commands.ClientCommandUseBrawler;
import com.fumbbl.ffb.report.ReportBlock;
import com.fumbbl.ffb.report.ReportBlockRoll;
import com.fumbbl.ffb.report.mixed.ReportBlockReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.bb2020.multiblock.AbstractStepMultiple;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.mixed.SingleReRollUseState;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepBlockRollMultiple extends AbstractStepMultiple {

	private State state = new State();
	private final Set<StepParameterKey> parameterToConsume = new HashSet<>();

	@SuppressWarnings("unused")
	public StepBlockRollMultiple(GameState pGameState) {
		super(pGameState);
	}

	@SuppressWarnings("unused")
	public StepBlockRollMultiple(GameState pGameState, StepAction defaultStepResult) {
		super(pGameState, defaultStepResult);
	}

	@Override
	protected SingleReRollUseState state() {
		return state;
	}


	@Override
	public StepId getId() {
		return StepId.BLOCK_ROLL_MULTIPLE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					case BLOCK_TARGETS:
						List<BlockTarget> targets = (List<BlockTarget>) parameter.getValue();
						state.blockRolls.addAll(targets.stream().map(target ->
							new BlockRollProperties(target.getPlayerId(), target.getOriginalPlayerState(), targets.indexOf(target))
						).collect(Collectors.toList()));
						break;
					case CONSUME_PARAMETER:
						parameterToConsume.addAll((Collection<? extends StepParameterKey>) parameter.getValue());
						break;
					default:
						break;
				}
			}
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			switch (parameter.getKey()) {
				case PLAYER_ID_TO_REMOVE:
					state.blockRolls.stream().filter(roll -> roll.getTargetId().equals(parameter.getValue())).findFirst()
						.ifPresent(roll -> state.blockRolls.remove(roll));
					return true;
				case PLAYER_ID_DAUNTLESS_SUCCESS:
					state.blockRolls.stream().filter(roll -> roll.getTargetId().equals(parameter.getValue())).findFirst()
						.ifPresent(roll -> roll.setSuccessFulDauntless(true));
					consume(parameter);
					return true;
				case DOUBLE_TARGET_STRENGTH_FOR_PLAYER:
					state.blockRolls.stream().filter(roll -> roll.getTargetId().equals(parameter.getValue())).findFirst()
						.ifPresent(roll -> roll.setDoubleTargetStrength(true));
					break;
				default:
					break;
			}
		}
		return super.setParameter(parameter);
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus stepCommandStatus = super.handleCommand(pReceivedCommand);
		if (stepCommandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_BLOCK_OR_RE_ROLL_CHOICE_FOR_TARGET:
					ClientCommandBlockOrReRollChoiceForTarget command =
						(ClientCommandBlockOrReRollChoiceForTarget) pReceivedCommand.getCommand();
					if (reRollSourceSuccessfully(command.getReRollSource())) {
						stepCommandStatus = StepCommandStatus.EXECUTE_STEP;
					}

					state.selectedTarget = command.getTargetId();
					state.blockRolls.stream().filter(roll -> roll.getTargetId().equals(command.getTargetId()))
						.findFirst().ifPresent(roll -> {
							roll.setSelectedIndex(command.getSelectedIndex());
							roll.setProIndex(command.getProIndex());
							roll.setReRollDiceIndexes(command.getAnyDiceIndexes());
						});

					break;

				case CLIENT_USE_BRAWLER:
					ClientCommandUseBrawler brawlerCommand = (ClientCommandUseBrawler) pReceivedCommand.getCommand();
					state.reRollSource = ReRollSources.BRAWLER;
					state.selectedTarget = brawlerCommand.getTargetId();
					stepCommandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				default:
					break;
			}
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

	private static ReRollSource getSingleDieReRollSource(ActingPlayer actingPlayer) {
		return UtilCards.getUnusedRerollSource(actingPlayer, ReRolledActions.SINGLE_DIE);
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		ReRollSource singleDieReRollSource = getSingleDieReRollSource(actingPlayer);

		if (state.firstRun) {
			state.firstRun = false;
			game.getFieldModel().clearDiceDecorations();

			final boolean teamReRollAvailable =
				UtilServerReRoll.isTeamReRollAvailable(getGameState(), actingPlayer.getPlayer());
			InducementSet inducementSet = game.getTurnData().getInducementSet();
			final boolean mascotAvailable = inducementSet.hasUsesLeft(inducementSet.forUsage(Usage.CONDITIONAL_REROLL));

			RollMechanic mechanic = game.getMechanic(Mechanic.Type.ROLL);

			state.blockRolls.forEach(roll -> {
				Player<?> defender = game.getPlayerById(roll.getTargetId());
				int nrOfDice = ServerUtilBlock.findNrOfBlockDice(getGameState(), actingPlayer.getPlayer(), defender, true,
					roll.isSuccessFulDauntless(), roll.isDoubleTargetStrength(), false).getLeft();
				roll.setNrOfDice(Math.abs(nrOfDice));
				roll.setOwnChoice(nrOfDice > 0);
				roll(roll, false, actingPlayer, singleDieReRollSource);

				addReRollData(game, teamReRollAvailable, mascotAvailable, mechanic, roll);

				getResult().setSound(SoundId.BLOCK);
				getGameState().removeAdditionalAssist(game.getActingTeam().getId());
				UtilServerGame.syncGameModel(this);
			});

			decideNextStep(game);

		} else {
			if (StringTool.isProvided(state.selectedTarget)) {
				state.blockRolls.stream()
					.filter(filteredRoll -> filteredRoll.getTargetId().equals(state.selectedTarget))
					.findFirst().ifPresent(roll -> {
						if (state.reRollSource != null) {

							Player<?> defender = game.getPlayerById(roll.getTargetId());
							getResult().addReport(new ReportBlock(defender.getId()));
							getResult().setSound(SoundId.BLOCK);

							if (state.reRollSource == ReRollSources.BRAWLER) {
								handleBrawler(actingPlayer.getPlayer(), roll);
							} else if (UtilServerReRoll.useReRoll(this, state.reRollSource, actingPlayer.getPlayer())) {
								roll(roll, true, actingPlayer, singleDieReRollSource);
							}
							getResult().addReport(
								new ReportBlockRoll(defender.getTeam().getId(), roll.getBlockRoll(), roll.getTargetId()));
						}
						roll.clearReRolls();
					});

				final boolean teamReRollAvailable =
					UtilServerReRoll.isTeamReRollAvailable(getGameState(), actingPlayer.getPlayer());
				InducementSet inducementSet = game.getTurnData().getInducementSet();
				final boolean mascotAvailable = inducementSet.hasUsesLeft(inducementSet.forUsage(Usage.CONDITIONAL_REROLL));

				RollMechanic mechanic = game.getMechanic(Mechanic.Type.ROLL);

				state.blockRolls.stream().filter(BlockRollProperties::hasReRollsLeft).forEach(roll -> {
					roll.clearReRolls();

					addReRollData(game, teamReRollAvailable, mascotAvailable, mechanic, roll);
				});

			}
			decideNextStep(game);
		}
	}

	private void handleBrawler(Player<?> player, BlockRollProperties blockRoll) {
		int reRolledDie = getGameState().getDiceRoller().rollBlockDice(1)[0];
		getResult().addReport(new ReportBlockReRoll(new int[]{reRolledDie}, player.getId(), ReRollSources.BRAWLER));
		BlockResultFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.BLOCK_RESULT);
		for (int i = 0; i < blockRoll.getNrOfDice(); i++) {
			if (factory.forRoll(blockRoll.getBlockRoll()[i]) == BlockResult.BOTH_DOWN) {
				int[] oldRoll = blockRoll.getBlockRoll();
				blockRoll.setBlockRoll(Arrays.copyOf(oldRoll, oldRoll.length));
				blockRoll.getBlockRoll()[i] = reRolledDie;
				break;
			}
		}
	}

	private void decideNextStep(Game game) {
		List<BlockRollProperties> unselected =
			state.blockRolls.stream().filter(BlockRollProperties::needsSelection).collect(Collectors.toList());

		if (unselected.isEmpty()) {
			nextStep();
			return;
		}

		ActingPlayer actingPlayer = game.getActingPlayer();

		boolean anyReRollLeft = state.blockRolls.stream().anyMatch(BlockRollProperties::hasReRollsLeft);

		if (state.attackerTeamSelects) {
			if (unselected.stream().anyMatch(BlockRollProperties::isOwnChoice) || anyReRollLeft) {
				UtilServerDialog.showDialog(getGameState(),
					createAttackerDialogParameter(actingPlayer.getPlayer(), state.blockRolls), false);
			} else {
				state.attackerTeamSelects = false;
			}
		}
		if (!state.attackerTeamSelects) {
			List<BlockRollProperties> defender =
				state.blockRolls.stream().filter(roll -> !roll.isOwnChoice()).collect(Collectors.toList());
			if (unselected.stream().anyMatch(roll -> !roll.isOwnChoice())) {
				Team otherTeam = game.getOtherTeam(game.getActingTeam());
				UtilServerDialog.showDialog(getGameState(), createDefenderDialogParameter(otherTeam, defender), true);
			} else {
				nextStep();
			}
		}
	}

	private void addReRollData(Game game, boolean teamReRollAvailable, boolean mascotAvailable, RollMechanic mechanic, BlockRollProperties roll) {
		Map<ReRolledAction, ReRollSource> actionReRollSourceMap = new HashMap<>();

		if (teamReRollAvailable) {
			roll.add(ReRollProperty.TRR);
		}
		if (mascotAvailable) {
			roll.add(ReRollProperty.MASCOT);
		}
		mechanic.findAdditionalReRollProperty(game.getTurnData()).ifPresent(roll::add);

		addReRollSourceMapping(actionReRollSourceMap, ReRolledActions.SINGLE_DIE_PER_ACTIVATION, game);
		addReRollSourceMapping(actionReRollSourceMap, ReRolledActions.SINGLE_BOTH_DOWN, game);
		addReRollSourceMapping(actionReRollSourceMap, ReRolledActions.SINGLE_DIE, game);
		addReRollSourceMapping(actionReRollSourceMap, ReRolledActions.MULTI_BLOCK_DICE, game);

		roll.setReRollSources(convertActionsMap(actionReRollSourceMap));
	}

	private void roll(BlockRollProperties roll, boolean reRolling, ActingPlayer actingPlayer, ReRollSource singleDieReRollSource) {
		if (reRolling) {
			if (state.reRollSource == ReRollSources.PRO) {
				adjustRollForIndexedReRoll(roll, actingPlayer, NamedProperties.canRerollOncePerTurn,
					new int[]{roll.getProIndex()});
			} else if (singleDieReRollSource != null && state.reRollSource == singleDieReRollSource) {
				adjustRollForIndexedReRoll(roll, actingPlayer, NamedProperties.canRerollSingleDieOncePerPeriod,
					new int[]{roll.getProIndex()});
			} else if (state.reRollSource == ReRollSources.SAVAGE_BLOW) {
				adjustRollForIndexedReRoll(roll, actingPlayer, NamedProperties.canReRollAnyNumberOfBlockDice,
					roll.getReRollDiceIndexes());
			} else {
				roll.setBlockRoll(getGameState().getDiceRoller().rollBlockDice(roll.getNrOfDice()));
			}
			roll.clearReRolls();
		} else {
			roll.setBlockRoll(getGameState().getDiceRoller().rollBlockDice(roll.getNrOfDice()));
		}
	}

	private void adjustRollForIndexedReRoll(BlockRollProperties roll, ActingPlayer actingPlayer, ISkillProperty propertyToMark,
		int[] indexesToReRoll) {
		actingPlayer.markSkillUsed(propertyToMark);
		int[] reRolledWithPro = getGameState().getDiceRoller().rollBlockDice(indexesToReRoll.length);
		getResult().addReport(new ReportBlockReRoll(reRolledWithPro, actingPlayer.getPlayerId(), state.reRollSource));
		int[] oldRoll = roll.getBlockRoll();
		roll.setBlockRoll(Arrays.copyOf(oldRoll, oldRoll.length));
		for (int i = 0; i < indexesToReRoll.length; i++) {
			roll.getBlockRoll()[indexesToReRoll[i]] = reRolledWithPro[i];
		}
	}

	private DialogOpponentBlockSelectionPropertiesParameter createDefenderDialogParameter(Team team, List<BlockRollProperties> blockRolls) {
		return new DialogOpponentBlockSelectionPropertiesParameter(team.getId(), blockRolls);
	}

	private DialogReRollBlockForTargetsPropertiesParameter createAttackerDialogParameter(Player<?> player, List<BlockRollProperties> blockRolls) {
		return new DialogReRollBlockForTargetsPropertiesParameter(player.getId(), blockRolls);
	}

	private void nextStep() {
		Collections.reverse(state.blockRolls);
		state.blockRolls.forEach(this::generateBlockEvaluationSequence);

		getResult().setNextAction(StepAction.NEXT_STEP);
	}


	private void generateBlockEvaluationSequence(BlockRollProperties blockRoll) {
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
		sequence.add(StepId.STEADY_FOOTING, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		sequence.add(StepId.STEADY_FOOTING, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ATTACKER));
		sequence.add(StepId.HANDLE_DROP_PLAYER_CONTEXT);
		sequence.add(StepId.CONSUME_PARAMETER, from(StepParameterKey.CONSUME_PARAMETER, parameterToConsume));

		getGameState().getStepStack().push(sequence.getSequence());

		BlockResultFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.BLOCK_RESULT);

		// these value is only used in StepEndBlocking
		publishParameter(new StepParameter(StepParameterKey.TARGET_PLAYER_ID, blockRoll.getTargetId()));
		publishParameter(new StepParameter(StepParameterKey.OLD_DEFENDER_STATE, blockRoll.getOldPlayerState()));
		publishParameter(new StepParameter(StepParameterKey.NR_OF_DICE, blockRoll.getNrOfDice()));
		publishParameter(new StepParameter(StepParameterKey.BLOCK_ROLL, blockRoll.getBlockRoll()));
		publishParameter(new StepParameter(StepParameterKey.DICE_INDEX, blockRoll.getSelectedIndex()));
		publishParameter(new StepParameter(StepParameterKey.BLOCK_RESULT,
			factory.forRoll(blockRoll.getBlockRoll()[blockRoll.getSelectedIndex()])));
	}

	private void addReRollSourceMapping(Map<ReRolledAction, ReRollSource> actionToSource, ReRolledAction reRolledAction, Game game) {
		ReRollSource unusedRerollSource = UtilCards.getUnusedRerollSource(game.getActingPlayer(),
			reRolledAction);
		if (unusedRerollSource != null) {
			actionToSource.put(reRolledAction, unusedRerollSource);
		}
	}

	private Map<String, String> convertActionsMap(Map<ReRolledAction, ReRollSource> input) {
		Game game = getGameState().getGame();
		SkillFactory factory = game.getFactory(FactoryType.Factory.SKILL);
		return input.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getName(factory),
			entry -> entry.getValue().getName(game)));
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.STEP_STATE.addTo(jsonObject, state.toJsonValue());
		String[] keys =
			parameterToConsume.stream().map(StepParameterKey::name).collect(Collectors.toList()).toArray(new String[]{});
		IJsonOption.STEP_PARAMETER_KEYS.addTo(jsonObject, keys);
		return jsonObject;
	}

	@Override
	public StepBlockRollMultiple initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state = new State().initFrom(source, IJsonOption.STEP_STATE.getFrom(source, jsonObject));
		parameterToConsume.addAll(
			Arrays.stream(IJsonOption.STEP_PARAMETER_KEYS.getFrom(source, UtilJson.toJsonObject(jsonValue)))
				.map(StepParameterKey::valueOf).collect(Collectors.toSet()));
		return this;
	}

	private static class State implements IJsonSerializable, SingleReRollUseState {
		private List<BlockRollProperties> blockRolls = new ArrayList<>();
		private boolean firstRun = true, attackerTeamSelects = true;
		private ReRollSource reRollSource;
		private String selectedTarget, playerIdForSingleUseReRoll;

		@Override
		public JsonObject toJsonValue() {
			JsonObject jsonObject = new JsonObject();
			JsonArray jsonArray = new JsonArray();
			blockRolls.stream().map(BlockRollProperties::toJsonValue).forEach(jsonArray::add);
			IJsonOption.BLOCK_ROLLS.addTo(jsonObject, jsonArray);
			IJsonOption.PLAYER_ID.addTo(jsonObject, selectedTarget);
			IJsonOption.FIRST_RUN.addTo(jsonObject, firstRun);
			IJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, reRollSource);
			IJsonOption.ATTACKER_SELECTS.addTo(jsonObject, attackerTeamSelects);
			IJsonOption.PLAYER_ID_SINGLE_USE_RE_ROLL.addTo(jsonObject, playerIdForSingleUseReRoll);
			return jsonObject;
		}

		@Override
		public State initFrom(IFactorySource source, JsonValue jsonValue) {
			JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
			JsonArray jsonArray = IJsonOption.BLOCK_ROLLS.getFrom(source, jsonObject);
			blockRolls =
				jsonArray.values().stream().map(value -> new BlockRollProperties().initFrom(source, value)).collect(Collectors.toList());
			selectedTarget = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
			firstRun = IJsonOption.FIRST_RUN.getFrom(source, jsonObject);
			reRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE.getFrom(source, jsonObject);
			attackerTeamSelects = IJsonOption.ATTACKER_SELECTS.getFrom(source, jsonObject);
			playerIdForSingleUseReRoll = IJsonOption.PLAYER_ID_SINGLE_USE_RE_ROLL.getFrom(source, jsonObject);
			return this;
		}

		@Override
		public void setReRollSource(ReRollSource reRollSource) {
			this.reRollSource = reRollSource;
		}

		@Override
		public String getId() {
			return playerIdForSingleUseReRoll;
		}

		@Override
		public void setId(String playerId) {
			playerIdForSingleUseReRoll = playerId;
		}
	}
}
