package com.fumbbl.ffb.server;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.GameStatus;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.BlitzTurnState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ISkillBehaviour;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.change.IModelChangeObserver;
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.model.change.ModelChangeList;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ServerCommand;
import com.fumbbl.ffb.server.factory.ObserverFactory;
import com.fumbbl.ffb.server.marking.AutoMarkingConfig;
import com.fumbbl.ffb.server.model.CarriedPlayer;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandApplyAutomatedPlayerMarkings;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepFactory;
import com.fumbbl.ffb.server.step.StepResult;
import com.fumbbl.ffb.server.step.StepStack;
import com.fumbbl.ffb.server.step.mixed.pass.state.PassState;
import com.fumbbl.ffb.server.util.ReRollService;
import com.fumbbl.ffb.server.util.UtilServerGame;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Kalimar
 */
public class GameState implements IModelChangeObserver, IJsonSerializable {

	private Game fGame;
	private final GameLog fGameLog;
	private GameStatus fStatus;
	private final StepStack fStepStack;
	private final Set<String> zappedPlayerIds = new HashSet<>();
	private int kickingSwarmers;

	private transient FantasyFootballServer fServer;
	private final transient DiceRoller fDiceRoller;
	private final transient ReRollService fReRollService;
	private transient IdGenerator fCommandNrGenerator;
	private transient long fTurnTimeStarted;
	private transient ModelChangeList fChangeList;
	private final transient Map<String, Long> fSpectatorCooldownTime;
	private StepFactory stepFactory;
	private PassState passState;
	private BlitzTurnState blitzTurnState;
	private PrayerState prayerState = new PrayerState();
	private ActiveEffects activeEffects = new ActiveEffects();
	private final StepExecutor stepExecutor = new StepExecutor(this);

	enum StepExecutionMode {
		Start, HandleCommand
	}

	public GameState(FantasyFootballServer pServer) {
		fServer = pServer;
		fGameLog = new GameLog(this);
		fDiceRoller = new DiceRoller(this);
		fReRollService = new ReRollService();
		fSpectatorCooldownTime = new HashMap<>();
		initCommandNrGenerator(0);
		fStepStack = new StepStack(this);
		fChangeList = new ModelChangeList();
		setGame(new Game(fServer.getFactorySource(), fServer.getFactoryManager()));
		setPassState(new PassState());
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
		this.passState = passState.populate(this.passState);
	}

	public BlitzTurnState getBlitzTurnState() {
		return blitzTurnState;
	}

	public void setBlitzTurnState(BlitzTurnState blitzTurnState) {
		this.blitzTurnState = blitzTurnState;
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

		if (getGame().isInitialized() && change != null) {
			ObserverFactory factory = getGame().getFactory(FactoryType.Factory.OBSERVERS);
			factory.getObservers().forEach(observer -> observer.next(this, change));
		}
	}

	public DiceRoller getDiceRoller() {
		return fDiceRoller;
	}

	public ReRollService getReRollService() {
		return fReRollService;
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
		return stepExecutor.getCurrentStep();
	}

	public void handleCommand(ReceivedCommand pReceivedCommand) {
		if (pReceivedCommand == null) {
			return;
		}
		stepExecutor.handleCommand(pReceivedCommand);
	}

	public void startNextStep() {
		stepExecutor.startNextStep();
	}

	public void pushCurrentStepOnStack() {
		stepExecutor.pushCurrentStepOnStack();
	}

    /**
     * Skips forward to the step with the provided label.
     * If the label does not exist on the stack, do nothing.
     *
     * @param pGotoLabel Label to skip forward to
     */
	public void cleanupStepStack(String pGotoLabel) {
		stepExecutor.cleanupStepStack(pGotoLabel);
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

	public PrayerState getPrayerState() {
		return prayerState;
	}

	public Weather replaceWeather(Weather weather) {
		if (activeEffects.getOldWeather() == null) {
			activeEffects.setOldWeather(getGame().getFieldModel().getWeather());
		}
		activeEffects.setSkipRestoreWeather(true);
		getGame().getFieldModel().setWeather(weather);
		return activeEffects.getOldWeather();
	}

	public void restoreWeather(boolean endOfDrive) {
		if (endOfDrive) {
			if (activeEffects.getOldWeather() != null) {
				getGame().getFieldModel().setWeather(activeEffects.getOldWeather());
			}
			activeEffects.setSkipRestoreWeather(false);
			activeEffects.setOldWeather(null);
		} else if (activeEffects.isSkipRestoreWeather()) {
			activeEffects.setSkipRestoreWeather(false);
		} else if (activeEffects.getOldWeather() != null) {
			getGame().getFieldModel().setWeather(activeEffects.getOldWeather());
			activeEffects.setOldWeather(null);
		}
	}

	public void setTeamIdsAdditionalAssist(Set<String> teamIdsAdditionalAssist) {
		activeEffects.setTeamIdsAdditionalAssist(teamIdsAdditionalAssist);
	}

	public int getAdditionalAssist(String teamId) {
		return (int) activeEffects.getTeamIdsAdditionalAssist().stream().filter(val -> val.equals(teamId)).count();
	}

	public void removeAdditionalAssist(String teamId) {
		activeEffects.removeAdditionalAssist(teamId);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isStalling() {
		return activeEffects.isStalling();
	}

	public void stallingDetected() {
		activeEffects.setStalling(true);
	}

	public void resetStalling() {
		activeEffects.setStalling(false);
	}

	public void resetShadowers() {
		activeEffects.clearShadowers();
	}

	public void addShadower(String playerId) {
		activeEffects.addShadower(playerId);
	}

	public int shadowingCount(String playerId) {
		return (int) activeEffects.getShadowers().stream().filter(id -> id.equals(playerId)).count();
	}

	public void addLeader(Player<?> leader) {
		activeEffects.addLeader(leader.getId());
	}

	public boolean hasLeader(Team team) {
		return Arrays.stream(team.getPlayers())
			.filter(player -> {
				FieldCoordinate playerCoordinate = getGame().getFieldModel().getPlayerCoordinate(player);
				return playerCoordinate != null && !playerCoordinate.isBoxCoordinate();
			})
			.anyMatch(player -> activeEffects.getLeaders().contains(player.getId()));
	}

	public void resetLeaders() {
		activeEffects.clearLeaders();
	}

	public CarriedPlayer getCarriedPlayer() {
		return activeEffects.getCarriedPlayer();
	}

	public void setCarriedPlayer(String carriedPlayerId, PlayerState oldCarriedPlayerState,
		FieldCoordinate oldCarriedPlayerCoordinate, boolean carriedPlayerHasBall, FieldCoordinate carrierCoordinate) {
		activeEffects.setCarriedPlayer(carriedPlayerId, oldCarriedPlayerState, oldCarriedPlayerCoordinate, carriedPlayerHasBall,
			carrierCoordinate);
	}

	public void clearCarriedPlayer() {
		activeEffects.clearCarriedPlayer();
	}

// JSON serialization

	public JsonObject toJsonValue() {
		return toJsonValue(true, 0);
	}

	public JsonObject toJsonValue(boolean includeLog, int logLimit) {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.GAME_STATUS.addTo(jsonObject, fStatus);
		IServerJsonOption.STEP_STACK.addTo(jsonObject, fStepStack.toJsonValue());
		if (includeLog) {
			if (logLimit > 0) {
				GameLog tmpLog = new GameLog(this);
				List<ServerCommand> message = Arrays.asList(fGameLog.getServerCommands());
				if (!message.isEmpty()) {
					int startIndex = Math.max(0, message.size() - logLimit);
					message.subList(startIndex, message.size()).forEach(tmpLog::add);
				}
				IServerJsonOption.GAME_LOG.addTo(jsonObject, tmpLog.toJsonValue());
			} else {
				IServerJsonOption.GAME_LOG.addTo(jsonObject, fGameLog.toJsonValue());
			}
		}
		IStep currentStep = stepExecutor.getCurrentStep();
		if (currentStep != null) {
			IServerJsonOption.CURRENT_STEP.addTo(jsonObject, currentStep.toJsonValue());
		}
		if (fGame != null) {
			IServerJsonOption.GAME.addTo(jsonObject, fGame.toJsonValue());
		}
		IServerJsonOption.PLAYER_IDS.addTo(jsonObject, zappedPlayerIds);
		IServerJsonOption.SWARMING_PLAYER_ACTUAL.addTo(jsonObject, kickingSwarmers);
		if (passState != null) {
			IServerJsonOption.PASS_STATE.addTo(jsonObject, passState.toJsonValue());
		}

		if (blitzTurnState != null) {
			IServerJsonOption.BLITZ_TURN_STATE.addTo(jsonObject, blitzTurnState.toJsonValue());
		}

		if (prayerState != null) {
			IServerJsonOption.PRAYER_STATE.addTo(jsonObject, prayerState.toJsonValue());
		}

		IServerJsonOption.ACTIVE_EFFECTS.addTo(jsonObject, activeEffects.toJsonValue());

		return jsonObject;
	}

	public GameState initFrom(IFactorySource emptySource, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);

		// Preinitialize the game so we can get the correct factories later.
		setGame(null);
		JsonObject gameObject = IServerJsonOption.GAME.getFrom(emptySource, jsonObject);
		stepExecutor.setCurrentStep(null);
		IFactorySource source = null;
		if (gameObject != null) {
			Game newGame = new Game(getServer().getFactorySource(), getServer().getFactoryManager());
			newGame.initFrom(emptySource, gameObject);
			source = newGame.getRules();
			setGame(newGame);
			initRulesDependentMembers();
			JsonObject currentStepObject = IServerJsonOption.CURRENT_STEP.getFrom(source, jsonObject);
			if (currentStepObject != null) {
				stepExecutor.setCurrentStep(stepFactory.forJsonValue(source, currentStepObject));
			}
		}

		if (source == null) {
			source = emptySource;
		}

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
		String[] ids = IServerJsonOption.PLAYER_IDS.getFrom(source, jsonObject);
		if (ids != null) {
			zappedPlayerIds.addAll(Arrays.asList(ids));
		}

		kickingSwarmers = IServerJsonOption.SWARMING_PLAYER_ACTUAL.getFrom(source, jsonObject);

		JsonObject passStateObject = IServerJsonOption.PASS_STATE.getFrom(source, jsonObject);
		if (passStateObject != null) {
			passState = new PassState().initFrom(source, passStateObject);
		}

		JsonObject blitzTurnStateObject = IServerJsonOption.BLITZ_TURN_STATE.getFrom(source, jsonObject);
		if (blitzTurnStateObject != null) {
			blitzTurnState = new BlitzTurnState().initFrom(source, blitzTurnStateObject);
		}

		JsonObject prayerStateObject = IServerJsonOption.PRAYER_STATE.getFrom(source, jsonObject);
		if (prayerStateObject != null) {
			prayerState = new PrayerState().initFrom(source, prayerStateObject);
		}

		if (IServerJsonOption.ACTIVE_EFFECTS.isDefinedIn(jsonObject)) {
			activeEffects =
				new ActiveEffects().initFrom(source, IServerJsonOption.ACTIVE_EFFECTS.getFrom(source, jsonObject));
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

		String modifiersString =
			modifiers.stream().map(modifier -> modifier.getClass().getName()).collect(Collectors.joining(", "));
		getServer().getDebugLog().log(IServerLogLevel.DEBUG, getGame().getId(),
			"Execute step hook: Sorted modifiers for step " + step.getName().toUpperCase() + " are: " + modifiersString);

		for (StepModifier<? extends IStep, ?> modifier : modifiers) {
			boolean stopProcessing = modifier.handleExecuteStep(step, state);
			if (stopProcessing) {
				getServer().getDebugLog().log(IServerLogLevel.DEBUG, getGame().getId(),
					"Execute step hook: Modifier " + modifier.getClass().getName().toUpperCase() +
						" stopped processing for step " + step.getName());
				return true;
			}
		}
		return false;
	}

	public void updatePlayerMarkings() {

		SessionManager sessionManager = getServer().getSessionManager();

		for (Session session : sessionManager.getSessionsForGameId(getId())) {
			AutoMarkingConfig config = sessionManager.getAutoMarking(session);
			if (config != null) {
				getServer().getCommunication().handleCommand(
					new ReceivedCommand(
						new InternalServerCommandApplyAutomatedPlayerMarkings(config, getId()), session));
			}
		}

	}
}
