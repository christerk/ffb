package com.balancedbytes.games.ffb.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.GameOptions;
import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.Team;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.util.DateTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilActingPlayer;
import com.balancedbytes.games.ffb.xml.IXmlWriteable;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class Game implements IXmlWriteable, IByteArraySerializable {
  
  public static final String XML_TAG = "game";
  
  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";
  private static final String _XML_ATTRIBUTE_ID = "id";
  private static final String _XML_ATTRIBUTE_ACTION = "action";
  
  private static final String _XML_TAG_SCHEDULED = "scheduled";
  private static final String _XML_TAG_STARTED = "started";
  private static final String _XML_TAG_FINISHED = "finished";
  private static final String _XML_TAG_HALF = "half";
  private static final String _XML_TAG_TURN_MODE = "turnMode";
  private static final String _XML_TAG_PASS_COORDINATE = "passCoordinate";
  private static final String _XML_TAG_HOME_PLAYING = "homePlaying";
  private static final String _XML_TAG_HOME_FIRST_OFFENSE = "homeFirstOffense";
  private static final String _XML_TAG_SETUP_OFFENSE = "setupOffense";
  private static final String _XML_TAG_HOME_DATA = "homeData";
  private static final String _XML_TAG_AWAY_DATA = "awayData";
  private static final String _XML_TAG_DEFENDER = "defender";
  private static final String _XML_TAG_THROWER = "thrower";
  private static final String _XML_TAG_WAITING_FOR_OPPONENT = "waitingForOpponent";
  private static final String _XML_TAG_TURN_TIME = "turnTime";
  private static final String _XML_TAG_GAME_TIME = "gameTime";
  private static final String _XML_TAG_TIMEOUT_POSSIBLE = "timeoutPossible";
  private static final String _XML_TAG_TIMEOUT_ENFORCED = "timeoutEnforced";
  private static final String _XML_TAG_CONCESSION_POSSIBLE = "timeoutPossible";
  private static final String _XML_TAG_TESTING = "testing";

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

  private IDialogParameter fDialogParameter;
  private FieldModel fFieldModel;
  private Team fTeamHome;
  private Team fTeamAway;
  private TurnData fTurnDataHome;
  private TurnData fTurnDataAway;
  private ActingPlayer fActingPlayer;
  private String fDefenderId;
  private PlayerAction fDefenderAction;
  private String fThrowerId;
  private PlayerAction fThrowerAction;
  private GameResult fGameResult;
  private long fTurnTime;
  private boolean fTimeoutPossible;
  private boolean fTimeoutEnforced;
  private boolean fConcessionPossible;
  private boolean fTesting;
  private GameOptions fOptions;
  
  private transient long fGameTime;  // transferred to client but not persisted
    
  private transient boolean fTrackingChanges;
  private transient ModelChangeList fChanges;

  public Game() {
    setFieldModel(new FieldModel(this));
    fTurnDataHome = new TurnData(this, true);
    fTurnDataAway = new TurnData(this, false);
    fActingPlayer = new ActingPlayer(this);
    fGameResult = new GameResult(this);
    fChanges = new ModelChangeList();
    fHomePlaying = true;
    setTeamHome(new Team());
    setTeamAway(new Team());
    fOptions = new GameOptions(this);
  }
  
  public void setId(long pId) {
    fId = pId;
    if (isTrackingChanges() && (pId != fId)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_ID, pId));
    }
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
    if (isTrackingChanges() && (pHalf != fHalf)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_HALF, (byte) pHalf));
    }
    fHalf = pHalf;
  }

  public TurnMode getTurnMode() {
    return fTurnMode;
  }

  public void setTurnMode(TurnMode pTurnMode) {
    if (isTrackingChanges() && (pTurnMode != fTurnMode)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_TURN_MODE, pTurnMode));
    }
    fTurnMode = pTurnMode;
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
    if (isTrackingChanges() && (pHomePlaying != fHomePlaying)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_HOME_PLAYING, pHomePlaying));
    }
    fHomePlaying = pHomePlaying;
  }

  public FieldCoordinate getPassCoordinate() {
    return fPassCoordinate;
  }

  public void setPassCoordinate(FieldCoordinate pPassCoordinate) {
    if (isTrackingChanges() && !FieldCoordinate.equals(pPassCoordinate, fPassCoordinate)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_PASS_COORDINATE, pPassCoordinate));
    }
    fPassCoordinate = pPassCoordinate;
  }

  public boolean isHomeFirstOffense() {
    return fHomeFirstOffense;
  }

  public void setHomeFirstOffense(boolean pHomeFirstOffense) {
    if (isTrackingChanges() && (pHomeFirstOffense != fHomeFirstOffense)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_HOME_FIRST_OFFENSE, pHomeFirstOffense));
    }
    fHomeFirstOffense = pHomeFirstOffense;
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
    if (isTrackingChanges() && (pSetupOffense != fSetupOffense)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_SETUP_OFFENSE, pSetupOffense));
    }
    fSetupOffense = pSetupOffense;
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
    getGameResult().getTeamResultHome().setTeam(pTeamHome);
  }

  public void setTeamAway(Team pTeamAway) {
    fTeamAway = pTeamAway;
    getGameResult().getTeamResultAway().setTeam(pTeamAway);
  }

  public Date getScheduled() {
    return fScheduled;
  }

  public void setScheduled(Date pScheduled) {
    if (isTrackingChanges() && !DateTool.isEqual(pScheduled, fScheduled)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_SCHEDULED, pScheduled));
    }
    fScheduled = pScheduled;
  }
  
  public Date getStarted() {
    return fStarted;
  }

  public void setStarted(Date pStarted) {
    if (isTrackingChanges() && !DateTool.isEqual(pStarted, fStarted)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_STARTED, pStarted));
    }
    fStarted = pStarted;
  }

  public void setDefenderId(String pDefenderId) {
    if (isTrackingChanges() && !StringTool.isEqual(pDefenderId, fDefenderId)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_DEFENDER_ID, pDefenderId));
    }
    fDefenderId = pDefenderId;
  }
  
  public String getDefenderId() {
    return fDefenderId;
  }
  
  public Player getDefender() {
  	return getPlayerById(getDefenderId());
  }
  
  public void setDefenderAction(PlayerAction pDefenderAction) {
    if (isTrackingChanges() && (pDefenderAction != fDefenderAction)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_DEFENDER_ACTION, pDefenderAction));
    }
    fDefenderAction = pDefenderAction;
  }
  
  public PlayerAction getDefenderAction() {
    return fDefenderAction;
  }
  
  public void setThrowerId(String pThrowerId) {
    if (isTrackingChanges() && !StringTool.isEqual(pThrowerId, fThrowerId)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_THROWER_ID, pThrowerId));
    }
    fThrowerId = pThrowerId;
  }
  
  public String getThrowerId() {
    return fThrowerId;
  }
  
  public Player getThrower() {
  	return getPlayerById(getThrowerId());
  }

  public void setThrowerAction(PlayerAction pThrowerAction) {
    if (isTrackingChanges() && (pThrowerAction != fThrowerAction)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_THROWER_ACTION, pThrowerAction));
    }
    fThrowerAction = pThrowerAction;
  }
  
  public PlayerAction getThrowerAction() {
    return fThrowerAction;
  }

  public void setWaitingForOpponent(boolean pWaitingForOpponent) {
    if (isTrackingChanges() && (pWaitingForOpponent != fWaitingForOpponent)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_WAITING_FOR_OPPONENT, pWaitingForOpponent));
    }
    fWaitingForOpponent = pWaitingForOpponent;
  }
  
  public boolean isWaitingForOpponent() {
    return fWaitingForOpponent;
  }
  
  public void setDialogParameter(IDialogParameter pDialogParameter) {
    if (isTrackingChanges() && !((pDialogParameter == null) && (fDialogParameter == null))) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_DIALOG_PARAMETER, pDialogParameter));
    }
    fDialogParameter = pDialogParameter;
  }
  
  public IDialogParameter getDialogParameter() {
    return fDialogParameter;
  }
  
  public Date getFinished() {
    return fFinished;
  }
  
  public void setFinished(Date pFinished) {
    if (isTrackingChanges() && !DateTool.isEqual(pFinished, fFinished)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_FINISHED, pFinished));
    }
    fFinished = pFinished;
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
    if (isTrackingChanges() && (pTimeout != fTimeoutPossible)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_TIMEOUT_POSSIBLE, pTimeout));
    }
    fTimeoutPossible = pTimeout;
  }
  
  public void setTimeoutEnforced(boolean pIllegalProcedure) {
    if (isTrackingChanges() && (pIllegalProcedure != fTimeoutEnforced)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_TIMEOUT_ENFORCED, pIllegalProcedure));
    }
    fTimeoutEnforced = pIllegalProcedure;
  }
  
  public boolean isTimeoutEnforced() {
    return fTimeoutEnforced;
  }
  
  public void setConcessionPossible(boolean pConcessionPossible) {
    if (isTrackingChanges() && (pConcessionPossible != fConcessionPossible)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_CONCESSION_POSSIBLE, pConcessionPossible));
    }
    fConcessionPossible = pConcessionPossible;
  }
  
  public boolean isConcessionPossible() {
    return fConcessionPossible;
  }
  
  public void setTesting(boolean pTesting) {
    if (isTrackingChanges() && (pTesting != fTesting)) {
      add(new ModelChangeGameAttribute(CommandGameAttributeChange.SET_TESTING, pTesting));
    }
    fTesting = pTesting;
  }
  
  public boolean isTesting() {
    return fTesting;
  }
  
  public GameOptions getOptions() {
    return fOptions;
  }
  
  // change tracking
  
  public void setTrackingChanges(boolean pTrackingChanges) {
    fTrackingChanges = pTrackingChanges;
  }
  
  public boolean isTrackingChanges() {
    return fTrackingChanges;
  }
  
  public void add(IModelChange pChange) {
    fChanges.add(pChange);
  }
  
  public ModelChangeList fetchChanges() {
    ModelChangeList changes = fChanges.copy();
    fChanges.clear();
    return changes;
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
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, getId());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    
    if (getScheduled() != null) {
      UtilXml.addValueElement(pHandler, _XML_TAG_SCHEDULED, DateTool.formatTimestamp(getScheduled()));
    }
    if (getStarted() != null) {
      UtilXml.addValueElement(pHandler, _XML_TAG_STARTED, DateTool.formatTimestamp(getStarted()));
    }
    if (getFinished() != null) {
      UtilXml.addValueElement(pHandler, _XML_TAG_FINISHED, DateTool.formatTimestamp(getFinished()));
    }
    
    UtilXml.addValueElement(pHandler, _XML_TAG_HALF, getHalf());
    UtilXml.addValueElement(pHandler, _XML_TAG_TURN_MODE, (getTurnMode() != null) ? getTurnMode().getName() : null);
    UtilXml.addValueElement(pHandler, _XML_TAG_HOME_PLAYING, isHomePlaying());
    UtilXml.addValueElement(pHandler, _XML_TAG_HOME_FIRST_OFFENSE, isHomeFirstOffense());
    UtilXml.addValueElement(pHandler, _XML_TAG_SETUP_OFFENSE, isSetupOffense());
    UtilXml.addValueElement(pHandler, _XML_TAG_WAITING_FOR_OPPONENT, isWaitingForOpponent());
    UtilXml.addValueElement(pHandler, _XML_TAG_TURN_TIME, getTurnTime());
    UtilXml.addValueElement(pHandler, _XML_TAG_GAME_TIME, getGameTime());
    UtilXml.addValueElement(pHandler, _XML_TAG_TIMEOUT_POSSIBLE, isTimeoutPossible());
    UtilXml.addValueElement(pHandler, _XML_TAG_TIMEOUT_ENFORCED, isTimeoutEnforced());
    UtilXml.addValueElement(pHandler, _XML_TAG_CONCESSION_POSSIBLE, isConcessionPossible());
    UtilXml.addValueElement(pHandler, _XML_TAG_TESTING, isTesting());

    if (getDialogParameter() != null) {
      getDialogParameter().addToXml(pHandler);
    }
    
    UtilXml.startElement(pHandler, _XML_TAG_HOME_DATA);
    
    if (getTeamHome().getRoster() != null) {
      getTeamHome().getRoster().addToXml(pHandler);
    }

    getTeamHome().addToXml(pHandler);

    getTurnDataHome().addToXml(pHandler);
    
    UtilXml.endElement(pHandler, _XML_TAG_HOME_DATA);

    UtilXml.startElement(pHandler, _XML_TAG_AWAY_DATA);
    
    if (getTeamAway().getRoster() != null) {
      getTeamAway().getRoster().addToXml(pHandler);
    }
    
    getTeamAway().addToXml(pHandler);

    getTurnDataAway().addToXml(pHandler);

    UtilXml.endElement(pHandler, _XML_TAG_AWAY_DATA);
    
    getFieldModel().addToXml(pHandler);
    
    getActingPlayer().addToXml(pHandler);
    
    attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, getDefenderId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ACTION, (getDefenderAction() != null) ? getDefenderAction().getName() : null);
    UtilXml.addEmptyElement(pHandler, _XML_TAG_DEFENDER, attributes);

    attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, getThrowerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ACTION, (getThrowerAction() != null) ? getThrowerAction().getName() : null);
    UtilXml.addEmptyElement(pHandler, _XML_TAG_THROWER, attributes);
    
    if (getPassCoordinate() != null) {
      attributes = new AttributesImpl();
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, getPassCoordinate().getX());
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, getPassCoordinate().getY());
      UtilXml.addEmptyElement(pHandler, _XML_TAG_PASS_COORDINATE, attributes);
    }
            
    getGameResult().addToXml(pHandler);
    
    getOptions().addToXml(pHandler);
    
    UtilXml.endElement(pHandler, XML_TAG);
    
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
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
    
    DialogId dialogId = DialogId.fromId(pByteArray.getByte());
    if (dialogId != null) {
      setDialogParameter(dialogId.createDialogParameter());
      getDialogParameter().initFrom(pByteArray);
    }
    
    setTurnMode(TurnMode.fromId(pByteArray.getByte()));

    // Defender
    setDefenderId(pByteArray.getString());
    setDefenderAction(PlayerAction.fromId(pByteArray.getByte()));
    
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
    	fThrowerAction = PlayerAction.fromId(pByteArray.getByte());
    }
    
    return byteArraySerializationVersion;
    
  }
  
}
