package com.fumbbl.ffb.server.step.bb2025.block;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.*;
import com.fumbbl.ffb.dialog.DialogFollowupChoiceParameter;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandFollowupChoice;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.*;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.util.UtilCards;

/**
 * Step in block sequence to handle followup.
 * <p>
 * Expects stepParameter DEFENDER_POSITION to be set by a preceding step.
 * Expects stepParameter FOLLOWUP_CHOICE to be set by a preceding step. Expects
 * stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 * <p>
 * Sets stepParameter COORDINATE_FROM for all steps on the stack. Sets
 * stepParameter FOLLOWUP_CHOICE for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepFollowup extends AbstractStep {

	private FieldCoordinate coordinateFrom;
	private FieldCoordinate defenderPosition;
	private Boolean usingSkillPreventingFollowUp;
	private Boolean followupChoice;
	private PlayerState oldDefenderState;
	private Boolean usingSkillForcingFollowUp;

	public StepFollowup(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.FOLLOWUP;
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
				case CLIENT_USE_SKILL:
					ClientCommandUseSkill useSkillCommand = (ClientCommandUseSkill) pReceivedCommand.getCommand();
					if (useSkillCommand.getSkill().hasSkillProperty(NamedProperties.preventOpponentFollowingUp)) {
						usingSkillPreventingFollowUp = useSkillCommand.isSkillUsed();
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					} else if (useSkillCommand.getSkill().hasSkillProperty(NamedProperties.forceOpponentToFollowUp)) {
						usingSkillForcingFollowUp = useSkillCommand.isSkillUsed();
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_FOLLOWUP_CHOICE:
					ClientCommandFollowupChoice followupChoiceCommand = (ClientCommandFollowupChoice) pReceivedCommand.getCommand();
					publishParameter(new StepParameter(StepParameterKey.FOLLOWUP_CHOICE, followupChoiceCommand.isChoiceFollowup()));
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
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case COORDINATE_FROM:
					coordinateFrom = (FieldCoordinate) parameter.getValue();
					return true;
				case DEFENDER_POSITION:
					defenderPosition = (FieldCoordinate) parameter.getValue();
					return true;
				case FOLLOWUP_CHOICE:
					followupChoice = (Boolean) parameter.getValue();
					return true;
				case OLD_DEFENDER_STATE:
					oldDefenderState = (PlayerState) parameter.getValue();
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
		PlayerState attackerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
		if (attackerState.isPinned() || actingPlayer.getPlayerAction() == PlayerAction.VICIOUS_VINES) {
			publishParameter(new StepParameter(StepParameterKey.FOLLOWUP_CHOICE, false));
		}
		if (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK) {
			publishParameter(new StepParameter(StepParameterKey.FOLLOWUP_CHOICE, false));
		}
		if (followupChoice == null) {
			PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());

			Skill skillPreventsFollowingUp = game.getDefender().getSkillWithProperty(NamedProperties.preventOpponentFollowingUp);
			if (skillPreventsFollowingUp != null && !defenderState.isProneOrStunned()
				&& !((oldDefenderState != null) && oldDefenderState.isProneOrStunned())) {
				boolean cancelSkillUsed = false;
				Skill skillCancelsSkillPreventingFollow = UtilCards.getSkillCancelling(actingPlayer.getPlayer(),
					skillPreventsFollowingUp);
				if (usingSkillPreventingFollowUp == null) {
					if ((PlayerAction.BLITZ == actingPlayer.getPlayerAction() || (PlayerAction.MOVE == actingPlayer.getPlayerAction() && actingPlayer.getPlayer().hasSkillProperty(NamedProperties.blocksDuringMove))) && skillCancelsSkillPreventingFollow != null) {
						usingSkillPreventingFollowUp = false;
						cancelSkillUsed = true;
						getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skillCancelsSkillPreventingFollow,
							true, SkillUse.CANCEL_FEND));
					}
				}
				boolean defenderHasTacklezones = oldDefenderState.hasTacklezones();
				if (usingSkillPreventingFollowUp == null) {
					if (defenderHasTacklezones) {
						UtilServerDialog.showDialog(getGameState(),
							new DialogSkillUseParameter(game.getDefenderId(), skillPreventsFollowingUp, 0), true);
					} else {
						getResult().addReport(new ReportSkillUse(game.getDefenderId(), skillPreventsFollowingUp, false, SkillUse.NO_TACKLEZONE));
						usingSkillPreventingFollowUp = false;
					}
				} else {
					if (usingSkillPreventingFollowUp) {
						publishParameter(new StepParameter(StepParameterKey.FOLLOWUP_CHOICE, false));
					}
					if (!cancelSkillUsed) {
						getResult().addReport(new ReportSkillUse(game.getDefenderId(), skillPreventsFollowingUp,
							usingSkillPreventingFollowUp, SkillUse.STAY_AWAY_FROM_OPPONENT));
					}
				}
			} else {
				usingSkillPreventingFollowUp = false;
			}
			if ((usingSkillPreventingFollowUp != null) && !usingSkillPreventingFollowUp
				&& actingPlayer.getPlayer().hasSkillProperty(NamedProperties.forceFollowup)) {
				publishParameter(new StepParameter(StepParameterKey.FOLLOWUP_CHOICE, true));
			}
			Skill skillForcesFollowup = game.getDefender().getSkillWithProperty(NamedProperties.forceOpponentToFollowUp);
			boolean cannotFollow = attackerState.isPinned() || actingPlayer.getPlayerAction() == PlayerAction.VICIOUS_VINES || actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK;
			if (skillForcesFollowup != null && followupChoice == null && usingSkillPreventingFollowUp != null && !usingSkillPreventingFollowUp && !cannotFollow) {
				if (usingSkillForcingFollowUp == null) {
					UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(game.getDefenderId(), skillForcesFollowup, 0), true);
					return;
				} else {
					if (usingSkillForcingFollowUp) {
						publishParameter(new StepParameter(StepParameterKey.FOLLOWUP_CHOICE, true));
					}
					getResult().addReport(new ReportSkillUse(game.getDefenderId(), skillForcesFollowup, usingSkillForcingFollowUp, SkillUse.FORCE_FOLLOW_UP));
				}
			} else if (usingSkillForcingFollowUp == null) {
				usingSkillForcingFollowUp = false;
			}
			if (followupChoice == null
				&& usingSkillPreventingFollowUp != null
				&& !Boolean.TRUE.equals(usingSkillForcingFollowUp)) {
				UtilServerDialog.showDialog(getGameState(), new DialogFollowupChoiceParameter(), false);
			}

		}

		if (followupChoice != null) {
			TrackNumber trackNumber;
			FieldCoordinate followupCoordinate;
			if (followupChoice) {
				followupCoordinate = defenderPosition;
				publishParameter(new StepParameter(StepParameterKey.COORDINATE_FROM,
					game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer())));
				game.getFieldModel().updatePlayerAndBallPosition(actingPlayer.getPlayer(), followupCoordinate);
				publishParameter(new StepParameter(StepParameterKey.PLAYER_ENTERING_SQUARE, actingPlayer.getPlayerId()));
				UtilServerPlayerMove.updateMoveSquares(getGameState(), false);
				if (PlayerAction.BLITZ == actingPlayer.getPlayerAction()) {
					trackNumber = new TrackNumber(coordinateFrom, actingPlayer.getCurrentMove() - 1);
					game.getFieldModel().add(trackNumber);
				}
				getResult().setSound(SoundId.STEP);
			} else {
				publishParameter(new StepParameter(StepParameterKey.COORDINATE_FROM, null));
			}
			publishParameter(new StepParameter(StepParameterKey.DEFENDER_POSITION,
				game.getFieldModel().getPlayerCoordinate(game.getDefender())));
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, coordinateFrom);
		IServerJsonOption.DEFENDER_POSITION.addTo(jsonObject, defenderPosition);
		IServerJsonOption.USING_FEND.addTo(jsonObject, usingSkillPreventingFollowUp);
		IServerJsonOption.FOLLOWUP_CHOICE.addTo(jsonObject, followupChoice);
		IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, oldDefenderState);
		IServerJsonOption.USING_TAUNT.addTo(jsonObject, usingSkillForcingFollowUp);
		return jsonObject;
	}

	@Override
	public StepFollowup initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		coordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(source, jsonObject);
		defenderPosition = IServerJsonOption.DEFENDER_POSITION.getFrom(source, jsonObject);
		usingSkillPreventingFollowUp = IServerJsonOption.USING_FEND.getFrom(source, jsonObject);
		followupChoice = IServerJsonOption.FOLLOWUP_CHOICE.getFrom(source, jsonObject);
		oldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(source, jsonObject);
		usingSkillForcingFollowUp = IServerJsonOption.USING_TAUNT.getFrom(source, jsonObject);
		return this;
	}

}
