package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.PlayerGender;
import com.balancedbytes.games.ffb.PlayerGenderFactory;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.PlayerTypeFactory;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.SkillCategoryFactory;
import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.IXmlReadable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZappedPosition implements Position {

  public static final String XML_TAG = "zappedPosition";

  private int move = 5;
  private int strength = 1;
  private int agility = 4;
  private int armour = 4;
  private List<Skill> skills = Arrays.asList(Skill.DODGE,
      Skill.NO_HANDS,
      Skill.TITCHY,
      Skill.STUNTY,
      Skill.VERY_LONG_LEGS,
      Skill.LEAP);
  private String race = "Transmogrified Frog";
  private String shortHand = "zf";

  private RosterPosition originalPosition;

  public ZappedPosition(RosterPosition originalPosition) {
    this.originalPosition = originalPosition;
  }

  @Override
  public PlayerType getType() {
    return PlayerType.REGULAR;
  }

  @Override
  public PlayerGender getGender() {
    return originalPosition.getGender();
  }

  @Override
  public int getAgility() {
    return agility;
  }

  @Override
  public int getArmour() {
    return armour;
  }

  @Override
  public int getMovement() {
    return move;
  }

  @Override
  public int getCost() {
    return originalPosition.getCost();
  }

  @Override
  public String getName() {
    return originalPosition.getName();
  }

  @Override
  public String getShorthand() {
    return shortHand;
  }

  @Override
  public int getStrength() {
    return strength;
  }

  @Override
  public boolean hasSkill(Skill pSkill) {
    return skills.contains(pSkill);
  }

  @Override
  public Skill[] getSkills() {
    return skills.toArray(new Skill[0]);
  }

  @Override
  public int getSkillValue(Skill pSkill) {
    return 0;
  }

  @Override
  public String getUrlPortrait() {
    return originalPosition.getUrlPortrait();
  }

  @Override
  public void setUrlPortrait(String pUrlPortrait) {
    // NOOP
  }

  @Override
  public String getUrlIconSet() {
    return originalPosition.getUrlIconSet();
  }

  @Override
  public int getQuantity() {
    return 0;
  }

  @Override
  public Roster getRoster() {
    return originalPosition.getRoster();
  }

  @Override
  public String getId() {
    return originalPosition.getId();
  }

  @Override
  public int getNrOfIcons() {
    return originalPosition.getNrOfIcons();
  }

  @Override
  public int findNextIconSetIndex() {
    return originalPosition.findNextIconSetIndex();
  }

  @Override
  public String getDisplayName() {
    return originalPosition.getDisplayName();
  }

  @Override
  public String getRace() {
    return race;
  }

  @Override
  public boolean isUndead() {
    return false;
  }

  @Override
  public boolean isThrall() {
    return false;
  }

  @Override
  public String getTeamWithPositionId() {
    return originalPosition.getTeamWithPositionId();
  }

  @Override
  public boolean isDoubleCategory(SkillCategory category) {
    return false;
  }

  @Override
  public SkillCategory[] getSkillCategories(boolean b) {
    return new SkillCategory[0];
  }

  public void addToXml(TransformerHandler pHandler) {

    AttributesImpl attributes = new AttributesImpl();
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    originalPosition.addToXml(pHandler);
    UtilXml.endElement(pHandler, XML_TAG);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {

    if (RosterPosition.XML_TAG.equals(pXmlTag)) {
      originalPosition = new RosterPosition();
      originalPosition.startXmlElement(pXmlTag, pXmlAttributes);
    }
    return this;
  }

  public boolean endXmlElement(String pTag, String pValue) {
    return XML_TAG.equals(pTag);
  }

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.ROSTER_POSITION.addTo(jsonObject, originalPosition.toJsonValue());
    return jsonObject;
  }

  public ZappedPosition initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    originalPosition = new RosterPosition().initFrom(IJsonOption.ROSTER_POSITION.getFrom(jsonObject));
    return this;
  }
}
