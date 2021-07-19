package com.fumbbl.ffb.server.step.bb2016.end;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.ReportMostValuablePlayers;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.ListTool;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Step in end game sequence to determine the MVP.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public final class StepMvp extends AbstractStep {

	private int fNrOfHomeMvps;
	private int fNrOfHomeChoices;
	private String[] fHomePlayersNominated;
	private final List<String> fHomePlayersMvp;
	private int fNrOfAwayMvps;
	private int fNrOfAwayChoices;
	private String[] fAwayPlayersNominated;
	private final List<String> fAwayPlayersMvp;

	public StepMvp(GameState pGameState) {
		super(pGameState);
		fHomePlayersMvp = new ArrayList<>();
		fAwayPlayersMvp = new ArrayList<>();
	}

	public StepId getId() {
		return StepId.MVP;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand receivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(receivedCommand);
		if ((receivedCommand != null) && (commandStatus == StepCommandStatus.UNHANDLED_COMMAND)) {
			switch (receivedCommand.getId()) {
			case CLIENT_PLAYER_CHOICE:
				ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) receivedCommand.getCommand();
				if (PlayerChoiceMode.MVP == playerChoiceCommand.getPlayerChoiceMode()) {
					if (playerChoiceCommand.getPlayerId() != null) {
						fHomePlayersNominated = null;
						fAwayPlayersNominated = null;
						if (UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), receivedCommand)) {
							fHomePlayersNominated = playerChoiceCommand.getPlayerIds();
						} else {
							fAwayPlayersNominated = playerChoiceCommand.getPlayerIds();
						}
					}
				}
				commandStatus = StepCommandStatus.EXECUTE_STEP;
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

		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();

		if ((fNrOfHomeMvps == 0) && (fNrOfAwayMvps == 0)) {
			fNrOfHomeMvps = 1;
			fNrOfAwayMvps = 1;
			if (UtilGameOption.isOptionEnabled(game, GameOptionId.EXTRA_MVP)) {
				fNrOfHomeMvps++;
				fNrOfAwayMvps++;
			}
			if (gameResult.getTeamResultHome().hasConceded()
					&& (UtilPlayer.findPlayersInReserveOrField(game, game.getTeamHome()).length > 2)) {
				fNrOfHomeMvps = 0;
				fNrOfAwayMvps++;
			}
			if (gameResult.getTeamResultAway().hasConceded()
					&& (UtilPlayer.findPlayersInReserveOrField(game, game.getTeamAway()).length > 2)) {
				fNrOfHomeMvps++;
				fNrOfAwayMvps = 0;
			}
		}

		int mvpNominations = UtilGameOption.getIntOption(game, GameOptionId.MVP_NOMINATIONS);
		if ((mvpNominations > 0) && !game.isAdminMode()) {

			if (fHomePlayersNominated != null) {
				fHomePlayersMvp.add(getGameState().getDiceRoller().randomPlayerId(fHomePlayersNominated));
				fNrOfHomeChoices++;
				fHomePlayersNominated = null;
			}

			if (fAwayPlayersNominated != null) {
				fAwayPlayersMvp.add(getGameState().getDiceRoller().randomPlayerId(fAwayPlayersNominated));
				fNrOfAwayChoices++;
				fAwayPlayersNominated = null;
			}

			if (fNrOfHomeChoices < fNrOfHomeMvps) {
				String[] playersForMvp = findPlayerIdsForMvp(game.getTeamHome());
				if (ArrayTool.isProvided(playersForMvp)) {
					if (playersForMvp.length == 1) {
						fHomePlayersMvp.add(playersForMvp[0]);
						fNrOfHomeChoices++;
					} else {
						int maxSelects = Math.min(mvpNominations, playersForMvp.length);
						DialogPlayerChoiceParameter dialogParameter = new DialogPlayerChoiceParameter(game.getTeamHome().getId(),
								PlayerChoiceMode.MVP, findPlayerIdsForMvp(game.getTeamHome()), null,
							maxSelects, maxSelects);
						UtilServerDialog.showDialog(getGameState(), dialogParameter, false);
					}
				} else {
					fNrOfHomeMvps = 0;
				}
				return;
			}

			if (fNrOfAwayChoices < fNrOfAwayMvps) {
				String[] playersForMvp = findPlayerIdsForMvp(game.getTeamAway());
				if (ArrayTool.isProvided(playersForMvp)) {
					if (playersForMvp.length == 1) {
						fAwayPlayersMvp.add(playersForMvp[0]);
						fNrOfAwayChoices++;
					} else {
						int maxSelects = Math.min(mvpNominations, playersForMvp.length);
						DialogPlayerChoiceParameter dialogParameter = new DialogPlayerChoiceParameter(game.getTeamAway().getId(),
								PlayerChoiceMode.MVP, playersForMvp, null, maxSelects, maxSelects);
						UtilServerDialog.showDialog(getGameState(), dialogParameter, false);
					}
				} else {
					fNrOfAwayMvps = 0;
				}
				return;
			}

		} else {

			fHomePlayersNominated = findPlayerIdsForMvp(game.getTeamHome());
			for (int i = 0; i < fNrOfHomeMvps; i++) {
				fNrOfHomeChoices++;
				fHomePlayersMvp.add(getGameState().getDiceRoller().randomPlayerId(fHomePlayersNominated));
			}
			fAwayPlayersNominated = findPlayerIdsForMvp(game.getTeamAway());
			for (int i = 0; i < fNrOfAwayMvps; i++) {
				fNrOfAwayChoices++;
				fAwayPlayersMvp.add(getGameState().getDiceRoller().randomPlayerId(fAwayPlayersNominated));
			}

		}

		if ((fHomePlayersMvp.size() >= fNrOfHomeMvps) || (fAwayPlayersMvp.size() >= fNrOfAwayMvps)) {
			ReportMostValuablePlayers mvpReport = new ReportMostValuablePlayers();
			for (String playerIdHome : fHomePlayersMvp) {
				Player<?> playerHome = game.getPlayerById(playerIdHome);
				PlayerResult playerResultHome = gameResult.getPlayerResult(playerHome);
				playerResultHome.setPlayerAwards(playerResultHome.getPlayerAwards() + 1);
				mvpReport.addPlayerIdHome(playerIdHome);
			}
			for (String playerIdAway : fAwayPlayersMvp) {
				Player<?> playerAway = game.getPlayerById(playerIdAway);
				PlayerResult playerResultAway = gameResult.getPlayerResult(playerAway);
				playerResultAway.setPlayerAwards(playerResultAway.getPlayerAwards() + 1);
				mvpReport.addPlayerIdAway(playerIdAway);
			}
			getResult().addReport(mvpReport);
			getResult().setNextAction(StepAction.NEXT_STEP);
		}

	}

	private String[] findPlayerIdsForMvp(Team pTeam) {
		List<String> playerIds = new ArrayList<>();
		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();
		for (Player<?> player : pTeam.getPlayers()) {
			PlayerState playerState = game.getFieldModel().getPlayerState(player);
			if (playerState.isKilled()) {
				continue;
			}
			PlayerResult playerResult = gameResult.getPlayerResult(player);
			if ((player.getRecoveringInjury() != null)
					|| (SendToBoxReason.NURGLES_ROT == playerResult.getSendToBoxReason())) {
				continue;
			}
			playerIds.add(player.getId());
		}
		return playerIds.toArray(new String[playerIds.size()]);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.NR_OF_AWAY_CHOICES.addTo(jsonObject, fNrOfAwayChoices);
		IServerJsonOption.NR_OF_AWAY_MVPS.addTo(jsonObject, fNrOfAwayMvps);
		IServerJsonOption.NR_OF_HOME_CHOICES.addTo(jsonObject, fNrOfHomeChoices);
		IServerJsonOption.NR_OF_HOME_MVPS.addTo(jsonObject, fNrOfHomeMvps);
		IServerJsonOption.AWAY_PLAYERS_MVP.addTo(jsonObject, fAwayPlayersMvp);
		IServerJsonOption.AWAY_PLAYERS_NOMINATED.addTo(jsonObject, fAwayPlayersNominated);
		IServerJsonOption.HOME_PLAYERS_MVP.addTo(jsonObject, fHomePlayersMvp);
		IServerJsonOption.HOME_PLAYERS_NOMINATED.addTo(jsonObject, fHomePlayersNominated);
		return jsonObject;
	}

	@Override
	public StepMvp initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fNrOfAwayChoices = IServerJsonOption.NR_OF_AWAY_CHOICES.getFrom(game, jsonObject);
		fNrOfAwayMvps = IServerJsonOption.NR_OF_AWAY_MVPS.getFrom(game, jsonObject);
		fNrOfHomeChoices = IServerJsonOption.NR_OF_HOME_CHOICES.getFrom(game, jsonObject);
		fNrOfHomeMvps = IServerJsonOption.NR_OF_HOME_MVPS.getFrom(game, jsonObject);
		fAwayPlayersNominated = IServerJsonOption.AWAY_PLAYERS_NOMINATED.getFrom(game, jsonObject);
		fHomePlayersNominated = IServerJsonOption.HOME_PLAYERS_NOMINATED.getFrom(game, jsonObject);
		ListTool.replaceAll(fAwayPlayersMvp, IServerJsonOption.AWAY_PLAYERS_MVP.getFrom(game, jsonObject));
		ListTool.replaceAll(fHomePlayersMvp, IServerJsonOption.HOME_PLAYERS_MVP.getFrom(game, jsonObject));
		return this;
	}

}
