package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandFieldCoordinate;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.bb2020.ReportPlaceBallDirection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerTimer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepPlaceBall extends AbstractStep {

	private String playerId;
	private CatchScatterThrowInMode catchScatterThrowInMode;
	private Phase phase = Phase.ASK;
	private boolean ballCarrierTeamTurn;
	private final Set<FieldCoordinate> adjacentSquares = new HashSet<>();
	private FieldCoordinate selectedCoordinate;

	public StepPlaceBall(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.PLACE_BALL;
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand receivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(receivedCommand);

		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (receivedCommand.getId()) {
				case CLIENT_USE_SKILL:
					ClientCommandUseSkill commandUseSkill = (ClientCommandUseSkill) receivedCommand.getCommand();
					if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canPlaceBallWhenKnockedDownOrPlacedProne)
						&& commandUseSkill.getPlayerId().equals(playerId)) {
						phase = commandUseSkill.isSkillUsed() ? Phase.SELECT : Phase.DONE;
						getResult().addReport(new ReportSkillUse(playerId, commandUseSkill.getSkill(), commandUseSkill.isSkillUsed(), SkillUse.PLACE_BALL));
					}
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_FIELD_COORDINATE:
					ClientCommandFieldCoordinate commandFieldCoordinate = (ClientCommandFieldCoordinate) receivedCommand.getCommand();
					if (commandFieldCoordinate.getFieldCoordinate() != null) {
						FieldCoordinate fieldCoordinate;
						if (UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), receivedCommand)) {
							fieldCoordinate = commandFieldCoordinate.getFieldCoordinate();
						} else {
							fieldCoordinate = commandFieldCoordinate.getFieldCoordinate().transform();
						}
						if (adjacentSquares.contains(fieldCoordinate)) {
							selectedCoordinate = fieldCoordinate;
							phase = Phase.PLACE;
							commandStatus = StepCommandStatus.EXECUTE_STEP;
						}
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
				case CATCH_SCATTER_THROW_IN_MODE:
					catchScatterThrowInMode = (CatchScatterThrowInMode) parameter.getValue();
					return true;
				case DROPPED_BALL_CARRIER:
					playerId = (String) parameter.getValue();
					return true;
				default:
					break;
			}
		}

		return super.setParameter(parameter);
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		UtilServerDialog.hideDialog(getGameState());

		if (playerId == null || catchScatterThrowInMode != CatchScatterThrowInMode.SCATTER_BALL) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}

		Game game = getGameState().getGame();
		Player<?> ballCarrier = game.getPlayerById(playerId);

		switch (phase) {
			case ASK:
				setup(game, ballCarrier);
				break;
			case SELECT:
				FieldModel fieldModel = game.getFieldModel();
				fieldModel.clearMoveSquares();
				adjacentSquares.stream().map(fieldCoordinate -> new MoveSquare(fieldCoordinate, 0, 0))
					.forEach(fieldModel::add);
				game.setTurnMode(TurnMode.SAFE_PAIR_OF_HANDS);
				break;
			case PLACE:
				if (!ballCarrierTeamTurn) {
					game.setWaitingForOpponent(true);
					UtilServerTimer.stopTurnTimer(getGameState(), System.currentTimeMillis());
				}
				Direction direction = FieldCoordinate.getDirection(game.getFieldModel().getBallCoordinate(), selectedCoordinate);
				getResult().addReport(new ReportPlaceBallDirection(playerId, direction));
				game.getFieldModel().setBallCoordinate(selectedCoordinate);
				game.getFieldModel().setBallMoving(true);
				game.setTurnMode(game.getLastTurnMode());
				publishParameter(StepParameter.from(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, null));
				leave(game);
				break;
			case DONE:
				leave(game);
				break;
		}

	}

	private void setup(Game game, Player<?> ballCarrier) {
		Skill skill = ballCarrier.getSkillWithProperty(NamedProperties.canPlaceBallWhenKnockedDownOrPlacedProne);
		if (skill == null) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}

		FieldModel fieldModel = game.getFieldModel();
		adjacentSquares.addAll(Arrays.stream(fieldModel
			.findAdjacentCoordinates(fieldModel.getBallCoordinate(), FieldCoordinateBounds.FIELD, 1, false))
			.filter(fieldCoordinate -> fieldModel.getPlayer(fieldCoordinate) == null).collect(Collectors.toSet()));

		if (adjacentSquares.isEmpty()) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}

		ballCarrierTeamTurn = game.isHomePlaying() == game.getTeamHome().hasPlayer(ballCarrier);
		if (!ballCarrierTeamTurn) {
			game.setHomePlaying(!game.isHomePlaying());
		}

		UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(playerId, skill, 0), !game.isHomePlaying());
	}

	private void leave(Game game) {
		if (!ballCarrierTeamTurn) {
			game.setHomePlaying(!game.isHomePlaying());
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IServerJsonOption.CATCH_SCATTER_THROW_IN_MODE.addTo(jsonObject, catchScatterThrowInMode);
		IServerJsonOption.STEP_PHASE.addTo(jsonObject, phase.name());
		IServerJsonOption.FIELD_COORDINATE.addTo(jsonObject, selectedCoordinate.toJsonValue());
		JsonArray jsonArray = new JsonArray();
		adjacentSquares.stream().map(FieldCoordinate::toJsonValue).forEach(jsonArray::add);
		IServerJsonOption.FIELD_COORDINATES.addTo(jsonObject, jsonArray);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);

		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		catchScatterThrowInMode = (CatchScatterThrowInMode) IServerJsonOption.CATCH_SCATTER_THROW_IN_MODE.getFrom(source, jsonObject);
		phase = Phase.valueOf(IServerJsonOption.STEP_PHASE.getFrom(source, jsonObject));

		JsonObject fieldCoordinate = IServerJsonOption.FIELD_COORDINATE.getFrom(source, jsonObject);
		if (fieldCoordinate != null) {
			selectedCoordinate = new FieldCoordinate(0).initFrom(source, fieldCoordinate);
		}

		JsonArray jsonArray = IServerJsonOption.FIELD_COORDINATES.getFrom(source, jsonObject);
		if (jsonArray != null) {
			adjacentSquares.clear();
			jsonArray.values().stream().map(value -> new FieldCoordinate(0).initFrom(source, value)).forEach(adjacentSquares::add);
		}

		return this;
	}

	private enum Phase {
		ASK, PLACE, SELECT, DONE
	}
}
