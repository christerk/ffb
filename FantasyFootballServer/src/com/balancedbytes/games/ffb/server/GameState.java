package com.balancedbytes.games.ffb.server;

import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepResult;
import com.balancedbytes.games.ffb.server.step.StepStack;
import com.balancedbytes.games.ffb.server.util.UtilGame;

/**
 * 
 * @author Kalimar
 */
public class GameState implements IByteArraySerializable {

	private Game fGame;

	private GameLog fGameLog;

	private GameStatus fStatus;
	
	private StepStack fStepStack;

	private transient IStep fCurrentStep;

	private transient FantasyFootballServer fServer;

	private transient DiceRoller fDiceRoller;

	private transient IdGenerator fCommandNrGenerator;

	private transient long fTurnTimeStarted;

	private transient Map<String, Long> fSpectatorCooldownTime;
	
	public GameState(FantasyFootballServer pServer) {
		fServer = pServer;
		fGameLog = new GameLog(this);
		fDiceRoller = new DiceRoller(this);
		fSpectatorCooldownTime = new HashMap<String, Long>();
		initCommandNrGenerator(0);
		fStepStack = new StepStack(this);
	}

	public FantasyFootballServer getServer() {
		return fServer;
	}

	public void setGame(Game pGame) {
		fGame = pGame;
	}

	public Game getGame() {
		return fGame;
	}

	public long getId() {
		return getGame().getId();
	}

	// public void changeServerState(ServerStateId pServerStateId) {
	// ServerState newServerState = getServer().getStateForId(pServerStateId);
	// if (newServerState != fServerState) {
	// ServerState oldServerState = fServerState;
	// fServerState = newServerState;
	// getServer().getDebugLog().logChangeState(IServerLogLevel.INFO, this,
	// pServerStateId);
	// fServerState.enterState(this, (oldServerState != null) ?
	// oldServerState.getId() : null);
	// }
	// }

	// public void changeServerState(ServerStateId pServerStateId, NetCommand
	// pNetCommand) {
	// changeServerState(pServerStateId);
	// if (pNetCommand != null) {
	// fServerState.handleNetCommand(this, pNetCommand);
	// }
	// }

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

	public void handleNetCommand(NetCommand pNetCommand) {
		if (pNetCommand == null) {
			return;
		}
		if (fCurrentStep == null) {
			findNextStep(null);
		}
		if (fCurrentStep != null) {
			fCurrentStep.handleNetCommand(pNetCommand);
			UtilGame.syncGameModel(fCurrentStep);
		}
		progressStepStack(pNetCommand);
	}
	
	public void pushCurrentStepOnStack() {
		if (fCurrentStep != null) {
			getStepStack().push(fCurrentStep);
		}
	}
	
	public void findNextStep(NetCommand pNetCommand) {
		fCurrentStep = getStepStack().pop();
		if (fCurrentStep != null) {
			getServer().getDebugLog().logCurrentStep(IServerLogLevel.DEBUG, this);
			if (pNetCommand == null) {
				fCurrentStep.start();
				UtilGame.syncGameModel(fCurrentStep);
			}
			progressStepStack(pNetCommand);
		}
	}

	private void progressStepStack(NetCommand pNetCommand) {
		if (fCurrentStep != null) {
			StepResult stepResult = fCurrentStep.getResult();
			switch (stepResult.getNextAction()) {
				case NEXT_STEP:
					handleStepResultNextStep(null);
					break;
				case NEXT_STEP_AND_REPEAT_COMMAND:
					handleStepResultNextStep(pNetCommand);
					break;
				case GOTO_LABEL:
					handleStepResultGotoLabel((String) stepResult.getNextActionParameter(), null);
					break;
				case GOTO_LABEL_AND_REPEAT_COMMAND:
					handleStepResultGotoLabel((String) stepResult.getNextActionParameter(), pNetCommand);
					break;
				default:
					break;
			}
		}
	}
	
	private void handleStepResultNextStep(NetCommand pNetCommand) {
		findNextStep(pNetCommand);
		if (pNetCommand != null) {
			handleNetCommand(pNetCommand);
		}
	}
	
	private void handleStepResultGotoLabel(String pGotoLabel, NetCommand pNetCommand) {
		if (pGotoLabel == null) {
			throw new StepException("No goto label set.");
		}
		fCurrentStep = null;
		IStep nextStep = getStepStack().pop();
		while (nextStep != null) {
			if (pGotoLabel.equals(nextStep.getLabel())) {
				getStepStack().push(nextStep);  // push back onto stack
				break;
			} else {
				nextStep = getStepStack().pop();
			}
		}
		if (nextStep == null) {
			throw new StepException("Goto unknown label " + pGotoLabel);
		}
		findNextStep(pNetCommand);
		if (pNetCommand != null) {
			handleNetCommand(pNetCommand);
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
			fStepStack.push(fCurrentStep);
      fStepStack.addTo(pByteList);
      fStepStack.pop();
    } else {
      fStepStack.addTo(pByteList);
    }
    fGameLog.addTo(pByteList);
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fStatus = GameStatus.fromId(pByteArray.getByte());
    if (pByteArray.getBoolean()) {
    	fGame = new Game();
    	fGame.initFrom(pByteArray);
    } else {
    	fGame = null;
    }
    fStepStack.initFrom(pByteArray);
    fGameLog.initFrom(pByteArray);
    initCommandNrGenerator(fGameLog.findMaxCommandNr());
    return byteArraySerializationVersion;
  }

}
