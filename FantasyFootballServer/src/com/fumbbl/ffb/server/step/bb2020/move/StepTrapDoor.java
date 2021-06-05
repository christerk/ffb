package com.fumbbl.ffb.server.step.bb2020.move;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.stadium.TrapDoor;
import com.fumbbl.ffb.report.bb2020.ReportTrapDoor;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeCrowdPush;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilPlayer;

public class StepTrapDoor extends AbstractStepWithReRoll {

	private static final ReRolledAction RE_ROLLED_ACTION = ReRolledActions.TRAP_DOOR;
	private String playerId;

	protected StepTrapDoor(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null && parameter.getKey() == StepParameterKey.PLAYER_ENTERING_SQUARE) {
			playerId = (String) parameter.getValue();
			consume(parameter);
			return true;
		}

		return super.setParameter(parameter);
	}

	@Override
	public StepId getId() {
		return StepId.TRAP_DOOR;
	}

	@Override
	public void start() {
		if (!StringTool.isProvided(playerId)) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}
		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		Player<?> player = game.getPlayerById(playerId);
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);
		boolean hasBall = UtilPlayer.hasBall(game, player);

		if (fieldModel.getTrapDoors().stream().map(TrapDoor::getCoordinate).anyMatch(playerCoordinate::equals)) {

			if (getReRolledAction() == RE_ROLLED_ACTION) {
				if (getReRollSource() == null || !UtilServerReRoll.useReRoll(this, getReRollSource(), player)) {
					trapDoorTriggered(game, player, playerCoordinate, hasBall);
					return;
				}
			}

			int roll = getGameState().getDiceRoller().rollDice(6);
			boolean escaped = roll != 1;
			getResult().addReport(new ReportTrapDoor(playerId, escaped));
			if (escaped) {
				getResult().setNextAction(StepAction.NEXT_STEP);
			} else if (getReRolledAction() != null || !UtilServerReRoll.askForReRollIfAvailable(getGameState(), player, RE_ROLLED_ACTION, 2, false)) {
				trapDoorTriggered(game, player, playerCoordinate, hasBall);
			}
		}
	}

	private void trapDoorTriggered(Game game, Player<?> player, FieldCoordinate playerCoordinate, boolean hasBall) {
		publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT,
			UtilServerInjury.handleInjury(this, new InjuryTypeCrowdPush(), null, player,
				playerCoordinate, null, null, ApothecaryMode.TRAP_DOOR)));
		game.getFieldModel().remove(player);
		if (hasBall) {
			publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
			publishParameter(new StepParameter(StepParameterKey.THROW_IN_COORDINATE, playerCoordinate));
		}
	}


	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		return jsonObject;
	}

	@Override
	public StepTrapDoor initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, UtilJson.toJsonObject(pJsonValue));
		return this;
	}
}
