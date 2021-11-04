package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.BlockResult;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.PlayerAction;
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
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandBlockChoice;
import com.fumbbl.ffb.net.commands.ClientCommandUseProReRollForBlock;
import com.fumbbl.ffb.report.ReportBlock;
import com.fumbbl.ffb.report.ReportBlockRoll;
import com.fumbbl.ffb.report.bb2020.ReportBlockReRoll;
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

import java.util.Arrays;

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
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepBlockRoll extends AbstractStepWithReRoll {

	private int fNrOfDice, fDiceIndex, proIndex, brawlerIndex = -1;
	private int[] fBlockRoll, reRolledDiceIndexes = new int[0];
	private BlockResult fBlockResult;
	private boolean successfulDauntless;

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
		if (parameter != null && parameter.getKey() == StepParameterKey.SUCCESSFUL_DAUNTLESS) {
			successfulDauntless = (boolean) parameter.getValue();
			consume(parameter);
			return true;
		}
		return false;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (fBlockResult == null) {
			boolean doRoll = true;
			if (ReRolledActions.BLOCK == getReRolledAction()) {
				if ((getReRollSource() == null)
					|| (getReRollSource() != ReRollSources.BRAWLER && !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer()))) {
					doRoll = false;
					showBlockRollDialog(doRoll);
				}
			}
			if (doRoll) {
				game.getFieldModel().clearDiceDecorations();
				getResult().addReport(new ReportBlock(game.getDefenderId()));
				getResult().setSound(SoundId.BLOCK);
				if (getReRollSource() == ReRollSources.BRAWLER) {
					handleBrawler(actingPlayer);
				} else {
					fNrOfDice = ServerUtilBlock.findNrOfBlockDice(game, actingPlayer.getPlayer(),
						game.getDefender(), false, successfulDauntless);
					if (getReRollSource() == ReRollSources.PRO) {
						actingPlayer.markSkillUsed(NamedProperties.canRerollOncePerTurn);
						int[] reRolledWithPro = getGameState().getDiceRoller().rollBlockDice(1);
						getResult().addReport(new ReportBlockReRoll(reRolledWithPro, actingPlayer.getPlayerId(), getReRollSource()));
						fBlockRoll = Arrays.copyOf(fBlockRoll, fBlockRoll.length);
						fBlockRoll[proIndex] = reRolledWithPro[0];
						reRolledDiceIndexes = add(reRolledDiceIndexes, proIndex);
					} else {
						fBlockRoll = getGameState().getDiceRoller().rollBlockDice(fNrOfDice);
					}
				}
				showBlockRollDialog(doRoll);
			}
		} else {
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

	private void showBlockRollDialog(boolean pDoRoll) {
		Game game = getGameState().getGame();
		BlockResultFactory factory = game.getFactory(Factory.BLOCK_RESULT);
		ActingPlayer actingPlayer = game.getActingPlayer();
		boolean teamReRollOption = getReRollSource() == null && UtilServerReRoll.isTeamReRollAvailable(getGameState(), actingPlayer.getPlayer());
		boolean proReRollOption = (getReRollSource() == null || (getReRollSource() == ReRollSources.BRAWLER && fBlockRoll.length > reRolledDiceIndexes.length))
			&& UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canRerollOncePerTurn);
		boolean brawlerOption = actingPlayer.getPlayerAction() != PlayerAction.BLITZ
			&& actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canRerollBothDowns) && brawlerIndex < 0
			&& (getReRollSource() == null || (getReRollSource() == ReRollSources.PRO && fBlockRoll.length > reRolledDiceIndexes.length));

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

		String teamId = game.isHomePlaying() ? game.getTeamHome().getId() : game.getTeamAway().getId();
		if ((fNrOfDice < 0) && (!pDoRoll || (!teamReRollOption && !proReRollOption && !brawlerOption))) {
			teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
			teamReRollOption = false;
			proReRollOption = false;
			brawlerOption = false;
		}
		getResult().addReport(new ReportBlockRoll(teamId, fBlockRoll));
		UtilServerDialog.showDialog(getGameState(),
			new DialogBlockRollPartialReRollParameter(teamId, fNrOfDice, fBlockRoll, teamReRollOption, proReRollOption, brawlerOption, reRolledDiceIndexes),
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
		return jsonObject;
	}

	@Override
	public StepBlockRoll initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fNrOfDice = IServerJsonOption.NR_OF_DICE.getFrom(source, jsonObject);
		fBlockRoll = IServerJsonOption.BLOCK_ROLL.getFrom(source, jsonObject);
		fDiceIndex = IServerJsonOption.DICE_INDEX.getFrom(source, jsonObject);
		fBlockResult = (BlockResult) IServerJsonOption.BLOCK_RESULT.getFrom(source, jsonObject);
		successfulDauntless = IServerJsonOption.SUCCESSFUL_DAUNTLESS.getFrom(source, jsonObject);
		reRolledDiceIndexes = IServerJsonOption.RE_ROLLED_DICE_INDEXES.getFrom(source, jsonObject);
		proIndex = IServerJsonOption.PRO_INDEX.getFrom(source, jsonObject);
		brawlerIndex = IServerJsonOption.BRAWLER_INDEX.getFrom(source, jsonObject);
		return this;
	}

}
