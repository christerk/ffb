package com.fumbbl.ffb.server.step.bb2020.block;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.*;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandFieldCoordinate;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.bb2020.ReportHitAndRun;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepHitAndRun extends AbstractStep {

	private boolean endPlayerAction, endTurn;
	private FieldCoordinate coordinate;
	private TurnMode savedTurnMode;

	public StepHitAndRun(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.HIT_AND_RUN;
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
					ClientCommandFieldCoordinate clientCommandFieldCoordinate = (ClientCommandFieldCoordinate) pReceivedCommand.getCommand();
					if (UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand)) {
						coordinate = clientCommandFieldCoordinate.getFieldCoordinate();
					} else {
						coordinate = clientCommandFieldCoordinate.getFieldCoordinate().transform();
					}
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_END_TURN:
					if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
						endTurn = true;
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

	private void executeStep() {

		getResult().setNextAction(StepAction.NEXT_STEP);

		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Skill skill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canMoveAfterBlock);
		PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());

		if (skill != null && !playerState.isRooted()) {

			if (endTurn || endPlayerAction) {
				resetState(game);
				return;
			}

			List<FieldCoordinate> eligibleSquares = findSquares(game);
			if (eligibleSquares.isEmpty()) {
				return;
			}

			FieldModel fieldModel = game.getFieldModel();
			if (coordinate == null) {
				getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skill, true, SkillUse.MOVE_SQUARE));
				prepareClientData(game, eligibleSquares);
			} else {

				FieldCoordinate fromCoordinate = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer());
				Direction direction = FieldCoordinate.getDirection(fromCoordinate, coordinate);
				getResult().addReport(new ReportHitAndRun(actingPlayer.getPlayerId(), direction));
				getResult().setSound(SoundId.STEP);
				actingPlayer.markSkillUsed(skill);
				fieldModel.updatePlayerAndBallPosition(actingPlayer.getPlayer(), coordinate);
				resetState(game);

				Sequence sequence = new Sequence(getGameState());
				sequence.add(StepId.PICK_UP,
					from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.SCATTER_BALL));
				sequence.jump(IStepLabel.NEXT);
				sequence.add(StepId.CATCH_SCATTER_THROW_IN, IStepLabel.SCATTER_BALL);
				sequence.add(StepId.NEXT_STEP, IStepLabel.NEXT);
				getGameState().getStepStack().push(sequence.getSequence());

			}
		}
	}

	private void prepareClientData(Game game, List<FieldCoordinate> eligibleSquares) {
		FieldModel fieldModel = game.getFieldModel();
		fieldModel.clearMoveSquares();
		eligibleSquares.stream().map(square -> new MoveSquare(square, 0, 0)).forEach(fieldModel::add);
		getResult().setNextAction(StepAction.CONTINUE);
		if (game.getTurnMode() != TurnMode.HIT_AND_RUN) {
			savedTurnMode = game.getLastTurnMode();
			game.setLastTurnMode(game.getTurnMode());
			game.setTurnMode(TurnMode.HIT_AND_RUN);
		}
	}

	private List<FieldCoordinate> findSquares(Game game) {
		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(game.getActingPlayer().getPlayer());
		FieldCoordinate[] possibleCoordinates = fieldModel.findAdjacentCoordinates(playerCoordinate, FieldCoordinateBounds.FIELD,
			1, false);
		return Arrays.stream(possibleCoordinates)
			.filter(possibleCoordinate -> (fieldModel.getPlayers(possibleCoordinate) == null))
			.filter(possibleCoordinate -> !ArrayTool.isProvided(UtilPlayer
				.findAdjacentPlayers(game, game.getOtherTeam(game.getActingTeam()), possibleCoordinate)))
			.collect(Collectors.toList());
	}

	private void resetState(Game game) {
		if (game.getTurnMode() == TurnMode.HIT_AND_RUN) {
			game.setTurnMode(game.getLastTurnMode());
			if (savedTurnMode != null) {
				game.setLastTurnMode(savedTurnMode);
			}
		}
		UtilServerPlayerMove.updateMoveSquares(getGameState(), game.getActingPlayer().isJumping());
		ServerUtilBlock.updateDiceDecorations(game);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, endPlayerAction);
		if (coordinate != null) {
			IServerJsonOption.FIELD_COORDINATE.addTo(jsonObject, coordinate.toJsonValue());
		}
		JsonArray jsonArray = new JsonArray();
		IServerJsonOption.MOVE_SQUARE_ARRAY.addTo(jsonObject, jsonArray);
		IServerJsonOption.OLD_TURN_MODE.addTo(jsonObject, savedTurnMode);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		endPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		if (IServerJsonOption.FIELD_COORDINATE.isDefinedIn(jsonObject)) {
			coordinate = new FieldCoordinate().initFrom(source, IServerJsonOption.FIELD_COORDINATE.getFrom(source, jsonObject));
		}
		savedTurnMode = (TurnMode) IServerJsonOption.OLD_TURN_MODE.getFrom(source, jsonObject);
		return this;
	}
}
