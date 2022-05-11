package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryManager;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogParameterFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.model.change.ModelChangeId;
import com.fumbbl.ffb.model.change.ModelChangeObservable;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.modifiers.ModifierAggregator;
import com.fumbbl.ffb.util.DateTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilActingPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Kalimar
 */
public class Game extends ModelChangeObservable implements IJsonSerializable {

	private long fId;
	private Date fScheduled;
	private Date fStarted;
	private Date fFinished;
	private int fHalf;
	private TurnMode fTurnMode;
	private TurnMode fLastTurnMode;
	private FieldCoordinate fPassCoordinate;
	private boolean fHomePlaying;
	private boolean fHomeFirstOffense;
	private boolean fSetupOffense;
	private boolean fWaitingForOpponent;
	private String fDefenderId;
	private PlayerAction fDefenderAction;
	private String fThrowerId;
	private PlayerAction fThrowerAction;
	private long fTurnTime; // no notification for observers
	private long fGameTime; // no notification for observers
	private boolean fTimeoutPossible;
	private boolean fTimeoutEnforced;
	private boolean fConcessionPossible;
	private boolean fTesting;
	private boolean fAdminMode;
	private boolean concededLegally;

	private IDialogParameter fDialogParameter;

	private FieldModel fFieldModel;
	private Team fTeamHome;
	private Team fTeamAway;
	private final TurnData fTurnDataHome;
	private final TurnData fTurnDataAway;
	private ActingPlayer fActingPlayer;
	private GameResult fGameResult;
	private final GameOptions fOptions;
	private GameRules rules;
	private final FactoryManager factoryManager;
	private final IFactorySource applicationSource;
	private ModifierAggregator modifierAggregator;
	private TeamState teamState = TeamState.FULL;

	public Game(IFactorySource applicationSource, FactoryManager manager) {
		this.applicationSource = applicationSource;
		factoryManager = manager;
		setFieldModel(new FieldModel(this));

		fTurnDataHome = new TurnData(this, true);
		fTurnDataAway = new TurnData(this, false);

		fActingPlayer = new ActingPlayer(this);

		fGameResult = new GameResult(this);

		fHomePlaying = true;

		setTeamHome(new Team(applicationSource));
		setTeamAway(new Team(applicationSource));

		fOptions = new GameOptions(this);
		rules = new GameRules(applicationSource, manager);
	}

	public void setId(long pId) {
		if (pId == fId) {
			return;
		}
		fId = pId;
		notifyObservers(ModelChangeId.GAME_SET_ID, null, fId);
		if (fTeamHome != null) {
			fTeamHome.setCurrentGameId(fId);
		}
		if (fTeamAway != null) {
			fTeamAway.setCurrentGameId(fId);
		}
	}

	public long getId() {
		return fId;
	}

	public void initializeRules() {
		modifierAggregator = new ModifierAggregator();
		rules.initialize(this);
		modifierAggregator.init(this);
	}

	public IFactorySource getApplicationSource() {
		return applicationSource;
	}

	public ModifierAggregator getModifierAggregator() {
		return modifierAggregator;
	}

	public GameRules getRules() {
		return rules;
	}

	public void setRules(GameRules rules) {
		this.rules = rules;
	}

	public GameResult getGameResult() {
		return fGameResult;
	}

	public TurnData getTurnDataHome() {
		return fTurnDataHome;
	}

	public TurnData getTurnDataAway() {
		return fTurnDataAway;
	}

	public int getHalf() {
		return fHalf;
	}

	public void setHalf(int pHalf) {
		if (pHalf == fHalf) {
			return;
		}
		fHalf = pHalf;
		notifyObservers(ModelChangeId.GAME_SET_HALF, null, fHalf);
	}

	public TurnMode getTurnMode() {
		return fTurnMode;
	}

	public void setTurnMode(TurnMode turnMode) {
		if (turnMode == fTurnMode) {
			return;
		}
		TurnMode lastTurnMode = fTurnMode;
		fTurnMode = turnMode;
		if (lastTurnMode != null) {
			setLastTurnMode(lastTurnMode);
		}
		notifyObservers(ModelChangeId.GAME_SET_TURN_MODE, null, fTurnMode);
	}

	public TurnMode getLastTurnMode() {
		return fLastTurnMode;
	}

	public void setLastTurnMode(TurnMode lastTurnMode) {
		if (lastTurnMode == fLastTurnMode) {
			return;
		}
		fLastTurnMode = lastTurnMode;
		notifyObservers(ModelChangeId.GAME_SET_LAST_TURN_MODE, null, fLastTurnMode);
	}

	public ActingPlayer getActingPlayer() {
		return fActingPlayer;
	}

	public Team getTeamHome() {
		return fTeamHome;
	}

	public Team getTeamAway() {
		return fTeamAway;
	}

	public Team getActingTeam() {
		return fHomePlaying ? fTeamHome : fTeamAway;
	}

	public FieldModel getFieldModel() {
		return fFieldModel;
	}

	public void setFieldModel(FieldModel pFieldModel) {
		fFieldModel = pFieldModel;
	}

	public boolean isHomePlaying() {
		return fHomePlaying;
	}

	public void setHomePlaying(boolean pHomePlaying) {
		if (pHomePlaying == fHomePlaying) {
			return;
		}
		fHomePlaying = pHomePlaying;
		notifyObservers(ModelChangeId.GAME_SET_HOME_PLAYING, null, fHomePlaying);
	}

	public FieldCoordinate getPassCoordinate() {
		return fPassCoordinate;
	}

	public void setPassCoordinate(FieldCoordinate pPassCoordinate) {
		if (FieldCoordinate.equals(pPassCoordinate, fPassCoordinate)) {
			return;
		}
		fPassCoordinate = pPassCoordinate;
		notifyObservers(ModelChangeId.GAME_SET_PASS_COORDINATE, null, fPassCoordinate);
	}

	public boolean isHomeFirstOffense() {
		return fHomeFirstOffense;
	}

	public void setHomeFirstOffense(boolean pHomeFirstOffense) {
		if (pHomeFirstOffense == fHomeFirstOffense) {
			return;
		}
		fHomeFirstOffense = pHomeFirstOffense;
		notifyObservers(ModelChangeId.GAME_SET_HOME_FIRST_OFFENSE, null, fHomeFirstOffense);
	}
	
	public void startTurn() {
		setPassCoordinate(null);
		UtilActingPlayer.changeActingPlayer(this, null, null, false);
		getTurnDataHome().startTurn();
		getTurnDataAway().startTurn();
		setThrowerId(null);
		setThrowerAction(null);
		setDefenderId(null);
		setDefenderAction(null);
		setWaitingForOpponent(false);
		setTimeoutPossible(false);
		setTimeoutEnforced(false);
		setConcessionPossible(true);
	}

	public TurnData getTurnData() {
		return (isHomePlaying() ? getTurnDataHome() : getTurnDataAway());
	}

	public boolean isSetupOffense() {
		return fSetupOffense;
	}

	public void setSetupOffense(boolean pSetupOffense) {
		if (pSetupOffense == fSetupOffense) {
			return;
		}
		fSetupOffense = pSetupOffense;
		notifyObservers(ModelChangeId.GAME_SET_SETUP_OFFENSE, null, fSetupOffense);
	}

	public Team getOtherTeam(Team team) {
		return team == fTeamHome ? fTeamAway : fTeamHome;
	}

	public Team getTeamById(String pTeamId) {
		Team team = null;
		if (pTeamId != null) {
			if ((getTeamHome() != null) && pTeamId.equals(getTeamHome().getId())) {
				team = getTeamHome();
			}
			if ((getTeamAway() != null) && pTeamId.equals(getTeamAway().getId())) {
				team = getTeamAway();
			}
		}
		return team;
	}

	public Player<?> getPlayerById(String pPlayerId) {
		Player<?> player = null;
		if (getTeamHome() != null) {
			player = getTeamHome().getPlayerById(pPlayerId);
		}
		if ((player == null) && (getTeamAway() != null)) {
			player = getTeamAway().getPlayerById(pPlayerId);
		}
		return player;
	}

	public Player<?>[] getPlayers() {
		Player<?>[] playersHome = getTeamHome().getPlayers();
		List<Player<?>> allPlayers = new ArrayList<>(Arrays.asList(playersHome));
		Player<?>[] playersAway = getTeamAway().getPlayers();
		allPlayers.addAll(Arrays.asList(playersAway));
		return allPlayers.toArray(new Player[0]);
	}

	public void setTeamHome(Team pTeam) {
		fTeamHome = pTeam;
		fGameResult.getTeamResultHome().setTeam(pTeam);
		if (pTeam != null) {
			pTeam.setCurrentGameId(fId);
		}
	}

	public void setTeamAway(Team pTeam) {
		fTeamAway = pTeam;
		fGameResult.getTeamResultAway().setTeam(pTeam);
		if (pTeam != null) {
			pTeam.setCurrentGameId(fId);
		}
	}

	public Date getScheduled() {
		return fScheduled;
	}

	public void setScheduled(Date pScheduled) {
		if (DateTool.isEqual(pScheduled, fScheduled)) {
			return;
		}
		fScheduled = pScheduled;
		notifyObservers(ModelChangeId.GAME_SET_SCHEDULED, null, fScheduled);
	}

	public Date getStarted() {
		return fStarted;
	}

	public void setStarted(Date pStarted) {
		if (DateTool.isEqual(pStarted, fStarted)) {
			return;
		}
		fStarted = pStarted;
		notifyObservers(ModelChangeId.GAME_SET_STARTED, null, fStarted);
	}

	public void setDefenderId(String pDefenderId) {
		if (StringTool.isEqual(pDefenderId, fDefenderId)) {
			return;
		}
		fDefenderId = pDefenderId;
		notifyObservers(ModelChangeId.GAME_SET_DEFENDER_ID, fDefenderId, null);
	}

	public String getDefenderId() {
		return fDefenderId;
	}

	public Player<?> getDefender() {
		return getPlayerById(getDefenderId());
	}

	public void setDefenderAction(PlayerAction pDefenderAction) {
		if (pDefenderAction == fDefenderAction) {
			return;
		}
		fDefenderAction = pDefenderAction;
		notifyObservers(ModelChangeId.GAME_SET_DEFENDER_ACTION, null, fDefenderAction);
	}

	public PlayerAction getDefenderAction() {
		return fDefenderAction;
	}

	public void setThrowerId(String pThrowerId) {
		if (StringTool.isEqual(pThrowerId, fThrowerId)) {
			return;
		}
		fThrowerId = pThrowerId;
		notifyObservers(ModelChangeId.GAME_SET_THROWER_ID, fThrowerId, null);
	}

	public String getThrowerId() {
		return fThrowerId;
	}

	public Player<?> getThrower() {
		return getPlayerById(getThrowerId());
	}

	public void setThrowerAction(PlayerAction pThrowerAction) {
		if (pThrowerAction == fThrowerAction) {
			return;
		}
		fThrowerAction = pThrowerAction;
		notifyObservers(ModelChangeId.GAME_SET_THROWER_ACTION, null, fThrowerAction);
	}

	public PlayerAction getThrowerAction() {
		return fThrowerAction;
	}

	public void setWaitingForOpponent(boolean pWaitingForOpponent) {
		if (pWaitingForOpponent == fWaitingForOpponent) {
			return;
		}
		fWaitingForOpponent = pWaitingForOpponent;
		notifyObservers(ModelChangeId.GAME_SET_WAITING_FOR_OPPONENT, null, fWaitingForOpponent);
	}

	public boolean isWaitingForOpponent() {
		return fWaitingForOpponent;
	}

	public void setDialogParameter(IDialogParameter pDialogParameter) {
		if ((pDialogParameter == null) && (fDialogParameter == null)) {
			return;
		}
		fDialogParameter = pDialogParameter;
		notifyObservers(ModelChangeId.GAME_SET_DIALOG_PARAMETER, null, fDialogParameter);
	}

	public IDialogParameter getDialogParameter() {
		return fDialogParameter;
	}

	public Date getFinished() {
		return fFinished;
	}

	public void setFinished(Date pFinished) {
		if (DateTool.isEqual(pFinished, fFinished)) {
			return;
		}
		fFinished = pFinished;
		notifyObservers(ModelChangeId.GAME_SET_FINISHED, null, fFinished);
	}

	public long getGameTime() {
		return fGameTime;
	}

	public void setGameTime(long pGameTime) {
		fGameTime = pGameTime;
	}

	public long getTurnTime() {
		return fTurnTime;
	}

	public void setTurnTime(long pTurnTime) {
		fTurnTime = pTurnTime;
	}

	public boolean isTurnTimeEnabled() {
		return ((getStarted() != null) && (getFinished() == null)
				&& ((TurnMode.REGULAR == getTurnMode()) || (TurnMode.BLITZ == getTurnMode())));
	}

	public boolean isTimeoutPossible() {
		return fTimeoutPossible;
	}

	public void setTimeoutPossible(boolean pTimeout) {
		if (pTimeout == fTimeoutPossible) {
			return;
		}
		fTimeoutPossible = pTimeout;
		notifyObservers(ModelChangeId.GAME_SET_TIMEOUT_POSSIBLE, null, fTimeoutPossible);
	}

	public void setTimeoutEnforced(boolean pTimeoutEnforced) {
		if (pTimeoutEnforced == fTimeoutEnforced) {
			return;
		}
		fTimeoutEnforced = pTimeoutEnforced;
		notifyObservers(ModelChangeId.GAME_SET_TIMEOUT_ENFORCED, null, fTimeoutEnforced);
	}

	public boolean isTimeoutEnforced() {
		return fTimeoutEnforced;
	}

	public void setConcessionPossible(boolean pConcessionPossible) {
		if (pConcessionPossible == fConcessionPossible) {
			return;
		}
		fConcessionPossible = pConcessionPossible;
		notifyObservers(ModelChangeId.GAME_SET_CONCESSION_POSSIBLE, null, fConcessionPossible);
	}

	public boolean isConcessionPossible() {
		return fConcessionPossible;
	}

	public void setTesting(boolean pTesting) {
		if (pTesting == fTesting) {
			return;
		}
		fTesting = pTesting;
		notifyObservers(ModelChangeId.GAME_SET_TESTING, null, fTesting);
	}

	public boolean isTesting() {
		return fTesting;
	}

	public void setAdminMode(boolean adminMode) {
		if (adminMode == fAdminMode) {
			return;
		}
		fAdminMode = adminMode;
		notifyObservers(ModelChangeId.GAME_SET_ADMIN_MODE, null, fAdminMode);
	}

	public boolean isAdminMode() {
		return fAdminMode;
	}

	public GameOptions getOptions() {
		return fOptions;
	}

	public Team findTeam(Player<?> pPlayer) {
		if (getTeamHome().hasPlayer(pPlayer)) {
			return getTeamHome();
		}
		if (getTeamAway().hasPlayer(pPlayer)) {
			return getTeamAway();
		}
		return null;
	}

	public boolean isActive(ISkillProperty property) {
		return Arrays.stream(new TurnData[]{getTurnDataHome(), getTurnDataAway()})
			.flatMap(turnData -> Arrays.stream(turnData.getInducementSet().getActiveCards()))
			.flatMap(card -> card.globalProperties().stream())
			.anyMatch(prop -> prop.equals(property));
	}

	// change tracking

	public void notifyObservers(ModelChangeId pChangeId, String pKey, Object pValue) {
		if (pChangeId == null) {
			return;
		}
		ModelChange modelChange = new ModelChange(pChangeId, pKey, pValue);
		notifyObservers(modelChange);
	}


	public boolean isConcededLegally() {
		return concededLegally;
	}

	public void setConcededLegally(boolean concededLegally) {
		if (this.concededLegally == concededLegally) {
			return;
		}
		this.concededLegally = concededLegally;
		notifyObservers(ModelChangeId.GAME_SET_CONCEDED_LEGALLY, null, concededLegally);
	}

	public void teamsAreInflated() {
		teamState = TeamState.FULL;
	}

	public void teamsAreSkeletons() {
		teamState = TeamState.SKELETON;
	}

	// transformation

	public Game transform() {

		Game transformedGame = new Game(applicationSource, factoryManager);

		// unmodified values

		transformedGame.setId(getId());
		transformedGame.setTurnMode(getTurnMode());
		transformedGame.setLastTurnMode(getLastTurnMode());
		transformedGame.setHalf(getHalf());
		transformedGame.fActingPlayer = getActingPlayer();
		transformedGame.setScheduled(getScheduled());
		transformedGame.setStarted(getStarted());
		transformedGame.setFinished(getFinished());
		transformedGame.setSetupOffense(isSetupOffense());
		transformedGame.setWaitingForOpponent(isWaitingForOpponent());
		transformedGame.setDialogParameter(getDialogParameter());
		transformedGame.setDefenderId(getDefenderId());
		transformedGame.setDefenderAction(getDefenderAction());
		transformedGame.setTurnTime(getTurnTime());
		transformedGame.setGameTime(getGameTime());
		transformedGame.setTimeoutPossible(isTimeoutPossible());
		transformedGame.setTimeoutEnforced(isTimeoutEnforced());
		transformedGame.setTesting(isTesting());
		transformedGame.setThrowerId(getThrowerId());
		transformedGame.setThrowerAction(getThrowerAction());
		transformedGame.getOptions().init(getOptions());
		transformedGame.setRules(getRules());

		// transformed values

		transformedGame.setHomePlaying(!isHomePlaying());
		transformedGame.setHomeFirstOffense(!isHomeFirstOffense());
		transformedGame.fFieldModel = getFieldModel().transform();

		transformedGame.setTeamHome(getTeamAway());
		transformedGame.getTurnDataHome().init(getTurnDataAway());

		transformedGame.setTeamAway(getTeamHome());
		transformedGame.getTurnDataAway().init(getTurnDataHome());

		if (getPassCoordinate() != null) {
			transformedGame.setPassCoordinate(getPassCoordinate().transform());
		}

		transformedGame.fGameResult = getGameResult().transform();

		transformedGame.concededLegally = concededLegally;
		transformedGame.teamState = teamState;
		return transformedGame;

	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();

		IJsonOption.GAME_ID.addTo(jsonObject, fId);
		IJsonOption.SCHEDULED.addTo(jsonObject, fScheduled);
		IJsonOption.STARTED.addTo(jsonObject, fStarted);
		IJsonOption.FINISHED.addTo(jsonObject, fFinished);
		IJsonOption.HOME_PLAYING.addTo(jsonObject, fHomePlaying);
		IJsonOption.HALF.addTo(jsonObject, fHalf);
		IJsonOption.HOME_FIRST_OFFENSE.addTo(jsonObject, fHomeFirstOffense);
		IJsonOption.SETUP_OFFENSE.addTo(jsonObject, fSetupOffense);
		IJsonOption.WAITING_FOR_OPPONENT.addTo(jsonObject, fWaitingForOpponent);
		IJsonOption.TURN_TIME.addTo(jsonObject, fTurnTime);
		IJsonOption.GAME_TIME.addTo(jsonObject, fGameTime);
		IJsonOption.TIMEOUT_POSSIBLE.addTo(jsonObject, fTimeoutPossible);
		IJsonOption.TIMEOUT_ENFORCED.addTo(jsonObject, fTimeoutEnforced);
		IJsonOption.CONCESSION_POSSIBLE.addTo(jsonObject, fConcessionPossible);
		IJsonOption.TESTING.addTo(jsonObject, fTesting);
		IJsonOption.TURN_MODE.addTo(jsonObject, fTurnMode);
		IJsonOption.LAST_TURN_MODE.addTo(jsonObject, fLastTurnMode);
		IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
		IJsonOption.DEFENDER_ACTION.addTo(jsonObject, fDefenderAction);
		IJsonOption.PASS_COORDINATE.addTo(jsonObject, fPassCoordinate);
		IJsonOption.THROWER_ID.addTo(jsonObject, fThrowerId);
		IJsonOption.THROWER_ACTION.addTo(jsonObject, fThrowerAction);

		IJsonOption.TEAM_STATE.addTo(jsonObject, teamState.name());
		IJsonOption.TEAM_AWAY.addTo(jsonObject, fTeamAway.toJsonValue());
		IJsonOption.TURN_DATA_AWAY.addTo(jsonObject, fTurnDataAway.toJsonValue());
		IJsonOption.TEAM_HOME.addTo(jsonObject, fTeamHome.toJsonValue());
		IJsonOption.TURN_DATA_HOME.addTo(jsonObject, fTurnDataHome.toJsonValue());
		IJsonOption.FIELD_MODEL.addTo(jsonObject, fFieldModel.toJsonValue());
		IJsonOption.ACTING_PLAYER.addTo(jsonObject, fActingPlayer.toJsonValue());
		IJsonOption.GAME_RESULT.addTo(jsonObject, fGameResult.toJsonValue());
		IJsonOption.GAME_OPTIONS.addTo(jsonObject, fOptions.toJsonValue());

		if (fDialogParameter != null) {
			IJsonOption.DIALOG_PARAMETER.addTo(jsonObject, fDialogParameter.toJsonValue());
		}
		IJsonOption.CONCEDED_LEGALLY.addTo(jsonObject, concededLegally);

		return jsonObject;

	}

	public Game initFrom(IFactorySource source, JsonValue jsonValue) {

		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);

		// We parse options first in order to get the correct context to deserialize the rest of the data
		fOptions.initFrom(source, IJsonOption.GAME_OPTIONS.getFrom(source, jsonObject));
		initializeRules();
		
		// Switch to the new source of factories.
		source = getRules();
		
		fId = IJsonOption.GAME_ID.getFrom(source, jsonObject);
		fScheduled = IJsonOption.SCHEDULED.getFrom(source, jsonObject);
		fStarted = IJsonOption.STARTED.getFrom(source, jsonObject);
		fFinished = IJsonOption.FINISHED.getFrom(source, jsonObject);
		fHomePlaying = IJsonOption.HOME_PLAYING.getFrom(source, jsonObject);
		fHalf = IJsonOption.HALF.getFrom(source, jsonObject);
		fHomeFirstOffense = IJsonOption.HOME_FIRST_OFFENSE.getFrom(source, jsonObject);
		fSetupOffense = IJsonOption.SETUP_OFFENSE.getFrom(source, jsonObject);
		fWaitingForOpponent = IJsonOption.WAITING_FOR_OPPONENT.getFrom(source, jsonObject);
		fTurnTime = IJsonOption.TURN_TIME.getFrom(source, jsonObject);
		fGameTime = IJsonOption.GAME_TIME.getFrom(source, jsonObject);
		fTimeoutPossible = IJsonOption.TIMEOUT_POSSIBLE.getFrom(source, jsonObject);
		fTimeoutEnforced = IJsonOption.TIMEOUT_ENFORCED.getFrom(source, jsonObject);
		fConcessionPossible = IJsonOption.CONCESSION_POSSIBLE.getFrom(source, jsonObject);
		fTesting = IJsonOption.TESTING.getFrom(source, jsonObject);
		fTurnMode = (TurnMode) IJsonOption.TURN_MODE.getFrom(source, jsonObject);
		fLastTurnMode = (TurnMode) IJsonOption.LAST_TURN_MODE.getFrom(source, jsonObject);
		fDefenderId = IJsonOption.DEFENDER_ID.getFrom(source, jsonObject);
		fDefenderAction = (PlayerAction) IJsonOption.DEFENDER_ACTION.getFrom(source, jsonObject);
		fPassCoordinate = IJsonOption.PASS_COORDINATE.getFrom(source, jsonObject);
		fThrowerId = IJsonOption.THROWER_ID.getFrom(source, jsonObject);
		fThrowerAction = (PlayerAction) IJsonOption.THROWER_ACTION.getFrom(source, jsonObject);

		String teamStateString = IJsonOption.TEAM_STATE.getFrom(source, jsonObject);

		if (StringTool.isProvided(teamStateString)) {
			teamState = TeamState.valueOf(teamStateString);
		}

		if (teamState == TeamState.SKELETON) {
			fTeamAway = new TeamSkeleton(source).initFrom(source, IJsonOption.TEAM_AWAY.getFrom(source, jsonObject));
			fTeamHome = new TeamSkeleton(source).initFrom(source, IJsonOption.TEAM_HOME.getFrom(source, jsonObject));
		} else {
			fTeamAway.initFrom(source, IJsonOption.TEAM_AWAY.getFrom(source, jsonObject));
			fTeamHome.initFrom(source, IJsonOption.TEAM_HOME.getFrom(source, jsonObject));
		}
		fTurnDataAway.initFrom(source, IJsonOption.TURN_DATA_AWAY.getFrom(source, jsonObject));
		fTurnDataHome.initFrom(source, IJsonOption.TURN_DATA_HOME.getFrom(source, jsonObject));
		fFieldModel.initFrom(source, IJsonOption.FIELD_MODEL.getFrom(source, jsonObject));
		fActingPlayer.initFrom(source, IJsonOption.ACTING_PLAYER.getFrom(source, jsonObject));
		fGameResult.initFrom(source, IJsonOption.GAME_RESULT.getFrom(source, jsonObject));

		fDialogParameter = null;
		JsonObject dialogParameterObject = IJsonOption.DIALOG_PARAMETER.getFrom(source, jsonObject);
		if (dialogParameterObject != null) {
			fDialogParameter = new DialogParameterFactory().forJsonValue(source, dialogParameterObject);
		}
		Boolean concededValue = IJsonOption.CONCEDED_LEGALLY.getFrom(source, jsonObject);
		concededLegally = concededValue != null && concededValue;

		return this;

	}

	public <T extends INamedObjectFactory<?>> T getFactory(Factory factory) {
		return getRules().getFactory(factory);
	}

	private enum TeamState {
		SKELETON, FULL
	}
}
