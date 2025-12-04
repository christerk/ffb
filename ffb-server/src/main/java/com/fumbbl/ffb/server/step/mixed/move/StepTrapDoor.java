package com.fumbbl.ffb.server.step.mixed.move;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.stadium.TrapDoor;
import com.fumbbl.ffb.report.mixed.ReportTrapDoor;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeTrapDoorFall;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeTrapDoorFallForSpp;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilPlayer;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepTrapDoor extends AbstractStepWithReRoll {

	private static final ReRolledAction RE_ROLLED_ACTION = ReRolledActions.TRAP_DOOR;
	private String playerId;
	private Boolean thrownPlayerHasBall;
	private boolean playerWasPushed;

	public StepTrapDoor(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus status = super.handleCommand(pReceivedCommand);

		if (status == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}

		return status;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			switch (parameter.getKey()) {
				case PLAYER_ENTERING_SQUARE:
					Game game = getGameState().getGame();
					FieldModel fieldModel = game.getFieldModel();
					Player<?> player = game.getPlayerById((String) parameter.getValue());
					FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);
					if (isOnTrapDoor(fieldModel, playerCoordinate)) {
						playerId = (String) parameter.getValue();
					}
					consume(parameter);
					return true;
				case THROWN_PLAYER_HAS_BALL:
					thrownPlayerHasBall = (Boolean) parameter.getValue();
					return true;
				case PLAYER_WAS_PUSHED:
					playerWasPushed = (boolean) parameter.getValue();
					consume(parameter);
					return true;
				default:
					break;
			}
		}
		return super.setParameter(parameter);
	}

	@Override
	public StepId getId() {
		return StepId.TRAP_DOOR;
	}

	@Override
	public void start() {
		executeStep();
	}

	private void executeStep() {
		if (!StringTool.isProvided(playerId)) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}
		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		Player<?> player = game.getPlayerById(playerId);
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);
		boolean hasBall = thrownPlayerHasBall == null ? UtilPlayer.hasBall(game, player) : thrownPlayerHasBall;

		if (isOnTrapDoor(fieldModel, playerCoordinate)) {

			if (getReRolledAction() == RE_ROLLED_ACTION) {
				if (getReRollSource() == null || !UtilServerReRoll.useReRoll(this, getReRollSource(), player)) {
					trapDoorTriggered(game, player, playerCoordinate, hasBall);
					return;
				}
			}

			int roll = getGameState().getDiceRoller().rollDice(6);
			boolean escaped = roll != 1;
			getResult().addReport(new ReportTrapDoor(playerId, roll, escaped));
			if (escaped) {
				getResult().setNextAction(StepAction.NEXT_STEP);
			} else if (getReRolledAction() != null || !UtilServerReRoll.askForReRollIfAvailable(getGameState(), player, RE_ROLLED_ACTION, 2, false)) {
				trapDoorTriggered(game, player, playerCoordinate, hasBall);
			}
		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	private boolean isOnTrapDoor(FieldModel fieldModel, FieldCoordinate playerCoordinate) {
		return fieldModel.getTrapDoors().stream().map(TrapDoor::getCoordinate).anyMatch(playerCoordinate::equals);
	}

	private void trapDoorTriggered(Game game, Player<?> player, FieldCoordinate playerCoordinate, boolean hasBall) {
		Player<?> attacker = game.getActingPlayer().getPlayer();
		boolean eligibleForSpp = playerWasPushed && attacker != null && getGameState().getPrayerState().hasFanInteraction(attacker.getTeam());
		publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT,
			UtilServerInjury.handleInjury(this, eligibleForSpp ? new InjuryTypeTrapDoorFallForSpp() : new InjuryTypeTrapDoorFall(), eligibleForSpp ? attacker : null, player,
				playerCoordinate, null, null, ApothecaryMode.TRAP_DOOR)));
		game.getFieldModel().remove(player);
		if (hasBall) {
			publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
			if (game.getActingTeam().hasPlayer(player)) {
				publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			}
		}
		// we are in ttm context so we need to break the scatter loop
		if (thrownPlayerHasBall != null) {
			game.setDefenderId(null);
			publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null));
			if (thrownPlayerHasBall) {
				game.getFieldModel().setBallCoordinate(playerCoordinate);
			}
		}
		getResult().setSound(SoundId.TRAPDOOR);
		getResult().setNextAction(StepAction.NEXT_STEP);
	}


	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IServerJsonOption.THROWN_PLAYER_HAS_BALL.addTo(jsonObject, thrownPlayerHasBall);
		IServerJsonOption.PLAYER_WAS_PUSHED.addTo(jsonObject, playerWasPushed);
		return jsonObject;
	}

	@Override
	public StepTrapDoor initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		thrownPlayerHasBall = IServerJsonOption.THROWN_PLAYER_HAS_BALL.getFrom(source, jsonObject);
		playerWasPushed = IServerJsonOption.PLAYER_WAS_PUSHED.getFrom(source, jsonObject);
		return this;
	}
}
