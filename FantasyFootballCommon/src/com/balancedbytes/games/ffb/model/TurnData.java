package com.balancedbytes.games.ffb.model;


import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.LeaderState;
import com.balancedbytes.games.ffb.LeaderStateFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.change.old.CommandTurnDataChange;
import com.balancedbytes.games.ffb.model.change.old.ModelChangeTurnData;
import com.balancedbytes.games.ffb.xml.IXmlWriteable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class TurnData implements IByteArraySerializable, IXmlWriteable {
  
  public static final String XML_TAG = "turnData";
  
  private static final String _XML_ATTRIBUTE_HOME = "home";
  private static final String _XML_ATTRIBUTE_BLITZ = "blitz";
  private static final String _XML_ATTRIBUTE_FOUL = "foul";
  private static final String _XML_ATTRIBUTE_RE_ROLL = "reRoll";
  private static final String _XML_ATTRIBUTE_HAND_OVER = "handOver";
  private static final String _XML_ATTRIBUTE_PASS = "pass";
  
  private static final String _XML_TAG_TURN_NR = "turnNr";
  private static final String _XML_TAG_FIRST_TURN_AFTER_KICKOFF = "firstTurnAfterKickoff";
  private static final String _XML_TAG_RE_ROLLS = "reRolls";
  private static final String _XML_TAG_APOTHECARIES = "apothecaries";
  private static final String _XML_TAG_ACTIONS_USED = "actionsUsed";
  private static final String _XML_TAG_LEADER_STATE = "leaderState";
  private static final String _XML_TAG_TURN_STARTED = "turnStarted";
  
  private boolean fHomeData;
  private int fTurnNr;
  private boolean fFirstTurnAfterKickoff;
  private boolean fTurnStarted;  // TODO: add to persistence
  private int fReRolls;
  private int fApothecaries;
  private boolean fBlitzUsed;
  private boolean fFoulUsed;
  private boolean fReRollUsed;
  private boolean fHandOverUsed;
  private boolean fPassUsed;
  private InducementSet fInducementSet;
  private LeaderState fLeaderState;
  
  private transient Game fGame;
  
  public TurnData(Game pGame, boolean pHomeData) {
    fGame = pGame;
    fHomeData = pHomeData;
    fInducementSet = new InducementSet(this);
    fLeaderState = LeaderState.NONE;
  }
  
  public InducementSet getInducementSet() {
    return fInducementSet;
  }
  
  public int getTurnNr() {
    return fTurnNr;
  }

  public void setTurnNr(int pTurnNr) {
    if (getGame().isTrackingChanges() && (pTurnNr != fTurnNr)) {
      getGame().add(new ModelChangeTurnData(CommandTurnDataChange.SET_TURN_NR, isHomeData(), (byte) pTurnNr));
    }
    fTurnNr = pTurnNr;
  }
  
  public boolean isTurnStarted() {
  	return fTurnStarted;
  }
  
  public void setTurnStarted(boolean pTurnStarted) {
  	if (getGame().isTrackingChanges() && (pTurnStarted != fTurnStarted)) {
      getGame().add(new ModelChangeTurnData(CommandTurnDataChange.SET_TURN_STARTED, isHomeData(), pTurnStarted));
    }
    fTurnStarted = pTurnStarted;
  }
  
  public boolean isFirstTurnAfterKickoff() {
    return fFirstTurnAfterKickoff;
  }
  
  public void setFirstTurnAfterKickoff(boolean pFirstTurnAfterKickoff) {
    if (getGame().isTrackingChanges() && (pFirstTurnAfterKickoff != fFirstTurnAfterKickoff)) {
      getGame().add(new ModelChangeTurnData(CommandTurnDataChange.SET_FIRST_TURN_AFTER_KICKOFF, isHomeData(), pFirstTurnAfterKickoff));
    }
    fFirstTurnAfterKickoff = pFirstTurnAfterKickoff;
  }

  public int getReRolls() {
    return fReRolls;
  }

  public void setReRolls(int pReRolls) {
    if (getGame().isTrackingChanges() && (pReRolls != fReRolls)) {
      getGame().add(new ModelChangeTurnData(CommandTurnDataChange.SET_RE_ROLLS, isHomeData(), (byte) pReRolls));
    }
    fReRolls = pReRolls;
  }

  public boolean isBlitzUsed() {
    return fBlitzUsed;
  }

  public void setBlitzUsed(boolean pBlitzUsed) {
    if (getGame().isTrackingChanges() && (pBlitzUsed != fBlitzUsed)) {
      getGame().add(new ModelChangeTurnData(CommandTurnDataChange.SET_BLITZ_USED, isHomeData(), pBlitzUsed));
    }
    fBlitzUsed = pBlitzUsed;
  }

  public boolean isFoulUsed() {
    return fFoulUsed;
  }

  public void setFoulUsed(boolean pFoulUsed) {
    if (getGame().isTrackingChanges() && (pFoulUsed != fFoulUsed)) {
      getGame().add(new ModelChangeTurnData(CommandTurnDataChange.SET_FOUL_USED, isHomeData(), pFoulUsed));
    }
    fFoulUsed = pFoulUsed;
  }

  public boolean isReRollUsed() {
    return fReRollUsed;
  }

  public void setReRollUsed(boolean pReRollUsed) {
    if (getGame().isTrackingChanges() && (pReRollUsed != fReRollUsed)) {
      getGame().add(new ModelChangeTurnData(CommandTurnDataChange.SET_RE_ROLL_USED, isHomeData(), pReRollUsed));
    }
    fReRollUsed = pReRollUsed;
  }

  public boolean isHandOverUsed() {
    return fHandOverUsed;
  }

  public void setHandOverUsed(boolean pHandOverUsed) {
    if (getGame().isTrackingChanges() && (pHandOverUsed != fHandOverUsed)) {
      getGame().add(new ModelChangeTurnData(CommandTurnDataChange.SET_HAND_OVER_USED, isHomeData(), pHandOverUsed));
    }
    fHandOverUsed = pHandOverUsed;
  }

  public boolean isPassUsed() {
    return fPassUsed;
  }

  public void setPassUsed(boolean pPassUsed) {
    if (getGame().isTrackingChanges() && (pPassUsed != fPassUsed)) {
      getGame().add(new ModelChangeTurnData(CommandTurnDataChange.SET_PASS_USED, isHomeData(), pPassUsed));
    }
    fPassUsed = pPassUsed;
  }
  
  public int getApothecaries() {
    return fApothecaries;
  }
  
  public void setApothecaries(int pApothecaries) {
    if (getGame().isTrackingChanges() && (pApothecaries != fApothecaries)) {
      getGame().add(new ModelChangeTurnData(CommandTurnDataChange.SET_APOTHECARIES, isHomeData(), (byte) pApothecaries));
    }
    fApothecaries = pApothecaries;
  }
  
  public boolean isHomeData() {
    return fHomeData;
  }
  
  public Game getGame() {
    return fGame;
  }
  
  public void setGame(Game pGame) {
    fGame = pGame;
  }
  
  public boolean isApothecaryAvailable() {
    return (getApothecaries() > 0);
  }
  
  public void useApothecary() {
    if (isApothecaryAvailable()) {
      setApothecaries(getApothecaries() - 1);
    }
  }

  public LeaderState getLeaderState() {
    return fLeaderState;
  }

  public void setLeaderState(LeaderState pLeaderState) {
    fLeaderState = pLeaderState;
  }

  public void startTurn() {
    setBlitzUsed(false);
    setHandOverUsed(false);
    setPassUsed(false);
    setFoulUsed(false);
    setReRollUsed(false);
  }
  
  public void init(TurnData pTurnData) {
    if (pTurnData != null) {
      fTurnNr = pTurnData.getTurnNr();
      fReRolls = pTurnData.getReRolls();
      fApothecaries = pTurnData.getApothecaries();
      fBlitzUsed = pTurnData.isBlitzUsed();
      fFoulUsed = pTurnData.isFoulUsed();
      fReRollUsed = pTurnData.isReRollUsed();
      fHandOverUsed = pTurnData.isHandOverUsed();
      fPassUsed = pTurnData.isPassUsed();
      fInducementSet.clear();
      fInducementSet.add(pTurnData.getInducementSet());
      fLeaderState = pTurnData.getLeaderState();
      fFirstTurnAfterKickoff = pTurnData.isFirstTurnAfterKickoff();
    }
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
	  	
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_HOME, isHomeData());
  	UtilXml.startElement(pHandler, XML_TAG, attributes);

  	UtilXml.addValueElement(pHandler, _XML_TAG_TURN_STARTED, isTurnStarted());
  	UtilXml.addValueElement(pHandler, _XML_TAG_TURN_NR, getTurnNr());
  	UtilXml.addValueElement(pHandler, _XML_TAG_FIRST_TURN_AFTER_KICKOFF, isFirstTurnAfterKickoff());
  	UtilXml.addValueElement(pHandler, _XML_TAG_RE_ROLLS, getReRolls());
  	UtilXml.addValueElement(pHandler, _XML_TAG_APOTHECARIES, getApothecaries());
  	UtilXml.addValueElement(pHandler, _XML_TAG_LEADER_STATE, ((getLeaderState() != null) ? getLeaderState().getName() : null));

  	attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_BLITZ, isBlitzUsed());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_FOUL, isFoulUsed());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_RE_ROLL, isReRollUsed());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_HAND_OVER, isHandOverUsed());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PASS, isPassUsed());
  	UtilXml.startElement(pHandler, _XML_TAG_ACTIONS_USED, attributes);
  	UtilXml.endElement(pHandler, _XML_TAG_ACTIONS_USED);

  	getInducementSet().addToXml(pHandler);

  	UtilXml.endElement(pHandler, XML_TAG);
  	
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
  	return 1;
  };
  
  public void addTo(ByteList pByteList) {
    pByteList.addBoolean(isHomeData());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addBoolean(isTurnStarted());
    pByteList.addByte((byte) getTurnNr());
    pByteList.addBoolean(isFirstTurnAfterKickoff());
    pByteList.addByte((byte) getReRolls());
    pByteList.addByte((byte) getApothecaries());
    pByteList.addBoolean(isBlitzUsed());
    pByteList.addBoolean(isFoulUsed());
    pByteList.addBoolean(isReRollUsed());
    pByteList.addBoolean(isHandOverUsed());
    pByteList.addBoolean(isPassUsed());
    pByteList.addByte((byte) ((getLeaderState() != null) ? getLeaderState().getId() : 0));
    getInducementSet().addTo(pByteList);
  }
  
  public int initFrom(ByteArray pByteArray) {
    fHomeData = pByteArray.getBoolean();
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTurnStarted = pByteArray.getBoolean();
    fTurnNr = pByteArray.getByte();
    fFirstTurnAfterKickoff = pByteArray.getBoolean();
    fReRolls = pByteArray.getByte();
    fApothecaries = pByteArray.getByte();
    fBlitzUsed = pByteArray.getBoolean();
    fFoulUsed = pByteArray.getBoolean();
    fReRollUsed = pByteArray.getBoolean();
    fHandOverUsed = pByteArray.getBoolean();
    fPassUsed = pByteArray.getBoolean();
    fLeaderState = new LeaderStateFactory().forId(pByteArray.getByte());
    getInducementSet().initFrom(pByteArray);
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.HOME_DATA.addTo(jsonObject, fHomeData);
    IJsonOption.TURN_STARTED.addTo(jsonObject, fTurnStarted);
    IJsonOption.TURN_NR.addTo(jsonObject, fTurnNr);
    IJsonOption.FIRST_TURN_AFTER_KICKOFF.addTo(jsonObject, fFirstTurnAfterKickoff);
    IJsonOption.RE_ROLLS.addTo(jsonObject, fReRolls);
    IJsonOption.APOTHECARIES.addTo(jsonObject, fApothecaries);
    IJsonOption.BLITZ_USED.addTo(jsonObject, fBlitzUsed);
    IJsonOption.FOUL_USED.addTo(jsonObject, fFoulUsed);
    IJsonOption.RE_ROLL_USED.addTo(jsonObject, fReRollUsed);
    IJsonOption.HAND_OVER_USED.addTo(jsonObject, fHandOverUsed);
    IJsonOption.PASS_USED.addTo(jsonObject, fPassUsed);
    IJsonOption.LEADER_STATE.addTo(jsonObject, fLeaderState);
    if (fInducementSet != null) {
      IJsonOption.INDUCEMENT_SET.addTo(jsonObject, fInducementSet.toJsonValue());
    }
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fHomeData = IJsonOption.HOME_DATA.getFrom(jsonObject);
    fTurnStarted = IJsonOption.TURN_STARTED.getFrom(jsonObject);
    fTurnNr = IJsonOption.TURN_NR.getFrom(jsonObject);
    fFirstTurnAfterKickoff = IJsonOption.FIRST_TURN_AFTER_KICKOFF.getFrom(jsonObject);
    fReRolls = IJsonOption.RE_ROLLS.getFrom(jsonObject);
    fApothecaries = IJsonOption.APOTHECARIES.getFrom(jsonObject);
    fBlitzUsed = IJsonOption.BLITZ_USED.getFrom(jsonObject);
    fFoulUsed = IJsonOption.FOUL_USED.getFrom(jsonObject);
    fReRollUsed = IJsonOption.RE_ROLL_USED.getFrom(jsonObject);
    fHandOverUsed = IJsonOption.HAND_OVER_USED.getFrom(jsonObject);
    fPassUsed = IJsonOption.PASS_USED.getFrom(jsonObject);
    fLeaderState = (LeaderState) IJsonOption.LEADER_STATE.getFrom(jsonObject);
    fInducementSet = new InducementSet();
    fInducementSet.initFrom(IJsonOption.INDUCEMENT_SET.getFrom(jsonObject));
  }
  
}
