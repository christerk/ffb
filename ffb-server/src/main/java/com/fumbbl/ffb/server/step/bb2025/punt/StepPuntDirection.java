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
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.ThrowInMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
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
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepPuntDirection extends AbstractStepWithReRoll {

	public StepPuntDirection(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.PUNT_DIRECTION;
	}

	private Direction direction;
	private FieldCoordinate coordinateTo, coordinateFrom;
	private boolean outOfBounds;
	private String goToLabelOnEnd;


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
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case COORDINATE_TO:
					coordinateTo = (FieldCoordinate) parameter.getValue();
					return true;
				case COORDINATE_FROM:
					coordinateFrom = (FieldCoordinate) parameter.getValue();
					return true;
				case TOUCHBACK:
					outOfBounds = parameter.getValue() != null && (boolean) parameter.getValue();
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

		if (outOfBounds) {
			fieldModel.setOutOfBounds(true);
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			publishParameter(
				new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.THROW_IN));
			getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnEnd);
			getResult().addReport(new ReportPuntDirection(direction, 0, actingPlayer.getPlayerId()));
			return;
		}

		if (ReRolledActions.PUNT_DIRECTION == getReRolledAction()) {
			if (getReRollSource() == null || !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
				leave();
				return;
			}
		}

		Direction baseDirection = coordinateFrom.getDirection(coordinateTo);
		ThrowInMechanic mechanic = game.getMechanic(Mechanic.Type.THROW_IN);
		int roll = getGameState().getDiceRoller().rollThrowInDirection();
		direction = mechanic.interpretThrowInDirectionRoll(baseDirection, roll);
		publishParameter(new StepParameter(StepParameterKey.DIRECTION, direction));
		FieldCoordinate ballIndicatorCoordinate = coordinateFrom.move(direction, 1);
		if (FieldCoordinateBounds.FIELD.isInBounds(ballIndicatorCoordinate)) {
			fieldModel.setBallCoordinate(ballIndicatorCoordinate);
		} else {
			fieldModel.setOutOfBounds(true);
		}
		getResult().addReport(new ReportPuntDirection(direction, roll, actingPlayer.getPlayerId()));


		if (getReRolledAction() == null) {
			setReRolledAction(ReRolledActions.PUNT_DIRECTION);

			ReRollSource skillReRoll = UtilCards.getUnusedRerollSource(actingPlayer, getReRolledAction());
			if (skillReRoll != null) {
				UtilServerDialog.showDialog(getGameState(),
					new DialogSkillUseParameter(actingPlayer.getPlayerId(), skillReRoll.getSkill(game), 0, null), false);
				getResult().setNextAction(StepAction.CONTINUE);
			} else {
				if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), getReRolledAction(),
					0, false, null, null)) {
					getResult().setNextAction(StepAction.CONTINUE);
				}
			}
		} else {
			leave();
		}
	}

	private void leave() {
		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		if (fieldModel.isOutOfBounds()) {
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			publishParameter(
				new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.THROW_IN));
			getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnEnd);
		} else {
			publishParameter(new StepParameter(StepParameterKey.DIRECTION, direction));
		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.DIRECTION.addTo(jsonObject, direction);
		IServerJsonOption.COORDINATE_TO.addTo(jsonObject, coordinateTo);
		IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, coordinateFrom);
		IServerJsonOption.OUT_OF_BOUNDS.addTo(jsonObject, outOfBounds);
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, goToLabelOnEnd);
		return jsonObject;
	}

	@Override
	public StepPuntDirection initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		direction = (Direction) IServerJsonOption.DIRECTION.getFrom(source, jsonObject);
		coordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(source, jsonObject);
		coordinateTo = IServerJsonOption.COORDINATE_TO.getFrom(source, jsonObject);
		outOfBounds = IServerJsonOption.OUT_OF_BOUNDS.getFrom(source, jsonObject);
		goToLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		return this;
	}
}

