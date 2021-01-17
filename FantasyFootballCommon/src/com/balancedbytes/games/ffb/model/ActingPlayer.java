package com.balancedbytes.games.ffb.model;

import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeId;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 *
 * @author Kalimar
 */
public class ActingPlayer implements IJsonSerializable {

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

	private transient Game fGame;

	public ActingPlayer(Game pGame) {
		fGame = pGame;
		fUsedSkills = new HashSet<>();
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public void setPlayerId(String pPlayerId) {
		if (StringTool.isEqual(pPlayerId, fPlayerId)) {
			return;
		}
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
		Player<?> player = getGame().getPlayerById(getPlayerId());
		setStrength((player != null) ? UtilCards.getPlayerStrength(getGame(), player) : 0);
		notifyObservers(ModelChangeId.ACTING_PLAYER_SET_PLAYER_ID, fPlayerId);
	}

	public Player<?> getPlayer() {
		return getGame().getPlayerById(getPlayerId());
	}

	public void setPlayer(Player<?> pPlayer) {
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
		if (pCurrentMove == fCurrentMove) {
			return;
		}
		fCurrentMove = pCurrentMove;
		notifyObservers(ModelChangeId.ACTING_PLAYER_SET_CURRENT_MOVE, fCurrentMove);
	}

	public boolean isGoingForIt() {
		return fGoingForIt;
	}

	public void setGoingForIt(boolean pGoingForIt) {
		if (pGoingForIt == fGoingForIt) {
			return;
		}
		fGoingForIt = pGoingForIt;
		notifyObservers(ModelChangeId.ACTING_PLAYER_SET_GOING_FOR_IT, fGoingForIt);
	}

	public PlayerAction getPlayerAction() {
		return fPlayerAction;
	}

	public void setPlayerAction(PlayerAction pPlayerAction) {
		if (pPlayerAction == fPlayerAction) {
			return;
		}
		fPlayerAction = pPlayerAction;
		notifyObservers(ModelChangeId.ACTING_PLAYER_SET_PLAYER_ACTION, fPlayerAction);
	}

	public boolean isSkillUsed(Skill pSkill) {
		return fUsedSkills.contains(pSkill);
	}

	public void markSkillUsed(Skill pSkill) {
		if ((pSkill == null) || isSkillUsed(pSkill)) {
			return;
		}
		fUsedSkills.add(pSkill);
		notifyObservers(ModelChangeId.ACTING_PLAYER_MARK_SKILL_USED, pSkill);
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
		if (pHasBlocked == fHasBlocked) {
			return;
		}
		fHasBlocked = pHasBlocked;
		notifyObservers(ModelChangeId.ACTING_PLAYER_SET_HAS_BLOCKED, fHasBlocked);
	}

	public boolean hasPassed() {
		return fHasPassed;
	}

	public void setHasPassed(boolean pHasPassed) {
		if (pHasPassed == fHasPassed) {
			return;
		}
		fHasPassed = pHasPassed;
		notifyObservers(ModelChangeId.ACTING_PLAYER_SET_HAS_PASSED, fHasPassed);
	}

	public boolean isDodging() {
		return fDodging;
	}

	public void setDodging(boolean pDodging) {
		if (pDodging == fDodging) {
			return;
		}
		fDodging = pDodging;
		notifyObservers(ModelChangeId.ACTING_PLAYER_SET_DODGING, fDodging);
	}

	public int getStrength() {
		return fStrength;
	}

	public void setStrength(int pStrength) {
		if (pStrength == fStrength) {
			return;
		}
		fStrength = pStrength;
		notifyObservers(ModelChangeId.ACTING_PLAYER_SET_STRENGTH, fStrength);
	}

	public boolean hasMoved() {
		return fHasMoved;
	}

	public void setHasMoved(boolean pHasMoved) {
		if (pHasMoved == fHasMoved) {
			return;
		}
		fHasMoved = pHasMoved;
		notifyObservers(ModelChangeId.ACTING_PLAYER_SET_HAS_MOVED, fHasMoved);
	}

	public boolean isLeaping() {
		return fLeaping;
	}

	public void setLeaping(boolean pLeaping) {
		if (pLeaping == fLeaping) {
			return;
		}
		fLeaping = pLeaping;
		notifyObservers(ModelChangeId.ACTING_PLAYER_SET_LEAPING, fLeaping);
	}

	public void setStandingUp(boolean pStandingUp) {
		if (pStandingUp == fStandingUp) {
			return;
		}
		fStandingUp = pStandingUp;
		notifyObservers(ModelChangeId.ACTING_PLAYER_SET_STANDING_UP, fStandingUp);
	}

	public boolean isStandingUp() {
		return fStandingUp;
	}

	public void setSufferingBloodLust(boolean pSufferingBloodLust) {
		if (pSufferingBloodLust == fSufferingBloodLust) {
			return;
		}
		fSufferingBloodLust = pSufferingBloodLust;
		notifyObservers(ModelChangeId.ACTING_PLAYER_SET_SUFFERING_BLOOD_LUST, fSufferingBloodLust);
	}

	public boolean isSufferingBloodLust() {
		return fSufferingBloodLust;
	}

	public void setSufferingAnimosity(boolean pSufferingAnimosity) {
		if (pSufferingAnimosity == fSufferingAnimosity) {
			return;
		}
		fSufferingAnimosity = pSufferingAnimosity;
		notifyObservers(ModelChangeId.ACTING_PLAYER_SET_SUFFERING_ANIMOSITY, fSufferingAnimosity);
	}

	public boolean isSufferingAnimosity() {
		return fSufferingAnimosity;
	}

	public boolean hasFed() {
		return fHasFed;
	}

	public void setHasFed(boolean pHasFed) {
		if (pHasFed == fHasFed) {
			return;
		}
		fHasFed = pHasFed;
		notifyObservers(ModelChangeId.ACTING_PLAYER_SET_HAS_FED, fHasFed);
	}

	public boolean hasFouled() {
		return fHasFouled;
	}

	public void setHasFouled(boolean pHasFouled) {
		if (pHasFouled == fHasFouled) {
			return;
		}
		fHasFouled = pHasFouled;
		notifyObservers(ModelChangeId.ACTING_PLAYER_SET_HAS_FOULED, fHasFouled);
	}

	public Game getGame() {
		return fGame;
	}

	public boolean hasActed() {
		return (hasMoved() || hasFouled() || hasBlocked() || hasPassed() || (fUsedSkills.size() > 0));
	}

	// change tracking

	private void notifyObservers(ModelChangeId pChangeId, Object pValue) {
		if ((getGame() == null) || (pChangeId == null)) {
			return;
		}
		getGame().notifyObservers(new ModelChange(pChangeId, null, pValue));
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

	public ActingPlayer initFrom(IFactorySource source, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fCurrentMove = IJsonOption.CURRENT_MOVE.getFrom(source, jsonObject);
		fGoingForIt = IJsonOption.GOING_FOR_IT.getFrom(source, jsonObject);
		fHasBlocked = IJsonOption.HAS_BLOCKED.getFrom(source, jsonObject);
		fHasFed = IJsonOption.HAS_FED.getFrom(source, jsonObject);
		fHasFouled = IJsonOption.HAS_FOULED.getFrom(source, jsonObject);
		fHasMoved = IJsonOption.HAS_MOVED.getFrom(source, jsonObject);
		fHasPassed = IJsonOption.HAS_PASSED.getFrom(source, jsonObject);
		fPlayerAction = (PlayerAction) IJsonOption.PLAYER_ACTION.getFrom(source, jsonObject);
		fStandingUp = IJsonOption.STANDING_UP.getFrom(source, jsonObject);
		fSufferingAnimosity = IJsonOption.SUFFERING_ANIMOSITY.getFrom(source, jsonObject);
		fSufferingBloodLust = IJsonOption.SUFFERING_BLOODLUST.getFrom(source, jsonObject);
		JsonArray usedSkillsArray = IJsonOption.USED_SKILLS.getFrom(source, jsonObject);
		fUsedSkills.clear();
		if (usedSkillsArray != null) {
			for (int i = 0; i < usedSkillsArray.size(); i++) {
				fUsedSkills.add((Skill) UtilJson.toEnumWithName(fGame.getRules().getSkillFactory(), usedSkillsArray.get(i)));
			}
		}
		return this;
	}

}
