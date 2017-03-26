package com.balancedbytes.games.ffb.model;


import com.balancedbytes.games.ffb.LeaderState;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class TurnData implements IJsonSerializable {
    
  private boolean fHomeData;
  private int fTurnNr;
  private boolean fFirstTurnAfterKickoff;
  private boolean fTurnStarted;
  private int fReRolls;
  private int fApothecaries;
  private boolean fBlitzUsed;
  private boolean fFoulUsed;
  private boolean fReRollUsed;
  private boolean fHandOverUsed;
  private boolean fPassUsed;
  private boolean fCoachBanned;
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
    if (pTurnNr == fTurnNr) {
      return;
    }
    fTurnNr = pTurnNr;
    notifyObservers(ModelChangeId.TURN_DATA_SET_TURN_NR, fTurnNr);
  }
  
  public boolean isTurnStarted() {
  	return fTurnStarted;
  }
  
  public void setTurnStarted(boolean pTurnStarted) {
  	if (pTurnStarted == fTurnStarted) {
      return;
    }
    fTurnStarted = pTurnStarted;
    notifyObservers(ModelChangeId.TURN_DATA_SET_TURN_STARTED, fTurnStarted);
  }
  
  public boolean isFirstTurnAfterKickoff() {
    return fFirstTurnAfterKickoff;
  }
  
  public void setFirstTurnAfterKickoff(boolean pFirstTurnAfterKickoff) {
    if (pFirstTurnAfterKickoff == fFirstTurnAfterKickoff) {
      return;
    }
    fFirstTurnAfterKickoff = pFirstTurnAfterKickoff;
    notifyObservers(ModelChangeId.TURN_DATA_SET_FIRST_TURN_AFTER_KICKOFF, fFirstTurnAfterKickoff);
  }

  public int getReRolls() {
    return fReRolls;
  }

  public void setReRolls(int pReRolls) {
    if (pReRolls == fReRolls) {
      return;
    }
    fReRolls = pReRolls;
    notifyObservers(ModelChangeId.TURN_DATA_SET_RE_ROLLS, fReRolls);
  }

  public boolean isBlitzUsed() {
    return fBlitzUsed;
  }

  public void setBlitzUsed(boolean pBlitzUsed) {
    if (pBlitzUsed == fBlitzUsed) {
      return;
    }
    fBlitzUsed = pBlitzUsed;
    notifyObservers(ModelChangeId.TURN_DATA_SET_BLITZ_USED, fBlitzUsed);
  }

  public boolean isFoulUsed() {
    return fFoulUsed;
  }

  public void setFoulUsed(boolean pFoulUsed) {
    if (pFoulUsed == fFoulUsed) {
      return;
    }
    fFoulUsed = pFoulUsed;
    notifyObservers(ModelChangeId.TURN_DATA_SET_FOUL_USED, fFoulUsed);
  }

  public boolean isReRollUsed() {
    return fReRollUsed;
  }

  public void setReRollUsed(boolean pReRollUsed) {
    if (pReRollUsed == fReRollUsed) {
      return;
    }
    fReRollUsed = pReRollUsed;
    notifyObservers(ModelChangeId.TURN_DATA_SET_RE_ROLL_USED, fReRollUsed);
  }

  public boolean isHandOverUsed() {
    return fHandOverUsed;
  }

  public void setHandOverUsed(boolean pHandOverUsed) {
    if (pHandOverUsed == fHandOverUsed) {
      return;
    }
    fHandOverUsed = pHandOverUsed;
    notifyObservers(ModelChangeId.TURN_DATA_SET_HAND_OVER_USED, fHandOverUsed);
  }

  public boolean isPassUsed() {
    return fPassUsed;
  }

  public void setPassUsed(boolean passUsed) {
    if (passUsed == fPassUsed) {
      return;
    }
    fPassUsed = passUsed;
    notifyObservers(ModelChangeId.TURN_DATA_SET_PASS_USED, fPassUsed);
  }
  
  public boolean isCoachBanned() {
    return fCoachBanned;
  }
  
  public void setCoachBanned(boolean coachBanned) {
    if (coachBanned == fCoachBanned) {
      return;
    }
    fCoachBanned = coachBanned;
    notifyObservers(ModelChangeId.TURN_DATA_SET_COACH_BANNED, fCoachBanned);
  }
  
  public int getApothecaries() {
    return fApothecaries;
  }
  
  public void setApothecaries(int apothecaries) {
    if (apothecaries == fApothecaries) {
      return;
    }
    fApothecaries = apothecaries;
    notifyObservers(ModelChangeId.TURN_DATA_SET_APOTHECARIES, fApothecaries);
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
      fTurnStarted = pTurnData.isTurnStarted();
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
    IJsonOption.COACH_BANNED.addTo(jsonObject, fCoachBanned);
    IJsonOption.LEADER_STATE.addTo(jsonObject, fLeaderState);
    if (fInducementSet != null) {
      IJsonOption.INDUCEMENT_SET.addTo(jsonObject, fInducementSet.toJsonValue());
    }
    return jsonObject;
  }
  
  public TurnData initFrom(JsonValue pJsonValue) {
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
    Boolean coachBanned =  IJsonOption.COACH_BANNED.getFrom(jsonObject); 
    fCoachBanned = (coachBanned != null) ? coachBanned : false;
    fLeaderState = (LeaderState) IJsonOption.LEADER_STATE.getFrom(jsonObject);
    fInducementSet = new InducementSet(this);
    fInducementSet.initFrom(IJsonOption.INDUCEMENT_SET.getFrom(jsonObject));
    return this;
  }
  
}
