package com.fumbbl.ffb.server.step.bb2025.block;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.BlockResult;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.Pair;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.dialog.DialogBlockRollPropertiesParameter;
import com.fumbbl.ffb.factory.BlockResultFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandBlockChoice;
import com.fumbbl.ffb.net.commands.ClientCommandUseConsummateReRollForBlock;
import com.fumbbl.ffb.net.commands.ClientCommandUseMultiBlockDiceReRoll;
import com.fumbbl.ffb.net.commands.ClientCommandUseProReRollForBlock;
import com.fumbbl.ffb.net.commands.ClientCommandUseSingleBlockDieReRoll;
import com.fumbbl.ffb.report.ReportBlock;
import com.fumbbl.ffb.report.ReportBlockRoll;
import com.fumbbl.ffb.report.mixed.ReportBlockReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.UtilCards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Step in block sequence to handle the block roll.
 * <p>
 * Sets stepParameter BLOCK_DICE_INDEX for all steps on the stack. Sets
 * stepParameter BLOCK_RESULT for all steps on the stack. Sets stepParameter
 * BLOCK_ROLL for all steps on the stack. Sets stepParameter NR_OF_BLOCK_DICE
 * for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepBlockRoll extends AbstractStepWithReRoll {

	private int fNrOfDice, fDiceIndex, dieIndex = -1;
	private int[] fBlockRoll, diceIndexes;
	private BlockResult fBlockResult;
	private boolean successfulDauntless, doubleTargetStrength;

	public StepBlockRoll(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.BLOCK_ROLL;
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
			ActingPlayer actingPlayer = getGameState().getGame().getActingPlayer();
			switch (pReceivedCommand.getId()) {
				case CLIENT_BLOCK_CHOICE:
					ClientCommandBlockChoice blockChoiceCommand = (ClientCommandBlockChoice) pReceivedCommand.getCommand();
					fDiceIndex = blockChoiceCommand.getDiceIndex();
					fBlockResult = getGameState().getGame().getRules().<BlockResultFactory>getFactory(Factory.BLOCK_RESULT)
						.forRoll(fBlockRoll[fDiceIndex]);
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_USE_BRAWLER:
					setReRollSource(ReRollSources.BRAWLER);
					setReRolledAction(ReRolledActions.BLOCK);
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_USE_HATRED:
					setReRollSource(ReRollSources.HATRED);
					setReRolledAction(ReRolledActions.BLOCK);
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_USE_PRO_RE_ROLL_FOR_BLOCK:
					ClientCommandUseProReRollForBlock command = (ClientCommandUseProReRollForBlock) pReceivedCommand.getCommand();
					setReRolledAction(ReRolledActions.BLOCK);
					setReRollSource(ReRollSources.PRO);
					dieIndex = command.getProIndex();
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_USE_CONSUMMATE_RE_ROLL_FOR_BLOCK:
					ClientCommandUseConsummateReRollForBlock consummateCommand =
						(ClientCommandUseConsummateReRollForBlock) pReceivedCommand.getCommand();
					setReRolledAction(ReRolledActions.BLOCK);
					Skill skill = getGameState().getGame().getActingPlayer().getPlayer()
						.getSkillWithProperty(NamedProperties.canRerollSingleDieOncePerPeriod);
					if (skill != null) {
						setReRollSource(skill.getRerollSource(ReRolledActions.SINGLE_DIE));
					}
					dieIndex = consummateCommand.getProIndex();
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_USE_SINGLE_BLOCK_DIE_RE_ROLL:
					ClientCommandUseSingleBlockDieReRoll commandUseSkill =
						(ClientCommandUseSingleBlockDieReRoll) pReceivedCommand.getCommand();
					Skill singleRrSkill =
						UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canRerollSingleBlockDieDuringBlitz);
					if (actingPlayer.getPlayerAction().isBlitzing() && singleRrSkill != null) {
						setReRolledAction(ReRolledActions.BLOCK);
						setReRollSource(singleRrSkill.getRerollSource(ReRolledActions.SINGLE_BLOCK_DIE));
						dieIndex = commandUseSkill.getDieIndex();
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_USE_MULTI_BLOCK_DICE_RE_ROLL:
					ClientCommandUseMultiBlockDiceReRoll commandMultiRrr =
						(ClientCommandUseMultiBlockDiceReRoll) pReceivedCommand.getCommand();
					Skill anyRrSkill =
						UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canReRollAnyNumberOfBlockDice);
					if (anyRrSkill != null) {
						setReRolledAction(ReRolledActions.BLOCK);
						setReRollSource(anyRrSkill.getRerollSource(ReRolledActions.MULTI_BLOCK_DICE));
						diceIndexes = commandMultiRrr.getDiceIndexes();
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				default:
					break;
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			switch (parameter.getKey()) {
				case SUCCESSFUL_DAUNTLESS:
					successfulDauntless = (boolean) parameter.getValue();
					consume(parameter);
					return true;
				case DOUBLE_TARGET_STRENGTH:
					doubleTargetStrength = (boolean) parameter.getValue();
					consume(parameter);
					return true;
				default:
					break;
			}
		}
		return false;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (fBlockResult == null) {
			boolean doRoll = true;
			if (ReRolledActions.BLOCK == getReRolledAction()) {
				if ((getReRollSource() == null) ||
					!UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
					doRoll = false;
					showBlockRollDialog(getReRollSource() == null);
				}
			}
			if (doRoll) {
				game.getFieldModel().clearDiceDecorations();
				getResult().addReport(new ReportBlock(game.getDefenderId()));
				getResult().setSound(SoundId.BLOCK);
				if (getReRollSource() == ReRollSources.BRAWLER) {
					handleImplicitReRollIndex(actingPlayer, BlockResult.BOTH_DOWN);
				} else if (getReRollSource() == ReRollSources.HATRED) {
					handleImplicitReRollIndex(actingPlayer, BlockResult.SKULL);
				} else {
					handleInitialRollAndReRollWithExplicitSelection(game, actingPlayer);
				}
				showBlockRollDialog(false);
			}
		} else {
			getGameState().removeAdditionalAssist(game.getActingTeam().getId());
			publishParameter(new StepParameter(StepParameterKey.NR_OF_DICE, fNrOfDice));
			publishParameter(new StepParameter(StepParameterKey.BLOCK_ROLL, fBlockRoll));
			publishParameter(new StepParameter(StepParameterKey.DICE_INDEX, fDiceIndex));
			publishParameter(new StepParameter(StepParameterKey.BLOCK_RESULT, fBlockResult));
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	private void handleInitialRollAndReRollWithExplicitSelection(Game game, ActingPlayer actingPlayer) {
		TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
		Optional<Skill> addDieSkill = Optional.empty();
		if (targetSelectionState != null) {
			addDieSkill = targetSelectionState.getUsedSkills().stream()
				.filter(skill -> skill.hasSkillProperty(NamedProperties.canAddBlockDie)).findFirst();
		}
		Pair<Integer, Boolean> dieCountWithAddInfo =
			ServerUtilBlock.findNrOfBlockDice(getGameState(), actingPlayer.getPlayer(),
				game.getDefender(), false, successfulDauntless, doubleTargetStrength, addDieSkill.isPresent());
		fNrOfDice = dieCountWithAddInfo.getLeft();
		if (addDieSkill.isPresent() && dieCountWithAddInfo.getRight()) {
			actingPlayer.markSkillUsed(addDieSkill.get());
		}


		ReRollSource singleDieReRollSource =
			UtilCards.getUnusedRerollSource(actingPlayer, ReRolledActions.SINGLE_DIE);

		if (getReRollSource() == ReRollSources.PRO ||
			(getReRollSource() == singleDieReRollSource && singleDieReRollSource != null)) {
			if (getReRollSource() == ReRollSources.PRO) {
				actingPlayer.markSkillUsed(NamedProperties.canRerollOncePerTurn);
			}
			int[] reRolledWithPro = getGameState().getDiceRoller().rollBlockDice(1);
			getResult().addReport(
				new ReportBlockReRoll(reRolledWithPro, actingPlayer.getPlayerId(), getReRollSource()));
			fBlockRoll = Arrays.copyOf(fBlockRoll, fBlockRoll.length);
			fBlockRoll[dieIndex] = reRolledWithPro[0];
		} else if (getReRollSource() == ReRollSources.UNSTOPPABLE_MOMENTUM) {
			if (dieIndex >= 0) {
				int rerolledDie = getGameState().getDiceRoller().rollBlockDice(1)[0];
				getResult().addReport(
					new ReportBlockReRoll(new int[]{rerolledDie}, actingPlayer.getPlayerId(), getReRollSource()));
				actingPlayer.markSkillUsed(NamedProperties.canRerollSingleBlockDieDuringBlitz);
				fBlockRoll = Arrays.copyOf(fBlockRoll, fBlockRoll.length);
				fBlockRoll[dieIndex] = rerolledDie;
			}
		} else if (getReRollSource() == ReRollSources.SAVAGE_BLOW) {
			if (diceIndexes != null) {
				actingPlayer.markSkillUsed(NamedProperties.canReRollAnyNumberOfBlockDice);
				int[] rerolledDice = getGameState().getDiceRoller().rollBlockDice(diceIndexes.length);
				getResult().addReport(new ReportBlockReRoll(rerolledDice, actingPlayer.getPlayerId(), getReRollSource()));
				fBlockRoll = Arrays.copyOf(fBlockRoll, fBlockRoll.length);
				for (int indexPos = 0; indexPos < diceIndexes.length; indexPos++) {
					int indexInRoll = diceIndexes[indexPos];
					fBlockRoll[indexInRoll] = rerolledDice[indexPos];
				}
			}
		} else {
			fBlockRoll = getGameState().getDiceRoller().rollBlockDice(fNrOfDice);
		}
	}

	private void handleImplicitReRollIndex(ActingPlayer player, BlockResult resultToReplace) {
		int rerolledDie = getGameState().getDiceRoller().rollBlockDice(1)[0];
		getResult().addReport(new ReportBlockReRoll(new int[]{rerolledDie}, player.getPlayerId(), getReRollSource()));
		BlockResultFactory factory = getGameState().getGame().getFactory(Factory.BLOCK_RESULT);
		for (int i = 0; i < fBlockRoll.length; i++) {
			if (factory.forRoll(fBlockRoll[i]) == resultToReplace) {
				dieIndex = i;
				break;
			}
		}
		if (dieIndex >= 0) {
			fBlockRoll = Arrays.copyOf(fBlockRoll, fBlockRoll.length);
			fBlockRoll[dieIndex] = rerolledDie;
		}
	}

	private void showBlockRollDialog(boolean noReRollUsed) {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		List<ReRollProperty> properties = new ArrayList<>();
		Map<ReRolledAction, ReRollSource> actionToSource = new HashMap<>();

		if (getReRollSource() == null) {

			if (actingPlayer.getPlayerAction().isBlitzing()) {
				addReRollSourceMapping(actionToSource, ReRolledActions.SINGLE_BLOCK_DIE, game);
			}
			addReRollSourceMapping(actionToSource, ReRolledActions.SINGLE_DIE, game);
			addReRollSourceMapping(actionToSource, ReRolledActions.MULTI_BLOCK_DICE, game);
			addReRollSourceMapping(actionToSource, ReRolledActions.SINGLE_DIE_PER_ACTIVATION, game);

			if (UtilServerReRoll.isTeamReRollAvailable(getGameState(),
				actingPlayer.getPlayer())) {
				properties.add(ReRollProperty.TRR);
			}

			evaluateBrawlerAvailability(game, actingPlayer, actionToSource);
			evaluateHatredAvailability(game, actingPlayer, actionToSource);

			RollMechanic mechanic = game.getMechanic(Mechanic.Type.ROLL);
			mechanic.findAdditionalReRollProperty(game.getTurnData()).ifPresent(properties::add);

			if (mechanic.isMascotAvailable(game)) {
				properties.add(ReRollProperty.MASCOT);
			}
		}

		String teamId = game.isHomePlaying() ? game.getTeamHome().getId() : game.getTeamAway().getId();
		if ((fNrOfDice < 0) &&
			(noReRollUsed || (properties.stream().noneMatch(ReRollProperty::isActualReRoll) && actionToSource.isEmpty()))) {
			properties.removeIf(ReRollProperty::isActualReRoll);
			actionToSource.clear();
			teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
		}
		getResult().addReport(new ReportBlockRoll(teamId, fBlockRoll));

		UtilServerDialog.showDialog(getGameState(),
			new DialogBlockRollPropertiesParameter(teamId, fNrOfDice, fBlockRoll, properties,
				convertActionsMap(actionToSource)), (fNrOfDice < 0));
	}

	private void evaluateBrawlerAvailability(Game game, ActingPlayer actingPlayer,
		Map<ReRolledAction, ReRollSource> actionToSource) {
		BlockResultFactory factory = game.getFactory(Factory.BLOCK_RESULT);
		Skill bothdownRrSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canRerollSingleBothDown);
		boolean brawlerOption = !actingPlayer.getPlayerAction().isBlitzing()
			&& bothdownRrSkill != null && !bothdownRrSkill.conflictsWithAnySkill(actingPlayer.getPlayer());

		if (brawlerOption) {
			for (int roll : fBlockRoll) {
				if (factory.forRoll(roll) == BlockResult.BOTH_DOWN) {
					addReRollSourceMapping(actionToSource, ReRolledActions.SINGLE_BOTH_DOWN, game);
					break;
				}
			}
		}
	}

	private void evaluateHatredAvailability(Game game, ActingPlayer actingPlayer,
		Map<ReRolledAction, ReRollSource> actionToSource) {
		BlockResultFactory factory = game.getFactory(Factory.BLOCK_RESULT);
		Skill hatred = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canRerollSingleSkull);
		if (hatred == null) {
			return;
		}
		List<Keyword> defenderKeywords = game.getDefender().getPosition().getKeywords();
		List<Keyword> attackerKeywords =
			hatred.evaluator().values(hatred, actingPlayer.getPlayer()).stream().map(Keyword::forName)
				.collect(Collectors.toList());
		boolean hatredPresent = !Collections.disjoint(defenderKeywords, attackerKeywords);

		if (hatredPresent) {
			for (int roll : fBlockRoll) {
				if (factory.forRoll(roll) == BlockResult.SKULL) {
					addReRollSourceMapping(actionToSource, ReRolledActions.SINGLE_SKULL, game);
					break;
				}
			}
		}
	}

	private void addReRollSourceMapping(Map<ReRolledAction, ReRollSource> actionToSource, ReRolledAction reRolledAction,
		Game game) {
		ReRollSource unusedRerollSource = UtilCards.getUnusedRerollSource(game.getActingPlayer(),
			reRolledAction);
		if (unusedRerollSource != null) {
			actionToSource.put(reRolledAction, unusedRerollSource);
		}
	}

	private Map<String, String> convertActionsMap(Map<ReRolledAction, ReRollSource> input) {
		Game game = getGameState().getGame();
		SkillFactory factory = game.getFactory(Factory.SKILL);
		return input.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getName(factory),
			entry -> entry.getValue().getName(game)));
	}
	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.NR_OF_DICE.addTo(jsonObject, fNrOfDice);
		IServerJsonOption.BLOCK_ROLL.addTo(jsonObject, fBlockRoll);
		IServerJsonOption.DICE_INDEX.addTo(jsonObject, fDiceIndex);
		IServerJsonOption.BLOCK_RESULT.addTo(jsonObject, fBlockResult);
		IServerJsonOption.SUCCESSFUL_DAUNTLESS.addTo(jsonObject, successfulDauntless);
		IServerJsonOption.BLOCK_DIE_INDEX.addTo(jsonObject, dieIndex);
		return jsonObject;
	}

	@Override
	public StepBlockRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fNrOfDice = IServerJsonOption.NR_OF_DICE.getFrom(source, jsonObject);
		fBlockRoll = IServerJsonOption.BLOCK_ROLL.getFrom(source, jsonObject);
		fDiceIndex = IServerJsonOption.DICE_INDEX.getFrom(source, jsonObject);
		fBlockResult = (BlockResult) IServerJsonOption.BLOCK_RESULT.getFrom(source, jsonObject);
		successfulDauntless = IServerJsonOption.SUCCESSFUL_DAUNTLESS.getFrom(source, jsonObject);
		if (IServerJsonOption.BLOCK_DIE_INDEX.isDefinedIn(jsonObject)) {
			dieIndex = IServerJsonOption.BLOCK_DIE_INDEX.getFrom(source, jsonObject);
		}
		return this;
	}

}
