package com.balancedbytes.games.ffb.server;

import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.change.IModelChangeObserver;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeList;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepFactory;
import com.balancedbytes.games.ffb.server.step.StepResult;
import com.balancedbytes.games.ffb.server.step.StepStack;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Kalimar
 */
public class GameState implements IModelChangeObserver, IJsonSerializable {

  private Game fGame;
  private GameLog fGameLog;
  private GameStatus fStatus;
  private StepStack fStepStack;
  private IStep fCurrentStep;
  private Set<String> zappedPlayerIds = new HashSet<String>();
  private int kickingSwarmers;

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

  public void update(ModelChange change) {
    if (change != null) {
      fChangeList.add(change);
    }
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
    update(null);
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
      UtilServerGame.syncGameModel(fCurrentStep);
    }
    progressStepStack(pReceivedCommand);
  }

  public void pushCurrentStepOnStack() {
    if (fCurrentStep != null) {
      getStepStack().push(fCurrentStep);
    }
  }

  public void findNextStep(ReceivedCommand receivedCommand) {
    fCurrentStep = getStepStack().pop();
    if (fCurrentStep != null) {
      getServer().getDebugLog().logCurrentStep(IServerLogLevel.DEBUG, this);
      if (receivedCommand == null) {
        fCurrentStep.start();
        UtilServerGame.syncGameModel(fCurrentStep);
      }
      progressStepStack(receivedCommand);
    }
  }

  private void progressStepStack(ReceivedCommand pReceivedCommand) {
	  if (fCurrentStep != null) {
		  StepResult stepResult = fCurrentStep.getResult();
		  StepAction action = stepResult.getNextAction();

		  if (action.triggerGoto()) {
			  handleStepResultGotoLabel((String) stepResult.getNextActionParameter());
		  }
		  
		  if (action.triggerNextStep()) {
			  ReceivedCommand forwardedCommand = action.forwardCommand() ? pReceivedCommand : null;

			  findNextStep(forwardedCommand);
			  if (action.forwardCommand()) {
				  handleCommand(forwardedCommand);
			  }
		  }
	  }
  }

  public void cleanupStepStack(String pGotoLabel) {
    if (StringTool.isProvided(pGotoLabel)) {
      List<IStep> poppedSteps = new ArrayList<>();
      while (getStepStack().peek() != null) {
        if (pGotoLabel.equals(getStepStack().peek().getLabel())) {
          return;
        } else {
          poppedSteps.add(getStepStack().pop());
        }
      }

      getStepStack().push(poppedSteps);
    }
  }

  private void handleStepResultGotoLabel(String pGotoLabel) {
    if (pGotoLabel == null) {
      String stepName = (fCurrentStep != null) ? fCurrentStep.getId().getName() : "unknown";
      throw new StepException("Step " + stepName + ": No goto label set.");
    }
    fCurrentStep = null;
    while (getStepStack().peek() != null) {
      if (pGotoLabel.equals(getStepStack().peek().getLabel())) {
        return;
      } else {
        getStepStack().pop();
      }
    }
    throw new StepException("Goto unknown label " + pGotoLabel);
  }

  public void addZappedPlayer(Player player) {
    zappedPlayerIds.add(player.getId());
  }

  public void removeZappedPlayer(Player player) {
    zappedPlayerIds.remove(player.getId());
  }

  public boolean isZapped(Player player) {
    return zappedPlayerIds.contains(player.getId());
  }

  public int getKickingSwarmers() {
    return kickingSwarmers;
  }

  public void setKickingSwarmers(int kickingSwarmers) {
    this.kickingSwarmers = kickingSwarmers;
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
    IServerJsonOption.PLAYER_IDS.addTo(jsonObject, zappedPlayerIds);
    IServerJsonOption.SWARMING_PLAYER_ACTUAL.addTo(jsonObject, kickingSwarmers);
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
    String[] ids = IServerJsonOption.PLAYER_IDS.getFrom(jsonObject);
    if (ids != null) {
      zappedPlayerIds.addAll(Arrays.asList(ids));
    }

    kickingSwarmers = IServerJsonOption.SWARMING_PLAYER_ACTUAL.getFrom(jsonObject);

    return this;
  }

}
