package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.PlayerGender;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.xml.IXmlReadable;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.xml.sax.Attributes;

import javax.xml.transform.sax.TransformerHandler;

public class ZappedPlayer implements Player<ZappedPosition> {

  private Player originalPlayer;

  @Override
  public String getName() {
    return originalPlayer.getName();
  }

  @Override
  public PlayerType getPlayerType() {
    return originalPlayer.getPlayerType();
  }

  @Override
  public void setType(PlayerType pType) {
      originalPlayer.setType(pType);
  }

  @Override
  public int getNr() {
    return originalPlayer.getNr();
  }

  @Override
  public int getAgility() {
    return getPosition().getAgility();
  }

  @Override
  public void setAgility(int pAgility) {
    // NOOP
  }

  @Override
  public int getArmour() {
    return getPosition().getArmour();
  }

  @Override
  public void setArmour(int pArmour) {
    // NOOP
  }

  @Override
  public int getMovement() {
    return getPosition().getMovement();
  }

  @Override
  public void setMovement(int pMovement) {
    // NOOP
  }

  @Override
  public int getStrength() {
    return getPosition().getStrength();
  }

  @Override
  public void setStrength(int pStrength) {
    // NOOP
  }

  @Override
  public void addLastingInjury(SeriousInjury pLastingInjury) {
    // NOOP
  }

  @Override
  public SeriousInjury[] getLastingInjuries() {
    return originalPlayer.getLastingInjuries();
  }

  @Override
  public void addSkill(Skill pSkill) {
     // NOOP
  }

  @Override
  public boolean removeSkill(Skill pSkill) {
    return false;
  }

  @Override
  public boolean hasSkill(Skill pSkill) {
    return getPosition().hasSkill(pSkill);
  }

  @Override
  public Skill[] getSkills() {
    return getPosition().getSkills();
  }

  @Override
  public String getUrlPortrait() {
    //TODO
    return null;
  }

  @Override
  public void setUrlPortrait(String pUrlPortrait) {
     // NOOP
  }

  @Override
  public String getUrlIconSet() {
    // TODO
    return null;
  }

  @Override
  public void setUrlIconSet(String pUrlIconSet) {
    // NOOP
  }

  @Override
  public int getNrOfIcons() {
    // TODO
    return 1;
  }

  @Override
  public void setNrOfIcons(int pNrOfIcons) {
    // NOOP
  }

  @Override
  public ZappedPosition getPosition() {
    // TODO
    return null;
  }

  @Override
  public void updatePosition(ZappedPosition pPosition) {
    // NOOE
  }

  @Override
  public Team getTeam() {
    return originalPlayer.getTeam();
  }

  @Override
  public void setTeam(Team pTeam) {
    // NOOP
  }

  @Override
  public String getId() {
    return originalPlayer.getId();
  }

  @Override
  public void setId(String pId) {
    // NOOP
  }

  @Override
  public PlayerGender getPlayerGender() {
    return originalPlayer.getPlayerGender();
  }

  @Override
  public SeriousInjury getRecoveringInjury() {
    return null;
  }

  @Override
  public void setRecoveringInjury(SeriousInjury pCurrentInjury) {
    // NOOP
  }

  @Override
  public int getCurrentSpps() {
    return originalPlayer.getCurrentSpps();
  }

  @Override
  public void setCurrentSpps(int pCurrentSpps) {
    // NOOP
  }

  @Override
  public void setName(String name) {
    // NOOP
  }

  @Override
  public void setGender(PlayerGender gender) {
    // NOOP
  }

  @Override
  public void setNr(int nr) {
    // NOOP
  }

  @Override
  public int getIconSetIndex() {
    // TODO
    return 0;
  }

  @Override
  public String getPositionId() {
    return originalPlayer.getPositionId();
  }

  @Override
  public void setPositionId(String pPositionId) {
    // NOOP
  }

  @Override
  public String getRace() {
    return originalPlayer.getRace();
  }

  @Override
  public void init(RosterPlayer pPlayer) {
    this.originalPlayer = pPlayer;
  }

  @Override
  public JsonObject toJsonValue() {
    // TODO
    return null;
  }

  @Override
  public Object initFrom(JsonValue pJsonValue) {
    // TODO
    return null;
  }

  @Override
  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    // TODO
    return null;
  }

  @Override
  public boolean endXmlElement(String pXmlTag, String pValue) {
    // TODO
    return false;
  }

  @Override
  public void addToXml(TransformerHandler pHandler) {
    // TODO

  }

  @Override
  public String toXml(boolean pIndent) {
    // TODO
    return null;
  }
}
