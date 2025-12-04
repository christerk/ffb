package com.fumbbl.ffb.server.step.bb2020.multiblock;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.*;
import com.fumbbl.ffb.dialog.DialogOpponentBlockSelectionParameter;
import com.fumbbl.ffb.dialog.DialogReRollBlockForTargetsParameter;
import com.fumbbl.ffb.factory.BlockResultFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandBlockOrReRollChoiceForTarget;
import com.fumbbl.ffb.net.commands.ClientCommandUseBrawler;
import com.fumbbl.ffb.report.ReportBlock;
import com.fumbbl.ffb.report.ReportBlockRoll;
import com.fumbbl.ffb.report.mixed.ReportBlockReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.mixed.SingleReRollUseState;
import com.fumbbl.ffb.server.step.*;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2020)
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
							new BlockRoll(target.getPlayerId(), target.getOriginalPlayerState(), targets.indexOf(target))
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
					ClientCommandBlockOrReRollChoiceForTarget command = (ClientCommandBlockOrReRollChoiceForTarget) pReceivedCommand.getCommand();
					if (reRollSourceSuccessfully(command.getReRollSource())) {
						stepCommandStatus = StepCommandStatus.EXECUTE_STEP;
					}

					state.selectedTarget = command.getTargetId();
					state.blockRolls.stream().filter(roll -> roll.getTargetId().equals(command.getTargetId()))
						.findFirst().ifPresent(roll -> {
							roll.setSelectedIndex(command.getSelectedIndex());
							roll.setProIndex(command.getProIndex());
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
		final Skill singleDieReRollSkill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canRerollSingleDieOncePerPeriod);
		if (singleDieReRollSkill != null) {
			return singleDieReRollSkill.getRerollSource(ReRolledActions.SINGLE_DIE);
		}
		return null;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		ReRollSource singleDieReRollSource = getSingleDieReRollSource(actingPlayer);

		if (state.firstRun) {
			state.firstRun = false;
			game.getFieldModel().clearDiceDecorations();

			final boolean teamReRollAvailable = UtilServerReRoll.isTeamReRollAvailable(getGameState(), actingPlayer.getPlayer());
			final boolean singleUseReRollAvailable = UtilServerReRoll.isSingleUseReRollAvailable(getGameState(), actingPlayer.getPlayer());
			final boolean proReRollAvailable = UtilServerReRoll.isProReRollAvailable(actingPlayer.getPlayer(), game, null);
			final boolean brawlerAvailable = actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canRerollBothDowns);

			state.blockRolls.forEach(roll -> {

				Player<?> defender = game.getPlayerById(roll.getTargetId());
				int nrOfDice = ServerUtilBlock.findNrOfBlockDice(game, actingPlayer.getPlayer(), defender, true, roll.isSuccessFulDauntless(), roll.isDoubleTargetStrength(), false).getLeft();
				roll.setNrOfDice(Math.abs(nrOfDice));
				roll.setOwnChoice(nrOfDice > 0);
				roll(roll, false, actingPlayer, singleDieReRollSource);
				if (teamReRollAvailable) {
					roll.add(ReRollSources.TEAM_RE_ROLL);
				}
				if (singleUseReRollAvailable) {
					roll.add(ReRollSources.LORD_OF_CHAOS);
				}
				if (proReRollAvailable) {
					roll.add(ReRollSources.PRO);
				}
				if (brawlerAvailable) {
					roll.add(ReRollSources.BRAWLER);
				}
				if (singleDieReRollSource != null) {
					roll.add(singleDieReRollSource);
				}
				getResult().setSound(SoundId.BLOCK);
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
							} else if (state.reRollSource == ReRollSources.PRO) {
								roll.setReRollDiceIndexes(add(roll.getReRollDiceIndexes(), roll.getProIndex()));
							}
							if (roll.getReRollDiceIndexes().length == roll.getNrOfDice()) {
								roll.clearReRollSources();
							} else {
								roll.remove(state.reRollSource);
								roll.remove(ReRollSources.TEAM_RE_ROLL);
								roll.remove(ReRollSources.LORD_OF_CHAOS);
							}
							getResult().addReport(new ReportBlockRoll(defender.getTeam().getId(), roll.getBlockRoll(), roll.getTargetId()));
						} else {
							roll.clearReRollSources();
						}
					});

			}
			decideNextStep(game);
		}
	}

	private void handleBrawler(Player<?> player, BlockRoll blockRoll) {
		int reRolledDie = getGameState().getDiceRoller().rollBlockDice(1)[0];
		getResult().addReport(new ReportBlockReRoll(new int[]{reRolledDie}, player.getId(), ReRollSources.BRAWLER));
		int brawlerIndex = -1;
		BlockResultFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.BLOCK_RESULT);
		for (int i = 0; i < blockRoll.getNrOfDice(); i++) {
			if (factory.forRoll(blockRoll.getBlockRoll()[i]) == BlockResult.BOTH_DOWN && !blockRoll.indexWasReRolled(i)) {
				int[] oldRoll = blockRoll.getBlockRoll();
				blockRoll.setBlockRoll(Arrays.copyOf(oldRoll, oldRoll.length));
				blockRoll.getBlockRoll()[i] = reRolledDie;
				brawlerIndex = i;
				break;
			}
		}

		if (brawlerIndex >= 0) {
			blockRoll.setReRollDiceIndexes(IntStream.concat(IntStream.of(brawlerIndex), Arrays.stream(blockRoll.getReRollDiceIndexes())).toArray());
		}
	}

	private void decideNextStep(Game game) {
		List<BlockRoll> unselected = state.blockRolls.stream().filter(BlockRoll::needsSelection).collect(Collectors.toList());

		if (unselected.isEmpty()) {
			nextStep();
			return;
		}

		ActingPlayer actingPlayer = game.getActingPlayer();
		final boolean teamReRollAvailable = UtilServerReRoll.isTeamReRollAvailable(getGameState(), actingPlayer.getPlayer());
		final boolean singleUseReRollAvailable = UtilServerReRoll.isSingleUseReRollAvailable(getGameState(), actingPlayer.getPlayer());
		final boolean proReRollAvailable = UtilServerReRoll.isProReRollAvailable(actingPlayer.getPlayer(), game, null);
		final Optional<Skill> singleDieReRollSkill = UtilCards.getSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.canRerollSingleDieOncePerPeriod);
		final boolean singleDieReRollAvailable = singleDieReRollSkill.isPresent() && !actingPlayer.isSkillUsed(singleDieReRollSkill.get());

		BlockResultFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.BLOCK_RESULT);

		state.blockRolls.forEach(roll -> {
			if (!teamReRollAvailable) {
				roll.remove(ReRollSources.TEAM_RE_ROLL);
			}
			if (!singleUseReRollAvailable) {
				roll.remove(ReRollSources.LORD_OF_CHAOS);
			}
			if (!proReRollAvailable) {
				roll.remove(ReRollSources.PRO);
			}
			if (!singleDieReRollAvailable && singleDieReRollSkill.isPresent()) {
				roll.remove(singleDieReRollSkill.get().getRerollSource(ReRolledActions.SINGLE_DIE));
			}

			boolean bothDownPresent = false;

			for (int i = 0; i < roll.getBlockRoll().length; i++) {
				if (!roll.indexWasReRolled(i) && factory.forRoll(roll.getBlockRoll()[i]) == BlockResult.BOTH_DOWN) {
					bothDownPresent = true;
					break;
				}
			}

			if (!bothDownPresent) {
				roll.remove(ReRollSources.BRAWLER);
			}
		});

		boolean anyReRollLeft = state.blockRolls.stream().anyMatch(BlockRoll::hasReRollsLeft);

		if (state.attackerTeamSelects) {
			if (unselected.stream().anyMatch(BlockRoll::isOwnChoice) || anyReRollLeft) {
				UtilServerDialog.showDialog(getGameState(), createAttackerDialogParameter(actingPlayer.getPlayer(), state.blockRolls), false);
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

	private void roll(BlockRoll roll, boolean reRolling, ActingPlayer actingPlayer, ReRollSource singleDieReRollSource) {
		Game game = getGameState().getGame();
		if (reRolling) {
			if (state.reRollSource == ReRollSources.PRO) {
				adjustRollForIndexedReRoll(roll, actingPlayer, NamedProperties.canRerollOncePerTurn);
			} else if (singleDieReRollSource != null && state.reRollSource == singleDieReRollSource) {
				adjustRollForIndexedReRoll(roll, actingPlayer, NamedProperties.canRerollSingleDieOncePerPeriod);
			} else {
				roll.clearReRollSources();
				roll.setBlockRoll(getGameState().getDiceRoller().rollBlockDice(roll.getNrOfDice()));
			}
		} else {
			roll.setBlockRoll(game, getGameState().getDiceRoller().rollBlockDice(roll.getNrOfDice()));
		}
	}

	private void adjustRollForIndexedReRoll(BlockRoll roll, ActingPlayer actingPlayer, ISkillProperty propertyToMark) {
		BlockResultFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.BLOCK_RESULT);
		actingPlayer.markSkillUsed(propertyToMark);
		int[] reRolledWithPro = getGameState().getDiceRoller().rollBlockDice(1);
		getResult().addReport(new ReportBlockReRoll(reRolledWithPro, actingPlayer.getPlayerId(), state.reRollSource));
		int[] oldRoll = roll.getBlockRoll();
		roll.setBlockRoll(Arrays.copyOf(oldRoll, oldRoll.length));
		roll.getBlockRoll()[roll.getProIndex()] = reRolledWithPro[0];
		roll.setReRollDiceIndexes(add(roll.getReRollDiceIndexes(), roll.getProIndex()));
		if (Arrays.stream(roll.getBlockRoll()).mapToObj(factory::forRoll).noneMatch(blockResult -> blockResult == BlockResult.BOTH_DOWN)) {
			roll.remove(ReRollSources.BRAWLER);
		}
	}

	private int[] add(int[] original, int newElement) {
		int[] updated = Arrays.copyOf(original, original.length + 1);
		updated[original.length] = newElement;
		return updated;
	}

	private DialogOpponentBlockSelectionParameter createDefenderDialogParameter(Team team, List<BlockRoll> blockRolls) {
		return new DialogOpponentBlockSelectionParameter(team.getId(), blockRolls);
	}

	private DialogReRollBlockForTargetsParameter createAttackerDialogParameter(Player<?> player, List<BlockRoll> blockRolls) {
		return new DialogReRollBlockForTargetsParameter(player.getId(), blockRolls);
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
		publishParameter(new StepParameter(StepParameterKey.BLOCK_RESULT, factory.forRoll(blockRoll.getBlockRoll()[blockRoll.getSelectedIndex()])));
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.STEP_STATE.addTo(jsonObject, state.toJsonValue());
		String[] keys = parameterToConsume.stream().map(StepParameterKey::name).collect(Collectors.toList()).toArray(new String[]{});
		IJsonOption.STEP_PARAMETER_KEYS.addTo(jsonObject, keys);
		return jsonObject;
	}

	@Override
	public StepBlockRollMultiple initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state = new State().initFrom(source, IJsonOption.STEP_STATE.getFrom(source, jsonObject));
		parameterToConsume.addAll(Arrays.stream(IJsonOption.STEP_PARAMETER_KEYS.getFrom(source, UtilJson.toJsonObject(jsonValue)))
			.map(StepParameterKey::valueOf).collect(Collectors.toSet()));
		return this;
	}

	private static class State implements IJsonSerializable, SingleReRollUseState {
		private List<BlockRoll> blockRolls = new ArrayList<>();
		private boolean firstRun = true, attackerTeamSelects = true;
		private ReRollSource reRollSource;
		private String selectedTarget, playerIdForSingleUseReRoll;

		@Override
		public JsonObject toJsonValue() {
			JsonObject jsonObject = new JsonObject();
			JsonArray jsonArray = new JsonArray();
			blockRolls.stream().map(BlockRoll::toJsonValue).forEach(jsonArray::add);
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
			blockRolls = jsonArray.values().stream().map(value -> new BlockRoll().initFrom(source, value)).collect(Collectors.toList());
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
