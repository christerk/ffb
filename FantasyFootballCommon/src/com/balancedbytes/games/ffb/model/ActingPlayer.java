package com.balancedbytes.games.ffb.model;

import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerActionFactory;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.change.old.CommandActingPlayerChange;
import com.balancedbytes.games.ffb.model.change.old.ModelChangeActingPlayer;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.xml.IXmlWriteable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ActingPlayer implements IXmlWriteable, IByteArraySerializable {
  
  public static final String XML_TAG = "actingPlayer";
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_PLAYER_ACTION = "playerAction";
  
  private static final String _XML_TAG_STRENGTH = "strength";
  private static final String _XML_TAG_CURRENT_MOVE = "currentMove";
  private static final String _XML_TAG_GOING_FOR_IT = "goingForIt";
  private static final String _XML_TAG_DODGING = "dodging";
  private static final String _XML_TAG_LEAPING = "leaping";
  private static final String _XML_TAG_STANDING_UP = "standingUp";
  private static final String _XML_TAG_SUFFERING_BLOOD_LUST = "sufferingBloodLust";
  private static final String _XML_TAG_SUFFERING_ANIMOSITY = "sufferingAnimosity";
  private static final String _XML_TAG_HAS_BLOCKED = "hasBlocked";
  private static final String _XML_TAG_HAS_FOULED = "hasFouled";
  private static final String _XML_TAG_HAS_PASSED = "hasPassed";
  private static final String _XML_TAG_HAS_MOVED = "hasMoved";
  private static final String _XML_TAG_HAS_FED = "hasFed";
  private static final String _XML_TAG_USED_SKILL_LIST = "usedSkillList";
  private static final String _XML_TAG_SKILL = "skill";
  
  private transient Game fGame;
  private String fPlayerId;
  private int fStrength;
  private int fCurrentMove;
  private boolean fGoingForIt;
  private boolean fDodging;
  private boolean fLeaping;
  private boolean fHasBlocked;
  private boolean fHasFouled;
  private boolean fHasPassed;
  private boolean fHasMoved;
  private boolean fHasFed;
  private PlayerAction fPlayerAction;
  private Set<Skill> fUsedSkills;
  private boolean fStandingUp;
  private boolean fSufferingBloodLust;
  private boolean fSufferingAnimosity;
  
  public ActingPlayer(Game pGame) {
    fGame = pGame;
    fUsedSkills = new HashSet<Skill>();
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public void setPlayerId(String pPlayerId) {
    fPlayerId = pPlayerId;
    fUsedSkills.clear();
    fCurrentMove = 0;
    fGoingForIt = false;
    fDodging = false;
    fHasBlocked = false;
    fHasFouled = false;
    fHasPassed = false;
    fHasMoved = false;
    fHasFed = false;
    fLeaping = false;
    fPlayerAction = null;
    fStandingUp = false;
    fSufferingBloodLust = false;
    fSufferingAnimosity = false;
    Player player = getGame().getPlayerById(getPlayerId());
    setStrength((player != null) ? UtilCards.getPlayerStrength(getGame(), player) : 0);
    if (getGame().isTrackingChanges()) {
      getGame().add(new ModelChangeActingPlayer(CommandActingPlayerChange.SET_PLAYER_ID, pPlayerId));
    }
  }
  
  public Player getPlayer() {
    return getGame().getPlayerById(getPlayerId());
  }
  
  public void setPlayer(Player pPlayer) {
    if (pPlayer != null) {
      setPlayerId(pPlayer.getId());
    } else {
      setPlayerId(null);
    }
  }
  
  public int getCurrentMove() {
    return fCurrentMove;
  }
  
  public void setCurrentMove(int pCurrentMove) {
    if (getGame().isTrackingChanges() && (pCurrentMove != fCurrentMove)) {
      getGame().add(new ModelChangeActingPlayer(CommandActingPlayerChange.SET_CURRENT_MOVE, (byte) pCurrentMove));
    }
    fCurrentMove = pCurrentMove;
  }
  
  public boolean isGoingForIt() {
    return fGoingForIt;
  }
  
  public void setGoingForIt(boolean pGoingForIt) {
    if (getGame().isTrackingChanges() && (pGoingForIt != fGoingForIt)) {
      getGame().add(new ModelChangeActingPlayer(CommandActingPlayerChange.SET_GOING_FOR_IT, pGoingForIt));
    }
    fGoingForIt = pGoingForIt;
  }
  
  public PlayerAction getPlayerAction() {
    return fPlayerAction;
  }
  
  public void setPlayerAction(PlayerAction pPlayerAction) {
    if (getGame().isTrackingChanges() && (pPlayerAction != fPlayerAction)) {
      getGame().add(new ModelChangeActingPlayer(CommandActingPlayerChange.SET_PLAYER_ACTION, pPlayerAction));
    }
    fPlayerAction = pPlayerAction;
  }
  
  public boolean isSkillUsed(Skill pSkill) {
    return fUsedSkills.contains(pSkill);
  }
  
  public void markSkillUsed(Skill pSkill) {
    if ((pSkill == null) || isSkillUsed(pSkill)) {
    	return;
    }
    fUsedSkills.add(pSkill);
    if (getGame().isTrackingChanges()) {
      getGame().add(new ModelChangeActingPlayer(CommandActingPlayerChange.MARK_SKILL_USED, pSkill));
    }
  }
  
  public String getRace() {
    if (getPlayer() != null) {
      return getPlayer().getRace();
    } else {
      return null;
    }
  }
  
  public Skill[] getUsedSkills() {
    return (Skill[]) fUsedSkills.toArray(new Skill[fUsedSkills.size()]);
  }
    
  public boolean hasBlocked() {
    return fHasBlocked;
  }
  
  public void setHasBlocked(boolean pHasBlocked) {
    if (getGame().isTrackingChanges() && (pHasBlocked != fHasBlocked)) {
      getGame().add(new ModelChangeActingPlayer(CommandActingPlayerChange.SET_HAS_BLOCKED, pHasBlocked));
    }
    fHasBlocked = pHasBlocked;
  }
  
  public boolean hasPassed() {
    return fHasPassed;
  }
  
  public void setHasPassed(boolean pHasPassed) {
    if (getGame().isTrackingChanges() && (pHasPassed != fHasPassed)) {
      getGame().add(new ModelChangeActingPlayer(CommandActingPlayerChange.SET_HAS_PASSED, pHasPassed));
    }
    fHasPassed = pHasPassed;
  }
  
  public boolean isDodging() {
    return fDodging;
  }
  
  public void setDodging(boolean pDodging) {
    if (getGame().isTrackingChanges() && (pDodging != fDodging)) {
      getGame().add(new ModelChangeActingPlayer(CommandActingPlayerChange.SET_DODGING, pDodging));
    }
    fDodging = pDodging;
  }
  
  public int getStrength() {
    return fStrength;
  }
  
  public void setStrength(int pStrength) {
    if (getGame().isTrackingChanges() && (pStrength != fStrength)) {
      getGame().add(new ModelChangeActingPlayer(CommandActingPlayerChange.SET_STRENGTH, (byte) pStrength));
    }
    fStrength = pStrength;
  }
  
  public boolean hasMoved() {
    return fHasMoved;
  }
  
  public void setHasMoved(boolean pHasMoved) {
    if (getGame().isTrackingChanges() && (pHasMoved != fHasMoved)) {
      getGame().add(new ModelChangeActingPlayer(CommandActingPlayerChange.SET_HAS_MOVED, pHasMoved));
    }
    fHasMoved = pHasMoved;
  }
  
  public boolean isLeaping() {
    return fLeaping;
  }
  
  public void setLeaping(boolean pLeaping) {
    if (getGame().isTrackingChanges() && (pLeaping != fLeaping)) {
      getGame().add(new ModelChangeActingPlayer(CommandActingPlayerChange.SET_LEAPING, pLeaping));
    }
    fLeaping = pLeaping;
  }
  
  public void setStandingUp(boolean pStandingUp) {
    if (getGame().isTrackingChanges() && (fStandingUp != pStandingUp)) {
      getGame().add(new ModelChangeActingPlayer(CommandActingPlayerChange.SET_STANDING_UP, pStandingUp));
    }
    fStandingUp = pStandingUp;
  }
  
  public boolean isStandingUp() {
    return fStandingUp;
  }
    
  public void setSufferingBloodLust(boolean pSufferingBloodLust) {
    if (getGame().isTrackingChanges() && (fSufferingBloodLust != pSufferingBloodLust)) {
      getGame().add(new ModelChangeActingPlayer(CommandActingPlayerChange.SET_SUFFERING_BLOOD_LUST, pSufferingBloodLust));
    }
    fSufferingBloodLust = pSufferingBloodLust;
  }

  public boolean isSufferingBloodLust() {
    return fSufferingBloodLust;
  }

  public void setSufferingAnimosity(boolean pSufferingAnimosity) {
    if (getGame().isTrackingChanges() && (fSufferingAnimosity != pSufferingAnimosity)) {
      getGame().add(new ModelChangeActingPlayer(CommandActingPlayerChange.SET_SUFFERING_ANIMOSITY, pSufferingAnimosity));
    }
    fSufferingAnimosity = pSufferingAnimosity;
  }

  public boolean isSufferingAnimosity() {
    return fSufferingAnimosity;
  }

  public boolean hasFed() {
    return fHasFed;
  }
  
  public void setHasFed(boolean pHasFed) {
    if (getGame().isTrackingChanges() && (pHasFed != fHasFed)) {
      getGame().add(new ModelChangeActingPlayer(CommandActingPlayerChange.SET_HAS_FED, pHasFed));
    }
    fHasFed = pHasFed;
  }
  
  public boolean hasFouled() {
    return fHasFouled;
  }
  
  public void setHasFouled(boolean pHasFouled) {
    if (getGame().isTrackingChanges() && (pHasFouled != fHasFouled)) {
      getGame().add(new ModelChangeActingPlayer(CommandActingPlayerChange.SET_HAS_FOULED, pHasFouled));
    }
    fHasFouled = pHasFouled;
  }
  
  public Game getGame() {
    return fGame;
  }
    
  public boolean hasActed() {
    return (hasMoved() || hasFouled() || hasBlocked() || hasPassed() || (fUsedSkills.size() > 0));
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    
    if (getPlayer() == null) {
      UtilXml.addEmptyElement(pHandler, XML_TAG);
      return;
    }

    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayer().getId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ACTION, (getPlayerAction() != null) ? getPlayerAction().getName() : null);
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    
    UtilXml.addValueElement(pHandler, _XML_TAG_STRENGTH, getStrength());
    UtilXml.addValueElement(pHandler, _XML_TAG_CURRENT_MOVE, getCurrentMove());
    UtilXml.addValueElement(pHandler, _XML_TAG_GOING_FOR_IT, isGoingForIt());
    UtilXml.addValueElement(pHandler, _XML_TAG_DODGING, isDodging());
    UtilXml.addValueElement(pHandler, _XML_TAG_LEAPING, isLeaping());
    UtilXml.addValueElement(pHandler, _XML_TAG_STANDING_UP, isStandingUp());
    UtilXml.addValueElement(pHandler, _XML_TAG_HAS_BLOCKED, hasBlocked());
    UtilXml.addValueElement(pHandler, _XML_TAG_HAS_FOULED, hasFouled());
    UtilXml.addValueElement(pHandler, _XML_TAG_HAS_PASSED, hasPassed());
    UtilXml.addValueElement(pHandler, _XML_TAG_HAS_MOVED, hasMoved());
    UtilXml.addValueElement(pHandler, _XML_TAG_STANDING_UP, isStandingUp());
    UtilXml.addValueElement(pHandler, _XML_TAG_SUFFERING_BLOOD_LUST, isSufferingBloodLust());
    UtilXml.addValueElement(pHandler, _XML_TAG_SUFFERING_ANIMOSITY, isSufferingAnimosity());
    UtilXml.addValueElement(pHandler, _XML_TAG_HAS_FED, hasFed());
     
    UtilXml.startElement(pHandler, _XML_TAG_USED_SKILL_LIST);
    Skill[] usedSkills = getUsedSkills();
    if (ArrayTool.isProvided(usedSkills)) {
      for (Skill skill : usedSkills) {
        UtilXml.addValueElement(pHandler, _XML_TAG_SKILL, skill.getName());
      }
    }
    UtilXml.endElement(pHandler, _XML_TAG_USED_SKILL_LIST);

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
  	
    pByteList.addString(getPlayerId());
    pByteList.addByte((byte) getCurrentMove());
    pByteList.addBoolean(isGoingForIt());
    pByteList.addBoolean(hasBlocked());
    pByteList.addBoolean(hasFouled());
    pByteList.addBoolean(hasPassed());
    pByteList.addByte((byte) ((getPlayerAction() != null) ? getPlayerAction().getId() : 0));
    pByteList.addBoolean(isStandingUp());
    pByteList.addBoolean(isSufferingBloodLust());
    pByteList.addBoolean(isSufferingAnimosity());
    
    Skill[] usedSkills = getUsedSkills();
    pByteList.addByte((byte) usedSkills.length);
    for (int i = 0; i < usedSkills.length; i++) {
      pByteList.addByte((byte) usedSkills[i].getId());
    }

    pByteList.addBoolean(hasFed());

  }
  
  public int initFrom(ByteArray pByteArray) {
    
  	int byteArraySerializationVersion = pByteArray.getSmallInt();
  	
    deprecatedInitFrom(pByteArray);

    if (byteArraySerializationVersion > 1) {
    	setHasFed(pByteArray.getBoolean());
    }

    return byteArraySerializationVersion;
    
  }
  
  // bad hack to cover up missing byteArraySerialization
  public void deprecatedInitFrom(ByteArray pByteArray) {
    setPlayerId(pByteArray.getString());
    setCurrentMove(pByteArray.getByte());
    setGoingForIt(pByteArray.getBoolean());
    setHasBlocked(pByteArray.getBoolean());
    setHasFouled(pByteArray.getBoolean());
    setHasPassed(pByteArray.getBoolean());
    setPlayerAction(new PlayerActionFactory().forId(pByteArray.getByte()));
    setStandingUp(pByteArray.getBoolean());
    setSufferingBloodLust(pByteArray.getBoolean());
    setSufferingAnimosity(pByteArray.getBoolean());
    
    int nrOfUsedSkills = pByteArray.getByte();
    SkillFactory skillFactory = new SkillFactory();
    for (int i = 0; i < nrOfUsedSkills; i++) {
      Skill usedSkill = skillFactory.forId(pByteArray.getByte());
      markSkillUsed(usedSkill);
    }
  }

  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.CURRENT_MOVE.addTo(jsonObject, fCurrentMove);
    IJsonOption.GOING_FOR_IT.addTo(jsonObject, fGoingForIt);
    IJsonOption.HAS_BLOCKED.addTo(jsonObject, fHasBlocked);
    IJsonOption.HAS_FED.addTo(jsonObject, fHasFed);
    IJsonOption.HAS_FOULED.addTo(jsonObject, fHasFouled);
    IJsonOption.HAS_MOVED.addTo(jsonObject, fHasMoved);
    IJsonOption.HAS_PASSED.addTo(jsonObject, fHasPassed);
    IJsonOption.PLAYER_ACTION.addTo(jsonObject, fPlayerAction);
    IJsonOption.STANDING_UP.addTo(jsonObject, fStandingUp);
    IJsonOption.SUFFERING_ANIMOSITY.addTo(jsonObject, fSufferingAnimosity);
    IJsonOption.SUFFERING_BLOODLUST.addTo(jsonObject, fSufferingBloodLust);
    JsonArray usedSkillsArray = new JsonArray();
    for (Skill skill : getUsedSkills()) {
      usedSkillsArray.add(UtilJson.toJsonValue(skill));
    }
    IJsonOption.USED_SKILLS.addTo(jsonObject, usedSkillsArray);
    return jsonObject;
  }
  
  public ActingPlayer initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fCurrentMove = IJsonOption.CURRENT_MOVE.getFrom(jsonObject);
    fGoingForIt = IJsonOption.GOING_FOR_IT.getFrom(jsonObject);
    fHasBlocked = IJsonOption.HAS_BLOCKED.getFrom(jsonObject);
    fHasFed = IJsonOption.HAS_FED.getFrom(jsonObject);
    fHasFouled = IJsonOption.HAS_FOULED.getFrom(jsonObject);
    fHasMoved = IJsonOption.HAS_MOVED.getFrom(jsonObject);
    fHasPassed = IJsonOption.HAS_PASSED.getFrom(jsonObject);
    fPlayerAction = (PlayerAction) IJsonOption.PLAYER_ACTION.getFrom(jsonObject);
    fStandingUp = IJsonOption.STANDING_UP.getFrom(jsonObject);
    fSufferingAnimosity = IJsonOption.SUFFERING_ANIMOSITY.getFrom(jsonObject);
    fSufferingBloodLust = IJsonOption.SUFFERING_BLOODLUST.getFrom(jsonObject);
    JsonArray usedSkillsArray = IJsonOption.USED_SKILLS.getFrom(jsonObject);
    fUsedSkills.clear();
    if (usedSkillsArray != null) {
      for (int i = 0; i < usedSkillsArray.size(); i++) {
        fUsedSkills.add((Skill) UtilJson.toEnumWithName(new SkillFactory(), usedSkillsArray.get(i))); 
      }
    }
    return this;
  }
      
}
