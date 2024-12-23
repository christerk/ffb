package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.*;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandActingPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandFieldCoordinate;
import com.fumbbl.ffb.report.bb2020.ReportSkillWasted;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.*;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepFirstMoveFuriousOutburst extends AbstractStep {

	private final Set<FieldCoordinate> eligibleSquares = new HashSet<>();
	private boolean endPlayerAction, withBall;
	private String goToLabelOnEnd;
	private FieldCoordinate coordinate;

	public StepFirstMoveFuriousOutburst(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.FIRST_MOVE_FURIOUS_OUTBURST;
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
			switch (pReceivedCommand.getId()) {
				case CLIENT_FIELD_COORDINATE:
					if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
						ClientCommandFieldCoordinate fieldCoordinateCommand = (ClientCommandFieldCoordinate) pReceivedCommand.getCommand();
						FieldCoordinate fieldCoordinate = fieldCoordinateCommand.getFieldCoordinate();
						if (UtilServerSteps.checkCommandIsFromAwayPlayer(getGameState(), pReceivedCommand)) {
							fieldCoordinate = fieldCoordinate.transform();
						}
						if (eligibleSquares.contains(fieldCoordinate)) {
							coordinate = fieldCoordinate;
							commandStatus = StepCommandStatus.EXECUTE_STEP;
						}
					}
					break;
				case CLIENT_ACTING_PLAYER:
					ClientCommandActingPlayer clientCommandActingPlayer = (ClientCommandActingPlayer) pReceivedCommand.getCommand();
					if (clientCommandActingPlayer.getPlayerAction() == null) {
						endPlayerAction = true;
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
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {

		getResult().setNextAction(StepAction.CONTINUE);
		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		ActingPlayer actingPlayer = game.getActingPlayer();


		if (endPlayerAction) {
			if (actingPlayer.hasActed()) {
				UtilCards.getSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.canTeleportBeforeAndAfterAvRollAttack)
					.ifPresent(skill ->
						getResult().addReport(new ReportSkillWasted(actingPlayer.getPlayerId(), skill))
					);
			}
			fieldModel.getTargetSelectionState().cancel();
			getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnEnd);
			return;
		}

		String targetId = game.getFieldModel().getTargetSelectionState().getSelectedPlayerId();
		Player<?> target = game.getPlayerById(targetId);
		if (coordinate == null) {
			game.setDefenderId(targetId);
			fieldModel.setPlayerState(target, fieldModel.getPlayerState(target).changeSelectedStabTarget(true));
			eligibleSquares.addAll(Arrays.stream(fieldModel.findAdjacentCoordinates(fieldModel.getPlayerCoordinate(target), FieldCoordinateBounds.FIELD, 1, false))
				.filter(coordinate -> fieldModel.getPlayer(coordinate) == null).collect(Collectors.toSet()));
			fieldModel.add(eligibleSquares.stream().map(coordinate -> new MoveSquare(coordinate, 0, 0)).toArray(MoveSquare[]::new));
		} else {
			Player<?> player = actingPlayer.getPlayer();
			getResult().setAnimation(new Animation(AnimationType.TRICKSTER, fieldModel.getPlayerCoordinate(player), coordinate, player.getId()));
			UtilServerGame.syncGameModel(this);
			fieldModel.setPlayerCoordinate(player, coordinate);
			fieldModel.setPlayerState(target, fieldModel.getPlayerState(target).changeSelectedStabTarget(true).removeSelectedBlitzTarget());
			fieldModel.getTargetSelectionState().commit(game);
			publishParameter(StepParameter.from(StepParameterKey.USING_STAB, true));
			if (withBall) {
				fieldModel.setBallCoordinate(coordinate);
			}
			bounceBall(fieldModel);
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	private void bounceBall(FieldModel fieldModel) {
		if (!withBall && coordinate.equals(fieldModel.getBallCoordinate()) && fieldModel.isBallMoving()) {
			publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			publishParameter(new StepParameter(StepParameterKey.USE_ALTERNATE_LABEL, true));
		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
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
