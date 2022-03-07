package com.fumbbl.ffb.server.step.bb2020.blitz;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogSelectBlitzTargetParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandTargetSelected;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.bb2020.ReportSelectBlitzTarget;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.util.UtilServerDialog;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepSelectBlitzTarget extends AbstractStep {

	private String gotoLabelOnEnd;
	private String selectedPlayerId;
	private boolean endPlayerAction, endTurn;
	private Skill usedSkill;

	public StepSelectBlitzTarget(GameState pGameState) {
		super(pGameState);
	}

	public StepSelectBlitzTarget(GameState pGameState, StepAction defaultStepResult) {
		super(pGameState, defaultStepResult);
	}

	@Override
	public StepId getId() {
		return StepId.SELECT_BLITZ_TARGET;
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
						usedSkill = commandUseSkill.getSkill();
						status = StepCommandStatus.SKIP_STEP;
					}
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
		Game game = getGameState().getGame();
		if (endPlayerAction || endTurn) {
			game.setTurnMode(game.getLastTurnMode());
			SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
			EndPlayerAction endGenerator = (EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name());
			endGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), false, true, endTurn));
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else if (selectedPlayerId == null) {
			if (hasStandingOpponents(game)) {
				game.setTurnMode(TurnMode.SELECT_BLITZ_TARGET);
				UtilServerDialog.showDialog(getGameState(), new DialogSelectBlitzTargetParameter(), false);
				getResult().setSound(SoundId.CLICK);
			} else {
				game.getFieldModel().setTargetSelectionState(new TargetSelectionState().skip());
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
		} else {
			game.setTurnMode(game.getLastTurnMode());
			if (selectedPlayerId.equals(game.getActingPlayer().getPlayerId())) {
				game.getFieldModel().setTargetSelectionState(new TargetSelectionState().cancel());
				getResult().setNextAction(StepAction.GOTO_LABEL, gotoLabelOnEnd);
			} else if (!game.getActingTeam().hasPlayer(game.getPlayerById(selectedPlayerId))) {
				Player<?> targetPlayer = game.getPlayerById(selectedPlayerId);
				PlayerState newState = game.getFieldModel().getPlayerState(targetPlayer).addSelectedBlitzTarget();
				game.getFieldModel().setPlayerState(targetPlayer, newState);
				TargetSelectionState targetSelectionState = new TargetSelectionState(selectedPlayerId);
				game.getFieldModel().setTargetSelectionState(targetSelectionState.select());
				getResult().setSound(SoundId.CLICK);
				getResult().addReport(new ReportSelectBlitzTarget(game.getActingPlayer().getPlayerId(), selectedPlayerId));
				if (usedSkill != null) {
					targetSelectionState.addUsedSkill(usedSkill);
					if (usedSkill.getEnhancements() != null) {
						game.getFieldModel().addSkillEnhancements(game.getActingPlayer().getPlayer(), usedSkill);
						getResult().addReport(new ReportSkillUse(game.getActingPlayer().getPlayerId(), usedSkill, true, SkillUse.GAIN_FRENZY_FOR_BLITZ));
					}
				}
				getResult().setNextAction(StepAction.NEXT_STEP);
			} else {
				getResult().setNextAction(StepAction.NEXT_STEP);
			}

		}
	}

	private boolean hasStandingOpponents(Game game) {
		Team inactiveTeam = game.isHomePlaying() ? game.getTeamAway() : game.getTeamHome();

		return Arrays.stream(inactiveTeam.getPlayers()).filter(player -> FieldCoordinateBounds.FIELD.isInBounds(game.getFieldModel().getPlayerCoordinate(player)))
			.map(player -> game.getFieldModel().getPlayerState(player)).anyMatch(PlayerState::canBeBlocked);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, gotoLabelOnEnd);
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, selectedPlayerId);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, endPlayerAction);
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		IServerJsonOption.SKILL.addTo(jsonObject, usedSkill);
		return jsonObject;
	}

	@Override
	public StepSelectBlitzTarget initFrom(IFactorySource source, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		super.initFrom(source, jsonObject);
		gotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		selectedPlayerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		endPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		usedSkill = (Skill) IServerJsonOption.SKILL.getFrom(source, jsonObject);
		return this;
	}

}
