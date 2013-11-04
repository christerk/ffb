package com.balancedbytes.games.ffb.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerActionFactory;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.TurnModeFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogIdFactory;
import com.balancedbytes.games.ffb.dialog.DialogParameterFactory;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeId;
import com.balancedbytes.games.ffb.model.change.ModelChangeObservable;
import com.balancedbytes.games.ffb.util.DateTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilActingPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class Game extends ModelChangeObservable implements IByteArraySerializable, IJsonSerializable {
  
  private long fId;
  private Date fScheduled;
  private Date fStarted;
  private Date fFinished;
  private int fHalf;
  private TurnMode fTurnMode;
  private FieldCoordinate fPassCoordinate;
  private boolean fHomePlaying;
  private boolean fHomeFirstOffense;
  private boolean fSetupOffense;
  private boolean fWaitingForOpponent;
  private String fDefenderId;
  private PlayerAction fDefenderAction;
  private String fThrowerId;
  private PlayerAction fThrowerAction;
  private long fTurnTime;
  private boolean fTimeoutPossible;
  private boolean fTimeoutEnforced;
  private boolean fConcessionPossible;
  private boolean fTesting;

  private IDialogParameter fDialogParameter;
  
  private FieldModel fFieldModel;
  private Team fTeamHome;
  private Team fTeamAway;
  private TurnData fTurnDataHome;
  private TurnData fTurnDataAway;
  private ActingPlayer fActingPlayer;
  private GameResult fGameResult;
  private GameOptions fOptions;
  
  private transient long fGameTime;  // transferred to client but not persisted

  public Game() {
    
  	setFieldModel(new FieldModel(this));
    
    fTurnDataHome = new TurnData(this, true);
    fTurnDataAway = new TurnData(this, false);

    fActingPlayer = new ActingPlayer(this);
    
    fGameResult = new GameResult(this);
    
    fHomePlaying = true;
    
    setTeamHome(new Team());
    setTeamAway(new Team());
    
    fOptions = new GameOptions(this);
    
  }
  
  public void setId(long pId) {
  	if (pId == fId) {
  		return;
  	}
    fId = pId;
    notifyObservers(ModelChangeId.GAME_SET_ID, null, fId);
  }
  
  public long getId() {
    return fId;
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

  public void setTurnMode(TurnMode pTurnMode) {
  	if (pTurnMode == fTurnMode)  {
  		return;
  	}
  	fTurnMode = pTurnMode;
  	notifyObservers(ModelChangeId.GAME_SET_TURN_MODE, null, fTurnMode);
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
  
  public Player getPlayerById(String pPlayerId) {
    Player player = null;
    if (getTeamHome() != null) {
      player = getTeamHome().getPlayerById(pPlayerId);
    }
    if ((player == null) && (getTeamAway() != null)) {
      player = getTeamAway().getPlayerById(pPlayerId);
    }
    return player;
  }

  public Player[] getPlayers() {
    List<Player> allPlayers = new ArrayList<Player>();
    Player[] playersHome = getTeamHome().getPlayers();
    for (int i = 0; i < playersHome.length; i++) {
      allPlayers.add(playersHome[i]);
    }
    Player[] playersAway = getTeamAway().getPlayers();
    for (int i = 0; i < playersAway.length; i++) {
      allPlayers.add(playersAway[i]);
    }
    return allPlayers.toArray(new Player[allPlayers.size()]);
  }

  public void setTeamHome(Team pTeamHome) {
    fTeamHome = pTeamHome;
  }

  public void setTeamAway(Team pTeamAway) {
    fTeamAway = pTeamAway;
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
  
  public Player getDefender() {
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
  
  public Player getThrower() {
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
    return ((getFinished() == null) && ((TurnMode.REGULAR == getTurnMode()) || (TurnMode.BLITZ == getTurnMode())));
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
  
  public GameOptions getOptions() {
    return fOptions;
  }
  
  public Team findTeam(Player pPlayer) {
  	if (getTeamHome().hasPlayer(pPlayer)) {
  		return getTeamHome();
  	}
  	if (getTeamAway().hasPlayer(pPlayer)) {
  		return getTeamAway();
  	}
  	return null;
  }
    
  // change tracking
  
  private void notifyObservers(ModelChangeId pChangeId, String pKey, Object pValue) {
  	if (pChangeId == null) {
  		return;
  	}
  	ModelChange modelChange = new ModelChange(pChangeId, pKey, pValue);
  	notifyObservers(modelChange);
  }

  // transformation
  
  public Game transform() {

    Game transformedGame = new Game();

    // unmodified values

    transformedGame.setId(getId());
    transformedGame.setTurnMode(getTurnMode());
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

    return transformedGame;

  }  
  
  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 2;
  }  
  
  public void addTo(ByteList pByteList) {

    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addLong(getId());
    pByteList.addLong((getScheduled() != null) ? getScheduled().getTime() : 0L);
    pByteList.addLong((getStarted() != null) ? getStarted().getTime() : 0L);
    pByteList.addLong((getFinished() != null) ? getFinished().getTime() : 0L);
    pByteList.addBoolean(isHomePlaying());
    pByteList.addByte((byte) getHalf());
    pByteList.addBoolean(isHomeFirstOffense());
    pByteList.addBoolean(isSetupOffense());
    pByteList.addBoolean(isWaitingForOpponent());
    pByteList.addLong(getTurnTime());
    pByteList.addLong(getGameTime());
    pByteList.addBoolean(isTimeoutPossible());
    pByteList.addBoolean(isTimeoutEnforced());
    pByteList.addBoolean(isConcessionPossible());
    pByteList.addBoolean(isTesting());
    
    pByteList.addByte((byte) ((getDialogParameter() != null) ? getDialogParameter().getId().getId() : 0));
    if (getDialogParameter() != null) {
      getDialogParameter().addTo(pByteList);
    }

    pByteList.addByte((byte) ((getTurnMode() != null) ? getTurnMode().getId() : 0));

    // Defender
    pByteList.addString(getDefenderId());
    pByteList.addByte((byte) ((getDefenderAction() != null) ? getDefenderAction().getId() : 0));
    
    pByteList.addFieldCoordinate(getPassCoordinate());

    // Home Team Data
    getTeamHome().addTo(pByteList);
    getTurnDataHome().addTo(pByteList);

    // Away Team Data
    getTeamAway().addTo(pByteList);
    getTurnDataAway().addTo(pByteList);

    // Field Model
    getFieldModel().addTo(pByteList);

    // Acting Player
    getActingPlayer().addTo(pByteList);
    
    // GameResult
    getGameResult().addTo(pByteList);
    
    // Options
    getOptions().addTo(pByteList);  
    
    // Thrower
    pByteList.addString(getThrowerId());
    pByteList.addByte((byte) ((getThrowerAction() != null) ? getThrowerAction().getId() : 0));

  }

  public int initFrom(ByteArray pByteArray) {

    int byteArraySerializationVersion = pByteArray.getSmallInt();
    
    setId(pByteArray.getLong());
    long scheduleTime = pByteArray.getLong();
    setScheduled((scheduleTime > 0) ? new Date(scheduleTime) : null);
    long startedTime = pByteArray.getLong();
    setStarted((startedTime > 0) ? new Date(startedTime) : null);
    long finishedTime = pByteArray.getLong();
    setFinished((finishedTime > 0) ? new Date(finishedTime) : null);
    setHomePlaying(pByteArray.getBoolean());
    setHalf(pByteArray.getByte());
    setHomeFirstOffense(pByteArray.getBoolean());
    setSetupOffense(pByteArray.getBoolean());
    setWaitingForOpponent(pByteArray.getBoolean());
    setTurnTime(pByteArray.getLong());
    setGameTime(pByteArray.getLong());
    setTimeoutPossible(pByteArray.getBoolean());
    setTimeoutEnforced(pByteArray.getBoolean());
    setConcessionPossible(pByteArray.getBoolean());
    setTesting(pByteArray.getBoolean());
    
    DialogId dialogId = new DialogIdFactory().forId(pByteArray.getByte());
    if (dialogId != null) {
      setDialogParameter(dialogId.createDialogParameter());
      getDialogParameter().initFrom(pByteArray);
    }
    
    setTurnMode(new TurnModeFactory().forId(pByteArray.getByte()));

    // Defender
    setDefenderId(pByteArray.getString());
    setDefenderAction(new PlayerActionFactory().forId(pByteArray.getByte()));
    
    setPassCoordinate(pByteArray.getFieldCoordinate());

    // Home Team Data
    getTeamHome().initFrom(pByteArray);
    getTurnDataHome().initFrom(pByteArray);

    // Away Team Data
    getTeamAway().initFrom(pByteArray);
    getTurnDataAway().initFrom(pByteArray);

    // Field Model
    if (byteArraySerializationVersion < 2) {
    	getFieldModel().deprecatedInitFrom(pByteArray, true);  // bad hack to cover up missing byteArraySerialization
    } else {
    	getFieldModel().initFrom(pByteArray);
    }

    // Acting Player
    if (byteArraySerializationVersion < 2) {
      getActingPlayer().deprecatedInitFrom(pByteArray);  // bad hack to cover up missing byteArraySerialization
      pByteArray.getString();  // duplicate defenderId, removed
    } else {
      getActingPlayer().initFrom(pByteArray);
    }
    
    // Game Result
    getGameResult().initFrom(pByteArray);
    
    // Options
    getOptions().initFrom(pByteArray);
    
    if (byteArraySerializationVersion > 1) {
    	fThrowerId = pByteArray.getString();
    	fThrowerAction = new PlayerActionFactory().forId(pByteArray.getByte());
    }
    
    return byteArraySerializationVersion;
    
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
    IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
    IJsonOption.DEFENDER_ACTION.addTo(jsonObject, fDefenderAction);
    IJsonOption.PASS_COORDINATE.addTo(jsonObject, fPassCoordinate);
    IJsonOption.THROWER_ID.addTo(jsonObject, fThrowerId);
    IJsonOption.THROWER_ACTION.addTo(jsonObject, fThrowerAction);
    
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
    
    return jsonObject;
    
  }
  
  public Game initFrom(JsonValue pJsonValue) {
    
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    
    fId = IJsonOption.GAME_ID.getFrom(jsonObject);
    fScheduled = IJsonOption.SCHEDULED.getFrom(jsonObject);
    fStarted = IJsonOption.STARTED.getFrom(jsonObject);
    fFinished = IJsonOption.FINISHED.getFrom(jsonObject);
    fHomePlaying = IJsonOption.HOME_PLAYING.getFrom(jsonObject);
    fHalf = IJsonOption.HALF.getFrom(jsonObject);
    fHomeFirstOffense = IJsonOption.HOME_FIRST_OFFENSE.getFrom(jsonObject);
    fSetupOffense = IJsonOption.SETUP_OFFENSE.getFrom(jsonObject);
    fWaitingForOpponent = IJsonOption.WAITING_FOR_OPPONENT.getFrom(jsonObject);
    fTurnTime = IJsonOption.TURN_TIME.getFrom(jsonObject);
    fGameTime = IJsonOption.GAME_TIME.getFrom(jsonObject);
    fTimeoutPossible = IJsonOption.TIMEOUT_POSSIBLE.getFrom(jsonObject);
    fTimeoutEnforced = IJsonOption.TIMEOUT_ENFORCED.getFrom(jsonObject);
    fConcessionPossible = IJsonOption.CONCESSION_POSSIBLE.getFrom(jsonObject);
    fTesting = IJsonOption.TESTING.getFrom(jsonObject);
    fTurnMode = (TurnMode) IJsonOption.TURN_MODE.getFrom(jsonObject);
    fDefenderId = IJsonOption.DEFENDER_ID.getFrom(jsonObject);
    fDefenderAction = (PlayerAction) IJsonOption.DEFENDER_ACTION.getFrom(jsonObject);
    fPassCoordinate = IJsonOption.PASS_COORDINATE.getFrom(jsonObject);
    fThrowerId = IJsonOption.THROWER_ID.getFrom(jsonObject);
    fThrowerAction = (PlayerAction) IJsonOption.THROWER_ACTION.getFrom(jsonObject);
    
    fTeamAway.initFrom(IJsonOption.TEAM_AWAY.getFrom(jsonObject));
    fTurnDataAway.initFrom(IJsonOption.TURN_DATA_AWAY.getFrom(jsonObject));
    fTeamHome.initFrom(IJsonOption.TEAM_HOME.getFrom(jsonObject));
    fTurnDataHome.initFrom(IJsonOption.TURN_DATA_HOME.getFrom(jsonObject));
    fFieldModel.initFrom(IJsonOption.FIELD_MODEL.getFrom(jsonObject));
    fActingPlayer.initFrom(IJsonOption.ACTING_PLAYER.getFrom(jsonObject));
    fGameResult.initFrom(IJsonOption.GAME_RESULT.getFrom(jsonObject));
    fOptions.initFrom(IJsonOption.GAME_OPTIONS.getFrom(jsonObject));

    fDialogParameter = null;
    JsonObject dialogParameterObject = IJsonOption.DIALOG_PARAMETER.getFrom(jsonObject);
    if (dialogParameterObject != null) {
      fDialogParameter = new DialogParameterFactory().forJsonValue(dialogParameterObject);
    }

    return this;
    
  }
  
}
