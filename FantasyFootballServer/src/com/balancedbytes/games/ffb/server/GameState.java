package com.balancedbytes.games.ffb.server;

import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.ISkillBehaviour;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.change.IModelChangeObserver;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeList;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepFactory;
import com.balancedbytes.games.ffb.server.step.StepResult;
import com.balancedbytes.games.ffb.server.step.StepStack;
import com.balancedbytes.games.ffb.server.step.bb2020.pass.state.PassState;
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
	private final GameLog fGameLog;
	private GameStatus fStatus;
	private final StepStack fStepStack;
	private IStep fCurrentStep;
	private final Set<String> zappedPlayerIds = new HashSet<>();
	private int kickingSwarmers;

	private transient FantasyFootballServer fServer;
	private final transient DiceRoller fDiceRoller;
	private transient IdGenerator fCommandNrGenerator;
	private transient long fTurnTimeStarted;
	private transient ModelChangeList fChangeList;
	private final transient Map<String, Long> fSpectatorCooldownTime;
	private StepFactory stepFactory;
	private PassState passState;

	
	private enum StepExecutionMode {
		Start, HandleCommand
	}

	public GameState(FantasyFootballServer pServer) {
		fServer = pServer;
		fGameLog = new GameLog(this);
		fDiceRoller = new DiceRoller(this);
		fSpectatorCooldownTime = new HashMap<>();
		initCommandNrGenerator(0);
		fStepStack = new StepStack(this);
		fChangeList = new ModelChangeList();
		setGame(new Game(fServer.getFactorySource(), fServer.getFactoryManager()));
	}

	public void initRulesDependentMembers() {
		stepFactory = new StepFactory(this);
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

	public PassState getPassState() {
		return passState;
	}

	public void setPassState(PassState passState) {
		this.passState = passState;
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

	public void handleCommand(ReceivedCommand pReceivedCommand) {
		if (pReceivedCommand == null) {
			return;
		}
		if (fCurrentStep == null) {
			startNextStep();
		}
		if (fCurrentStep != null) {
			executeStep(StepExecutionMode.HandleCommand, pReceivedCommand);
		}
	}

	public void startNextStep() {
		progressToNextStep();
		if (fCurrentStep != null) {
			getServer().getDebugLog().logCurrentStep(IServerLogLevel.DEBUG, this);
			executeStep(StepExecutionMode.Start, null);
		}
	}
	
	private void progressToNextStep() {
		fCurrentStep = getStepStack().pop();
	}
	
	private void executeStep(StepExecutionMode mode, ReceivedCommand receivedCommand) {
		boolean forward;
		do {
			if (mode == StepExecutionMode.Start) {
				fCurrentStep.start();
			} else {
				fCurrentStep.handleCommand(receivedCommand);
			}
	
			while (fCurrentStep.getResult().getNextAction().triggerRepeat()) {
				fCurrentStep.repeat();
			}
	
			UtilServerGame.syncGameModel(fCurrentStep);
			forward = processStepResult();
			if (forward) {
				mode = StepExecutionMode.HandleCommand;
			}
		} while (forward);
	}

	private boolean processStepResult() {
		if (fCurrentStep == null) {
			throw new StepException("Trying to process result from a null step.");
		}

		StepResult stepResult = fCurrentStep.getResult();
		StepAction action = stepResult.getNextAction();

		if (action.triggerGoto()) {
			// Skip forward until we're at the appropriate label
			handleStepResultGotoLabel(stepResult.getNextActionParameter());
		}

		if (action.triggerNextStep()) {
			if (action.forwardCommand()) {
				// With forwarded commands, we're expected to not run start()
				// So just get the next step off of the stack
				progressToNextStep();
				return true;
			} else {
				// We're triggering the next step normally, so get it off of the stack
				// and execute
				startNextStep();
			}
		}
		return false;
	}

	public void pushCurrentStepOnStack() {
		if (fCurrentStep != null) {
			getStepStack().push(fCurrentStep);
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

	public void addZappedPlayer(Player<?> player) {
		zappedPlayerIds.add(player.getId());
	}

	public void removeZappedPlayer(Player<?> player) {
		zappedPlayerIds.remove(player.getId());
	}

	public boolean isZapped(Player<?> player) {
		return zappedPlayerIds.contains(player.getId());
	}

	public int getKickingSwarmers() {
		return kickingSwarmers;
	}

	public void setKickingSwarmers(int kickingSwarmers) {
		this.kickingSwarmers = kickingSwarmers;
	}
	
	public StepFactory getStepFactory() {
		return stepFactory;
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
		if (passState != null) {
			IServerJsonOption.PASS_STATE.addTo(jsonObject, passState.toJsonValue());
		}
		return jsonObject;
	}

	public GameState initFrom(IFactorySource source, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fStatus = (GameStatus) IServerJsonOption.GAME_STATUS.getFrom(source, jsonObject);
		fStepStack.clear();
		JsonObject stepStackObject = IServerJsonOption.STEP_STACK.getFrom(source, jsonObject);
		if (stepStackObject != null) {
			fStepStack.initFrom(source, stepStackObject);
		}
		fGameLog.clear();
		JsonObject gameLogObject = IServerJsonOption.GAME_LOG.getFrom(source, jsonObject);
		if (gameLogObject != null) {
			fGameLog.initFrom(source, gameLogObject);
		}
		fCurrentStep = null;
		setGame(null);
		JsonObject gameObject = IServerJsonOption.GAME.getFrom(source, jsonObject);
		if (gameObject != null) {
			Game newGame = new Game(getServer().getFactorySource(), getServer().getFactoryManager());
			newGame.initFrom(source, gameObject);
			setGame(newGame);
			initRulesDependentMembers();
			JsonObject currentStepObject = IServerJsonOption.CURRENT_STEP.getFrom(source, jsonObject);
			if (currentStepObject != null) {
				fCurrentStep = stepFactory.forJsonValue(source, currentStepObject);
			}
		}
		String[] ids = IServerJsonOption.PLAYER_IDS.getFrom(source, jsonObject);
		if (ids != null) {
			zappedPlayerIds.addAll(Arrays.asList(ids));
		}

		kickingSwarmers = IServerJsonOption.SWARMING_PLAYER_ACTUAL.getFrom(source, jsonObject);

		JsonObject passStateObject = IServerJsonOption.PASS_STATE.getFrom(source, jsonObject);
		if (passStateObject != null) {
			passState = new PassState().initFrom(source, passStateObject);
		}
		return this;
	}

	public boolean executeStepHooks(IStep step, Object state) {
		List<StepModifier<? extends IStep, ?>> modifiers = new ArrayList<>();

		for (Skill skill : getGame().getRules().getSkillFactory().getSkills()) {
			ISkillBehaviour<? extends Skill> behaviour = skill.getSkillBehaviour();
			if (behaviour != null) {
				List<StepModifier<? extends IStep, ?>> skillModifiers = ((SkillBehaviour<? extends Skill>) behaviour)
						.getStepModifiers();
				for (StepModifier<? extends IStep, ?> modifier : skillModifiers) {
					if (modifier.appliesTo(step)) {
						getServer().getDebugLog().log(IServerLogLevel.DEBUG, getGame().getId(),
								"Detected StepModifier: " + modifier.getClass().getName());
						modifiers.add(modifier);
					}
				}
			}
		}

		modifiers.sort(StepModifier.Comparator);

		for (StepModifier<? extends IStep, ?> modifier : modifiers) {
			boolean stopProcessing = modifier.handleExecuteStep(step, state);
			if (stopProcessing) {
				return true;
			}
		}
		return false;
	}

}
