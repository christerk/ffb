package com.fumbbl.ffb.server.step.bb2025.block;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.BlockResult;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.Pair;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.dialog.DialogBlockRollPartialReRollParameter;
import com.fumbbl.ffb.factory.BlockResultFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
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
import java.util.List;
import java.util.Optional;

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

	private int fNrOfDice, fDiceIndex, proIndex, brawlerIndex = -1, dieIndex = -1;
	private int[] fBlockRoll, reRolledDiceIndexes = new int[0], diceIndexes;
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
					fBlockResult = getGameState().getGame().getRules().<BlockResultFactory>getFactory(Factory.BLOCK_RESULT).forRoll(fBlockRoll[fDiceIndex]);
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_USE_BRAWLER:
					setReRollSource(ReRollSources.BRAWLER);
					setReRolledAction(ReRolledActions.BLOCK);
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_USE_PRO_RE_ROLL_FOR_BLOCK:
					ClientCommandUseProReRollForBlock command = (ClientCommandUseProReRollForBlock) pReceivedCommand.getCommand();
					setReRolledAction(ReRolledActions.BLOCK);
					setReRollSource(ReRollSources.PRO);
					proIndex = command.getProIndex();
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_USE_CONSUMMATE_RE_ROLL_FOR_BLOCK:
					ClientCommandUseConsummateReRollForBlock consummateCommand = (ClientCommandUseConsummateReRollForBlock) pReceivedCommand.getCommand();
					setReRolledAction(ReRolledActions.BLOCK);
					Skill skill = getGameState().getGame().getActingPlayer().getPlayer().getSkillWithProperty(NamedProperties.canRerollSingleDieOncePerPeriod);
					if (skill != null) {
						setReRollSource(skill.getRerollSource(ReRolledActions.SINGLE_DIE));
					}
					proIndex = consummateCommand.getProIndex();
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_USE_SINGLE_BLOCK_DIE_RE_ROLL:
					ClientCommandUseSingleBlockDieReRoll commandUseSkill = (ClientCommandUseSingleBlockDieReRoll) pReceivedCommand.getCommand();
					Skill singleRrSkill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canRerollSingleBlockDieDuringBlitz);
					if (actingPlayer.getPlayerAction().isBlitzing() && singleRrSkill != null) {
						setReRolledAction(ReRolledActions.BLOCK);
						setReRollSource(singleRrSkill.getRerollSource(ReRolledActions.SINGLE_BLOCK_DIE));
						dieIndex = commandUseSkill.getDieIndex();
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_USE_MULTI_BLOCK_DICE_RE_ROLL:
					ClientCommandUseMultiBlockDiceReRoll commandMultiRrr = (ClientCommandUseMultiBlockDiceReRoll) pReceivedCommand.getCommand();
					Skill anyRrSkill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canReRollAnyNumberOfBlockDice);
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
				if ((getReRollSource() == null) || (
					getReRollSource() != ReRollSources.BRAWLER
						&& getReRollSource() != ReRollSources.UNSTOPPABLE_MOMENTUM
						&& getReRollSource() != ReRollSources.SAVAGE_BLOW
						&& !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer()))) {
					doRoll = false;
					if (getReRollSource() == ReRollSources.PRO) {
						reRolledDiceIndexes = add(reRolledDiceIndexes, proIndex);
					}
					showBlockRollDialog(getReRollSource() == null);
				}
			}
			if (doRoll) {
				game.getFieldModel().clearDiceDecorations();
				getResult().addReport(new ReportBlock(game.getDefenderId()));
				getResult().setSound(SoundId.BLOCK);
				if (getReRollSource() == ReRollSources.BRAWLER) {
					handleBrawler(actingPlayer);
				} else {
					TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
					Optional<Skill> addDieSkill = Optional.empty();
					if (targetSelectionState != null) {
						addDieSkill = targetSelectionState.getUsedSkills().stream()
							.filter(skill -> skill.hasSkillProperty(NamedProperties.canAddBlockDie)).findFirst();
					}
					Pair<Integer, Boolean> dieCountWithAddInfo = ServerUtilBlock.findNrOfBlockDice(getGameState(), actingPlayer.getPlayer(),
						game.getDefender(), false, successfulDauntless, doubleTargetStrength, addDieSkill.isPresent());
					fNrOfDice = dieCountWithAddInfo.getLeft();
					if (addDieSkill.isPresent() && dieCountWithAddInfo.getRight()) {
						actingPlayer.markSkillUsed(addDieSkill.get());
					}

					Skill singleDieRrSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canRerollSingleDieOncePerPeriod);

					ReRollSource singleDieReRollSource = null;

					if (singleDieRrSkill != null) {
						singleDieReRollSource = singleDieRrSkill.getRerollSource(ReRolledActions.SINGLE_DIE);
					}

					if (getReRollSource() == ReRollSources.PRO || (getReRollSource() == singleDieReRollSource && singleDieReRollSource != null)) {
						if (getReRollSource() == ReRollSources.PRO) {
							actingPlayer.markSkillUsed(NamedProperties.canRerollOncePerTurn);
						}
						int[] reRolledWithPro = getGameState().getDiceRoller().rollBlockDice(1);
						getResult().addReport(new ReportBlockReRoll(reRolledWithPro, actingPlayer.getPlayerId(), getReRollSource()));
						fBlockRoll = Arrays.copyOf(fBlockRoll, fBlockRoll.length);
						fBlockRoll[proIndex] = reRolledWithPro[0];
						reRolledDiceIndexes = add(reRolledDiceIndexes, proIndex);
					} else if (getReRollSource() == ReRollSources.UNSTOPPABLE_MOMENTUM) {
						if (dieIndex >= 0) {
							int rerolledDie = getGameState().getDiceRoller().rollBlockDice(1)[0];
							getResult().addReport(new ReportBlockReRoll(new int[]{rerolledDie}, actingPlayer.getPlayerId(), getReRollSource()));
							actingPlayer.markSkillUsed(NamedProperties.canRerollSingleBlockDieDuringBlitz);
							fBlockRoll = Arrays.copyOf(fBlockRoll, fBlockRoll.length);
							fBlockRoll[dieIndex] = rerolledDie;
							this.reRolledDiceIndexes = add(reRolledDiceIndexes, dieIndex);
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
								this.reRolledDiceIndexes = add(reRolledDiceIndexes, indexInRoll);
							}
						}
					} else {
						fBlockRoll = getGameState().getDiceRoller().rollBlockDice(fNrOfDice);
					}
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

	private int[] add(int[] original, int newElement) {
		int[] updated = Arrays.copyOf(original, original.length + 1);
		updated[original.length] = newElement;
		return updated;
	}

	private void handleBrawler(ActingPlayer player) {
		int rerolledDie = getGameState().getDiceRoller().rollBlockDice(1)[0];
		getResult().addReport(new ReportBlockReRoll(new int[]{rerolledDie}, player.getPlayerId(), getReRollSource()));
		BlockResultFactory factory = getGameState().getGame().getFactory(Factory.BLOCK_RESULT);
		for (int i = 0; i < fBlockRoll.length; i++) {
			int finalI = i;
			if (factory.forRoll(fBlockRoll[i]) == BlockResult.BOTH_DOWN && Arrays.stream(this.reRolledDiceIndexes).noneMatch(index -> index == finalI)) {
				brawlerIndex = i;
				break;
			}
		}
		if (brawlerIndex >= 0) {
			fBlockRoll = Arrays.copyOf(fBlockRoll, fBlockRoll.length);
			fBlockRoll[brawlerIndex] = rerolledDie;
			this.reRolledDiceIndexes = add(reRolledDiceIndexes, brawlerIndex);
		}
	}

	private void showBlockRollDialog(boolean noReRollUsed) {
		Game game = getGameState().getGame();
		BlockResultFactory factory = game.getFactory(Factory.BLOCK_RESULT);
		ActingPlayer actingPlayer = game.getActingPlayer();
		Skill singleDieRrSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canRerollSingleDieOncePerPeriod);
		ReRollSource singleDieReRollSource = null;
		if (singleDieRrSkill != null) {
			singleDieReRollSource = singleDieRrSkill.getRerollSource(ReRolledActions.SINGLE_DIE);
		}

		Skill singleBlockDieRrSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canRerollSingleBlockDieDuringBlitz);
		ReRollSource singleBlockDieReRollSource = null;
		if (singleBlockDieRrSkill != null) {
			singleBlockDieReRollSource = singleBlockDieRrSkill.getRerollSource(ReRolledActions.SINGLE_BLOCK_DIE);
		}

		Skill anyBlockDiceRrSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canReRollAnyNumberOfBlockDice);
		ReRollSource anyBlockDiceReRollSource = null;
		if (anyBlockDiceRrSkill != null) {
			anyBlockDiceReRollSource = anyBlockDiceRrSkill.getRerollSource(ReRolledActions.MULTI_BLOCK_DICE);
		}

		boolean singleBlockDieRrUsed = getReRollSource() == singleBlockDieReRollSource && singleBlockDieReRollSource != null;
		boolean anyBlockDiceRrUsed = getReRollSource() == anyBlockDiceReRollSource && anyBlockDiceReRollSource != null;
		boolean singleDieRrUsed = getReRollSource() == singleDieReRollSource && singleDieReRollSource != null;
		boolean teamReRollOption = getReRollSource() == null && UtilServerReRoll.isTeamReRollAvailable(getGameState(), actingPlayer.getPlayer());
		boolean singleUseReRollOption = getReRollSource() == null && UtilServerReRoll.isSingleUseReRollAvailable(getGameState(), actingPlayer.getPlayer());
		boolean proReRollOption = (getReRollSource() == null ||
			((getReRollSource() == ReRollSources.BRAWLER || singleDieRrUsed || singleBlockDieRrUsed || anyBlockDiceRrUsed)
				&& fBlockRoll.length > reRolledDiceIndexes.length))
			&& UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canRerollOncePerTurn);
		boolean consummateOption = (getReRollSource() == null ||
			((getReRollSource() == ReRollSources.BRAWLER || getReRollSource() == ReRollSources.PRO || singleBlockDieRrUsed || anyBlockDiceRrUsed)
				&& fBlockRoll.length > reRolledDiceIndexes.length))
			&& UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canRerollSingleDieOncePerPeriod);
		Skill bothdownRrSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canRerollBothDowns);
		boolean brawlerOption = !actingPlayer.getPlayerAction().isBlitzing()
			&& bothdownRrSkill != null && !bothdownRrSkill.conflictsWithAnySkill(actingPlayer.getPlayer()) && brawlerIndex < 0
			&& (getReRollSource() == null ||
			((getReRollSource() == ReRollSources.PRO || singleDieRrUsed || singleBlockDieRrUsed || anyBlockDiceRrUsed)
				&& fBlockRoll.length > reRolledDiceIndexes.length));

		if (brawlerOption) {
			for (int i = 0; i < fBlockRoll.length; i++) {
				int finalI = i;
				if (Arrays.stream(reRolledDiceIndexes).noneMatch(index -> index == finalI) && factory.forRoll(fBlockRoll[i]) == BlockResult.BOTH_DOWN) {
					brawlerOption = true;
					break;
				}
				brawlerOption = false;
			}
		}

		boolean someDiceCanBeReRolled = (getReRollSource() == null || getReRollSource() != ReRollSources.TEAM_RE_ROLL)
			&& fBlockRoll.length > reRolledDiceIndexes.length;
		boolean singleBlockDieOption = actingPlayer.getPlayerAction().isBlitzing()
			&& UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canRerollSingleBlockDieDuringBlitz) && someDiceCanBeReRolled;

		boolean anyBlockDiceOption = UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canReRollAnyNumberOfBlockDice) && someDiceCanBeReRolled;

		String teamId = game.isHomePlaying() ? game.getTeamHome().getId() : game.getTeamAway().getId();
		if ((fNrOfDice < 0) && (noReRollUsed ||
			(!teamReRollOption
				&& !proReRollOption
				&& !brawlerOption
				&& !singleUseReRollOption
				&& !consummateOption
				&& !singleBlockDieOption
				&& !anyBlockDiceOption))) {
			teamReRollOption = false;
			proReRollOption = false;
			brawlerOption = false;
			singleUseReRollOption = false;
			consummateOption = false;
			singleBlockDieOption = false;
			anyBlockDiceOption = false;
			teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
		}
		getResult().addReport(new ReportBlockRoll(teamId, fBlockRoll));
		List<Skill> skills = new ArrayList<>();
		if (singleBlockDieOption) {
			skills.add(singleBlockDieRrSkill);
		}
		if (anyBlockDiceOption) {
			skills.add(anyBlockDiceRrSkill);
		}
		UtilServerDialog.showDialog(getGameState(),
			new DialogBlockRollPartialReRollParameter(teamId, fNrOfDice, fBlockRoll, teamReRollOption, proReRollOption,
				brawlerOption, consummateOption, reRolledDiceIndexes, singleUseReRollOption ? ReRollSources.LORD_OF_CHAOS : null,
				skills),
			(fNrOfDice < 0));
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
		IServerJsonOption.RE_ROLLED_DICE_INDEXES.addTo(jsonObject, reRolledDiceIndexes);
		IServerJsonOption.PRO_INDEX.addTo(jsonObject, proIndex);
		IServerJsonOption.BRAWLER_INDEX.addTo(jsonObject, brawlerIndex);
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
		reRolledDiceIndexes = IServerJsonOption.RE_ROLLED_DICE_INDEXES.getFrom(source, jsonObject);
		proIndex = IServerJsonOption.PRO_INDEX.getFrom(source, jsonObject);
		brawlerIndex = IServerJsonOption.BRAWLER_INDEX.getFrom(source, jsonObject);
		if (IServerJsonOption.BLOCK_DIE_INDEX.isDefinedIn(jsonObject)) {
			dieIndex = IServerJsonOption.BLOCK_DIE_INDEX.getFrom(source, jsonObject);
		}
		return this;
	}

}
