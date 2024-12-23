package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.*;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.net.commands.ClientCommandActingPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandFieldCoordinate;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.*;
import com.fumbbl.ffb.server.util.UtilServerGame;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepSecondMoveFuriousOutburst extends AbstractStep {

	private final Set<FieldCoordinate> eligibleSquares = new HashSet<>();
	private boolean endPlayerAction, endTurn, withBall;
	private String goToLabelOnEnd;
	private FieldCoordinate coordinate;

	public StepSecondMoveFuriousOutburst(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.SECOND_MOVE_FURIOUS_OUTBURST;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		super.init(pParameterSet);
		if (pParameterSet != null) {
			Arrays.stream(pParameterSet.values()).forEach(parameter -> {
				if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_END) {
					goToLabelOnEnd = (String) parameter.getValue();
				}
			});
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			switch (parameter.getKey()) {
				case END_TURN:
					endTurn = toPrimitive((Boolean) parameter.getValue());
					return true;
				case END_PLAYER_ACTION:
					endPlayerAction = toPrimitive((Boolean) parameter.getValue());
					return true;
				default:
					break;
			}
		}

		return super.setParameter(parameter);
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
				switch (pReceivedCommand.getId()) {
					case CLIENT_FIELD_COORDINATE:
						ClientCommandFieldCoordinate fieldCoordinateCommand = (ClientCommandFieldCoordinate) pReceivedCommand.getCommand();
						FieldCoordinate fieldCoordinate = fieldCoordinateCommand.getFieldCoordinate();
						if (UtilServerSteps.checkCommandIsFromAwayPlayer(getGameState(), pReceivedCommand)) {
							fieldCoordinate = fieldCoordinate.transform();
						}
						if (eligibleSquares.contains(fieldCoordinate)) {
							coordinate = fieldCoordinate;
							commandStatus = StepCommandStatus.EXECUTE_STEP;
						}
						break;
					case CLIENT_ACTING_PLAYER:
						ClientCommandActingPlayer clientCommandActingPlayer = (ClientCommandActingPlayer) pReceivedCommand.getCommand();
						if (clientCommandActingPlayer.getPlayerAction() == null) {
							endPlayerAction = true;
							commandStatus = StepCommandStatus.EXECUTE_STEP;
						}
						break;
					case CLIENT_END_TURN:
						endTurn = true;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
						break;
					default:
						break;
				}
			}
		}


		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}


	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {

		getResult().setNextAction(StepAction.CONTINUE);
		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (endTurn || endPlayerAction) {
			getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnEnd);
			return;
		}

		if (coordinate == null) {
			eligibleSquares.addAll(Arrays.stream(fieldModel.findAdjacentCoordinates(fieldModel.getPlayerCoordinate(actingPlayer.getPlayer()), FieldCoordinateBounds.FIELD, 3, false))
				.filter(coordinate -> fieldModel.getPlayer(coordinate) == null).collect(Collectors.toSet()));
			fieldModel.add(eligibleSquares.stream().map(coordinate -> new MoveSquare(coordinate, 0, 0)).toArray(MoveSquare[]::new));
		} else {
			Player<?> player = actingPlayer.getPlayer();
			getResult().setAnimation(new Animation(AnimationType.TRICKSTER, fieldModel.getPlayerCoordinate(player), coordinate, player.getId()));
			UtilServerGame.syncGameModel(this);
			fieldModel.setPlayerCoordinate(player, coordinate);
			if (withBall) {
				fieldModel.setBallCoordinate(coordinate);
			}
			getResult().setNextAction(StepAction.NEXT_STEP);
			bounceBall(fieldModel);
		}
	}

	private void bounceBall(FieldModel fieldModel) {
		if (!withBall && coordinate.equals(fieldModel.getBallCoordinate()) && fieldModel.isBallMoving()) {
			publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, endPlayerAction);
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, goToLabelOnEnd);
		JsonArray jsonArray = new JsonArray();
		eligibleSquares.stream().map(FieldCoordinate::toJsonValue).forEach(jsonArray::add);
		IServerJsonOption.FIELD_COORDINATES.addTo(jsonObject, jsonArray);
		IServerJsonOption.COORDINATE.addTo(jsonObject, coordinate);
		IServerJsonOption.WITH_BALL.addTo(jsonObject, withBall);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		endPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		goToLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		JsonArray jsonArray = IServerJsonOption.FIELD_COORDINATES.getFrom(source, jsonObject);
		if (jsonArray != null) {
			eligibleSquares.clear();
			jsonArray.values().stream().map(value -> new FieldCoordinate().initFrom(source, value)).forEach(eligibleSquares::add);
		}
		coordinate = IServerJsonOption.COORDINATE.getFrom(source, jsonObject);
		withBall = IServerJsonOption.WITH_BALL.getFrom(source, jsonObject);
		return this;
	}
}
