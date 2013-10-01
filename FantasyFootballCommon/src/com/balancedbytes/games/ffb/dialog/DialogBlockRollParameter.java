package com.balancedbytes.games.ffb.dialog;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogBlockRollParameter implements IDialogParameter {

  private static final String _XML_ATTRIBUTE_CHOOSING_TEAM_ID = "choosingTeamId";
  private static final String _XML_ATTRIBUTE_NR_OF_DICE = "nrOfDice";
  private static final String _XML_ATTRIBUTE_BLOCK_ROLL = "blockRoll";
  private static final String _XML_ATTRIBUTE_TEAM_RE_ROLL_OPTION = "teamReRollOption";
  private static final String _XML_ATTRIBUTE_PRO_RE_ROLL_OPTION = "proReRollOption";

  private String fChoosingTeamId;
  private int fNrOfDice;
  private int[] fBlockRoll;
  private boolean fTeamReRollOption;
  private boolean fProReRollOption;

  public DialogBlockRollParameter() {
    super();
  }
  
  public DialogBlockRollParameter(String pChoosingTeamId, int pNrOfDice, int[] pBlockRoll, boolean pTeamReRollOption, boolean pProReRollOption) {
    fChoosingTeamId = pChoosingTeamId;
    fNrOfDice = pNrOfDice;
    fBlockRoll = pBlockRoll;
    fTeamReRollOption = pTeamReRollOption;
    fProReRollOption = pProReRollOption;
  }
  
  public DialogId getId() {
    return DialogId.BLOCK_ROLL;
  }

  public String getChoosingTeamId() {
    return fChoosingTeamId;
  }
  
  public int getNrOfDice() {
    return fNrOfDice;
  }
  
  public int[] getBlockRoll() {
    return fBlockRoll;
  }
  
  public boolean hasTeamReRollOption() {
    return fTeamReRollOption;
  }
  
  public boolean hasProReRollOption() {
    return fProReRollOption;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogBlockRollParameter(getChoosingTeamId(), getNrOfDice(), getBlockRoll(), hasTeamReRollOption(), hasProReRollOption());
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CHOOSING_TEAM_ID, getChoosingTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NR_OF_DICE, getNrOfDice());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_BLOCK_ROLL, getBlockRoll());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_RE_ROLL_OPTION, hasTeamReRollOption());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PRO_RE_ROLL_OPTION, hasProReRollOption());
    UtilXml.addEmptyElement(pHandler, XML_TAG, attributes);
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
    pByteList.addString(getChoosingTeamId());
    pByteList.addByte((byte) getNrOfDice());
    pByteList.addByteArray(getBlockRoll());
    pByteList.addBoolean(hasTeamReRollOption());
    pByteList.addBoolean(hasProReRollOption());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fChoosingTeamId = pByteArray.getString();
    fNrOfDice = pByteArray.getByte();
    fBlockRoll = pByteArray.getByteArrayAsIntArray();
    fTeamReRollOption = pByteArray.getBoolean();
    fProReRollOption = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.CHOOSING_TEAM_ID.addTo(jsonObject, fChoosingTeamId);
    IJsonOption.NR_OF_DICE.addTo(jsonObject, fNrOfDice);
    IJsonOption.BLOCK_ROLL.addTo(jsonObject, fBlockRoll);
    IJsonOption.TEAM_RE_ROLL_OPTION.addTo(jsonObject, fTeamReRollOption);
    IJsonOption.PRO_RE_ROLL_OPTION.addTo(jsonObject, fProReRollOption);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fChoosingTeamId = IJsonOption.CHOOSING_TEAM_ID.getFrom(jsonObject);
    fNrOfDice = IJsonOption.NR_OF_DICE.getFrom(jsonObject);
    fBlockRoll = IJsonOption.BLOCK_ROLL.getFrom(jsonObject);
    fTeamReRollOption = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(jsonObject);
    fProReRollOption = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(jsonObject);
  }

}
