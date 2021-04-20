package com.fumbbl.ffb.server.step.bb2020.blitz;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogSelectBlitzTargetParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.BlitzState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandBlitzTargetSelected;
import com.fumbbl.ffb.report.ReportSelectBlitzTarget;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerDialog;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepSelectBlitzTarget extends AbstractStep {

	private String gotoLabelOnEnd;
	private String selectedPlayerId;

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
			if (pReceivedCommand.getId() == NetCommandId.CLIENT_BLITZ_TARGET_SELECTED) {
				selectedPlayerId = ((ClientCommandBlitzTargetSelected) pReceivedCommand.getCommand()).getTargetPlayerId();
				status = StepCommandStatus.EXECUTE_STEP;
			}
		}
		if (status == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return status;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		if (selectedPlayerId == null) {
			if (hasStandingOpponents(game)) {
				game.setTurnMode(TurnMode.SELECT_BLITZ_TARGET);
				UtilServerDialog.showDialog(getGameState(), new DialogSelectBlitzTargetParameter(), false);
				getResult().setSound(SoundId.CLICK);
			} else {
				game.getFieldModel().setBlitzState(new BlitzState().skip());
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
		} else {
			game.setTurnMode(game.getLastTurnMode());
			if (selectedPlayerId.equals(game.getActingPlayer().getPlayerId())) {
				game.getFieldModel().setBlitzState(new BlitzState().cancel());
				getResult().setNextAction(StepAction.GOTO_LABEL, gotoLabelOnEnd);
			} else if (!game.getActingTeam().hasPlayer(game.getPlayerById(selectedPlayerId))) {
				Player<?> targetPlayer = game.getPlayerById(selectedPlayerId);
				PlayerState newState = game.getFieldModel().getPlayerState(targetPlayer).addSelectedBlitzTarget();
				game.getFieldModel().setPlayerState(targetPlayer, newState);
				game.getFieldModel().setBlitzState(new BlitzState(selectedPlayerId).select());
				getResult().setSound(SoundId.CLICK);
				getResult().addReport(new ReportSelectBlitzTarget(game.getActingPlayer().getPlayerId(), selectedPlayerId));
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
		return jsonObject;
	}

	@Override
	public StepSelectBlitzTarget initFrom(IFactorySource source, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		super.initFrom(source, jsonObject);
		gotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		selectedPlayerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}

}
