package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogConcedeGameParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.net.commands.ClientCommandConcedeGame;
import com.balancedbytes.games.ffb.report.ReportList;
import com.balancedbytes.games.ffb.report.ReportTimeoutEnforced;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.SessionManager;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilGame;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public abstract class AbstractStep implements IStep {

  private GameState fGameState;
  private StepResult fStepResult;
  private String fLabel;

  protected AbstractStep(GameState pGameState) {
    fGameState = pGameState;
    setStepResult(new StepResult());
  }

  public void setLabel(String pLabel) {
    fLabel = pLabel;
    // System.out.println("setLabel(" + pLabel + ")");
  }

  public String getLabel() {
    return fLabel;
  }

  public GameState getGameState() {
    return fGameState;
  }

  private void setStepResult(StepResult pStepResult) {
    fStepResult = pStepResult;
  }

  public StepResult getResult() {
    return fStepResult;
  }

  public void init(StepParameterSet pParameterSet) {
    // do nothing, override in subclass if needed
  }

  public void start() {
    // do nothing, override in subclass if needed
  }

  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = StepCommandStatus.UNHANDLED_COMMAND;
    switch (pReceivedCommand.getId()) {
    case CLIENT_CONCEDE_GAME:
      commandStatus = handleConcedeGame(pReceivedCommand);
      break;
    case CLIENT_ILLEGAL_PROCEDURE:
      commandStatus = handleIllegalProcedure(pReceivedCommand);
      break;
    default:
      break;
    }
    return commandStatus;
  }

  public boolean setParameter(StepParameter pParameter) {
    // do nothing, override in subclass if needed
    return false;
  }

  protected void publishParameter(StepParameter pParameter) {
    if (pParameter != null) {
      DebugLog debugLog = getGameState().getServer().getDebugLog();
      if (debugLog.isLogging(IServerLogLevel.TRACE)) {
        StringBuilder trace = new StringBuilder();
        trace.append(getId()).append(" publishes ").append(pParameter.getKey()).append("=").append(pParameter.getValue());
        debugLog.log(IServerLogLevel.TRACE, trace.toString());
      }
      setParameter(pParameter);
      getGameState().getStepStack().publishStepParameter(pParameter);
    }
  }

  protected void publishParameters(StepParameterSet pParameterSet) {
    if (pParameterSet != null) {
      for (StepParameter parameter : pParameterSet.values()) {
        publishParameter(parameter);
      }
    }
  }
  
  // ByteArray serialization

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getLabel());
    getResult().addTo(pByteList);
  }

  public int initFrom(ByteArray pByteArray) {
    UtilSteps.validateStepId(this, new StepIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setLabel(pByteArray.getString());
    setStepResult(new StepResult());
    getResult().initFrom(pByteArray);
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IServerJsonOption.STEP_ID.addTo(jsonObject, getId());
    IServerJsonOption.LABEL.addTo(jsonObject, fLabel);
    IServerJsonOption.STEP_RESULT.addTo(jsonObject, fStepResult.toJsonValue());
    return jsonObject;
  }
  
  public AbstractStep initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilSteps.validateStepId(this, (StepId) IServerJsonOption.STEP_ID.getFrom(jsonObject));
    fLabel = IServerJsonOption.LABEL.getFrom(jsonObject);
    fStepResult = null;
    JsonObject stepResultObject = IServerJsonOption.STEP_RESULT.getFrom(jsonObject);
    if (stepResultObject != null) {
      fStepResult = new StepResult().initFrom(stepResultObject);
    }
    return this;
  }
  
  // Helper methods

  private StepCommandStatus handleConcedeGame(ReceivedCommand pReceivedCommand) {
    ClientCommandConcedeGame concedeGameCommand = (ClientCommandConcedeGame) pReceivedCommand.getCommand();
    StepCommandStatus commandStatus = StepCommandStatus.UNHANDLED_COMMAND;
    Game game = getGameState().getGame();
    GameResult gameResult = game.getGameResult();
    if (concedeGameCommand.getConcedeGameStatus() != null) {
      SessionManager sessionManager = getGameState().getServer().getSessionManager();
      boolean homeCommand = (sessionManager.getSessionOfHomeCoach(getGameState()) == pReceivedCommand.getSession());
      boolean awayCommand = (sessionManager.getSessionOfAwayCoach(getGameState()) == pReceivedCommand.getSession());
      switch (concedeGameCommand.getConcedeGameStatus()) {
      case REQUESTED:
        if (game.isConcessionPossible() && ((game.isHomePlaying() && homeCommand) || (!game.isHomePlaying() && awayCommand))) {
          UtilDialog.showDialog(getGameState(), new DialogConcedeGameParameter());
        }
        break;
      case CONFIRMED:
        game.setConcessionPossible(false);
        gameResult.getTeamResultHome().setConceded(game.isHomePlaying() && homeCommand);
        gameResult.getTeamResultAway().setConceded(!game.isHomePlaying() && awayCommand);
        break;
      case DENIED:
        UtilDialog.hideDialog(getGameState());
        break;
      }
      if (gameResult.getTeamResultHome().hasConceded() || gameResult.getTeamResultAway().hasConceded()) {
        getGameState().getStepStack().clear();
        SequenceGenerator.getInstance().pushEndGameSequence(getGameState(), false);
        getResult().setNextAction(StepAction.NEXT_STEP);
      }
      commandStatus = StepCommandStatus.SKIP_STEP;
    }
    return commandStatus;
  }

  private StepCommandStatus handleIllegalProcedure(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = StepCommandStatus.UNHANDLED_COMMAND;
    Game game = getGameState().getGame();
    if (game.isTimeoutPossible()) {
      ReportList reports = new ReportList();
      FantasyFootballServer server = getGameState().getServer();
      String coach = server.getSessionManager().getCoachForSession(pReceivedCommand.getSession());
      reports.add(new ReportTimeoutEnforced(coach));
      game.setTimeoutEnforced(true);
      game.setTimeoutPossible(false);
      UtilGame.syncGameModel(getGameState(), reports, null, Sound.WHISTLE);
      commandStatus = StepCommandStatus.EXECUTE_STEP;
    }
    return commandStatus;
  }

}
