package com.balancedbytes.games.ffb.dialog;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class DialogPlayerChoiceParameter implements IDialogParameter {
  
  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_MODE = "mode";
  private static final String _XML_ATTRIBUTE_MAX_SELECTS = "maxSelects";

  private static final String _XML_TAG_PLAYER = "player";
  private static final String _XML_ATTRIBUTE_DESCRIPTION = "description";
  
  private String fTeamId;
  private PlayerChoiceMode fMode;
  private List<String> fPlayerIds;
  private List<String> fDescriptions;
  private int fMaxSelects;
  
  public DialogPlayerChoiceParameter() {
    fPlayerIds = new ArrayList<String>();
    fDescriptions = new ArrayList<String>();
  }
  
  public DialogPlayerChoiceParameter(String pTeamId, PlayerChoiceMode pMode, Player[] pPlayers, String[] pDescriptions, int pMaxSelects) {
    this(pTeamId, pMode, findPlayerIds(pPlayers), pDescriptions, pMaxSelects);
  }
  
  public DialogPlayerChoiceParameter(String pTeamId, PlayerChoiceMode pMode, String[] pPlayerIds, String[] pDescriptions, int pMaxSelects) {
    this();
    fTeamId = pTeamId;
    fMode = pMode;
    fMaxSelects = pMaxSelects;
    addDescriptions(pDescriptions);
    addPlayerIds(pPlayerIds);
  }
  
  public DialogId getId() {
    return DialogId.PLAYER_CHOICE;
  }
  
  public String getTeamId() {
    return fTeamId;
  }
  
  public int getMaxSelects() {
    return fMaxSelects;
  }
  
  public PlayerChoiceMode getMode() {
    return fMode;
  }
  
  public String[] getPlayerIds() {
    return fPlayerIds.toArray(new String[fPlayerIds.size()]);
  }
  
  public void addPlayerId(String pPlayerId) {
    if (StringTool.isProvided(pPlayerId)) {
      fPlayerIds.add(pPlayerId);
    }
  }
  
  private void addPlayerIds(String[] pPlayerIds) {
    if (ArrayTool.isProvided(pPlayerIds)) {
      for (int i = 0; i < pPlayerIds.length; i++) {
        addPlayerId(pPlayerIds[i]);
      }
    }
  }

  public String[] getDescriptions() {
    return fDescriptions.toArray(new String[fDescriptions.size()]);
  }
  
  public void addDescription(String pDescription) {
    if (StringTool.isProvided(pDescription)) {
      fDescriptions.add(pDescription);
    }
  }
  
  private void addDescriptions(String[] pDescriptions) {
    if (ArrayTool.isProvided(pDescriptions)) {
      for (int i = 0; i < pDescriptions.length; i++) {
        addDescription(pDescriptions[i]);
      }
    }
  }

  // transformation

  public IDialogParameter transform() {
    return new DialogPlayerChoiceParameter(getTeamId(), getMode(), getPlayerIds(), getDescriptions(), getMaxSelects());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MODE, (getMode() != null) ? getMode().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MAX_SELECTS, getMaxSelects());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    String[] playerIds = getPlayerIds();
    String[] descriptions = getDescriptions();
    if (ArrayTool.isProvided(playerIds)) {
      for (int i = 0; i < playerIds.length; i++) {
        attributes = new AttributesImpl();
        UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, playerIds[i]);
        if (ArrayTool.isProvided(descriptions)) {
          UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, playerIds[i]);
          UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DESCRIPTION, descriptions[i]);
        }
        UtilXml.addEmptyElement(pHandler, _XML_TAG_PLAYER, attributes);
      }
    }
    UtilXml.endElement(pHandler, XML_TAG);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getId().getId());
    pByteList.addString(getTeamId());
    pByteList.addByte((byte) ((getMode() != null) ? getMode().getId() :0));
    pByteList.addByte((byte) getMaxSelects());
    pByteList.addStringArray(getPlayerIds());
    pByteList.addStringArray(getDescriptions());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    DialogId dialogId = DialogId.fromId(pByteArray.getByte());
    if (getId() != dialogId) {
      throw new IllegalStateException("Wrong dialog id. Expected " + getId().getName() + " received " + ((dialogId != null) ? dialogId.getName() : "null"));
    }
    fTeamId = pByteArray.getString();
    fMode = PlayerChoiceMode.fromId(pByteArray.getByte());
    fMaxSelects = pByteArray.getByte();
    addPlayerIds(pByteArray.getStringArray());
    addDescriptions(pByteArray.getStringArray());
    return byteArraySerializationVersion;
  }

  private static String[] findPlayerIds(Player[] pPlayers) {
    if (ArrayTool.isProvided(pPlayers)) {
      String[] playerIds = new String[pPlayers.length];
      for (int i = 0; i < playerIds.length; i++) {
        playerIds[i] = pPlayers[i].getId();
      }
      return playerIds;
    } else {
      return new String[0];
    }
  }

}
