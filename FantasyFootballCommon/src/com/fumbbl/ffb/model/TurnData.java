package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.LeaderState;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.model.change.ModelChangeId;

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
	private int reRollsBrilliantCoachingOneDrive;
	private int fApothecaries;
	private boolean fBlitzUsed;
	private boolean fFoulUsed;
	private boolean fReRollUsed;
	private boolean fHandOverUsed;
	private boolean fPassUsed;
	private boolean fCoachBanned;
	private boolean ktmUsed;
	private boolean bombUsed;
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

	public int getReRollsBrilliantCoachingOneDrive() {
		return reRollsBrilliantCoachingOneDrive;
	}

	public void setReRollsBrilliantCoachingOneDrive(int reRollsBrilliantCoachingOneDrive) {
		if (this.reRollsBrilliantCoachingOneDrive == reRollsBrilliantCoachingOneDrive) {
			return;
		}
		this.reRollsBrilliantCoachingOneDrive = reRollsBrilliantCoachingOneDrive;
		notifyObservers(ModelChangeId.TURN_DATA_SET_RE_ROLLS_BRILLIANT_COACHING_ONE_DRIVE, reRollsBrilliantCoachingOneDrive);
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

	public boolean isBombUsed() {
		return bombUsed;
	}

	public void setBombUsed(boolean bombUsed) {
		if (this.bombUsed == bombUsed) {
			return;
		}
		this.bombUsed = bombUsed;
		notifyObservers(ModelChangeId.TURN_DATA_SET_BOMB_USED, bombUsed);
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

	public boolean isKtmUsed() {
		return ktmUsed;
	}

	public void setKtmUsed(boolean ktmUsed) {
		if (ktmUsed == this.ktmUsed) {
			return;
		}
		this.ktmUsed = ktmUsed;
		notifyObservers(ModelChangeId.TURN_DATA_SET_KTM_USED, ktmUsed);
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
		setKtmUsed(false);
		setBombUsed(false);
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
			fCoachBanned = pTurnData.isCoachBanned();
			ktmUsed = pTurnData.isKtmUsed();
			bombUsed = pTurnData.isBombUsed();
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
		IJsonOption.RE_ROLLS_BRILLIANT_COACHING_ONE_DRIVE.addTo(jsonObject, reRollsBrilliantCoachingOneDrive);
		IJsonOption.APOTHECARIES.addTo(jsonObject, fApothecaries);
		IJsonOption.BLITZ_USED.addTo(jsonObject, fBlitzUsed);
		IJsonOption.FOUL_USED.addTo(jsonObject, fFoulUsed);
		IJsonOption.RE_ROLL_USED.addTo(jsonObject, fReRollUsed);
		IJsonOption.HAND_OVER_USED.addTo(jsonObject, fHandOverUsed);
		IJsonOption.PASS_USED.addTo(jsonObject, fPassUsed);
		IJsonOption.COACH_BANNED.addTo(jsonObject, fCoachBanned);
		IJsonOption.KTM_USED.addTo(jsonObject, ktmUsed);
		IJsonOption.BOMB_USED.addTo(jsonObject, bombUsed);
		IJsonOption.LEADER_STATE.addTo(jsonObject, fLeaderState);
		if (fInducementSet != null) {
			IJsonOption.INDUCEMENT_SET.addTo(jsonObject, fInducementSet.toJsonValue());
		}
		return jsonObject;
	}

	public TurnData initFrom(IFactorySource source, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fHomeData = IJsonOption.HOME_DATA.getFrom(source, jsonObject);
		fTurnStarted = IJsonOption.TURN_STARTED.getFrom(source, jsonObject);
		fTurnNr = IJsonOption.TURN_NR.getFrom(source, jsonObject);
		fFirstTurnAfterKickoff = IJsonOption.FIRST_TURN_AFTER_KICKOFF.getFrom(source, jsonObject);
		fReRolls = IJsonOption.RE_ROLLS.getFrom(source, jsonObject);
		reRollsBrilliantCoachingOneDrive = IJsonOption.RE_ROLLS_BRILLIANT_COACHING_ONE_DRIVE.getFrom(source, jsonObject);
		fApothecaries = IJsonOption.APOTHECARIES.getFrom(source, jsonObject);
		fBlitzUsed = IJsonOption.BLITZ_USED.getFrom(source, jsonObject);
		fFoulUsed = IJsonOption.FOUL_USED.getFrom(source, jsonObject);
		fReRollUsed = IJsonOption.RE_ROLL_USED.getFrom(source, jsonObject);
		fHandOverUsed = IJsonOption.HAND_OVER_USED.getFrom(source, jsonObject);
		fPassUsed = IJsonOption.PASS_USED.getFrom(source, jsonObject);

		Boolean ktmValue = IJsonOption.KTM_USED.getFrom(source, jsonObject);
		ktmUsed = ktmValue != null && ktmValue;
		Boolean bombValue = IJsonOption.BOMB_USED.getFrom(source, jsonObject);
		bombUsed = bombValue != null && bombValue;
		Boolean coachBanned = IJsonOption.COACH_BANNED.getFrom(source, jsonObject);
		fCoachBanned = (coachBanned != null) ? coachBanned : false;
		fLeaderState = (LeaderState) IJsonOption.LEADER_STATE.getFrom(source, jsonObject);
		fInducementSet = new InducementSet(this);
		fInducementSet.initFrom(source, IJsonOption.INDUCEMENT_SET.getFrom(source, jsonObject));
		return this;
	}

}
