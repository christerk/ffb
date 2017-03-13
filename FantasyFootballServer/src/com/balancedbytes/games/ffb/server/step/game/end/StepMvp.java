package com.balancedbytes.games.ffb.server.step.game.end;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.dialog.DialogPlayerChoiceParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPlayerChoice;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.report.ReportMostValuablePlayers;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.util.ListTool;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in end game sequence to determine the MVP.
 * 
 * Needs to be initialized with stepParameter ADMIN_MODE.
 * 
 * @author Kalimar
 */
public final class StepMvp extends AbstractStep {

  private int fNrOfHomeMvps;
  private int fNrOfHomeChoices;
  private String[] fHomePlayersNominated;
  private List<String> fHomePlayersMvp;
  private int fNrOfAwayMvps;
  private int fNrOfAwayChoices;
  private String[] fAwayPlayersNominated;
  private List<String> fAwayPlayersMvp;
  private boolean fAdminMode;
  
  public StepMvp(GameState pGameState) {
    super(pGameState);
    fHomePlayersMvp = new ArrayList<String>();
    fAwayPlayersMvp = new ArrayList<String>();
  }

  public StepId getId() {
    return StepId.MVP;
  }
  
  @Override
  public void init(StepParameterSet pParameterSet) {
    if (pParameterSet != null) {
      for (StepParameter parameter : pParameterSet.values()) {
        switch (parameter.getKey()) {
        // mandatory
        case ADMIN_MODE:
          fAdminMode = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
          break;
        default:
          break;
        }
      }
    }
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
      if (gameResult.getTeamResultHome().hasConceded() && (UtilPlayer.findPlayersInReserveOrField(game, game.getTeamHome()).length > 2)) {
        fNrOfHomeMvps = 0;
        fNrOfAwayMvps++;
      }
      if (gameResult.getTeamResultAway().hasConceded() && (UtilPlayer.findPlayersInReserveOrField(game, game.getTeamAway()).length > 2)) {
        fNrOfHomeMvps++;
        fNrOfAwayMvps = 0;
      }
    }
    
    int mvpNominations = UtilGameOption.getIntOption(game, GameOptionId.MVP_NOMINATIONS);
    if ((mvpNominations > 0) || fAdminMode) {
      
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
        DialogPlayerChoiceParameter dialogParameter = new DialogPlayerChoiceParameter(
          game.getTeamHome().getId(),
          PlayerChoiceMode.MVP,
          findPlayerIdsForMvp(game.getTeamHome()),
          null,
          mvpNominations
        );
        UtilServerDialog.showDialog(getGameState(), dialogParameter);
        return;
      }

      if (fNrOfAwayChoices < fNrOfAwayMvps) {
        DialogPlayerChoiceParameter dialogParameter = new DialogPlayerChoiceParameter(
          game.getTeamAway().getId(),
          PlayerChoiceMode.MVP,
          findPlayerIdsForMvp(game.getTeamAway()),
          null,
          mvpNominations
        );
        UtilServerDialog.showDialog(getGameState(), dialogParameter);
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
        Player playerHome = game.getPlayerById(playerIdHome);
        PlayerResult playerResultHome = gameResult.getPlayerResult(playerHome);
        playerResultHome.setPlayerAwards(playerResultHome.getPlayerAwards() + 1);
        mvpReport.addPlayerIdHome(playerIdHome);
      }
      for (String playerIdAway : fAwayPlayersMvp) {
        Player playerAway = game.getPlayerById(playerIdAway);
        PlayerResult playerResultAway = gameResult.getPlayerResult(playerAway);
        playerResultAway.setPlayerAwards(playerResultAway.getPlayerAwards() + 1);
        mvpReport.addPlayerIdAway(playerIdAway);
      }
      getResult().addReport(mvpReport);
      if (fAdminMode) {
        game.setAdminMode(true);
      }
      getResult().setNextAction(StepAction.NEXT_STEP);
    }

  }

  private String[] findPlayerIdsForMvp(Team pTeam) {
    List<String> playerIds = new ArrayList<String>();
    Game game = getGameState().getGame();
    GameResult gameResult = game.getGameResult();
    for (Player player : pTeam.getPlayers()) {
      PlayerState playerState = game.getFieldModel().getPlayerState(player);
      if (playerState.isKilled()) {
        continue;
      }
      PlayerResult playerResult = gameResult.getPlayerResult(player);
      if (SendToBoxReason.NURGLES_ROT == playerResult.getSendToBoxReason()) {
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
    IServerJsonOption.ADMIN_MODE.addTo(jsonObject, fAdminMode);
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
  public StepMvp initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fAdminMode = IServerJsonOption.ADMIN_MODE.getFrom(jsonObject);
    fNrOfAwayChoices = IServerJsonOption.NR_OF_AWAY_CHOICES.getFrom(jsonObject);
    fNrOfAwayMvps = IServerJsonOption.NR_OF_AWAY_MVPS.getFrom(jsonObject);
    fNrOfHomeChoices = IServerJsonOption.NR_OF_HOME_CHOICES.getFrom(jsonObject);
    fNrOfHomeMvps = IServerJsonOption.NR_OF_HOME_MVPS.getFrom(jsonObject);
    fAwayPlayersNominated = IServerJsonOption.AWAY_PLAYERS_NOMINATED.getFrom(jsonObject);
    fHomePlayersNominated = IServerJsonOption.HOME_PLAYERS_NOMINATED.getFrom(jsonObject);
    ListTool.replaceAll(fAwayPlayersMvp, IServerJsonOption.AWAY_PLAYERS_MVP.getFrom(jsonObject));
    ListTool.replaceAll(fHomePlayersMvp, IServerJsonOption.HOME_PLAYERS_MVP.getFrom(jsonObject));
    return this;
  }

}
