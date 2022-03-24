package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.report.bb2020.ReportPickMeUp;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepPickMeUp extends AbstractStep {

	private final List<String> playerIds = new ArrayList<>();
	private final List<String> playerIdsSelected = new ArrayList<>();
	private boolean firstRun = true;

	public StepPickMeUp(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.PICK_ME_UP;
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND && pReceivedCommand.getId() == NetCommandId.CLIENT_PLAYER_CHOICE) {
			ClientCommandPlayerChoice playerChoice = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();

			String[] selected = playerChoice.getPlayerIds();

			if (ArrayTool.isProvided(selected)) {
				playerIdsSelected.addAll(Arrays.asList(selected));
				playerIds.removeAll(playerIdsSelected);
			} else {
				playerIds.clear();
			}
			commandStatus = StepCommandStatus.EXECUTE_STEP;
		}

		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}

		return commandStatus;
	}

	@Override
	public void start() {
		executeStep();
	}

	private void executeStep() {
		UtilServerDialog.hideDialog(getGameState());
		Game game = getGameState().getGame();
		Team team = game.getOtherTeam(game.getActingTeam());
		List<Player<?>> pickMeUpPlayers = Arrays.asList(UtilPlayer.findPlayersOnPitchWithProperty(game, team, NamedProperties.canStandUpTeamMates));
		FieldModel fieldModel = game.getFieldModel();
		if (firstRun) {
			playerIds.addAll(pickMeUpPlayers.stream().filter(player -> fieldModel.getPlayerState(player).hasTacklezones())
				.flatMap(player -> Arrays.stream(team.getPlayers()).filter(teamMate -> {
						FieldCoordinate coordinate = fieldModel.getPlayerCoordinate(teamMate);
						PlayerState playerState = fieldModel.getPlayerState(teamMate);
						return coordinate != null && !coordinate.isBoxCoordinate()
							&& playerState.isProne()
							&& coordinate.distanceInSteps(fieldModel.getPlayerCoordinate(player)) <= 3;
					})
				).map(Player::getId).collect(Collectors.toSet()));
			firstRun = false;
		} else {

			playerIdsSelected.stream().map(game::getPlayerById).forEach(player -> {
				int roll = getGameState().getDiceRoller().rollSkill();
				boolean success = DiceInterpreter.getInstance().interpretPickMeUp(roll);
				if (success) {
					fieldModel.setPlayerState(player, fieldModel.getPlayerState(player).changeBase(PlayerState.STANDING));
				}
				getResult().addReport(new ReportPickMeUp(player.getId(), roll, success));
			});
			playerIdsSelected.clear();
		}

		if (playerIds.isEmpty()) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}

		UtilServerDialog.showDialog(getGameState(),
			new DialogPlayerChoiceParameter(team.getId(), PlayerChoiceMode.PICK_ME_UP, playerIds.toArray(new String[0]), null, playerIds.size(), 0), false);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.PLAYER_IDS.addTo(jsonObject, playerIds);
		IServerJsonOption.PLAYER_IDS_SELECTED.addTo(jsonObject, playerIdsSelected);
		IServerJsonOption.FIRST_RUN.addTo(jsonObject, firstRun);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerIds.addAll(Arrays.asList(IServerJsonOption.PLAYER_IDS.getFrom(source, jsonObject)));
		playerIdsSelected.addAll(Arrays.asList(IServerJsonOption.PLAYER_IDS_SELECTED.getFrom(source, jsonObject)));
		firstRun = IServerJsonOption.FIRST_RUN.getFrom(source, jsonObject);
		return this;
	}
}
