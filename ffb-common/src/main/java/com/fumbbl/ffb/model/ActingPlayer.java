package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Constant;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.model.change.ModelChangeId;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.StatBasedRollModifier;
import com.fumbbl.ffb.modifiers.StatBasedRollModifierFactory;
import com.fumbbl.ffb.util.StringTool;

import java.util.*;

/**
 * @author Kalimar
 */
public class ActingPlayer implements IJsonSerializable {

  private String fPlayerId;
  private int fStrength;
  private int fCurrentMove;
  private boolean fGoingForIt;
  private boolean fDodging;
  private boolean jumping;
  private boolean fHasBlocked;
  private boolean fHasFouled;
  private boolean fHasPassed;
  private boolean fHasMoved;
  private boolean fHasFed;
  private boolean hasJumped;
  private boolean fumblerooskiePending;
  private PlayerAction fPlayerAction;
  private final Set<Skill> fUsedSkills;
  private boolean fStandingUp;
  private boolean fSufferingBloodLust;
  private boolean fSufferingAnimosity;
  private boolean wasProne;
  private boolean jumpsWithoutModifiers;
  private boolean heldInPlace;
  private PlayerState oldPlayerState;
  private final Map<String, List<String>> skillsGrantedBy = new HashMap<>();

  private final transient Game fGame;

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
    oldPlayerState = null;
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
    jumping = false;
    fPlayerAction = null;
    fStandingUp = false;
    fSufferingBloodLust = false;
    fSufferingAnimosity = false;
    hasJumped = false;
    wasProne = false;
    fumblerooskiePending = false;
    jumpsWithoutModifiers = false;
    heldInPlace = false;
    Player<?> player = getGame().getPlayerById(getPlayerId());
    setStrength((player != null) ? player.getStrengthWithModifiers() : 0);
    skillsGrantedBy.clear();
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

  public boolean isHeldInPlace() {
    return heldInPlace;
  }

  public void setHeldInPlace(boolean heldInPlace) {
    if (this.heldInPlace == heldInPlace) {
      return;
    }
    this.heldInPlace = heldInPlace;
    notifyObservers(ModelChangeId.ACTING_PLAYER_SET_HELD_IN_PLACE, heldInPlace);
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

  public boolean wasProne() {
    return wasProne;
  }

  public void setWasProne(boolean wasProne) {
    this.wasProne = wasProne;
  }

  public boolean isJumpsWithoutModifiers() {
    return jumpsWithoutModifiers;
  }

  public void setJumpsWithoutModifiers(boolean jumpsWithoutModifiers) {
    if (this.jumpsWithoutModifiers == jumpsWithoutModifiers) {
      return;
    }
    this.jumpsWithoutModifiers = jumpsWithoutModifiers;

    if (this.jumpsWithoutModifiers) {
      setJumping(true);
    }
    notifyObservers(ModelChangeId.ACTING_PLAYER_SET_JUMPS_WITHOUT_MODIFIERS, this.jumpsWithoutModifiers);
  }

  public boolean isSkillUsed(Skill pSkill) {
    return fUsedSkills.contains(pSkill) || getPlayer().isUsed(pSkill);
  }

  public void markSkillUsed(Skill pSkill) {
    if ((pSkill == null) || fUsedSkills.contains(pSkill)) {
      return;
    }
    fUsedSkills.add(pSkill);
    notifyObservers(ModelChangeId.ACTING_PLAYER_MARK_SKILL_USED, pSkill);

    if (pSkill.getSkillUsageType().isTrackOutsideActivation() && !getPlayer().isUsed(pSkill)) {
      getPlayer().markUsed(pSkill, getGame());
    }
  }

  public void markSkillUsed(ISkillProperty property) {
    Skill skill = getPlayer().getSkillWithProperty(property);
    markSkillUsed(skill);
  }

  public void markSkillUnused(Skill pSkill) {
    if ((pSkill == null) || !isSkillUsed(pSkill)) {
      return;
    }
    fUsedSkills.remove(pSkill);

    if (pSkill.getSkillUsageType().isTrackOutsideActivation()) {
      getPlayer().markUnused(pSkill, getGame());
    } else {
      notifyObservers(ModelChangeId.ACTING_PLAYER_MARK_SKILL_USED, pSkill);
    }
  }

  public void markSkillUnused(ISkillProperty property) {
    Skill skill = getPlayer().getSkillWithProperty(property);
    markSkillUnused(skill);
  }

  public String getRace() {
    if (getPlayer() != null) {
      return getPlayer().getRace();
    } else {
      return null;
    }
  }

  public Skill[] getUsedSkills() {
    return fUsedSkills.toArray(new Skill[0]);
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

  public boolean hasJumped() {
    return hasJumped;
  }

  public void setHasJumped(boolean hasJumped) {
    if (this.hasJumped == hasJumped) {
      return;
    }
    this.hasJumped = hasJumped;
    notifyObservers(ModelChangeId.ACTING_PLAYER_SET_HAS_JUMPED, hasJumped);
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

  public boolean isJumping() {
    return jumping;
  }

  public void setJumping(boolean jumping) {
    if (jumping == this.jumping) {
      return;
    }
    this.jumping = jumping;
    if (!this.jumping) {
      setJumpsWithoutModifiers(false);
    }
    notifyObservers(ModelChangeId.ACTING_PLAYER_SET_JUMPING, this.jumping);
  }

  public void setStandingUp(boolean pStandingUp) {
    if (pStandingUp == fStandingUp) {
      return;
    }

    if (pStandingUp) {
      setWasProne(true);
    }
    fStandingUp = pStandingUp;
    notifyObservers(ModelChangeId.ACTING_PLAYER_SET_STANDING_UP, fStandingUp);
  }

  public boolean isStandingUp() {
    return fStandingUp;
  }

  public PlayerState getOldPlayerState() {
    return oldPlayerState;
  }

  public void setOldPlayerState(PlayerState oldPlayerState) {
    if (this.oldPlayerState != null && oldPlayerState != null) {
      return;
    }
    this.oldPlayerState = oldPlayerState;
    notifyObservers(ModelChangeId.ACTING_PLAYER_SET_OLD_PLAYER_STATE, oldPlayerState);
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

  public boolean isFumblerooskiePending() {
    return fumblerooskiePending;
  }

  public void setFumblerooskiePending(boolean fumblerooskiePending) {
    this.fumblerooskiePending = fumblerooskiePending;
  }

  public Game getGame() {
    return fGame;
  }

  public boolean hasActed() {
    return (hasMoved() || hasFouled() || hasBlocked() || hasPassed() || !fUsedSkills.isEmpty());
  }

  public boolean hasActedIgnoringNegativeTraits() {
    return hasMoved() || hasFouled() || hasBlocked() || hasPassed() || fUsedSkills.stream().anyMatch(skill -> !skill.isNegativeTrait());
  }

  public StatBasedRollModifier statBasedModifier(ISkillProperty property) {
    Skill skill = getPlayer().getSkillWithProperty(property);
    if (skill != null && !isSkillUsed(skill)) {
      StatBasedRollModifierFactory factory = skill.getStatBasedRollModifierFactory();
      if (factory != null) {
        return factory.create(getPlayer());
      }
    }

    return null;
  }

  public boolean justStoodUp() {
    Skill jumpUp = getPlayer().getSkillWithProperty(NamedProperties.canStandUpForFree);
    boolean hasJumpUp = jumpUp != null;
    boolean jumpUpUsedForBlock = hasJumpUp && isSkillUsed(jumpUp) && fPlayerAction.isBlockAction();

    boolean justStoodUp = (isStandingUp() || wasProne()) && !hasJumpUp && fCurrentMove == Math.min(Constant.MINIMUM_MOVE_TO_STAND_UP, getPlayer().getMovementWithModifiers());
    boolean justStoodUpForFree = isStandingUp() && hasJumpUp && fCurrentMove == 0;

    return jumpUpUsedForBlock || justStoodUp || justStoodUpForFree;
  }

  public void addGrantedSkill(Skill skill, Player<?> player) {
    List<String> players = skillsGrantedBy.computeIfAbsent(skill.getName(), k -> new ArrayList<>());
    if (player != null) {
      players.add(player.getId());
    }
  }

  public Map<String, List<String>> getSkillsGrantedBy() {
    return skillsGrantedBy;
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
    IJsonOption.FUMBLEROOSKIE_PENDING.addTo(jsonObject, fumblerooskiePending);
    IJsonOption.JUMPS_WITHOUT_MODIFIERS.addTo(jsonObject, jumpsWithoutModifiers);
    JsonArray usedSkillsArray = new JsonArray();
    for (Skill skill : getUsedSkills()) {
      usedSkillsArray.add(UtilJson.toJsonValue(skill));
    }
    IJsonOption.USED_SKILLS.addTo(jsonObject, usedSkillsArray);
    IJsonOption.SKILLS_GRANTED_BY.addTo(jsonObject, skillsGrantedBy);
    IJsonOption.PLAYER_STATE_OLD.addTo(jsonObject, oldPlayerState);
    IJsonOption.HELD_IN_PLACE.addTo(jsonObject, heldInPlace);
    return jsonObject;
  }

  public ActingPlayer initFrom(IFactorySource source, JsonValue jsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
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
    Boolean fumblerroskieValue = IJsonOption.FUMBLEROOSKIE_PENDING.getFrom(source, jsonObject);
    fumblerooskiePending = fumblerroskieValue != null && fumblerroskieValue;
    if (IJsonOption.JUMPS_WITHOUT_MODIFIERS.isDefinedIn(jsonObject)) {
      jumpsWithoutModifiers = IJsonOption.JUMPS_WITHOUT_MODIFIERS.getFrom(source, jsonObject);
    }
    JsonArray usedSkillsArray = IJsonOption.USED_SKILLS.getFrom(source, jsonObject);
    fUsedSkills.clear();
    if (usedSkillsArray != null) {
      for (int i = 0; i < usedSkillsArray.size(); i++) {
        fUsedSkills.add((Skill) UtilJson.toEnumWithName(fGame.getRules().getSkillFactory(), usedSkillsArray.get(i)));
      }
    }
    if (IJsonOption.SKILLS_GRANTED_BY.isDefinedIn(jsonObject)) {
      skillsGrantedBy.clear();
      skillsGrantedBy.putAll(IJsonOption.SKILLS_GRANTED_BY.getFrom(source, jsonObject));
    }
    oldPlayerState = IJsonOption.PLAYER_STATE_OLD.getFrom(source, jsonObject);
    if (IJsonOption.HELD_IN_PLACE.isDefinedIn(jsonObject)) {
      heldInPlace = IJsonOption.HELD_IN_PLACE.getFrom(source, jsonObject);
    }
    return this;
  }

}
