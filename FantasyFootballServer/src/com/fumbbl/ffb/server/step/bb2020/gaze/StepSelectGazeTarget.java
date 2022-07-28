package com.fumbbl.ffb.server.step.bb2020.gaze;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogConfirmEndActionParameter;
import com.fumbbl.ffb.dialog.DialogSelectGazeTargetParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandTargetSelected;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.bb2020.ReportSelectGazeTarget;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.BalefulHex;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.LookIntoMyEyes;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.Treacherous;
import com.fumbbl.ffb.server.step.generator.bb2020.RaidingParty;
import com.fumbbl.ffb.server.util.UtilServerDialog;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepSelectGazeTarget extends AbstractStep {

	private String gotoLabelOnEnd;
	private String selectedPlayerId;
	private boolean confirmed, endPlayerAction, endTurn;
	private Skill usedSkill;

	public StepSelectGazeTarget(GameState pGameState) {
		super(pGameState);
	}

	public StepSelectGazeTarget(GameState pGameState, StepAction defaultStepResult) {
		super(pGameState, defaultStepResult);
	}

	@Override
	public StepId getId() {
		return StepId.SELECT_GAZE_TARGET;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			super.init(pParameterSet);
			for (StepParameter parameter : pParameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_END) {
					gotoLabelOnEnd = (String) parameter.getValue();
				}
			}
		}
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		getResult().setNextAction(StepAction.CONTINUE);
		StepCommandStatus status = super.handleCommand(pReceivedCommand);
		if (status == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_TARGET_SELECTED:
					selectedPlayerId = ((ClientCommandTargetSelected) pReceivedCommand.getCommand()).getTargetPlayerId();
					status = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_END_TURN:
					if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
						endTurn = true;
						status = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_USE_SKILL:
					ClientCommandUseSkill commandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
					if (commandUseSkill.isSkillUsed()) {
						status = StepCommandStatus.SKIP_STEP;
						if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canStabTeamMateForBall)) {
							getGameState().pushCurrentStepOnStack();
							Treacherous generator = (Treacherous) getGameState().getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR)
								.forName(SequenceGenerator.Type.Treacherous.name());
							generator.pushSequence(new Treacherous.SequenceParams(getGameState(), IStepLabel.SELECT));
							getResult().setNextAction(StepAction.NEXT_STEP);
						} else if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canMoveOpenTeamMate)) {
							getGameState().pushCurrentStepOnStack();
							RaidingParty generator = (RaidingParty) getGameState().getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR)
								.forName(SequenceGenerator.Type.RaidingParty.name());
							generator.pushSequence(new RaidingParty.SequenceParams(getGameState(), IStepLabel.SELECT));
							getResult().setNextAction(StepAction.NEXT_STEP);
						} else if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canMakeOpponentMissTurn)) {
							getGameState().pushCurrentStepOnStack();
							BalefulHex generator = (BalefulHex) getGameState().getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR)
								.forName(SequenceGenerator.Type.BalefulHex.name());
							generator.pushSequence(new BalefulHex.SequenceParams(getGameState(), IStepLabel.SELECT));
							getResult().setNextAction(StepAction.NEXT_STEP);
						} else if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canStealBallFromOpponent)) {
							getGameState().pushCurrentStepOnStack();
							LookIntoMyEyes generator = (LookIntoMyEyes) getGameState().getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR)
								.forName(SequenceGenerator.Type.LookIntoMyEyes.name());
							generator.pushSequence(new LookIntoMyEyes.SequenceParams(getGameState(), false, gotoLabelOnEnd));
							getResult().setNextAction(StepAction.NEXT_STEP);
						} else {
							usedSkill = commandUseSkill.getSkill();
						}
					}
					break;
				case CLIENT_USE_TEAM_MATES_WISDOM:
					status = StepCommandStatus.SKIP_STEP;
					getGameState().pushCurrentStepOnStack();
					Sequence sequence = new Sequence(getGameState());
					sequence.add(StepId.WISDOM_OF_THE_WHITE_DWARF);
					getGameState().getStepStack().push(sequence.getSequence());
					getResult().setNextAction(StepAction.NEXT_STEP);
					break;
				case CLIENT_CONFIRM: // confirms ending blitz action
					confirmed = true;
					status = StepCommandStatus.EXECUTE_STEP;
					break;
				default:
					break;
			}
		}
		if (status == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return status;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			if (parameter.getKey() == StepParameterKey.END_PLAYER_ACTION) {
				endPlayerAction = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
				consume(parameter);
				return true;
			}
		}
		return false;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		getResult().setNextAction(StepAction.CONTINUE);
		Game game = getGameState().getGame();
		if (endPlayerAction || endTurn) {
			game.setTurnMode(game.getLastTurnMode());
			getGameState().getStepStack().clear();
			SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
			EndPlayerAction endGenerator = (EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name());
			endGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), false, true, endTurn));
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else if (selectedPlayerId == null) {
			game.setTurnMode(TurnMode.SELECT_GAZE_TARGET);
			UtilServerDialog.showDialog(getGameState(), new DialogSelectGazeTargetParameter(), false);
			getResult().setSound(SoundId.CLICK);
		} else {
			if (selectedPlayerId.equals(game.getActingPlayer().getPlayerId())) {
				if (game.getActingPlayer().hasActed() && !confirmed) {
					UtilServerDialog.showDialog(getGameState(), new DialogConfirmEndActionParameter(game.getActingTeam().getId(), game.getActingPlayer().getPlayerAction()), false);
				} else {
					game.setTurnMode(game.getLastTurnMode());
					game.getFieldModel().setTargetSelectionState(new TargetSelectionState().cancel());
					getResult().setNextAction(StepAction.GOTO_LABEL, gotoLabelOnEnd);
				}
			} else if (!game.getActingTeam().hasPlayer(game.getPlayerById(selectedPlayerId))) {
				game.setTurnMode(game.getLastTurnMode());
				Player<?> targetPlayer = game.getPlayerById(selectedPlayerId);
				PlayerState newState = game.getFieldModel().getPlayerState(targetPlayer).changeSelectedGazeTarget(true);
				game.getFieldModel().setPlayerState(targetPlayer, newState);
				TargetSelectionState targetSelectionState = new TargetSelectionState(selectedPlayerId);
				if (game.getActingPlayer().hasActed()) {
					targetSelectionState.commit();
				}
				game.getFieldModel().setTargetSelectionState(targetSelectionState.select());
				getResult().setSound(SoundId.CLICK);
				if (usedSkill != null) {
					targetSelectionState.addUsedSkill(usedSkill);
					if (usedSkill.getEnhancements() != null) {
						game.getFieldModel().addSkillEnhancements(game.getActingPlayer().getPlayer(), usedSkill);
						getResult().addReport(new ReportSkillUse(game.getActingPlayer().getPlayerId(), usedSkill, true, SkillUse.GAIN_FRENZY_FOR_BLITZ));
					}
				}
				getResult().addReport(new ReportSelectGazeTarget(game.getActingPlayer().getPlayerId(), selectedPlayerId));
				getResult().setNextAction(StepAction.NEXT_STEP);
			} else {
				game.setTurnMode(game.getLastTurnMode());
				getResult().setNextAction(StepAction.NEXT_STEP);
			}

		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, gotoLabelOnEnd);
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, selectedPlayerId);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, endPlayerAction);
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		IServerJsonOption.SKILL.addTo(jsonObject, usedSkill);
		IServerJsonOption.CONFIRMED.addTo(jsonObject, confirmed);
		return jsonObject;
	}

	@Override
	public StepSelectGazeTarget initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		super.initFrom(source, jsonObject);
		gotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		selectedPlayerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		endPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		usedSkill = (Skill) IServerJsonOption.SKILL.getFrom(source, jsonObject);
		confirmed = toPrimitive(IServerJsonOption.CONFIRMED.getFrom(source, jsonObject));
		return this;
	}

}
