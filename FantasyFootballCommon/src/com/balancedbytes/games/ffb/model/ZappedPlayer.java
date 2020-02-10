package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.PlayerGender;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;

public class ZappedPlayer extends Player<ZappedPosition> {

  static final String XML_TAG = "zappedPlayer";
  public static final String KIND = "zappedPlayer";

  private RosterPlayer originalPlayer;
  private ZappedPosition position;

  public RosterPlayer getOriginalPlayer() {
    return originalPlayer;
  }

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
    return originalPlayer.getUrlPortrait();
  }

  @Override
  public void setUrlPortrait(String pUrlPortrait) {
     // NOOP
  }

  @Override
  public String getUrlIconSet() {
    return originalPlayer.getUrlIconSet();
  }

  @Override
  public void setUrlIconSet(String pUrlIconSet) {
    // NOOP
  }

  @Override
  public int getNrOfIcons() {
    return originalPlayer.getNrOfIcons();
  }

  @Override
  public void setNrOfIcons(int pNrOfIcons) {
    // NOOP
  }

  @Override
  public ZappedPosition getPosition() {
    return position;
  }

  @Override
  public void updatePosition(RosterPosition pPosition) {
    position = new ZappedPosition(pPosition);
    originalPlayer.updatePosition(pPosition);
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
    return originalPlayer.getIconSetIndex();
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
    this.position = new ZappedPosition(pPlayer.getPosition());
  }

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.PLAYER_KIND.addTo(jsonObject, KIND);
    IJsonOption.PLAYER.addTo(jsonObject, originalPlayer.toJsonValue());
    return jsonObject;
  }

  @Override
  public ZappedPlayer initFrom(JsonValue pJsonValue) {
    originalPlayer = new RosterPlayer().initFrom(IJsonOption.PLAYER.getFrom((JsonObject) pJsonValue));
    return this;
  }

  @Override
  public IXmlSerializable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    IXmlSerializable xmlElement = this;
    if (RosterPlayer.XML_TAG.equals(pXmlTag)) {
      RosterPlayer player = new RosterPlayer();
      player.startXmlElement(pXmlTag, pXmlAttributes);
      init(player);
    }
    return xmlElement;
  }

  @Override
  public boolean endXmlElement(String pXmlTag, String pValue) {
    return XML_TAG.equals(pXmlTag);
  }

  @Override
  public void addToXml(TransformerHandler pHandler) {

    AttributesImpl attributes = new AttributesImpl();

    UtilXml.startElement(pHandler, XML_TAG, attributes);

    originalPlayer.addToXml(pHandler);

    UtilXml.endElement(pHandler, XML_TAG);

  }

  @Override
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
}
