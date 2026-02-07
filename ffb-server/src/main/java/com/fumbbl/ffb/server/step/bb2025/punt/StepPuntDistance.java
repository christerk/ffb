package com.fumbbl.ffb.server.step.bb2025.punt;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.bb2025.ReportPuntDirection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepPuntDistance extends AbstractStepWithReRoll {

	public StepPuntDistance(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.PUNT_DISTANCE;
	}

	private Direction direction;
	private FieldCoordinate coordinateFrom;
	private int distance;

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case COORDINATE_FROM:
					coordinateFrom = (FieldCoordinate) parameter.getValue();
					return true;
				case DIRECTION:
					direction = (Direction) parameter.getValue();
					return true;
				default:
					break;
			}
		}
		return false;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND &&
			pReceivedCommand.getId() == NetCommandId.CLIENT_USE_SKILL) {
			ClientCommandUseSkill command = (ClientCommandUseSkill) pReceivedCommand.getCommand();
			if (command.isSkillUsed()) {
				setReRollSource(command.getSkill().getRerollSource(getReRolledAction()));
			}
			commandStatus = StepCommandStatus.EXECUTE_STEP;
		}

		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		getResult().setNextAction(StepAction.NEXT_STEP);

		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		fieldModel.setBallMoving(true);
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (ReRolledActions.PUNT_DISTANCE == getReRolledAction()) {
			if (getReRollSource() == null || !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
				leave();
				return;
			}
		}

		int roll = getGameState().getDiceRoller().rollDice(6);
		FieldCoordinate ballIndicatorCoordinate = coordinateFrom.move(direction, roll);
		if (!FieldCoordinateBounds.FIELD.isInBounds(ballIndicatorCoordinate)) {
			ballIndicatorCoordinate = findLastSquareOnPitch(distance - 1);
			fieldModel.setOutOfBounds(true);
		}
		fieldModel.setBallCoordinate(ballIndicatorCoordinate);

		getResult().addReport(new ReportPuntDirection(direction, roll, actingPlayer.getPlayerId()));


		if (getReRolledAction() == null) {
			setReRolledAction(ReRolledActions.PUNT_DISTANCE);

			ReRollSource skillReRoll = UtilCards.getUnusedRerollSource(actingPlayer, getReRolledAction());
			if (skillReRoll != null) {
				Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
				UtilServerDialog.showDialog(getGameState(),
					new DialogSkillUseParameter(game.getThrowerId(), skillReRoll.getSkill(game), 0, null),
					actingTeam.hasPlayer(game.getThrower()));
				getResult().setNextAction(StepAction.CONTINUE);
			} else {
				if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), game.getThrower(), getReRolledAction(),
					0, false, null, null)) {
					getResult().setNextAction(StepAction.CONTINUE);
				}
			}
		} else {
			leave();
		}
	}

	private FieldCoordinate findLastSquareOnPitch(int distance) {
		FieldCoordinate coordinate = coordinateFrom.move(direction, distance);
		if (FieldCoordinateBounds.FIELD.isInBounds(coordinate)) {
			return coordinate;
		}
		return findLastSquareOnPitch(distance - 1);
	}

	private void leave() {
		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		if (fieldModel.isOutOfBounds()) {
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			publishParameter(
				new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.THROW_IN));
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			publishParameter(new StepParameter(StepParameterKey.DIRECTION, direction));
		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.DIRECTION.addTo(jsonObject, direction);
		IServerJsonOption.DISTANCE.addTo(jsonObject, distance);
		IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, coordinateFrom);
		return jsonObject;
	}

	@Override
	public StepPuntDistance initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		direction = (Direction) IServerJsonOption.DIRECTION.getFrom(source, jsonObject);
		coordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(source, jsonObject);
		distance = IServerJsonOption.DISTANCE.getFrom(source, jsonObject);
		return this;
	}
}

