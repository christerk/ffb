package com.fumbbl.ffb.server.step.mixed;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.*;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepInitFuriousOutburst extends AbstractStep {

	private final Set<String> eligiblePlayers = new HashSet<>();
	private boolean endPlayerAction, endTurn;
	private String goToLabelOnEnd;
	private String targetId;

	public StepInitFuriousOutburst(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.INIT_FURIOUS_OUTBURST;
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
				case CLIENT_PLAYER_CHOICE:
					if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
						ClientCommandPlayerChoice commandPlayerChoice = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
						String playerId = commandPlayerChoice.getPlayerId();
						if (!StringTool.isProvided(playerId)) {
							endPlayerAction = true;
							commandStatus = StepCommandStatus.EXECUTE_STEP;
						} else if (eligiblePlayers.contains(playerId)) {
							targetId = playerId;
							commandStatus = StepCommandStatus.EXECUTE_STEP;
						}
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
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {

		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Skill skill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canTeleportBeforeAndAfterAvRollAttack);
		if (!fieldModel.getPlayerState(actingPlayer.getPlayer()).isProneOrStunned()) {
			if (endTurn) {
				publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
				publishParameter(StepParameter.from(StepParameterKey.CHECK_FORGO, true));
			} else if (endPlayerAction) {
				publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
				game.getFieldModel().setTargetSelectionState(new TargetSelectionState().cancel());
			} else if (skill != null) {
				if (StringTool.isProvided(targetId)) {
					game.getFieldModel().setTargetSelectionState(new TargetSelectionState(targetId));
					getResult().setNextAction(StepAction.NEXT_STEP);
					return;
				} else {
					Set<Player<?>> foundPlayers = findEligiblePlayers();
					if (!foundPlayers.isEmpty()) {
						eligiblePlayers.addAll(foundPlayers.stream().map(Player::getId).collect(Collectors.toSet()));

						UtilServerDialog.showDialog(getGameState(),
							new DialogPlayerChoiceParameter(game.getActingTeam().getId(), PlayerChoiceMode.FURIOUS_OUTBURST,
								foundPlayers.toArray(new Player<?>[0]), null, 1), false);
						getResult().setNextAction(StepAction.CONTINUE);
						return;
					}
				}
			}
		}
		getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnEnd);
	}


	private Set<Player<?>> findEligiblePlayers() {
		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		Team opponentTeam = game.getOtherTeam(game.getActingTeam());
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(game.getActingPlayer().getPlayer());

		return Arrays.stream(UtilPlayer.findBlockablePlayers(game, opponentTeam, playerCoordinate, 3))
			.filter(player -> hasEmptyAdjacentSquare(player, fieldModel)).collect(Collectors.toSet());
	}

	private boolean hasEmptyAdjacentSquare(Player<?> player, FieldModel fieldModel) {
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(player);
		return Arrays.stream(fieldModel.findAdjacentCoordinates(playerCoordinate, FieldCoordinateBounds.FIELD, 1, false))
			.anyMatch(coordinate -> fieldModel.getPlayer(coordinate) == null);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, endPlayerAction);
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, goToLabelOnEnd);
		IServerJsonOption.PLAYER_IDS.addTo(jsonObject, eligiblePlayers);
		IServerJsonOption.TARGET_PLAYER_ID.addTo(jsonObject, targetId);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		endPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		goToLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		eligiblePlayers.addAll(Arrays.stream(IServerJsonOption.PLAYER_IDS
			.getFrom(source, jsonObject)).collect(Collectors.toSet()));

		targetId = IServerJsonOption.TARGET_PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}
}
