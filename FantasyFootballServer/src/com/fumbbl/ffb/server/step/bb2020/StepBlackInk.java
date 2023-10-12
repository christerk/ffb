package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.List;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepBlackInk extends AbstractStep {

	private boolean endPlayerAction, endTurn;
	private String goToLabelOnFailure, playerId;

	public StepBlackInk(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.BLACK_INK;
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
						ActingPlayer actingPlayer = game.getActingPlayer();
						getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canGazeAutomatically), false, SkillUse.REMOVE_TACKLEZONE));
						getResult().setNextAction(StepAction.NEXT_STEP);
					}
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
		Skill skill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canGazeAutomatically);
		if (skill != null) {

			if (endTurn || endPlayerAction) {
				getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnFailure);
				return;
			}

			if (!StringTool.isProvided(playerId)) {
				List<Player<?>> eligiblePlayers = findPlayers(game, actingPlayer.getPlayer());

				if (eligiblePlayers.isEmpty()) {
					return;
				}
				getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skill, true, SkillUse.REMOVE_TACKLEZONE));

				UtilServerDialog.showDialog(getGameState(),
					new DialogPlayerChoiceParameter(game.getActingTeam().getId(), PlayerChoiceMode.BLACK_INK, eligiblePlayers.toArray(new Player<?>[0]), null, 1), false);
				getResult().setNextAction(StepAction.CONTINUE);
				return;
			}

			FieldModel fieldModel = game.getFieldModel();
			Player<?> player = game.getPlayerById(playerId);


			if (StringTool.isProvided(playerId)) {

				getResult().setSound(SoundId.HYPNO);
				fieldModel.setPlayerState(player, fieldModel.getPlayerState(player).changeHypnotized(true));
				actingPlayer.markSkillUsed(skill);
				UtilServerPlayerMove.updateMoveSquares(getGameState(), game.getActingPlayer().isJumping());
				ServerUtilBlock.updateDiceDecorations(game);

			}
		}
	}

	private List<Player<?>> findPlayers(Game game, Player<?> player) {
		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);

		return Arrays.asList(UtilPlayer.findAdjacentStandingOrPronePlayers(game, game.getOtherTeam(game.getActingTeam()), playerCoordinate));
	}


	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, endPlayerAction);
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, goToLabelOnFailure);
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		JsonArray jsonArray = new JsonArray();
		IServerJsonOption.MOVE_SQUARE_ARRAY.addTo(jsonObject, jsonArray);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		endPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}
}
