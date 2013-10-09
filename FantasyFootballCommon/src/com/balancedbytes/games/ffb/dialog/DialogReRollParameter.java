package com.balancedbytes.games.ffb.dialog;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.ReRolledActionFactory;
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
public class DialogReRollParameter implements IDialogParameter {
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_RE_ROLLED_ACTION = "reRolledAction";
  private static final String _XML_ATTRIBUTE_MINIMUM_ROLL = "minimumRoll";
  private static final String _XML_ATTRIBUTE_TEAM_RE_ROLL_OPTION = "teamReRollOption";
  private static final String _XML_ATTRIBUTE_PRO_OPTION = "proOption";
  private static final String _XML_ATTRIBUTE_FUMBLE = "fumble";
  
  private String fPlayerId;
  private ReRolledAction fReRolledAction;
  private int fMinimumRoll;
  private boolean fTeamReRollOption;
  private boolean fProReRollOption;
  private boolean fFumble;

  public DialogReRollParameter() {
    super();
  }
  
  public DialogReRollParameter(String pPlayerId, ReRolledAction pReRolledAction, int pMinimumRoll, boolean pTeamReRollOption, boolean pProReRollOption, boolean pFumble) {
    fPlayerId = pPlayerId;
    fReRolledAction = pReRolledAction;
    fMinimumRoll = pMinimumRoll;
    fTeamReRollOption = pTeamReRollOption;
    fProReRollOption = pProReRollOption;
    fFumble = pFumble;
  }
  
  public DialogId getId() {
    return DialogId.RE_ROLL;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public ReRolledAction getReRolledAction() {
    return fReRolledAction;
  }
  
  public int getMinimumRoll() {
    return fMinimumRoll;
  }
  
  public boolean isTeamReRollOption() {
    return fTeamReRollOption;
  }
  
  public boolean isProReRollOption() {
    return fProReRollOption;
  }
  
  public boolean isFumble() {
	  return fFumble;
  }

  // transformation

  public IDialogParameter transform() {
    return new DialogReRollParameter(getPlayerId(), getReRolledAction(), getMinimumRoll(), isTeamReRollOption(), isProReRollOption(), isFumble());
  }
    
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_RE_ROLLED_ACTION, (getReRolledAction() != null) ? getReRolledAction().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MINIMUM_ROLL, getMinimumRoll());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_RE_ROLL_OPTION, isTeamReRollOption());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PRO_OPTION, isProReRollOption());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_FUMBLE, isFumble());
    UtilXml.addEmptyElement(pHandler, XML_TAG, attributes);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 2;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getId().getId());
    pByteList.addString(getPlayerId());
    pByteList.addByte((byte) ((getReRolledAction() != null) ? getReRolledAction().getId() : 0));
    pByteList.addByte((byte) getMinimumRoll());
    pByteList.addBoolean(isTeamReRollOption());
    pByteList.addBoolean(isProReRollOption());
    pByteList.addBoolean(isFumble());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fPlayerId = pByteArray.getString();
    fReRolledAction = new ReRolledActionFactory().forId(pByteArray.getByte());
    fMinimumRoll = pByteArray.getByte();
    fTeamReRollOption = pByteArray.getBoolean();
    fProReRollOption = pByteArray.getBoolean();
    if (byteArraySerializationVersion > 1) {
    	fFumble = pByteArray.getBoolean();
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.RE_ROLLED_ACTION.addTo(jsonObject, fReRolledAction);
    IJsonOption.MINIMUM_ROLL.addTo(jsonObject, fMinimumRoll);
    IJsonOption.TEAM_RE_ROLL_OPTION.addTo(jsonObject, fTeamReRollOption);
    IJsonOption.PRO_RE_ROLL_OPTION.addTo(jsonObject, fProReRollOption);
    IJsonOption.FUMBLE.addTo(jsonObject, fFumble);
    return jsonObject;
  }
  
  public DialogReRollParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fReRolledAction = (ReRolledAction) IJsonOption.RE_ROLLED_ACTION.getFrom(jsonObject);
    fMinimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(jsonObject);
    fTeamReRollOption = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(jsonObject);
    fProReRollOption = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(jsonObject);
    fFumble = IJsonOption.FUMBLE.getFrom(jsonObject);
    return this;
  }

}
