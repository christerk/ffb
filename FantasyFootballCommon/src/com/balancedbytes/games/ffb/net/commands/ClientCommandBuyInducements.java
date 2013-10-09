package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.InducementSet;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandBuyInducements extends NetCommand {

  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_AVAILABLE_GOLD = "availableGold";
  private static final String _XML_TAG_STAR_PLAYERS = "starPlayers";
  private static final String _XML_TAG_STAR_PLAYER = "starPlayer";
  private static final String _XML_ATTRIBUTE_POSITION_ID = "positionId";
  private static final String _XML_TAG_MERCENARIES = "mercenaries";
  private static final String _XML_TAG_MERCENARY = "mercenary";
  private static final String _XML_ATTRIBUTE_SKILL = "skill";

  private String fTeamId;
  private int fAvailableGold;
  private InducementSet fInducementSet;
  private List<String> fStarPlayerPositionIds;
  private List<String> fMercenaryPositionIds;
  private List<Skill> fMercenarySkills;

  public ClientCommandBuyInducements() {
    fStarPlayerPositionIds = new ArrayList<String>();
    fMercenaryPositionIds = new ArrayList<String>();
    fMercenarySkills = new ArrayList<Skill>();
  }

  public ClientCommandBuyInducements(String pTeamId, int pAvailableGold, InducementSet pInducementSet, String[] pStarPlayerPositionIds,
      String[] pMercenaryPositionIds, Skill[] pMercenarySkills) {
    this();
    fTeamId = pTeamId;
    fAvailableGold = pAvailableGold;
    fInducementSet = pInducementSet;
    if (ArrayTool.isProvided(pStarPlayerPositionIds)) {
      for (String starPlayerPositionId : pStarPlayerPositionIds) {
        addStarPlayerPositionId(starPlayerPositionId);
      }
    }
    if (ArrayTool.isProvided(pMercenaryPositionIds) && ArrayTool.isProvided(pMercenarySkills)) {
      for (int i = 0; i < pMercenaryPositionIds.length; i++) {
        addMercenaryPosition(pMercenaryPositionIds[i], pMercenarySkills[i]);
      }
    }
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_BUY_INDUCEMENTS;
  }

  public String getTeamId() {
    return fTeamId;
  }

  public InducementSet getInducementSet() {
    return fInducementSet;
  }

  public String[] getStarPlayerPositionIds() {
    return fStarPlayerPositionIds.toArray(new String[fStarPlayerPositionIds.size()]);
  }

  public int getNrOfStarPlayerPositions() {
    return fStarPlayerPositionIds.size();
  }

  public void addStarPlayerPositionId(String pStarPlayerPositionId) {
    fStarPlayerPositionIds.add(pStarPlayerPositionId);
  }

  public void addMercenaryPosition(String pMercenaryPositionId, Skill pMercenarySkill) {
    fMercenaryPositionIds.add(pMercenaryPositionId);
    fMercenarySkills.add(pMercenarySkill);
  }

  public int getNrOfMercenaryPositions() {
    return fMercenaryPositionIds.size();
  }

  public String[] getMercenaryPositionIds() {
    return fMercenaryPositionIds.toArray(new String[fMercenaryPositionIds.size()]);
  }

  public Skill[] getMercenarySkills() {
    return fMercenarySkills.toArray(new Skill[fMercenarySkills.size()]);
  }

  public int getAvailableGold() {
    return fAvailableGold;
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {

    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_AVAILABLE_GOLD, getAvailableGold());
    UtilXml.startElement(pHandler, getId().getName(), attributes);

    if (getInducementSet() != null) {

      getInducementSet().addToXml(pHandler);

      String[] starPlayerPositions = getStarPlayerPositionIds();
      if (ArrayTool.isProvided(starPlayerPositions)) {
        UtilXml.startElement(pHandler, _XML_TAG_STAR_PLAYERS);
        for (String starPlayerPosition : starPlayerPositions) {
          attributes = new AttributesImpl();
          UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_POSITION_ID, starPlayerPosition);
          UtilXml.addEmptyElement(pHandler, _XML_TAG_STAR_PLAYER, attributes);
        }
        UtilXml.endElement(pHandler, _XML_TAG_STAR_PLAYERS);
      }

      String[] mercenaryPositionIds = getMercenaryPositionIds();
      Skill[] mercenarySkills = getMercenarySkills();
      if (ArrayTool.isProvided(mercenaryPositionIds) && ArrayTool.isProvided(mercenarySkills)) {
        UtilXml.startElement(pHandler, _XML_TAG_MERCENARIES);
        for (int i = 0; i < mercenaryPositionIds.length; i++) {
          attributes = new AttributesImpl();
          UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_POSITION_ID, mercenaryPositionIds[i]);
          UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SKILL, (mercenarySkills[i] != null) ? mercenarySkills[i].getName() : null);
          UtilXml.addEmptyElement(pHandler, _XML_TAG_MERCENARY, attributes);
        }
        UtilXml.endElement(pHandler, _XML_TAG_MERCENARIES);
      }

    }

    UtilXml.endElement(pHandler, getId().getName());

  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 3;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getTeamId());
    if (getInducementSet() != null) {
      pByteList.addBoolean(true);
      getInducementSet().addTo(pByteList);
    } else {
      pByteList.addBoolean(false);
    }
    pByteList.addStringArray(getStarPlayerPositionIds());
    pByteList.addInt(getAvailableGold());
    pByteList.addStringArray(getMercenaryPositionIds());
    Skill[] mercenarySkills = getMercenarySkills();
    byte[] mercenarySkillIds = new byte[mercenarySkills.length];
    for (int i = 0; i < mercenarySkillIds.length; i++) {
      if (mercenarySkills[i] != null) {
        mercenarySkillIds[i] = (byte) mercenarySkills[i].getId();
      } else {
        mercenarySkillIds[i] = (byte) 0;
      }
    }
    pByteList.addByteArray(mercenarySkillIds);
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    if (pByteArray.getBoolean()) {
      fInducementSet = new InducementSet();
      fInducementSet.initFrom(pByteArray);
    }
    String[] starPlayerPositionIds = pByteArray.getStringArray();
    for (int i = 0; i < starPlayerPositionIds.length; i++) {
      addStarPlayerPositionId(starPlayerPositionIds[i]);
    }
    if (byteArraySerializationVersion > 1) {
      fAvailableGold = pByteArray.getInt();
    }
    if (byteArraySerializationVersion > 2) {
      String[] mercenaryPositionIds = pByteArray.getStringArray();
      byte[] mercenarySkillIds = pByteArray.getByteArray();
      SkillFactory skillFactory = new SkillFactory();
      for (int i = 0; i < mercenaryPositionIds.length; i++) {
        addMercenaryPosition(mercenaryPositionIds[i], skillFactory.forId(mercenarySkillIds[i]));
      }
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    if (fInducementSet != null) {
      IJsonOption.INDUCEMENT_SET.addTo(jsonObject, fInducementSet.toJsonValue());
    }
    IJsonOption.STAR_PLAYER_POSTION_IDS.addTo(jsonObject, fStarPlayerPositionIds);
    IJsonOption.AVAILABLE_GOLD.addTo(jsonObject, fAvailableGold);
    IJsonOption.MERCENARY_POSTION_IDS.addTo(jsonObject, fMercenaryPositionIds);
    String[] mercenarySkillNames = new String[fMercenarySkills.size()];
    for (int i = 0; i < mercenarySkillNames.length; i++) {
      mercenarySkillNames[i] = fMercenarySkills.get(i).getName();
    }
    IJsonOption.MERCENARY_SKILLS.addTo(jsonObject, mercenarySkillNames);
    return jsonObject;
  }
  
  public ClientCommandBuyInducements initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fInducementSet = new InducementSet();
    JsonObject inducementSetObject = IJsonOption.INDUCEMENT_SET.getFrom(jsonObject);
    if (inducementSetObject != null) {
      fInducementSet.initFrom(inducementSetObject);
    }
    String[] starPlayerPositionIds = IJsonOption.STAR_PLAYER_POSTION_IDS.getFrom(jsonObject);
    for (String positionId : starPlayerPositionIds) {
      addStarPlayerPositionId(positionId);
    }
    fAvailableGold = IJsonOption.AVAILABLE_GOLD.getFrom(jsonObject);
    String[] mercenaryPositionIds = IJsonOption.MERCENARY_POSTION_IDS.getFrom(jsonObject);
    String[] mercenarySkillNames = IJsonOption.MERCENARY_SKILLS.getFrom(jsonObject);
    if (StringTool.isProvided(mercenaryPositionIds) && StringTool.isProvided(mercenarySkillNames)) {
      SkillFactory skillFactory = new SkillFactory();
      for (int i = 0; i < mercenaryPositionIds.length; i++) {
        addMercenaryPosition(
          mercenaryPositionIds[i],
          skillFactory.forName(mercenarySkillNames[i])
        );
      }
    }
    return this;
  }

}
