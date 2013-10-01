package com.balancedbytes.games.ffb.model;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Team;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class TeamResult implements IByteArraySerializable, IXmlSerializable {
  
  public static final String XML_TAG = "teamResult";
  
  private static final String XML_ATTRIBUTE_TEAM_ID = "teamId";
  
  private static final String _XML_TAG_SCORE = "score";
  private static final String _XML_TAG_CONCEDED = "conceded";
  private static final String _XML_TAG_RAISED_DEAD = "raisedDead";
  private static final String _XML_TAG_FAME = "fame";
  private static final String _XML_TAG_SPECTATORS = "spectators";
  private static final String _XML_TAG_WINNINGS = "winnings";
  private static final String _XML_TAG_FAN_FACTOR_MODIFIER = "fanFactorModifier";
  private static final String _XML_TAG_SPIRALLING_EXPENSES = "spirallingExpenses";
  
  private static final String _XML_TAG_CASUALTIES_SUFFERED = "casualtiesSuffered";
  private static final String _XML_ATTRIBUTE_BADLY_HURT = "badlyHurt";
  private static final String _XML_ATTRIBUTE_SERIOUS_INJURY = "seriousInjury";
  private static final String _XML_ATTRIBUTE_RIP = "rip";
  private static final String _XML_TAG_PETTY_CASH_TRANSFERRED = "pettyCashTransferred";
  private static final String _XML_TAG_PETTY_CASH_USED = "pettyCashUsed";
  private static final String _XML_TAG_TEAM_VALUE = "teamValue";
  
  private static final String _XML_TAG_PLAYER_RESULT_LIST = "playerResultList";

  private transient GameResult fGameResult;
  private transient Team fTeam;
  private transient boolean fHomeData;
  
  private int fScore;
  private int fFame;
  private int fSpectators;
  private int fWinnings;
  private int fFanFactorModifier;
  private int fSpirallingExpenses;
  
  private int fBadlyHurtSuffered;
  private int fSeriousInjurySuffered;
  private int fRipSuffered;
  
  private boolean fConceded;
  private int fRaisedDead;
  private int fPettyCashTransferred;
  private int fPettyCashUsed;
  private int fTeamValue;
  
  private Map<Player, PlayerResult> fPlayerResultByPlayer;
  
  private transient PlayerResult fCurrentPlayerResult;

  public TeamResult(GameResult pGameResult, boolean pHomeData, Team pTeam) {
    fGameResult = pGameResult;
    fHomeData = pHomeData;
    setTeam(pTeam);
    fPlayerResultByPlayer = new HashMap<Player, PlayerResult>();
  }
  
  public GameResult getGameResult() {
    return fGameResult;
  }
  
  public boolean isHomeData() {
    return fHomeData;
  }
  
  public void setTeam(Team pTeam) {
    fTeam = pTeam;
  }
  
  public Team getTeam() {
    return fTeam;
  }
  
  public void setConceded(boolean pConceded) {
    if (getGame().isTrackingChanges() && (pConceded != fConceded)) {
      getGame().add(new ModelChangeTeamResult(CommandTeamResultChange.SET_CONCEDED, isHomeData(), pConceded));
    }
    fConceded = pConceded;
  }
  
  public boolean hasConceded() {
    return fConceded;
  }
  
  public void setRaisedDead(int pRaisedDead) {
    if (getGame().isTrackingChanges() && (pRaisedDead != fRaisedDead)) {
      getGame().add(new ModelChangeTeamResult(CommandTeamResultChange.SET_RAISED_DEAD, isHomeData(), pRaisedDead));
    }
    fRaisedDead = pRaisedDead;
  }
  
  public int getRaisedDead() {
    return fRaisedDead;
  }

  public int getFame() {
    return fFame;
  }
  
  public void setFame(int pFame) {
    if (getGame().isTrackingChanges() && (pFame != fFame)) {
      getGame().add(new ModelChangeTeamResult(CommandTeamResultChange.SET_FAME, isHomeData(), (byte) pFame));
    }
    fFame = pFame;
  }
  
  public int getSpectators() {
    return fSpectators;
  }
  
  public void setSpectators(int pSpectators) {
    if (getGame().isTrackingChanges() && (pSpectators != fSpectators)) {
      getGame().add(new ModelChangeTeamResult(CommandTeamResultChange.SET_SPECTATORS, isHomeData(), pSpectators));
    }
    fSpectators = pSpectators;
  }
  
  public int getWinnings() {
    return fWinnings;
  }
  
  public void setWinnings(int pWinnings) {
    if (getGame().isTrackingChanges() && (pWinnings != fWinnings)) {
      getGame().add(new ModelChangeTeamResult(CommandTeamResultChange.SET_WINNINGS, isHomeData(), pWinnings));
    }
    fWinnings = pWinnings;
  }
  
  public int getFanFactorModifier() {
    return fFanFactorModifier;
  }
  
  public void setFanFactorModifier(int pFanFactorModifier) {
    if (getGame().isTrackingChanges() && (pFanFactorModifier != fFanFactorModifier)) {
      getGame().add(new ModelChangeTeamResult(CommandTeamResultChange.SET_FAN_FACTOR_MODIFIER, isHomeData(), (byte) pFanFactorModifier));
    }
    fFanFactorModifier = pFanFactorModifier;
  }
  
  public int getScore() {
    return fScore;
  }
  
  public void setScore(int pScore) {
    if (getGame().isTrackingChanges() && (fScore != pScore)) {
      getGame().add(new ModelChangeTeamResult(CommandTeamResultChange.SET_SCORE, isHomeData(), (byte) pScore));
    }
    fScore = pScore;
  }

  public void sufferInjury(PlayerState pPlayerState) {
    if (pPlayerState != null) {
      switch (pPlayerState.getBase()) {
        case PlayerState.BADLY_HURT:
          setBadlyHurtSuffered(getBadlyHurtSuffered() + 1);
          break;
        case PlayerState.SERIOUS_INJURY:
          setSeriousInjurySuffered(getSeriousInjurySuffered() + 1);
          break;
        case PlayerState.RIP:
          setRipSuffered(getRipSuffered() + 1);
          break;
      }
    }
  }
 
  public int getBadlyHurtSuffered() {
    return fBadlyHurtSuffered;
  }
  
  public void setBadlyHurtSuffered(int pBadlyHurtSuffered) {
    if (getGame().isTrackingChanges() && (fBadlyHurtSuffered != pBadlyHurtSuffered)) {
      getGame().add(new ModelChangeTeamResult(CommandTeamResultChange.SET_BADLY_HURT_SUFFERED, isHomeData(), (byte) pBadlyHurtSuffered));
    }
    fBadlyHurtSuffered = pBadlyHurtSuffered;
  }
  
  public int getSeriousInjurySuffered() {
    return fSeriousInjurySuffered;
  }
  
  public void setSeriousInjurySuffered(int pSeriousInjurySuffered) {
    if (getGame().isTrackingChanges() && (fSeriousInjurySuffered != pSeriousInjurySuffered)) {
      getGame().add(new ModelChangeTeamResult(CommandTeamResultChange.SET_SERIOUS_INJURY_SUFFERED, isHomeData(), (byte) pSeriousInjurySuffered));
    }
    fSeriousInjurySuffered = pSeriousInjurySuffered;
  }
  
  public int getRipSuffered() {
    return fRipSuffered;
  }
  
  public void setRipSuffered(int pRipSuffered) {
    if (getGame().isTrackingChanges() && (fRipSuffered != pRipSuffered)) {
      getGame().add(new ModelChangeTeamResult(CommandTeamResultChange.SET_RIP_SUFFERED, isHomeData(), (byte) pRipSuffered));
    }
    fRipSuffered = pRipSuffered;
  }
  
  public int getSpirallingExpenses() {
    return fSpirallingExpenses;
  }
  
  public void setSpirallingExpenses(int pSpirallingExpenses) {
    if (getGame().isTrackingChanges() && (pSpirallingExpenses != fSpirallingExpenses)) {
      getGame().add(new ModelChangeTeamResult(CommandTeamResultChange.SET_SPIRALLING_EXPENSES, isHomeData(), pSpirallingExpenses));
    }
    fSpirallingExpenses = pSpirallingExpenses;
  }
  
  public int getPettyCashTransferred() {
    return fPettyCashTransferred;
  }
  
  public void setPettyCashTransferred(int pPettyCash) {
    if (getGame().isTrackingChanges() && (pPettyCash != fPettyCashTransferred)) {
      getGame().add(new ModelChangeTeamResult(CommandTeamResultChange.SET_PETTY_CASH_TRANSFERRED, isHomeData(), pPettyCash));
    }
    fPettyCashTransferred = pPettyCash;
  }

  public int getPettyCashUsed() {
    return fPettyCashUsed;
  }
  
  public void setPettyCashUsed(int pPettyCash) {
    if (getGame().isTrackingChanges() && (pPettyCash != fPettyCashUsed)) {
      getGame().add(new ModelChangeTeamResult(CommandTeamResultChange.SET_PETTY_CASH_USED, isHomeData(), pPettyCash));
    }
    fPettyCashUsed = pPettyCash;
  }

  public int getTeamValue() {
    return fTeamValue;
  }
  
  public void setTeamValue(int pTeamValue) {
    if (getGame().isTrackingChanges() && (pTeamValue != fTeamValue)) {
      getGame().add(new ModelChangeTeamResult(CommandTeamResultChange.SET_TEAM_VALUE, isHomeData(), pTeamValue));
    }
    fTeamValue = pTeamValue;
  }

  public int totalCompletions() {
    int completions = 0;
    for (Player player : getTeam().getPlayers()) {
      completions += getPlayerResult(player).getCompletions();
    }
    return completions;
  }

  public int totalInterceptions() {
    int interceptions = 0;
    for (Player player : getTeam().getPlayers()) {
      interceptions += getPlayerResult(player).getInterceptions();
    }
    return interceptions;
  }

  public int totalCasualties() {
    int casualties = 0;
    for (Player player : getTeam().getPlayers()) {
      casualties += getPlayerResult(player).getCasualties();
    }
    return casualties;
  }

  public int totalBlocks() {
    int blocks = 0;
    for (Player player : getTeam().getPlayers()) {
      blocks += getPlayerResult(player).getBlocks();
    }
    return blocks;
  }
  
  public int totalFouls() {
    int fouls = 0;
    for (Player player : getTeam().getPlayers()) {
      fouls += getPlayerResult(player).getFouls();
    }
    return fouls;
  }
    
  public int totalRushing() {
    int rushing = 0;
    for (Player player : getTeam().getPlayers()) {
      rushing += getPlayerResult(player).getRushing();
    }
    return rushing;
  }

  public int totalPassing() {
    int passing = 0;
    for (Player player : getTeam().getPlayers()) {
      passing += getPlayerResult(player).getPassing();
    }
    return passing;
  }

  public int totalEarnedSpps() {
    int earnedSpps = 0;
    for (Player player : getTeam().getPlayers()) {
      earnedSpps += getPlayerResult(player).totalEarnedSpps();
    }
    return earnedSpps;
  }
  
  public PlayerResult getPlayerResult(Player pPlayer) {
    PlayerResult playerResult = fPlayerResultByPlayer.get(pPlayer);
    if ((playerResult == null) && getTeam().hasPlayer(pPlayer)) {
      playerResult = new PlayerResult(this, pPlayer);
      fPlayerResultByPlayer.put(playerResult.getPlayer(), playerResult);
    }
    return playerResult;
  }
   
  public void removePlayerResult(Player pPlayer) {
    fPlayerResultByPlayer.remove(pPlayer);
  }
    
  public Game getGame() {
    return getGameResult().getGame();
  }
  
  public void init(TeamResult pTeamResult) {
    if (pTeamResult != null) {
      fScore = pTeamResult.getScore();
      fFame = pTeamResult.getFame();
      fSpectators = pTeamResult.getSpectators();
      fWinnings = pTeamResult.getWinnings();
      fFanFactorModifier = pTeamResult.getFanFactorModifier();
      fSpirallingExpenses = pTeamResult.getSpirallingExpenses();
      fBadlyHurtSuffered = pTeamResult.getBadlyHurtSuffered();
      fSeriousInjurySuffered = pTeamResult.getSeriousInjurySuffered();
      fRipSuffered = pTeamResult.getRipSuffered();
      fConceded = pTeamResult.hasConceded();
      fRaisedDead = pTeamResult.getRaisedDead();
      fPettyCashTransferred = pTeamResult.getPettyCashTransferred();
      fPettyCashUsed = pTeamResult.getPettyCashUsed();
      fTeamValue = pTeamResult.getTeamValue();
      for (Player player : fTeam.getPlayers()) {
        PlayerResult oldPlayerResult = pTeamResult.getPlayerResult(player);
        PlayerResult newPlayerResult = new PlayerResult(this);
        newPlayerResult.init(oldPlayerResult);
        fPlayerResultByPlayer.put(player, newPlayerResult);
      }
    }
  }
    
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {

    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_TEAM_ID, (getTeam() != null) ? getTeam().getId() : null);
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    
    UtilXml.addValueElement(pHandler, _XML_TAG_SCORE, getScore());
    UtilXml.addValueElement(pHandler, _XML_TAG_CONCEDED, hasConceded());
    UtilXml.addValueElement(pHandler, _XML_TAG_RAISED_DEAD, getRaisedDead());
    
    if (getSpectators() > 0) {
      UtilXml.addValueElement(pHandler, _XML_TAG_SPECTATORS, getSpectators());
      UtilXml.addValueElement(pHandler, _XML_TAG_FAME, getFame());
    }
    
    if (getWinnings() > 0) {
      UtilXml.addValueElement(pHandler, _XML_TAG_WINNINGS, getWinnings());
    }
    
    if (getFanFactorModifier() != 0) {
      UtilXml.addValueElement(pHandler, _XML_TAG_FAN_FACTOR_MODIFIER, getFanFactorModifier());
    }
    
    if (getSpirallingExpenses() > 0) {
      UtilXml.addValueElement(pHandler, _XML_TAG_SPIRALLING_EXPENSES, getSpirallingExpenses());
    }
    
    if (getPettyCashTransferred() > 0) {
      UtilXml.addValueElement(pHandler, _XML_TAG_PETTY_CASH_TRANSFERRED, getPettyCashTransferred());
    }
    
    if (getPettyCashUsed() > 0) {
      UtilXml.addValueElement(pHandler, _XML_TAG_PETTY_CASH_USED, getPettyCashUsed());
    }
    
    if (getTeamValue() > 0) {
      UtilXml.addValueElement(pHandler, _XML_TAG_TEAM_VALUE, getTeamValue());
    }
    
    attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_BADLY_HURT, getBadlyHurtSuffered());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SERIOUS_INJURY, getSeriousInjurySuffered());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_RIP, getRipSuffered());
    UtilXml.addEmptyElement(pHandler, _XML_TAG_CASUALTIES_SUFFERED, attributes);
    
    UtilXml.startElement(pHandler, _XML_TAG_PLAYER_RESULT_LIST);
    for (Player player : getTeam().getPlayers()) {
      getPlayerResult(player).addToXml(pHandler);
    }
    UtilXml.endElement(pHandler, _XML_TAG_PLAYER_RESULT_LIST);

    UtilXml.endElement(pHandler, XML_TAG);
    
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  public IXmlSerializable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    IXmlSerializable xmlElement = this;
    if (_XML_TAG_CASUALTIES_SUFFERED.equals(pXmlTag)) {
      fBadlyHurtSuffered = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_BADLY_HURT);
      fSeriousInjurySuffered = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_SERIOUS_INJURY);
      fRipSuffered = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_RIP);
    }
    if (PlayerResult.XML_TAG.equals(pXmlTag)) {
      fCurrentPlayerResult = new PlayerResult(this);
      fCurrentPlayerResult.startXmlElement(pXmlTag, pXmlAttributes);
      xmlElement = fCurrentPlayerResult;
    }
    return xmlElement;
  }
  
  public boolean endXmlElement(String pXmlTag, String pValue) {
    if (_XML_TAG_SCORE.equals(pXmlTag)) {
      fScore = Integer.parseInt(pValue);
    }
    if (_XML_TAG_CONCEDED.equals(pXmlTag)) {
      fConceded = Boolean.parseBoolean(pValue);
    }
    if (_XML_TAG_RAISED_DEAD.equals(pXmlTag)) {
      fRaisedDead = Integer.parseInt(pValue);
    }
    if (_XML_TAG_FAME.equals(pXmlTag)) {
      fFame = Integer.parseInt(pValue);
    }
    if (_XML_TAG_SPECTATORS.equals(pXmlTag)) {
      fSpectators = Integer.parseInt(pValue);
    }
    if (_XML_TAG_WINNINGS.equals(pXmlTag)) {
      fWinnings = Integer.parseInt(pValue);
    }
    if (_XML_TAG_FAN_FACTOR_MODIFIER.equals(pXmlTag)) {
      fFanFactorModifier = Integer.parseInt(pValue);
    }
    if (_XML_TAG_SPIRALLING_EXPENSES.equals(pXmlTag)) {
      fSpirallingExpenses = Integer.parseInt(pValue);
    }
    if (_XML_TAG_PETTY_CASH_TRANSFERRED.equals(pXmlTag)) {
      fPettyCashTransferred = Integer.parseInt(pValue);
    }
    if (_XML_TAG_PETTY_CASH_USED.equals(pXmlTag)) {
      fPettyCashUsed = Integer.parseInt(pValue);
    }
    if (_XML_TAG_TEAM_VALUE.equals(pXmlTag)) {
      fTeamValue = Integer.parseInt(pValue);
    }
    if (PlayerResult.XML_TAG.equals(pXmlTag)) {
      fPlayerResultByPlayer.put(fCurrentPlayerResult.getPlayer(), fCurrentPlayerResult);
    }
    return XML_TAG.equals(pXmlTag);
  }
  
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 3;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getScore());
    pByteList.addBoolean(hasConceded());
    pByteList.addByte((byte) getRaisedDead());
    pByteList.addInt(getSpectators());
    pByteList.addByte((byte) getFame());
    pByteList.addInt(getWinnings());
    pByteList.addByte((byte) getFanFactorModifier());
    pByteList.addByte((byte) getBadlyHurtSuffered());
    pByteList.addByte((byte) getSeriousInjurySuffered());
    pByteList.addByte((byte) getRipSuffered());
    pByteList.addInt(getSpirallingExpenses());
    Player[] players = getTeam().getPlayers();
    pByteList.addByte((byte) players.length);
    for (Player player : players) {
      getPlayerResult(player).addTo(pByteList);
    }
    pByteList.addInt(getPettyCashTransferred());
    pByteList.addInt(getPettyCashUsed());
    pByteList.addInt(getTeamValue());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fScore = pByteArray.getByte();
    fConceded = pByteArray.getBoolean();
    fRaisedDead = pByteArray.getByte();
    fSpectators = pByteArray.getInt();
    fFame = pByteArray.getByte();
    fWinnings = pByteArray.getInt();
    fFanFactorModifier = pByteArray.getByte();
    fBadlyHurtSuffered = pByteArray.getByte();
    fSeriousInjurySuffered = pByteArray.getByte();
    fRipSuffered = pByteArray.getByte();
    fSpirallingExpenses = pByteArray.getInt();
    int nrOfPlayers = pByteArray.getByte();
    for (int i = 0; i < nrOfPlayers; i++) {
      PlayerResult playerResult = new PlayerResult(this);
      playerResult.initFrom(pByteArray);
      fPlayerResultByPlayer.put(playerResult.getPlayer(), playerResult);
    }
    if (byteArraySerializationVersion > 1) {
      fPettyCashTransferred = pByteArray.getInt();
      fPettyCashUsed = pByteArray.getInt();
    }
    if (byteArraySerializationVersion > 2) {
      fTeamValue = pByteArray.getInt();
    }
    return byteArraySerializationVersion;
  }
    
}
