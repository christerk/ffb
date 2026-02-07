package com.fumbbl.ffb.server.step.bb2025.punt;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandActingPlayer;
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
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepInitPunt extends AbstractStep {

	private boolean endPlayerAction, endTurn;
	private String goToLabelOnEnd;
	private FieldCoordinate coordinateTo;

	public StepInitPunt(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.INIT_PUNT;
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
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_END_TURN:
					if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
						endTurn = true;
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_ACTING_PLAYER:
					ClientCommandActingPlayer actingPlayerCommand = (ClientCommandActingPlayer) pReceivedCommand.getCommand();
					if (StringTool.isProvided(actingPlayerCommand.getPlayerId())) {
						UtilServerSteps.changePlayerAction(this, actingPlayerCommand.getPlayerId(),
							actingPlayerCommand.getPlayerAction(), actingPlayerCommand.isJumping());
					} else {
						endPlayerAction = true;
					}
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_FIELD_COORDINATE:
					ClientCommandFieldCoordinate commandFieldCoordinate = (ClientCommandFieldCoordinate) pReceivedCommand.getCommand();
					coordinateTo = commandFieldCoordinate.getFieldCoordinate();
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
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {

		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer());

		Skill skill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canPunt);
		if (endTurn) {
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			publishParameter(StepParameter.from(StepParameterKey.CHECK_FORGO, true));
			getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnEnd);
		} else if (endPlayerAction) {
			publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
			getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnEnd);
		} else if (actingPlayer.getPlayerAction() == PlayerAction.PUNT && skill != null) {
			if (coordinateTo != null) {
				game.getTurnData().setPuntUsed(true);
				publishParameter(new StepParameter(StepParameterKey.COORDINATE_TO, coordinateTo));
				publishParameter(new StepParameter(StepParameterKey.COORDINATE_FROM, playerCoordinate));
				fieldModel.clearMoveSquares();
				getResult().setNextAction(StepAction.NEXT_STEP);
			} else {
				findPuntSquares(playerCoordinate).stream()
					.map(coord -> new MoveSquare(coord, 0, 0))
					.forEach(fieldModel::add);
				getResult().setNextAction(StepAction.CONTINUE);
			}
		}
		getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnEnd);
	}

	private Set<FieldCoordinate> findPuntSquares(FieldCoordinate playerCoordinate) {
		List<Integer> deltas = new ArrayList<Integer>() {{
			add(1);
			add(-1);
		}};

		Set<FieldCoordinate> coordinates = new HashSet<>();

		for (int delta : deltas) {
			FieldCoordinate deltaX = playerCoordinate.add(delta, 0);
			if (FieldCoordinateBounds.FIELD.isInBounds(deltaX)) {
				coordinates.add(deltaX);
			}
			FieldCoordinate deltaY = playerCoordinate.add(0, delta);
			if (FieldCoordinateBounds.FIELD.isInBounds(deltaY)) {
				coordinates.add(deltaY);
			}
		}
		return coordinates;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, endPlayerAction);
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, goToLabelOnEnd);
		IServerJsonOption.COORDINATE_TO.addTo(jsonObject, coordinateTo);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		endPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		goToLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		coordinateTo = IServerJsonOption.COORDINATE_TO.getFrom(source, jsonObject);
		return this;
	}
}
