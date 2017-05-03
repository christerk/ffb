package com.balancedbytes.games.ffb.server.step.game.start;

import java.util.Date;

import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.option.IGameOption;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.SessionManager;
import com.balancedbytes.games.ffb.server.request.fumbbl.FumbblRequestCreateGamestate;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step to init the start game sequence.
 * 
 * @author Kalimar
 */
public final class StepInitStartGame extends AbstractStep {

  private boolean fFumbblGameCreated;
  private boolean fStartedHome;
  private boolean fStartedAway;

  public StepInitStartGame(GameState pGameState) {
    super(pGameState);
    getResult().setSynchronize(false);
  }

  public StepId getId() {
    return StepId.INIT_START_GAME;
  }

  @Override
  public void start() {
    super.start();
    executeStep();
  }

  @Override
  public StepCommandStatus handleCommand(ReceivedCommand receivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(receivedCommand);
    if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
      Game game = getGameState().getGame();
      switch (receivedCommand.getId()) {
        case INTERNAL_SERVER_FUMBBL_GAME_CREATED:
          fFumbblGameCreated = true;
          commandStatus = StepCommandStatus.EXECUTE_STEP;
          break;
        case CLIENT_START_GAME:
          SessionManager sessionManager = getGameState().getServer().getSessionManager();
          if (receivedCommand.getSession() == sessionManager.getSessionOfHomeCoach(getGameState().getId())) {
            fStartedHome = true;
          }
          if (receivedCommand.getSession() == sessionManager.getSessionOfAwayCoach(getGameState().getId())) {
            fStartedAway = true;
          }
          if (fStartedHome && fStartedAway && (game.getStarted() == null)) {
            game.setStarted(new Date());
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
    FantasyFootballServer server = getGameState().getServer();
    GameCache gameCache = server.getGameCache();
    if (game.getStarted() != null) {
      gameCache.removeMappingForGameId(getGameState().getId());
      if (server.getMode() == ServerMode.FUMBBL) {
        if (fFumbblGameCreated) {
          leaveStep();
        } else {
          server.getRequestProcessor().add(new FumbblRequestCreateGamestate(getGameState()));
        }
      } else {
        leaveStep();
      }
    }
  }

  private void leaveStep() {
    Game game = getGameState().getGame();
    FantasyFootballServer server = getGameState().getServer();
    GameCache gameCache = server.getGameCache();
    if ((game != null) && (game.getTeamHome() != null) && (game.getTeamAway() != null)) {
      getGameState().setStatus(GameStatus.ACTIVE);
      gameCache.queueDbUpdate(getGameState(), true);
      // log start game -->
      StringBuilder logEntry = new StringBuilder();
      logEntry.append("START GAME ").append(StringTool.print(game.getTeamHome().getName())).append(" vs. ")
          .append(StringTool.print(game.getTeamAway().getName()));
      server.getDebugLog().log(IServerLogLevel.WARN, getGameState().getId(), logEntry.toString());
      if (game.getOptions() != null) {
        StringBuilder optionValues = new StringBuilder();
        for (IGameOption option : game.getOptions().getOptions()) {
          if (option.isChanged()) {
            if (optionValues.length() > 0) {
              optionValues.append(",");
            }
            optionValues.append(option.getId().getName()).append("=").append(option.getValueAsString());
          }
        }
        if (optionValues.length() > 0) {
          server.getDebugLog().log(IServerLogLevel.WARN, getGameState().getId(), "Options " + optionValues.toString());
        } else {
          server.getDebugLog().log(IServerLogLevel.WARN, getGameState().getId(), "Default Options");
        }
      }
      // <-- log start game
    }
    getResult().setNextAction(StepAction.NEXT_STEP);
  }

  // JSON serialization

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.FUMBBL_GAME_CREATED.addTo(jsonObject, fFumbblGameCreated);
    return jsonObject;
  }

  @Override
  public StepInitStartGame initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fFumbblGameCreated = IServerJsonOption.FUMBBL_GAME_CREATED.getFrom(jsonObject);
    return this;
  }

}
