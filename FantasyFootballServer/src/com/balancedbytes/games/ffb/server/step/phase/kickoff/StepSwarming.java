package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.dialog.DialogSwarmingErrorParameter;
import com.balancedbytes.games.ffb.dialog.DialogSwarmingPlayersParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.commands.ClientCommandSetupPlayer;
import com.balancedbytes.games.ffb.report.ReportSwarmingRoll;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerSetup;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.HashSet;
import java.util.Set;

public class StepSwarming extends AbstractStep {

  private boolean fEndTurn;
  private boolean handleReceivingTeam;
  private int allowedAmount;
  private String teamId;

  public StepSwarming(GameState pGameState) {
    super(pGameState);
  }

  @Override
  public void start() {
    executeStep();
  }

  @Override
  public StepId getId() {
    return StepId.SWARMING;
  }

  @Override
  public void init(StepParameterSet pParameterSet) {
    super.init(pParameterSet);
    if (pParameterSet != null) {
      for (StepParameter parameter: pParameterSet.values()) {
        if (parameter.getKey() == StepParameterKey.HANDLE_RECEIVING_TEAM) {
          handleReceivingTeam = (boolean) parameter.getValue();
        }
      }
    }
  }

  @Override
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);

    switch (pReceivedCommand.getId()) {
      case CLIENT_END_TURN:
        fEndTurn = true;
        executeStep();
        break;

      case CLIENT_SETUP_PLAYER:
        ClientCommandSetupPlayer setupPlayerCommand = (ClientCommandSetupPlayer) pReceivedCommand.getCommand();
        UtilServerSetup.setupPlayer(getGameState(), setupPlayerCommand.getPlayerId(), setupPlayerCommand.getCoordinate());
        break;

    }
    return commandStatus;
  }

  private void executeStep() {
    Game game = getGameState().getGame();
    boolean hasSwarmingReserves = false;


    if (game.getTurnMode() == TurnMode.SWARMING) {
      if (fEndTurn) {
        fEndTurn = false;
        getResult().setSound(SoundId.DING);
        int placedSwarmingPlayers = 0;
        for (Player player: game.getTeamById(teamId).getPlayers()) {
          PlayerState playerState = game.getFieldModel().getPlayerState(player);
          FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
          if (playerState.isActive() && !playerCoordinate.isBoxCoordinate()) {
            placedSwarmingPlayers++;
          }
        }

        if (placedSwarmingPlayers > allowedAmount) {
          UtilServerDialog.showDialog(getGameState(), new DialogSwarmingErrorParameter(allowedAmount, placedSwarmingPlayers), false);
        } else {

          for (Player player: game.getTeamById(teamId).getPlayers()) {
            PlayerState playerState = game.getFieldModel().getPlayerState(player);
            if (playerState.getBase() == PlayerState.PRONE) {
              game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.RESERVE));
            }
          }
          
          game.setTurnMode(TurnMode.KICKOFF);
          UtilPlayer.refreshPlayersForTurnStart(game);
          game.getFieldModel().clearTrackNumbers();
          if (handleReceivingTeam) {
            game.setHomePlaying(!game.isHomePlaying());
          } else {
            getGameState().setKickingSwarmers(placedSwarmingPlayers);
          }
          getGameState().getStepStack().pop();
          getResult().setNextAction(StepAction.NEXT_STEP);
        }
      }
    } else {
      if (!handleReceivingTeam) {
        getGameState().setKickingSwarmers(0);
      }
      teamId = swarmingTeam(game).getId();
      Set<Player> playersOnPitch = new HashSet<>();
      Set<Player> playersReserveNoSwarming = new HashSet<>();
      for (Player player: game.getTeamById(teamId).getPlayers()) {
        FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
        if (FieldCoordinateBounds.FIELD.isInBounds(playerCoordinate)) {
          playersOnPitch.add(player);
        } else if (game.getFieldModel().getPlayerState(player).getBase() == PlayerState.RESERVE) {
          if (UtilCards.hasSkill(game, player, Skill.SWARMING)) {
            hasSwarmingReserves = true;
          } else {
            playersReserveNoSwarming.add(player);
          }
        }
      }

      if (hasSwarmingReserves) {
        for (Player player : playersOnPitch) {
          PlayerState playerState = game.getFieldModel().getPlayerState(player);
          game.getFieldModel().setPlayerState(player, playerState.changeActive(false));
        }

        for (Player player : playersReserveNoSwarming) {
          PlayerState playerState = game.getFieldModel().getPlayerState(player);
          game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.PRONE));
        }

        if (handleReceivingTeam) {
          game.setHomePlaying(!game.isHomePlaying());
        }

        game.setTurnMode(TurnMode.SWARMING);
        getGameState().pushCurrentStepOnStack();

        allowedAmount = getGameState().getDiceRoller().rollSwarmingPlayers();
        getResult().addReport(new ReportSwarmingRoll(teamId, allowedAmount));
        UtilServerDialog.showDialog(getGameState(), new DialogSwarmingPlayersParameter(allowedAmount), false);
      } else {
        getResult().setNextAction(StepAction.NEXT_STEP);
      }
    }

  }

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
    IServerJsonOption.HANDLE_RECEIVING_TEAM.addTo(jsonObject, handleReceivingTeam);
    IServerJsonOption.SWARMING_PLAYER_AMOUT.addTo(jsonObject, allowedAmount);
    IServerJsonOption.TEAM_ID.addTo(jsonObject, teamId);
    return jsonObject;
  }

  @Override
  public StepSwarming initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fEndTurn = IServerJsonOption.END_TURN.getFrom(jsonObject);
    handleReceivingTeam = IServerJsonOption.HANDLE_RECEIVING_TEAM.getFrom(jsonObject);
    allowedAmount = IServerJsonOption.SWARMING_PLAYER_AMOUT.getFrom(jsonObject);
    teamId = IServerJsonOption.TEAM_ID.getFrom(jsonObject);
    return this;
  }

  private Team swarmingTeam(Game game) {
    if (handleReceivingTeam) {
      return game.isHomePlaying() ? game.getTeamAway() : game.getTeamHome();
    }
    return game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
  }
}
