package com.fumbbl.ffb.server.step.bb2020;

import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogUseSafePairOfHandsParameter;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandFieldCoordinate;
import com.fumbbl.ffb.net.commands.ClientCommandUseSafePairOfHands;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.util.UtilServerDialog;

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
	private Set<FieldCoordinate> adjacentSquares = new HashSet<>();
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
				case CLIENT_USE_SAFE_PAIR_OF_HANDS:
					ClientCommandUseSafePairOfHands commandUseSafePairOfHands = (ClientCommandUseSafePairOfHands) receivedCommand.getCommand();
					phase = commandUseSafePairOfHands.isUsingSafePairOfHands() ? Phase.SELECT : Phase.DONE;
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_FIELD_COORDINATE:
					ClientCommandFieldCoordinate commandFieldCoordinate = (ClientCommandFieldCoordinate) receivedCommand.getCommand();
					selectedCoordinate = commandFieldCoordinate.getFieldCoordinate();
					phase = Phase.PLACE;
					commandStatus = StepCommandStatus.EXECUTE_STEP;
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
				game.getFieldModel().setBallCoordinate(selectedCoordinate);
				game.getFieldModel().setBallMoving(false);
				leave(game);
				break;
			case DONE:
				leave(game);
				break;
		}

	}

	private void setup(Game game, Player<?> ballCarrier) {
		if (!ballCarrier.hasSkillProperty(NamedProperties.canPlaceBallWhenKnockedDownOrPlacedProne)) {
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

		UtilServerDialog.showDialog(getGameState(), new DialogUseSafePairOfHandsParameter(playerId), !ballCarrierTeamTurn);
	}

	private void leave(Game game) {
		if (!ballCarrierTeamTurn) {
			game.setHomePlaying(!game.isHomePlaying());
		}
		game.setTurnMode(game.getLastTurnMode());
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private enum Phase {
		ASK, PLACE, SELECT, DONE
	}
}
