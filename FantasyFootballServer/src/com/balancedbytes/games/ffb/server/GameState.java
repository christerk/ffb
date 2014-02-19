package com.balancedbytes.games.ffb.server;

import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.GameStatusFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.change.IModelChangeObserver;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeList;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepFactory;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepIdFactory;
import com.balancedbytes.games.ffb.server.step.StepResult;
import com.balancedbytes.games.ffb.server.step.StepStack;
import com.balancedbytes.games.ffb.server.util.UtilGame;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class GameState implements IModelChangeObserver, IByteArraySerializable, IJsonSerializable {

  private Game fGame;
  private GameLog fGameLog;
  private GameStatus fStatus;
  private StepStack fStepStack;
  private IStep fCurrentStep;

  private transient FantasyFootballServer fServer;
  private transient DiceRoller fDiceRoller;
  private transient IdGenerator fCommandNrGenerator;
  private transient long fTurnTimeStarted;
  private transient ModelChangeList fChangeList;
  private transient Map<String, Long> fSpectatorCooldownTime;

  public GameState(FantasyFootballServer pServer) {
    fServer = pServer;
    fGameLog = new GameLog(this);
    fDiceRoller = new DiceRoller(this);
    fSpectatorCooldownTime = new HashMap<String, Long>();
    initCommandNrGenerator(0);
    fStepStack = new StepStack(this);
    fChangeList = new ModelChangeList();
    setGame(new Game());
  }

  public void setServer(FantasyFootballServer pServer) {
    fServer = pServer;
  }

  public FantasyFootballServer getServer() {
    return fServer;
  }

  public void setGame(Game pGame) {
    fGame = pGame;
    if (fGame != null) {
      fGame.addObserver(this);
    }
  }

  public Game getGame() {
    return fGame;
  }

  public long getId() {
    return getGame().getId();
  }

  public ModelChangeList fetchChanges() {
    ModelChangeList changeList = fChangeList;
    fChangeList = new ModelChangeList();
    return changeList;
  }

  public void update(ModelChange pChange) {
    fChangeList.add(pChange);
  }

  public DiceRoller getDiceRoller() {
    return fDiceRoller;
  }

  public int generateCommandNr() {
    return (int) fCommandNrGenerator.generateId();
  }

  public int lastCommandNr() {
    return (int) fCommandNrGenerator.lastId();
  }

  public void initCommandNrGenerator(long pLastId) {
    fCommandNrGenerator = new IdGenerator(pLastId);
  }

  public GameLog getGameLog() {
    return fGameLog;
  }

  public long getTurnTimeStarted() {
    return fTurnTimeStarted;
  }

  public void setTurnTimeStarted(long pTurnTimeStarted) {
    fTurnTimeStarted = pTurnTimeStarted;
  }

  public GameStatus getStatus() {
    return fStatus;
  }

  public void setStatus(GameStatus pStatus) {
    fStatus = pStatus;
  }

  public long getSpectatorCooldownTime(String pCoach) {
    return (fSpectatorCooldownTime.get(pCoach) != null) ? fSpectatorCooldownTime.get(pCoach) : 0;
  }

  public void putSpectatorCooldownTime(String pCoach, long pTimestamp) {
    fSpectatorCooldownTime.put(pCoach, pTimestamp);
  }

  public StepStack getStepStack() {
    return fStepStack;
  }

  public IStep getCurrentStep() {
    return fCurrentStep;
  }

  public void setCurrentStep(IStep pCurrentStep) {
    fCurrentStep = pCurrentStep;
  }

  public void handleCommand(ReceivedCommand pReceivedCommand) {
    if (pReceivedCommand == null) {
      return;
    }
    if (fCurrentStep == null) {
      findNextStep(null);
    }
    if (fCurrentStep != null) {
      fCurrentStep.handleCommand(pReceivedCommand);
      UtilGame.syncGameModel(fCurrentStep);
    }
    progressStepStack(pReceivedCommand);
  }

  public void pushCurrentStepOnStack() {
    if (fCurrentStep != null) {
      getStepStack().push(fCurrentStep);
    }
  }

  public void findNextStep(ReceivedCommand pReceivedCommand) {
    fCurrentStep = getStepStack().pop();
    if (fCurrentStep != null) {
      getServer().getDebugLog().logCurrentStep(IServerLogLevel.DEBUG, this);
      if (pReceivedCommand == null) {
        fCurrentStep.start();
        UtilGame.syncGameModel(fCurrentStep);
      }
      progressStepStack(pReceivedCommand);
    }
  }

  private void progressStepStack(ReceivedCommand pReceivedCommand) {
    if (fCurrentStep != null) {
      StepResult stepResult = fCurrentStep.getResult();
      switch (stepResult.getNextAction()) {
      case NEXT_STEP:
        handleStepResultNextStep(null);
        break;
      case NEXT_STEP_AND_REPEAT:
        handleStepResultNextStep(pReceivedCommand);
        break;
      case GOTO_LABEL:
        handleStepResultGotoLabel((String) stepResult.getNextActionParameter(), null);
        break;
      case GOTO_LABEL_AND_REPEAT:
        handleStepResultGotoLabel((String) stepResult.getNextActionParameter(), pReceivedCommand);
        break;
      default:
        break;
      }
    }
  }

  private void handleStepResultNextStep(ReceivedCommand pReceivedCommand) {
    findNextStep(pReceivedCommand);
    if (pReceivedCommand != null) {
      handleCommand(pReceivedCommand);
    }
  }

  private void handleStepResultGotoLabel(String pGotoLabel, ReceivedCommand pReceivedCommand) {
    if (pGotoLabel == null) {
      throw new StepException("No goto label set.");
    }
    fCurrentStep = null;
    IStep nextStep = getStepStack().pop();
    while (nextStep != null) {
      if (pGotoLabel.equals(nextStep.getLabel())) {
        getStepStack().push(nextStep); // push back onto stack
        break;
      } else {
        nextStep = getStepStack().pop();
      }
    }
    if (nextStep == null) {
      throw new StepException("Goto unknown label " + pGotoLabel);
    }
    findNextStep(pReceivedCommand);
    if (pReceivedCommand != null) {
      handleCommand(pReceivedCommand);
    }
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 1;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) ((getStatus() != null) ? getStatus().getId() : 0));
    if (fGame != null) {
      pByteList.addBoolean(true);
      fGame.addTo(pByteList);
    } else {
      pByteList.addBoolean(false);
    }
    if (fCurrentStep != null) {
      pByteList.addBoolean(true);
      fCurrentStep.addTo(pByteList);
    } else {
      pByteList.addBoolean(false);
    }
    fStepStack.addTo(pByteList);
    fGameLog.addTo(pByteList);
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fStatus = new GameStatusFactory().forId(pByteArray.getByte());
    if (pByteArray.getBoolean()) {
      fGame = new Game();
      fGame.initFrom(pByteArray);
    } else {
      fGame = null;
    }
    if (pByteArray.getBoolean()) {
      fCurrentStep = initStepFrom(pByteArray);
    } else {
      fCurrentStep = null;
    }
    fStepStack.initFrom(pByteArray);
    fGameLog.initFrom(pByteArray);
    initCommandNrGenerator(fGameLog.findMaxCommandNr());
    return byteArraySerializationVersion;
  }

  private IStep initStepFrom(ByteArray pByteArray) {
    StepId stepId = new StepIdFactory().forId(pByteArray.getSmallInt(pByteArray.getPosition()));
    IStep step = new StepFactory(this).forStepId(stepId);
    step.initFrom(pByteArray);
    return step;
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IServerJsonOption.GAME_STATUS.addTo(jsonObject, fStatus);
    IServerJsonOption.STEP_STACK.addTo(jsonObject, fStepStack.toJsonValue());
    IServerJsonOption.GAME_LOG.addTo(jsonObject, fGameLog.toJsonValue());
    if (fCurrentStep != null) {
      IServerJsonOption.CURRENT_STEP.addTo(jsonObject, fCurrentStep.toJsonValue());
    }
    if (fGame != null) {
      IServerJsonOption.GAME.addTo(jsonObject, fGame.toJsonValue());
    }
    return jsonObject;
  }

  public GameState initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fStatus = (GameStatus) IServerJsonOption.GAME_STATUS.getFrom(jsonObject);
    fStepStack.clear();
    JsonObject stepStackObject = IServerJsonOption.STEP_STACK.getFrom(jsonObject);
    if (stepStackObject != null) {
      fStepStack.initFrom(stepStackObject);
    }
    fGameLog.clear();
    JsonObject gameLogObject = IServerJsonOption.GAME_LOG.getFrom(jsonObject);
    if (gameLogObject != null) {
      fGameLog.initFrom(gameLogObject);
    }
    fCurrentStep = null;
    JsonObject currentStepObject = IServerJsonOption.CURRENT_STEP.getFrom(jsonObject);
    if (currentStepObject != null) {
      fCurrentStep = new StepFactory(this).forJsonValue(currentStepObject);
    }
    setGame(null);
    JsonObject gameObject = IServerJsonOption.GAME.getFrom(jsonObject);
    if (gameObject != null) {
      setGame(new Game().initFrom(gameObject));
    }
    return this;
  }

}
