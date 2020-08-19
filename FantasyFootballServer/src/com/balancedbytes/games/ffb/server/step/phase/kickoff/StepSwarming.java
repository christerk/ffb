package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.commands.ClientCommandSetupPlayer;
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
import com.balancedbytes.games.ffb.server.util.UtilServerSetup;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.HashSet;
import java.util.Set;

public class StepSwarming extends AbstractStep {

  private boolean fEndTurn;
  private boolean handleKickingTeam;
  private int amount;

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
        if (parameter.getKey() == StepParameterKey.HANDLE_KICKING_TEAM) {
          handleKickingTeam = (boolean) parameter.getValue();
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
        game.setTurnMode(TurnMode.KICKOFF);
        UtilPlayer.refreshPlayersForTurnStart(game);
        game.getFieldModel().clearTrackNumbers();
        if (!handleKickingTeam) {
          game.setHomePlaying(!game.isHomePlaying());
        }
        getGameState().getStepStack().pop();
        getResult().setNextAction(StepAction.NEXT_STEP);
      }
    } else {
      Team swarmingTeam = homeTeamSwarms(game) ? game.getTeamHome() : game.getTeamAway();
      Set<Player> passivePlayers = new HashSet<>();
      for (Player player: swarmingTeam.getPlayers()) {
        FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
        if (FieldCoordinateBounds.FIELD.isInBounds(playerCoordinate)) {
          passivePlayers.add(player);
        } else if (game.getFieldModel().getPlayerState(player).getBase() == PlayerState.RESERVE) {
          if (UtilCards.hasSkill(game, player, Skill.SWARMING)) {
            hasSwarmingReserves = true;
          } else {
            passivePlayers.add(player);
          }
        }
      }

      if (hasSwarmingReserves) {
        for (Player player : passivePlayers) {
          PlayerState playerState = game.getFieldModel().getPlayerState(player);
          game.getFieldModel().setPlayerState(player, playerState.changeActive(false));
        }

        if (!handleKickingTeam) {
          game.setHomePlaying(!game.isHomePlaying());
        }

        game.setTurnMode(TurnMode.SWARMING);
        getGameState().pushCurrentStepOnStack();

        amount = getGameState().getDiceRoller().rollSwarmingPlayers();
        getResult().addReport(new ReportSwarmingRoll(swarmingTeam.getId(), amount));
      } else {
        getResult().setNextAction(StepAction.NEXT_STEP);
      }
    }

  }

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
    IServerJsonOption.HANDLE_KICKING_TEAM.addTo(jsonObject, handleKickingTeam);
    IServerJsonOption.SWARMING_PLAYER_AMOUT.addTo(jsonObject, amount);
    return jsonObject;
  }

  @Override
  public StepSwarming initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fEndTurn = IServerJsonOption.END_TURN.getFrom(jsonObject);
    handleKickingTeam = IServerJsonOption.HANDLE_KICKING_TEAM.getFrom(jsonObject);
    amount = IServerJsonOption.SWARMING_PLAYER_AMOUT.getFrom(jsonObject);
    return this;
  }

  private boolean homeTeamSwarms(Game game) {
    boolean isHomePlaying = game.isHomePlaying();
    if (!handleKickingTeam) {
      isHomePlaying = !isHomePlaying;
    }
    return isHomePlaying;
  }
}
