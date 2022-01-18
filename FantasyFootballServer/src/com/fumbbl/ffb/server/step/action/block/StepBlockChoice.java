package com.fumbbl.ffb.server.step.action.block;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.BlockResult;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.ReportBlockChoice;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

/**
 * Step in block sequence to handle the block choice.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_DODGE. Needs to be
 * initialized with stepParameter GOTO_LABEL_ON_JUGGERNAUT. Needs to be
 * initialized with stepParameter GOTO_LABEL_ON_PUSHBACK.
 * <p>
 * Expects stepParameter BLOCK_DICE_INDEX to be set by a preceding step. Expects
 * stepParameter BLOCK_RESULT to be set by a preceding step. Expects
 * stepParameter BLOCK_ROLL to be set by a preceding step. Expects stepParameter
 * NR_OF_BLOCK_DICE to be set by a preceding step. Expects stepParameter
 * OLD_DEFENDER_STATE to be set by a preceding step.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepBlockChoice extends AbstractStep {

	private String fGotoLabelOnDodge;
	private String fGotoLabelOnJuggernaut;
	private String fGotoLabelOnPushback;

	private int fNrOfDice;
	private int[] fBlockRoll;
	private int fDiceIndex, blockRollId;
	private BlockResult fBlockResult;
	private PlayerState fOldDefenderState;
	private boolean suppressExtraEffectHandling, showNameInReport;

	public StepBlockChoice(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.BLOCK_CHOICE;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					// mandatory
					case GOTO_LABEL_ON_DODGE:
						fGotoLabelOnDodge = (String) parameter.getValue();
						break;
					// mandatory
					case GOTO_LABEL_ON_JUGGERNAUT:
						fGotoLabelOnJuggernaut = (String) parameter.getValue();
						break;
					// mandatory
					case GOTO_LABEL_ON_PUSHBACK:
						fGotoLabelOnPushback = (String) parameter.getValue();
						break;
					case SUPPRESS_EXTRA_EFFECT_HANDLING:
						suppressExtraEffectHandling = parameter.getValue() != null && (boolean) parameter.getValue();
						break;
					case SHOW_NAME_IN_REPORT:
						showNameInReport = parameter.getValue() != null && (boolean) parameter.getValue();
						break;
					case BLOCK_ROLL_ID:
						blockRollId = (int) parameter.getValue();
						break;
					default:
						break;
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnDodge)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_DODGE + " is not initialized.");
		}
		if (!StringTool.isProvided(fGotoLabelOnJuggernaut)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_JUGGERNAUT + " is not initialized.");
		}
		if (!StringTool.isProvided(fGotoLabelOnPushback)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_PUSHBACK + " is not initialized.");
		}
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case DICE_INDEX:
					fDiceIndex = (Integer) parameter.getValue();
					return true;
				case BLOCK_RESULT:
					fBlockResult = (BlockResult) parameter.getValue();
					return true;
				case BLOCK_ROLL:
					fBlockRoll = (int[]) parameter.getValue();
					return true;
				case NR_OF_DICE:
					fNrOfDice = (Integer) parameter.getValue();
					return true;
				case OLD_DEFENDER_STATE:
					fOldDefenderState = (PlayerState) parameter.getValue();
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
		UtilServerDialog.hideDialog(getGameState());
		PlayerState attackerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
		PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
		switch (fBlockResult) {
			case SKULL:
				game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), attackerState.changeBase(PlayerState.FALLING));
				game.getFieldModel().setPlayerState(game.getDefender(), fOldDefenderState);
				getResult().setNextAction(StepAction.NEXT_STEP);
				break;
			case BOTH_DOWN:
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnJuggernaut);
				break;
			case POW_PUSHBACK:
				Skill defenderDodgeSkill = game.getDefender().getSkillWithProperty(NamedProperties.ignoreDefenderStumblesResult);
				if (defenderDodgeSkill != null) {
					Skill attackerCanCancelDodgeSkill = UtilCards.getSkillCancelling(actingPlayer.getPlayer(), defenderDodgeSkill);
					if ((attackerCanCancelDodgeSkill != null)
						&& (!actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canBlockSameTeamPlayer)
						|| actingPlayer.getPlayer().getTeam() != game.getDefender().getTeam())) {

						Skill ignoreTackleSkill = game.getDefender().getSkillWithProperty(NamedProperties.ignoreTackleWhenBlocked);
						if (UtilGameOption.isOptionEnabled(game, GameOptionId.RIGHT_STUFF_CANCELS_TACKLE)
							&& ignoreTackleSkill != null) {
							getResult().addReport(
								new ReportSkillUse(game.getDefenderId(), ignoreTackleSkill, true, SkillUse.CANCEL_TACKLE));
							getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnDodge);
						} else {
							getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), attackerCanCancelDodgeSkill, true,
								SkillUse.CANCEL_DODGE));
							game.getFieldModel().setPlayerState(game.getDefender(), defenderState.changeBase(PlayerState.FALLING));
							publishParameters(UtilBlockSequence.initPushback(this));
							getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnPushback);
						}
					} else {
						getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnDodge);
					}
				} else {
					game.getFieldModel().setPlayerState(game.getDefender(), defenderState.changeBase(PlayerState.FALLING));
					publishParameters(UtilBlockSequence.initPushback(this));
					getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnPushback);
				}
				break;
			case POW:
				game.getFieldModel().setPlayerState(game.getDefender(), defenderState.changeBase(PlayerState.FALLING));
				publishParameters(UtilBlockSequence.initPushback(this));
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnPushback);
				break;
			case PUSHBACK:
				game.getFieldModel().setPlayerState(game.getDefender(), fOldDefenderState);
				publishParameters(UtilBlockSequence.initPushback(this));
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnPushback);
				break;
			default:
				break;
		}
		getResult().addReport(new ReportBlockChoice(fNrOfDice, fBlockRoll, fDiceIndex, fBlockResult, game.getDefenderId(), suppressExtraEffectHandling, showNameInReport, blockRollId));
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_DODGE.addTo(jsonObject, fGotoLabelOnDodge);
		IServerJsonOption.GOTO_LABEL_ON_JUGGERNAUT.addTo(jsonObject, fGotoLabelOnJuggernaut);
		IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.addTo(jsonObject, fGotoLabelOnPushback);
		IServerJsonOption.NR_OF_DICE.addTo(jsonObject, fNrOfDice);
		IServerJsonOption.BLOCK_ROLL.addTo(jsonObject, fBlockRoll);
		IServerJsonOption.DICE_INDEX.addTo(jsonObject, fDiceIndex);
		IServerJsonOption.BLOCK_RESULT.addTo(jsonObject, fBlockResult);
		IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, fOldDefenderState);
		IServerJsonOption.SUPPRESS_EXTRA_EFFECT_HANDLING.addTo(jsonObject, suppressExtraEffectHandling);
		IServerJsonOption.SHOW_NAME_IN_REPORT.addTo(jsonObject, showNameInReport);
		IServerJsonOption.BLOCK_ROLL_ID.addTo(jsonObject, blockRollId);
		return jsonObject;
	}

	@Override
	public StepBlockChoice initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnDodge = IServerJsonOption.GOTO_LABEL_ON_DODGE.getFrom(source, jsonObject);
		fGotoLabelOnJuggernaut = IServerJsonOption.GOTO_LABEL_ON_JUGGERNAUT.getFrom(source, jsonObject);
		fGotoLabelOnPushback = IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.getFrom(source, jsonObject);
		fNrOfDice = IServerJsonOption.NR_OF_DICE.getFrom(source, jsonObject);
		fBlockRoll = IServerJsonOption.BLOCK_ROLL.getFrom(source, jsonObject);
		fDiceIndex = IServerJsonOption.DICE_INDEX.getFrom(source, jsonObject);
		fBlockResult = (BlockResult) IServerJsonOption.BLOCK_RESULT.getFrom(source, jsonObject);
		fOldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(source, jsonObject);
		suppressExtraEffectHandling = IServerJsonOption.SUPPRESS_EXTRA_EFFECT_HANDLING.getFrom(source, jsonObject);
		showNameInReport = IServerJsonOption.SHOW_NAME_IN_REPORT.getFrom(source, jsonObject);
		blockRollId = IServerJsonOption.BLOCK_ROLL_ID.getFrom(source, jsonObject);
		return this;
	}

}
