package com.balancedbytes.games.ffb.model;

import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeId;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class TeamResult implements IByteArraySerializable, IJsonSerializable {

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
  
  private Map<String, PlayerResult> fPlayerResultByPlayerId;

  private transient GameResult fGameResult;
  private transient Team fTeam;
  private transient boolean fHomeData;

  public TeamResult(GameResult pGameResult, boolean pHomeData) {
    fGameResult = pGameResult;
    fHomeData = pHomeData;
    fPlayerResultByPlayerId = new HashMap<String, PlayerResult>();
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
    if (pConceded == fConceded) {
    	return;
    }
    fConceded = pConceded;
    notifyObservers(ModelChangeId.TEAM_RESULT_SET_CONCEDED, fConceded);
  }
  
  public boolean hasConceded() {
    return fConceded;
  }
  
  public void setRaisedDead(int pRaisedDead) {
    if (pRaisedDead == fRaisedDead) {
    	return;
    }
    fRaisedDead = pRaisedDead;
    notifyObservers(ModelChangeId.TEAM_RESULT_SET_RAISED_DEAD, fRaisedDead);
  }
  
  public int getRaisedDead() {
    return fRaisedDead;
  }

  public int getFame() {
    return fFame;
  }
  
  public void setFame(int pFame) {
    if (pFame == fFame) {
    	return;
    }
    fFame = pFame;
    notifyObservers(ModelChangeId.TEAM_RESULT_SET_FAME, fFame);
  }
  
  public int getSpectators() {
    return fSpectators;
  }
  
  public void setSpectators(int pSpectators) {
    if (pSpectators == fSpectators) {
    	return;
    }
    fSpectators = pSpectators;
    notifyObservers(ModelChangeId.TEAM_RESULT_SET_SPECTATORS, fSpectators);
  }
  
  public int getWinnings() {
    return fWinnings;
  }
  
  public void setWinnings(int pWinnings) {
    if (pWinnings == fWinnings) {
    	return;
    }
    fWinnings = pWinnings;
    notifyObservers(ModelChangeId.TEAM_RESULT_SET_WINNINGS, fWinnings);
  }
  
  public int getFanFactorModifier() {
    return fFanFactorModifier;
  }
  
  public void setFanFactorModifier(int pFanFactorModifier) {
    if (pFanFactorModifier == fFanFactorModifier) {
    	return;
    }
    fFanFactorModifier = pFanFactorModifier;
    notifyObservers(ModelChangeId.TEAM_RESULT_SET_FAN_FACTOR_MODIFIER, fFanFactorModifier);
  }
  
  public int getScore() {
    return fScore;
  }
  
  public void setScore(int pScore) {
    if (pScore == fScore) {
    	return;
    }
    fScore = pScore;
    notifyObservers(ModelChangeId.TEAM_RESULT_SET_SCORE, fScore);
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
    if (pBadlyHurtSuffered == fBadlyHurtSuffered) {
    	return;
    }
    fBadlyHurtSuffered = pBadlyHurtSuffered;
  }
  
  public int getSeriousInjurySuffered() {
    return fSeriousInjurySuffered;
  }
  
  public void setSeriousInjurySuffered(int pSeriousInjurySuffered) {
    if (pSeriousInjurySuffered == fSeriousInjurySuffered) {
    	return;
    }
    fSeriousInjurySuffered = pSeriousInjurySuffered;
    notifyObservers(ModelChangeId.TEAM_RESULT_SET_SERIOUS_INJURY_SUFFERED, fSeriousInjurySuffered);
  }
  
  public int getRipSuffered() {
    return fRipSuffered;
  }
  
  public void setRipSuffered(int pRipSuffered) {
    if (pRipSuffered == fRipSuffered) {
    	return;
    }
    fRipSuffered = pRipSuffered;
    notifyObservers(ModelChangeId.TEAM_RESULT_SET_RIP_SUFFERED, fRipSuffered);
  }
  
  public int getSpirallingExpenses() {
    return fSpirallingExpenses;
  }
  
  public void setSpirallingExpenses(int pSpirallingExpenses) {
    if (pSpirallingExpenses == fSpirallingExpenses) {
      return;
    }
    fSpirallingExpenses = pSpirallingExpenses;
    notifyObservers(ModelChangeId.TEAM_RESULT_SET_SPIRALLING_EXPENSES, fSpirallingExpenses);
  }
  
  public int getPettyCashTransferred() {
    return fPettyCashTransferred;
  }
  
  public void setPettyCashTransferred(int pPettyCash) {
    if (pPettyCash == fPettyCashTransferred) {
      return;
    }
    fPettyCashTransferred = pPettyCash;
    notifyObservers(ModelChangeId.TEAM_RESULT_SET_PETTY_CASH_TRANSFERRED, fPettyCashTransferred);
  }

  public int getPettyCashUsed() {
    return fPettyCashUsed;
  }
  
  public void setPettyCashUsed(int pPettyCash) {
    if (pPettyCash == fPettyCashUsed) {
      return;
    }
    fPettyCashUsed = pPettyCash;
    notifyObservers(ModelChangeId.TEAM_RESULT_SET_PETTY_CASH_USED, fPettyCashUsed);
  }

  public int getTeamValue() {
    return fTeamValue;
  }
  
  public void setTeamValue(int pTeamValue) {
    if (pTeamValue == fTeamValue) {
     return;
    }
    fTeamValue = pTeamValue;
    notifyObservers(ModelChangeId.TEAM_RESULT_SET_TEAM_VALUE, fTeamValue);
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
    String playerId = (pPlayer != null) ? pPlayer.getId() : null;
    PlayerResult playerResult = fPlayerResultByPlayerId.get(playerId);
    if ((playerResult == null) && getTeam().hasPlayer(pPlayer)) {
      playerResult = new PlayerResult(this, pPlayer);
      fPlayerResultByPlayerId.put(playerResult.getPlayerId(), playerResult);
    }
    return playerResult;
  }
   
  public void removePlayerResult(Player pPlayer) {
    String playerId = (pPlayer != null) ? pPlayer.getId() : null;
    fPlayerResultByPlayerId.remove(playerId);
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
        fPlayerResultByPlayerId.put(player.getId(), newPlayerResult);
      }
    }
  }

  // change tracking
  
  private void notifyObservers(ModelChangeId pChangeId, Object pValue) {
  	if ((getGame() == null) || (pChangeId == null)) {
  		return;
  	}
  	String key = isHomeData() ? ModelChange.HOME : ModelChange.AWAY;
  	ModelChange modelChange = new ModelChange(pChangeId, key, pValue);
  	getGame().notifyObservers(modelChange);
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
      fPlayerResultByPlayerId.put(playerResult.getPlayerId(), playerResult);
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
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.SCORE.addTo(jsonObject, fScore);
    IJsonOption.CONCEDED.addTo(jsonObject, fConceded);
    IJsonOption.RAISED_DEAD.addTo(jsonObject, fRaisedDead);
    IJsonOption.SPECTATORS.addTo(jsonObject, fSpectators);
    IJsonOption.FAME.addTo(jsonObject, fFame);
    IJsonOption.WINNINGS.addTo(jsonObject, fWinnings);
    IJsonOption.FAN_FACTOR_MODIFIER.addTo(jsonObject, fFanFactorModifier);
    IJsonOption.BADLY_HURT_SUFFERED.addTo(jsonObject, fBadlyHurtSuffered);
    IJsonOption.SERIOUS_INJURY_SUFFERED.addTo(jsonObject, fSeriousInjurySuffered);
    IJsonOption.RIP_SUFFERED.addTo(jsonObject, fRipSuffered);
    IJsonOption.SPIRALLING_EXPENSES.addTo(jsonObject, fSpirallingExpenses);
    if (getTeam() != null) {
      JsonArray playerResultArray = new JsonArray();
      for (Player player : getTeam().getPlayers()) {
        playerResultArray.add(getPlayerResult(player).toJsonValue());
      }
      IJsonOption.PLAYER_RESULTS.addTo(jsonObject, playerResultArray);
    }
    IJsonOption.PETTY_CASH_TRANSFERRED.addTo(jsonObject, fPettyCashTransferred);
    IJsonOption.PETTY_CASH_USED.addTo(jsonObject, fPettyCashUsed);
    IJsonOption.TEAM_VALUE.addTo(jsonObject, fTeamValue);
    return jsonObject;
  }
  
  public TeamResult initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fScore = IJsonOption.SCORE.getFrom(jsonObject);
    fConceded = IJsonOption.CONCEDED.getFrom(jsonObject);
    fRaisedDead = IJsonOption.RAISED_DEAD.getFrom(jsonObject);
    fSpectators = IJsonOption.SPECTATORS.getFrom(jsonObject);
    fFame = IJsonOption.FAME.getFrom(jsonObject);
    fWinnings = IJsonOption.WINNINGS.getFrom(jsonObject);
    fFanFactorModifier = IJsonOption.FAN_FACTOR_MODIFIER.getFrom(jsonObject);
    fBadlyHurtSuffered = IJsonOption.BADLY_HURT_SUFFERED.getFrom(jsonObject);
    fSeriousInjurySuffered = IJsonOption.SERIOUS_INJURY_SUFFERED.getFrom(jsonObject);
    fRipSuffered = IJsonOption.RIP_SUFFERED.getFrom(jsonObject);
    fSpirallingExpenses = IJsonOption.SPIRALLING_EXPENSES.getFrom(jsonObject);
    fPlayerResultByPlayerId.clear();
    JsonArray playerResultArray = IJsonOption.PLAYER_RESULTS.getFrom(jsonObject);
    if (playerResultArray != null) {
      for (int i = 0; i < playerResultArray.size(); i++) {
        PlayerResult playerResult = new PlayerResult(this);
        playerResult.initFrom(playerResultArray.get(i));
        fPlayerResultByPlayerId.put(playerResult.getPlayer().getId(), playerResult);
      }
    }
    fPettyCashTransferred = IJsonOption.PETTY_CASH_TRANSFERRED.getFrom(jsonObject);
    fPettyCashUsed = IJsonOption.PETTY_CASH_USED.getFrom(jsonObject);
    fTeamValue = IJsonOption.TEAM_VALUE.getFrom(jsonObject);
    return this;
  }
    
}
