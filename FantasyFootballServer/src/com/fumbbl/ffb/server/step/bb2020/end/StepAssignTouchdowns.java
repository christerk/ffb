package com.fumbbl.ffb.server.step.bb2020.end;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.report.bb2020.ReportPlayerEvent;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.StringTool;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepAssignTouchdowns extends AbstractStep {

	private int touchdowns;
	private String winningTeamId, playerId;

	public StepAssignTouchdowns(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.ASSIGN_TOUCHDOWNS;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			switch (parameter.getKey()) {
				case TOUCHDOWNS:
					touchdowns = (int) parameter.getValue();
					return true;
				case TEAM_ID:
					winningTeamId = (String) parameter.getValue();
					return true;
				default:
					break;
			}
		}

		return super.setParameter(parameter);
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand receivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(receivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			if (receivedCommand.getId() == NetCommandId.CLIENT_PLAYER_CHOICE) {
				ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) receivedCommand.getCommand();
				if (PlayerChoiceMode.ASSIGN_TOUCHDOWN == playerChoiceCommand.getPlayerChoiceMode()) {
					if (playerChoiceCommand.getPlayerId() != null) {
						playerId = playerChoiceCommand.getPlayerId();
					}
				}
				commandStatus = StepCommandStatus.EXECUTE_STEP;
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
		if (winningTeamId == null) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}

		List<Player<?>> players = findPlayers(game, game.getTeamById(winningTeamId));
		if (players.isEmpty()) {
			touchdowns = 0;
		}

		if (game.isAdminMode()) {
			while (touchdowns-- > 0) {
				Collections.shuffle(players);
				Player<?> player = players.get(0);
				PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
				playerResult.setTouchdowns(playerResult.getTouchdowns() + 1);
				getResult().addReport(new ReportPlayerEvent(player.getId(), "is awarded a touchdown"));
			}
		}

		if (StringTool.isProvided(playerId)) {
			PlayerResult playerResult = game.getGameResult().getPlayerResult(game.getPlayerById(playerId));
			playerResult.setTouchdowns(playerResult.getTouchdowns() + 1);
			getResult().addReport(new ReportPlayerEvent(playerId, "is awarded a touchdown"));
			touchdowns--;
			playerId = null;
		}

		if (touchdowns <= 0) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			Arrays.stream(game.getOtherTeam(game.getTeamById(winningTeamId)).getPlayers())
				.map(player -> game.getGameResult().getPlayerResult(player)).forEach(playerResult -> playerResult.setTouchdowns(0));
			return;
		}


		String[] playerIds = players.stream().map(Player::getId).toArray(String[]::new);

		DialogPlayerChoiceParameter dialogParameter = new DialogPlayerChoiceParameter(winningTeamId,
			PlayerChoiceMode.ASSIGN_TOUCHDOWN, playerIds, null, 1, 1);

		UtilServerDialog.showDialog(getGameState(), dialogParameter, false);

	}

	private List<Player<?>> findPlayers(Game game, Team team) {
		return Arrays.stream(team.getPlayers())
			.filter(player ->
				player.getRecoveringInjury() == null
					&& !game.getFieldModel().getPlayerState(player).isKilled()
					&& game.getGameResult().getPlayerResult(player).getSendToBoxReason() != SendToBoxReason.NURGLES_ROT)
			.collect(Collectors.toList());
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.TOUCHDOWNS.addTo(jsonObject, touchdowns);
		IServerJsonOption.TEAM_ID.addTo(jsonObject, winningTeamId);
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		return jsonObject;
	}

	@Override
	public StepAssignTouchdowns initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		touchdowns = IServerJsonOption.TOUCHDOWNS.getFrom(game, jsonObject);
		winningTeamId = IServerJsonOption.TEAM_ID.getFrom(game, jsonObject);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		return this;
	}

}
