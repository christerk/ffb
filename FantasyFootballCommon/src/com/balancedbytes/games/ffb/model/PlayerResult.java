package com.balancedbytes.games.ffb.model;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.SeriousInjuryFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class PlayerResult implements IByteArraySerializable, IXmlSerializable {
  
  public static final String XML_TAG = "playerResult";

  private static final String XML_ATTRIBUTE_PLAYER_ID = "playerId";

  private static final String _XML_TAG_STAR_PLAYER_POINTS = "starPlayerPoints";
  private static final String _XML_ATTRIBUTE_CURRENT = "current";
  private static final String _XML_ATTRIBUTE_EARNED = "earned";

  private static final String _XML_TAG_COMPLETIONS = "completions";
  private static final String _XML_TAG_TOUCHDOWNS = "touchdowns";
  private static final String _XML_TAG_INTERCEPTIONS = "interceptions";
  private static final String _XML_TAG_CASUALTIES = "casualties";
  private static final String _XML_TAG_PLAYER_AWARDS = "playerAwards";

  private static final String _XML_TAG_STATISTICS = "statistics";
  private static final String _XML_TAG_BLOCKS = "blocks";
  private static final String _XML_TAG_FOULS = "fouls";
  private static final String _XML_TAG_RUSHING = "rushing";
  private static final String _XML_TAG_PASSING = "passing";
  private static final String _XML_TAG_TURNS_PLAYED = "turnsPlayed";
  
  private static final String _XML_TAG_USED_SECRET_WEAPON = "usedSecretWeapon";
  private static final String _XML_TAG_DEFECTING = "defecting";
  
  private static final String _XML_TAG_INJURY = "injury";

  private static final String _XML_TAG_SEND_TO_BOX = "sendToBox";
  private static final String _XML_ATTRIBUTE_REASON = "reason";
  private static final String _XML_ATTRIBUTE_TURN = "turn";
  private static final String _XML_ATTRIBUTE_HALF = "half";
  private static final String _XML_ATTRIBUTE_BY_PLAYER_ID = "byPlayerId";
  
  private TeamResult fTeamResult;
  private Player fPlayer;
  
  private int fCompletions;
  private int fTouchdowns;
  private int fInterceptions;
  private int fCasualties;
  private int fPlayerAwards;
  private int fBlocks;
  private int fFouls;
  private int fRushing;
  private int fPassing;
  private int fTurnsPlayed;
  private int fCurrentSpps;
  
  private boolean fDefecting;
  private SeriousInjury fSeriousInjury;
  private SeriousInjury fSeriousInjuryDecay;
  private SendToBoxReason fSendToBoxReason;
  private int fSendToBoxTurn;
  private int fSendToBoxHalf;
  private String fSendToBoxByPlayerId;
  private boolean fHasUsedSecretWeapon;
  
  public PlayerResult(TeamResult pTeamResult) {
    this(pTeamResult, null);
  }
  
  public PlayerResult(TeamResult pTeamResult, Player pPlayer) {
    fTeamResult = pTeamResult;
    fPlayer = pPlayer;
  }
  
  public TeamResult getTeamResult() {
    return fTeamResult;
  }
  
  public Player getPlayer() {
    return fPlayer;
  }
  
  public String getPlayerId() {
    return ((getPlayer() != null) ? getPlayer().getId() : null);
  }

  public SeriousInjury getSeriousInjury() {
    return fSeriousInjury;
  }

  public void setSeriousInjury(SeriousInjury pSeriousInjury) {
    if (getGame().isTrackingChanges() && (pSeriousInjury != fSeriousInjury)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_SERIOUS_INJURY, getPlayerId(), pSeriousInjury));
    }
    fSeriousInjury = pSeriousInjury;
  }
  
  public SeriousInjury getSeriousInjuryDecay() {
    return fSeriousInjuryDecay;
  }

  public void setSeriousInjuryDecay(SeriousInjury pSeriousInjuryDecay) {
    if (getGame().isTrackingChanges() && (pSeriousInjuryDecay != fSeriousInjuryDecay)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_SERIOUS_INJURY_DECAY, getPlayerId(), pSeriousInjuryDecay));
    }
    fSeriousInjuryDecay = pSeriousInjuryDecay;
  }

  public SendToBoxReason getSendToBoxReason() {
    return fSendToBoxReason;
  }
  
  public void setSendToBoxReason(SendToBoxReason pSendToBoxReason) {
    if (getGame().isTrackingChanges() && (pSendToBoxReason != fSendToBoxReason)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_SEND_TO_BOX_REASON, getPlayerId(), pSendToBoxReason));
    }
    fSendToBoxReason = pSendToBoxReason;
  }
  
  public int getSendToBoxTurn() {
    return fSendToBoxTurn;
  }
  
  public void setSendToBoxTurn(int pSendToBoxTurn) {
    if (getGame().isTrackingChanges() && (pSendToBoxTurn != fSendToBoxTurn)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_SEND_TO_BOX_TURN, getPlayerId(), (byte) pSendToBoxTurn));
    }
    fSendToBoxTurn = pSendToBoxTurn;
  }

  public int getSendToBoxHalf() {
    return fSendToBoxHalf;
  }
  
  public void setSendToBoxHalf(int pSendToBoxHalf) {
    if (getGame().isTrackingChanges() && (pSendToBoxHalf != fSendToBoxHalf)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_SEND_TO_BOX_HALF, getPlayerId(), (byte) pSendToBoxHalf));
    }
    fSendToBoxHalf = pSendToBoxHalf;
  }

  public int getTurnsPlayed() {
    return fTurnsPlayed;
  }
  
  public void setSendToBoxByPlayerId(String pSendToBoxByPlayerId) {
    if (getGame().isTrackingChanges() && !StringTool.isEqual(pSendToBoxByPlayerId, fSendToBoxByPlayerId)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_SEND_TO_BOX_BY_PLAYER_ID, getPlayerId(), pSendToBoxByPlayerId));
    }
    fSendToBoxByPlayerId = pSendToBoxByPlayerId;
  }
  
  public String getSendToBoxByPlayerId() {
    return fSendToBoxByPlayerId;
  }
  
  public void setTurnsPlayed(int pTurnsPlayed) {
    if (getGame().isTrackingChanges() && (fTurnsPlayed != pTurnsPlayed)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_TURNS_PLAYED, getPlayerId(), (byte) pTurnsPlayed));
    }
    fTurnsPlayed = pTurnsPlayed;
  }

  public void setHasUsedSecretWeapon(boolean pHasUsedSecretWeapon) {
    if (getGame().isTrackingChanges() && (pHasUsedSecretWeapon != fHasUsedSecretWeapon)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_HAS_USED_SECRET_WEAPON, getPlayerId(), pHasUsedSecretWeapon));
    }
    fHasUsedSecretWeapon = pHasUsedSecretWeapon;
  }
  
  public boolean hasUsedSecretWeapon() {
    return fHasUsedSecretWeapon;
  }

  public int getCompletions() {
    return fCompletions;
  }
  
  public void setCompletions(int pCompletions) {
    if (getGame().isTrackingChanges() && (fCompletions != pCompletions)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_COMPLETIONS, getPlayerId(), (byte) pCompletions));
    }
    fCompletions = pCompletions;
  }

  public int getTouchdowns() {
    return fTouchdowns;
  }
  
  public void setTouchdowns(int pTouchdowns) {
    if (getGame().isTrackingChanges() && (pTouchdowns != fTouchdowns)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_TOUCHDOWNS, getPlayerId(), (byte) pTouchdowns));
    }
    fTouchdowns = pTouchdowns;
  }

  public int getInterceptions() {
    return fInterceptions;
  }

  public void setInterceptions(int pInterceptions) {
    if (getGame().isTrackingChanges() && (pInterceptions != fInterceptions)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_INTERCEPTIONS, getPlayerId(), (byte) pInterceptions));
    }
    fInterceptions = pInterceptions;
  }
  
  public int getCasualties() {
    return fCasualties;
  }

  public void setCasualties(int pCasualties) {
    if (getGame().isTrackingChanges() && (pCasualties != fCasualties)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_CASUALTIES, getPlayerId(), (byte) pCasualties));
    }
    fCasualties = pCasualties;
  }
  
  public int getPlayerAwards() {
    return fPlayerAwards;
  }

  public void setPlayerAwards(int pPlayerAwards) {
    if (getGame().isTrackingChanges() && (pPlayerAwards != fPlayerAwards)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_PLAYER_AWARDS, getPlayerId(), (byte) pPlayerAwards));
    }
    fPlayerAwards = pPlayerAwards;
  }
  
  public int getBlocks() {
    return fBlocks;
  }
  
  public void setBlocks(int pBlocks) {
    if (getGame().isTrackingChanges() && (pBlocks != fBlocks)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_BLOCKS, getPlayerId(), (byte) pBlocks));
    }
    fBlocks = pBlocks;
  }

  public int getFouls() {
    return fFouls;
  }

  public void setFouls(int pFouls) {
    if (getGame().isTrackingChanges() && (pFouls != fFouls)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_FOULS, getPlayerId(), (byte) pFouls));
    }
    fFouls = pFouls;
  }
  
  public int getRushing() {
    return fRushing;
  }

  public void setRushing(int pRushing) {
    if (getGame().isTrackingChanges() && (pRushing != fRushing)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_RUSHING, getPlayerId(), pRushing));
    }
    fRushing = pRushing;
  }
  
  public int getPassing() {
    return fPassing;
  }
  
  public void setPassing(int pPassing) {
    if (getGame().isTrackingChanges() && (pPassing != fPassing)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_PASSING, getPlayerId(), pPassing));
    }
    fPassing = pPassing;
  }

  public int getCurrentSpps() {
    return fCurrentSpps;
  }
  
  public void setCurrentSpps(int pOldSpps) {
    if (getGame().isTrackingChanges() && (pOldSpps != fCurrentSpps)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_CURRENT_SPPS, getPlayerId(), pOldSpps));
    }
    fCurrentSpps = pOldSpps;
  }
  
  public boolean isDefecting() {
    return fDefecting;
  }
  
  public void setDefecting(boolean pDefecting) {
    if (getGame().isTrackingChanges() && (pDefecting != fDefecting)) {
      getGame().add(new ModelChangePlayerResult(CommandPlayerResultChange.SET_DEFECTING, getPlayerId(), pDefecting));
    }
    fDefecting = pDefecting;
  }
  
  public int totalEarnedSpps() {
    return ((getPlayerAwards() * 5) + (getTouchdowns() * 3) + (getCasualties() * 2) + (getInterceptions() * 2) + getCompletions());
  }
  
  public Game getGame() {
    return getTeamResult().getGame();
  }
  
  public void init(PlayerResult pPlayerResult) {
    if (pPlayerResult != null) {
      fPlayer = pPlayerResult.getPlayer();
      fCompletions = pPlayerResult.getCompletions();
      fTouchdowns = pPlayerResult.getTouchdowns();
      fInterceptions = pPlayerResult.getInterceptions();
      fCasualties = pPlayerResult.getCasualties();
      fPlayerAwards = pPlayerResult.getPlayerAwards();
      fBlocks = pPlayerResult.getBlocks();
      fFouls = pPlayerResult.getFouls();
      fRushing = pPlayerResult.getRushing();
      fPassing = pPlayerResult.getPassing();
      fTurnsPlayed = pPlayerResult.getTurnsPlayed();
      fCurrentSpps = pPlayerResult.getCurrentSpps();
      fDefecting = pPlayerResult.isDefecting();
      fSeriousInjury = pPlayerResult.getSeriousInjury();
      fSendToBoxReason = pPlayerResult.getSendToBoxReason();
      fSendToBoxTurn = pPlayerResult.getSendToBoxTurn();
      fSendToBoxHalf = pPlayerResult.getSendToBoxHalf();
      fSendToBoxByPlayerId = pPlayerResult.getSendToBoxByPlayerId();
      fHasUsedSecretWeapon = pPlayerResult.hasUsedSecretWeapon();
    }
  }
  
  // XML serialization

  public void addToXml(TransformerHandler pHandler) {

    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.startElement(pHandler, XML_TAG, attributes);

    UtilXml.addValueElement(pHandler,_XML_TAG_USED_SECRET_WEAPON, hasUsedSecretWeapon());
    UtilXml.addValueElement(pHandler, _XML_TAG_DEFECTING, isDefecting());
    
    if (totalEarnedSpps() > 0) {

      attributes = new AttributesImpl();
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CURRENT, getCurrentSpps());
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_EARNED, totalEarnedSpps());
      UtilXml.startElement(pHandler, _XML_TAG_STAR_PLAYER_POINTS, attributes);

      if (getCompletions() > 0) {
        UtilXml.addValueElement(pHandler, _XML_TAG_COMPLETIONS, getCompletions());
      }
      if (getTouchdowns() > 0) {
        UtilXml.addValueElement(pHandler, _XML_TAG_TOUCHDOWNS, getTouchdowns());
      }
      if (getInterceptions() > 0) {
        UtilXml.addValueElement(pHandler, _XML_TAG_INTERCEPTIONS, getInterceptions());
      }
      if (getCasualties() > 0) {
        UtilXml.addValueElement(pHandler, _XML_TAG_CASUALTIES, getCasualties());
      }
      if (getPlayerAwards() > 0) {
        UtilXml.addValueElement(pHandler, _XML_TAG_PLAYER_AWARDS, getPlayerAwards());
      }
      
      UtilXml.endElement(pHandler, _XML_TAG_STAR_PLAYER_POINTS);
      
    }
    
    if (((totalEarnedSpps() > 0) || (getBlocks() > 0) || (getFouls() > 0) || (getRushing() != 0) || (getPassing() != 0) || (getTurnsPlayed() > 0))) {
      
      UtilXml.startElement(pHandler, _XML_TAG_STATISTICS);

      if (getBlocks() > 0) {
        UtilXml.addValueElement(pHandler, _XML_TAG_BLOCKS, getBlocks());
      }
      if (getFouls() > 0) {
        UtilXml.addValueElement(pHandler, _XML_TAG_FOULS, getFouls());
      }
      if (getRushing() != 0) {
        UtilXml.addValueElement(pHandler, _XML_TAG_RUSHING, getRushing());
      }
      if (getPassing() != 0) {
        UtilXml.addValueElement(pHandler, _XML_TAG_PASSING, getPassing());
      }
      if (getTurnsPlayed() > 0) {
        UtilXml.addValueElement(pHandler, _XML_TAG_TURNS_PLAYED, getTurnsPlayed());
      }

      UtilXml.endElement(pHandler, _XML_TAG_STATISTICS);
      
    }
    
    if (getSeriousInjury() != null) {
      UtilXml.addValueElement(pHandler, _XML_TAG_INJURY, getSeriousInjury().getName());
    }
    
    if (getSendToBoxReason() != null) {
      attributes = new AttributesImpl();
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_REASON, getSendToBoxReason().getName());
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TURN, getSendToBoxTurn());
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_HALF, getSendToBoxHalf());
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_BY_PLAYER_ID, getSendToBoxByPlayerId());
      UtilXml.addEmptyElement(pHandler, _XML_TAG_SEND_TO_BOX, attributes);
    }
    
    UtilXml.endElement(pHandler, XML_TAG);
    
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  public IXmlSerializable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    if (XML_TAG.equals(pXmlTag)) {
      String playerId = UtilXml.getStringAttribute(pXmlAttributes, XML_ATTRIBUTE_PLAYER_ID);
      fPlayer = getTeamResult().getTeam().getPlayerById(playerId);
    }
    if (_XML_TAG_STAR_PLAYER_POINTS.equals(pXmlTag)) {
      setCurrentSpps(UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_CURRENT));
    }
    if (_XML_TAG_SEND_TO_BOX.equals(pXmlTag)) {
      setSendToBoxReason(SendToBoxReason.fromName(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_REASON)));
      setSendToBoxTurn(UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_TURN));
      setSendToBoxHalf(UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_HALF));
      setSendToBoxByPlayerId(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_BY_PLAYER_ID));
    }
    return this;
  }
  
  public boolean endXmlElement(String pXmlTag, String pValue) {
    boolean complete = XML_TAG.equals(pXmlTag);
    if (!complete) {
      if (_XML_TAG_COMPLETIONS.equals(pXmlTag)) {
        fCompletions = Integer.parseInt(pValue);
      }
      if (_XML_TAG_TOUCHDOWNS.equals(pXmlTag)) {
        fTouchdowns = Integer.parseInt(pValue);
      }
      if (_XML_TAG_INTERCEPTIONS.equals(pXmlTag)) {
        fInterceptions = Integer.parseInt(pValue);
      }
      if (_XML_TAG_CASUALTIES.equals(pXmlTag)) {
        fCasualties = Integer.parseInt(pValue);
      }
      if (_XML_TAG_PLAYER_AWARDS.equals(pXmlTag)) {
        fPlayerAwards = Integer.parseInt(pValue);
      }
      if (_XML_TAG_BLOCKS.equals(pXmlTag)) {
        fBlocks = Integer.parseInt(pValue);
      }
      if (_XML_TAG_FOULS.equals(pXmlTag)) {
        fFouls = Integer.parseInt(pValue);
      }
      if (_XML_TAG_RUSHING.equals(pXmlTag)) {
        fRushing = Integer.parseInt(pValue);
      }
      if (_XML_TAG_PASSING.equals(pXmlTag)) {
        fPassing = Integer.parseInt(pValue);
      }
      if (_XML_TAG_TURNS_PLAYED.equals(pXmlTag)) {
        fTurnsPlayed = Integer.parseInt(pValue);
      }
      if (_XML_TAG_USED_SECRET_WEAPON.equals(pXmlTag)) {
        setHasUsedSecretWeapon(Boolean.parseBoolean(pValue));
      }
      if (_XML_TAG_DEFECTING.equals(pXmlTag)) {
        setDefecting(Boolean.parseBoolean(pValue));
      }
      if (_XML_TAG_INJURY.equals(pXmlTag)) {
        setSeriousInjury(new SeriousInjuryFactory().forName(pValue));
      }

    }
    return complete;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getPlayerId());
    pByteList.addByte((byte) getCompletions());
    pByteList.addByte((byte) getTouchdowns());
    pByteList.addByte((byte) getInterceptions());
    pByteList.addByte((byte) getCasualties());
    pByteList.addByte((byte) getPlayerAwards());
    pByteList.addByte((byte) getBlocks());
    pByteList.addByte((byte) getFouls());
    pByteList.addSmallInt(getRushing());
    pByteList.addSmallInt(getPassing());
    pByteList.addSmallInt(getCurrentSpps());
    pByteList.addByte((byte) ((getSeriousInjury() != null) ? getSeriousInjury().getId() : 0));
    pByteList.addByte((byte) ((getSendToBoxReason() != null) ? getSendToBoxReason().getId() : 0));
    pByteList.addByte((byte) getSendToBoxTurn());
    pByteList.addByte((byte) getSendToBoxHalf());
    pByteList.addString(getSendToBoxByPlayerId());
    pByteList.addByte((byte) getTurnsPlayed());
    pByteList.addBoolean(hasUsedSecretWeapon());
    pByteList.addBoolean(isDefecting());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    String playerId = pByteArray.getString();
    fPlayer = getTeamResult().getTeam().getPlayerById(playerId);
    fCompletions = pByteArray.getByte();
    fTouchdowns = pByteArray.getByte();
    fInterceptions = pByteArray.getByte();
    fCasualties = pByteArray.getByte();
    fPlayerAwards = pByteArray.getByte();
    fBlocks = pByteArray.getByte();
    fFouls = pByteArray.getByte();
    fRushing = pByteArray.getSmallInt();
    fPassing = pByteArray.getSmallInt();
    setCurrentSpps(pByteArray.getSmallInt());
    fSeriousInjury = new SeriousInjuryFactory().forId(pByteArray.getByte());
    fSendToBoxReason = SendToBoxReason.fromId(pByteArray.getByte());
    fSendToBoxTurn = pByteArray.getByte();
    fSendToBoxHalf = pByteArray.getByte();
    fSendToBoxByPlayerId = pByteArray.getString();
    fTurnsPlayed = pByteArray.getByte();
    setHasUsedSecretWeapon(pByteArray.getBoolean());
    setDefecting(pByteArray.getBoolean());
    return byteArraySerializationVersion;
  }
    
}
