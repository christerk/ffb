package com.fumbbl.ffb.server.step.bb2025;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogInformationOkayParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandFieldCoordinate;
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
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepPlaceCarriedPlayer extends AbstractStep {

	private final List<FieldCoordinate> eligibleSquares = new ArrayList<>();
	private FieldCoordinate selectedCoordinate;
	private TurnMode savedTurnMode;

	public StepPlaceCarriedPlayer(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.PLACE_CARRIED_PLAYER;
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
				case CLIENT_FIELD_COORDINATE:
					ClientCommandFieldCoordinate command = (ClientCommandFieldCoordinate) pReceivedCommand.getCommand();
					FieldCoordinate coordinate;
					if (UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand)) {
						coordinate = command.getFieldCoordinate();
					} else {
						coordinate = command.getFieldCoordinate().transform();
					}
					selectedCoordinate = eligibleSquares.contains(coordinate) ? coordinate : null;
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

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		String carriedPlayerId = getGameState().getCarriedPlayerId();

		if (!StringTool.isProvided(carriedPlayerId) || actingPlayer == null || actingPlayer.getPlayer() == null) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}

		Player<?> carrier = actingPlayer.getPlayer();
		Player<?> carriedPlayer = game.getPlayerById(carriedPlayerId);
		FieldCoordinate carrierCoordinate = game.getFieldModel().getPlayerCoordinate(carrier);

		if (carriedPlayer == null || carrierCoordinate == null) {
			leave(game, carrier);
			return;
		}

		if (eligibleSquares.isEmpty()) {
			eligibleSquares.addAll(findSquares(game, carrierCoordinate));
		}

		if (eligibleSquares.isEmpty()) {
			leave(game, carrier);
			return;
		}

		if (eligibleSquares.size() == 1) {
			placePlayer(game, carriedPlayer, eligibleSquares.get(0));
			leave(game, carrier);
			return;
		}

		if (selectedCoordinate != null) {
			placePlayer(game, carriedPlayer, selectedCoordinate);
			leave(game, carrier);
			return;
		}

		prepareClientData(game);
	}

	private List<FieldCoordinate> findSquares(Game game, FieldCoordinate carrierCoordinate) {
		FieldModel fieldModel = game.getFieldModel();
		return Arrays.stream(fieldModel.findAdjacentCoordinates(carrierCoordinate, FieldCoordinateBounds.FIELD, 1, false))
			.filter(coordinate -> fieldModel.getPlayer(coordinate) == null)
			.collect(Collectors.toList());
	}

	private void prepareClientData(Game game) {
		FieldModel fieldModel = game.getFieldModel();
		fieldModel.clearMoveSquares();
		eligibleSquares.stream().map(square -> new MoveSquare(square, 0, 0)).forEach(fieldModel::add);

		if (game.getTurnMode() != TurnMode.PLACE_CARRIED_PLAYER) {
			savedTurnMode = game.getTurnMode();
			game.setTurnMode(TurnMode.PLACE_CARRIED_PLAYER);
		}

		UtilServerDialog.showDialog(
			getGameState(),
			new DialogInformationOkayParameter(
				"I'll Carry You",
				"Select a square to place carried player.",
				false
			),
			false
		);

		getResult().setNextAction(StepAction.CONTINUE);
	}

	private void placePlayer(Game game, Player<?> carriedPlayer, FieldCoordinate coordinate) {
		PlayerState oldState = getGameState().getOldCarriedPlayerState();
		game.getFieldModel().setPlayerCoordinate(carriedPlayer, coordinate);
		if (oldState != null) {
			game.getFieldModel().setPlayerState(carriedPlayer, oldState);
		}
		if (getGameState().isCarriedPlayerHasBall()) {
			game.getFieldModel().setBallCoordinate(coordinate);
			game.getFieldModel().setBallMoving(false);
		} else if (game.getFieldModel().isBallMoving() && coordinate.equals(game.getFieldModel().getBallCoordinate())) {
			publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
		}
	}

	private void leave(Game game, Player<?> carrier) {
		UtilServerDialog.hideDialog(getGameState());
		game.getFieldModel().clearMoveSquares();

		if (game.getTurnMode() == TurnMode.PLACE_CARRIED_PLAYER && savedTurnMode != null) {
			game.setTurnMode(savedTurnMode);
		}

		Skill skill = carrier.getSkillWithProperty(NamedProperties.canCarryPartner);
		if (skill != null) {
			game.getFieldModel().removeSkillEnhancements(carrier, skill);
		}

		getGameState().clearCarriedPlayer();
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		if (selectedCoordinate != null) {
			IServerJsonOption.FIELD_COORDINATE.addTo(jsonObject, selectedCoordinate.toJsonValue());
		}
		JsonArray jsonArray = new JsonArray();
		eligibleSquares.stream().map(FieldCoordinate::toJsonValue).forEach(jsonArray::add);
		IServerJsonOption.FIELD_COORDINATES.addTo(jsonObject, jsonArray);
		IServerJsonOption.OLD_TURN_MODE.addTo(jsonObject, savedTurnMode);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);

		JsonObject fieldCoordinate = IServerJsonOption.FIELD_COORDINATE.getFrom(source, jsonObject);
		if (fieldCoordinate != null) {
			selectedCoordinate = new FieldCoordinate().initFrom(source, fieldCoordinate);
		}

		JsonArray jsonArray = IServerJsonOption.FIELD_COORDINATES.getFrom(source, jsonObject);
		if (jsonArray != null) {
			eligibleSquares.clear();
			jsonArray.values().stream()
				.map(value -> new FieldCoordinate().initFrom(source, value))
				.forEach(eligibleSquares::add);
		}

		savedTurnMode = (TurnMode) IServerJsonOption.OLD_TURN_MODE.getFrom(source, jsonObject);

		return this;
	}
}
