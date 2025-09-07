package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandFieldCoordinate;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.bb2020.ReportRaidingParty;
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
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepRaidingParty extends AbstractStep {

	private boolean endPlayerAction, endTurn;
	private String goToLabelOnFailure, playerId, gotoLabelOnSuccess;
	private FieldCoordinate coordinate;
	private TurnMode savedTurnMode;

	public StepRaidingParty(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.RAIDING_PARTY;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		super.init(pParameterSet);
		if (pParameterSet != null) {
			Arrays.stream(pParameterSet.values()).forEach(parameter -> {
				switch (parameter.getKey()) {
					case GOTO_LABEL_ON_FAILURE:
						goToLabelOnFailure = (String) parameter.getValue();
						break;
					case GOTO_LABEL_ON_SUCCESS:
						gotoLabelOnSuccess = (String) parameter.getValue();
						break;
					default:
						break;
				}
			});
		}
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);

		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_PLAYER_CHOICE:
					ClientCommandPlayerChoice clientCommandPlayerChoice = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
					if (StringTool.isProvided(clientCommandPlayerChoice.getPlayerId())) {
						playerId = clientCommandPlayerChoice.getPlayerId();
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					} else {
						commandStatus = StepCommandStatus.SKIP_STEP;
						Game game = getGameState().getGame();
						resetState(game);
						ActingPlayer actingPlayer = game.getActingPlayer();
						getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canMoveOpenTeamMate), false, SkillUse.MOVE_OPEN_TEAM_MATE));
						getResult().setNextAction(StepAction.NEXT_STEP);
					}
					break;
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
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {

		getResult().setNextAction(StepAction.NEXT_STEP);

		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Skill skill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canMoveOpenTeamMate);
		if (skill != null) {

			if (endTurn || endPlayerAction) {
				getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnFailure);
				resetState(game);
				return;
			}

			if (!StringTool.isProvided(playerId)) {
				List<Player<?>> eligiblePlayers = findPlayers(game, actingPlayer.getPlayer());

				if (eligiblePlayers.isEmpty()) {
					return;
				}
				getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skill, true, SkillUse.MOVE_OPEN_TEAM_MATE));
				if (eligiblePlayers.size() == 1) {
					playerId = eligiblePlayers.get(0).getId();
				} else {
					UtilServerDialog.showDialog(getGameState(),
						new DialogPlayerChoiceParameter(game.getActingTeam().getId(), PlayerChoiceMode.RAIDING_PARTY, eligiblePlayers.toArray(new Player<?>[0]), null, 1), false);
					List<FieldCoordinate> eligibleSquares = eligiblePlayers.stream().flatMap(player -> findSquares(game, player).stream()).collect(Collectors.toList());
					prepareClientData(game, eligibleSquares);
					return;
				}
			}

			FieldModel fieldModel = game.getFieldModel();
			Player<?> player = game.getPlayerById(playerId);
			if (StringTool.isProvided(playerId) && coordinate == null) {
				List<FieldCoordinate> eligibleSquares = findSquares(game, player);

				if (eligibleSquares.isEmpty()) {
					return;
				}

				prepareClientData(game, eligibleSquares);
				return;
			}

			if (StringTool.isProvided(playerId) && coordinate != null) {

				FieldCoordinate fromCoordinate = fieldModel.getPlayerCoordinate(player);
				Direction direction = fromCoordinate.getDirection(coordinate);
				getResult().addReport(new ReportRaidingParty(actingPlayer.getPlayerId(), playerId, direction));
				getResult().setSound(SoundId.STEP);
				fieldModel.updatePlayerAndBallPosition(player, coordinate);
				actingPlayer.markSkillUsed(skill);
				resetState(game);

				Sequence sequence = new Sequence(getGameState());
				sequence.add(StepId.PICK_UP,
					from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.SCATTER_BALL),
					from(StepParameterKey.THROWN_PLAYER_ID, playerId));
				if (StringTool.isProvided(gotoLabelOnSuccess)) {
					sequence.jump(gotoLabelOnSuccess);
				} else {
					sequence.jump(IStepLabel.NEXT);
				}
				sequence.add(StepId.CATCH_SCATTER_THROW_IN, IStepLabel.SCATTER_BALL);
				sequence.jump(goToLabelOnFailure);
				if (!StringTool.isProvided(gotoLabelOnSuccess)) {
					sequence.add(StepId.NEXT_STEP_AND_REPEAT, IStepLabel.NEXT);
				}
				getGameState().getStepStack().push(sequence.getSequence());

			}
		}
	}

	private void resetState(Game game) {
		if (game.getTurnMode() == TurnMode.RAIDING_PARTY) {
			game.setTurnMode(game.getLastTurnMode());
			if (savedTurnMode != null) {
				game.setLastTurnMode(savedTurnMode);
			}
		}
		UtilServerPlayerMove.updateMoveSquares(getGameState(), game.getActingPlayer().isJumping());
		ServerUtilBlock.updateDiceDecorations(game);
	}

	private void prepareClientData(Game game, List<FieldCoordinate> eligibleSquares) {
		FieldModel fieldModel = game.getFieldModel();
		fieldModel.clearMoveSquares();
		eligibleSquares.stream().map(square -> new MoveSquare(square, 0, 0)).forEach(fieldModel::add);
		getResult().setNextAction(StepAction.CONTINUE);
		if (game.getTurnMode() != TurnMode.RAIDING_PARTY) {
			savedTurnMode = game.getLastTurnMode();
			game.setLastTurnMode(game.getTurnMode());
			game.setTurnMode(TurnMode.RAIDING_PARTY);
		}
	}

	private List<FieldCoordinate> findSquares(Game game, Player<?> player) {
		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);
		FieldCoordinate[] possibleCoordinates = fieldModel.findAdjacentCoordinates(playerCoordinate, FieldCoordinateBounds.FIELD,
			1, false);
		return Arrays.stream(possibleCoordinates).filter(possibleCoordinate -> {
			List<Player<?>> playersOnSquare = fieldModel.getPlayers(possibleCoordinate);

			return (playersOnSquare == null || playersOnSquare.isEmpty())
				&& Arrays.stream(fieldModel.findAdjacentCoordinates(possibleCoordinate, FieldCoordinateBounds.FIELD, 1, false))
				.anyMatch(adjacentCoordinate -> {
					List<Player<?>> players = fieldModel.getPlayers(adjacentCoordinate);
					return players != null && !players.isEmpty() && !game.getActingTeam().hasPlayer(players.get(0));
				});
		}).collect(Collectors.toList());
	}

	private List<Player<?>> findPlayers(Game game, Player<?> player) {
		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);

		return Arrays.stream(game.getActingTeam().getPlayers()).filter(
			teamMate -> {
				FieldCoordinate teamMateCoordinate = fieldModel.getPlayerCoordinate(teamMate);
				Player<?>[] adjacentPlayersWithTacklezones = UtilPlayer.findAdjacentPlayersWithTacklezones(game, game.getOtherTeam(game.getActingTeam()), teamMateCoordinate, false);
				FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(teamMateCoordinate, FieldCoordinateBounds.FIELD,
					1, false);
				PlayerState playerState = fieldModel.getPlayerState(teamMate);
				return (playerState.getBase() == PlayerState.STANDING || player.equals(teamMate))
					&& !playerState.isRooted()
					&& playerState.hasTacklezones() // has to mark opponent after move so needs tz
					&& teamMateCoordinate.distanceInSteps(playerCoordinate) <= 5
					&& !ArrayTool.isProvided(adjacentPlayersWithTacklezones)
					&& Arrays.stream(adjacentCoordinates).anyMatch(adjacentCoordinate -> {
					List<Player<?>> playersOnSquare = fieldModel.getPlayers(adjacentCoordinate);
					return (playersOnSquare == null || playersOnSquare.isEmpty())
						&& Arrays.stream(fieldModel.findAdjacentCoordinates(adjacentCoordinate, FieldCoordinateBounds.FIELD,
						1, false)).anyMatch(fieldCoordinate -> {
						List<Player<?>> players = game.getFieldModel().getPlayers(fieldCoordinate);
						return players != null && !players.isEmpty() && !game.getActingTeam().hasPlayer(players.get(0));
					});
				});
			}
		).collect(Collectors.toList());
	}


	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, endPlayerAction);
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, goToLabelOnFailure);
		IServerJsonOption.GOTO_LABEL_ON_SUCCESS.addTo(jsonObject, gotoLabelOnSuccess);
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
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
		goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		gotoLabelOnSuccess = IServerJsonOption.GOTO_LABEL_ON_SUCCESS.getFrom(source, jsonObject);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		if (IServerJsonOption.FIELD_COORDINATE.isDefinedIn(jsonObject)) {
			coordinate = new FieldCoordinate().initFrom(source, IServerJsonOption.FIELD_COORDINATE.getFrom(source, jsonObject));
		}
		savedTurnMode = (TurnMode) IServerJsonOption.OLD_TURN_MODE.getFrom(source, jsonObject);
		return this;
	}
}
