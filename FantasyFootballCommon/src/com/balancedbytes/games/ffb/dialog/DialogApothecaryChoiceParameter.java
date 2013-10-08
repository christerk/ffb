package com.balancedbytes.games.ffb.dialog;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.SeriousInjuryFactory;
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
public class DialogApothecaryChoiceParameter implements IDialogParameter {

  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_PLAYER_STATE_OLD = "playerStateOld";
  private static final String _XML_ATTRIBUTE_PLAYER_STATE_NEW = "playerStateNew";
  private static final String _XML_ATTRIBUTE_SERIOUS_INJURY_OLD = "seriousInjuryOld";
  private static final String _XML_ATTRIBUTE_SERIOUS_INJURY_NEW = "seriousInjuryNew";

  private String fPlayerId;
  private PlayerState fPlayerStateOld;
  private SeriousInjury fSeriousInjuryOld;
  private PlayerState fPlayerStateNew;
  private SeriousInjury fSeriousInjuryNew;

  public DialogApothecaryChoiceParameter() {
    super();
  }

  public DialogApothecaryChoiceParameter(
    String pPlayerId,
    PlayerState pPlayerStateOld,
    SeriousInjury pSeriousInjuryOld,
    PlayerState pPlayerStateNew,
    SeriousInjury pSeriousInjuryNew
  ) {
    fPlayerId = pPlayerId;
    fPlayerStateOld = pPlayerStateOld;
    fSeriousInjuryOld = pSeriousInjuryOld;
    fPlayerStateNew = pPlayerStateNew;
    fSeriousInjuryNew = pSeriousInjuryNew;
  }

  public DialogId getId() {
    return DialogId.APOTHECARY_CHOICE;
  }

  public String getPlayerId() {
    return fPlayerId;
  }

  public PlayerState getPlayerStateOld() {
    return fPlayerStateOld;
  }

  public SeriousInjury getSeriousInjuryOld() {
    return fSeriousInjuryOld;
  }

  public PlayerState getPlayerStateNew() {
    return fPlayerStateNew;
  }

  public SeriousInjury getSeriousInjuryNew() {
    return fSeriousInjuryNew;
  }

  // transformation

  public IDialogParameter transform() {
    return new DialogApothecaryChoiceParameter(getPlayerId(), getPlayerStateOld(), getSeriousInjuryOld(), getPlayerStateNew(), getSeriousInjuryNew());
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_STATE_OLD, (getPlayerStateOld() != null) ? getPlayerStateOld().getId() : 0);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SERIOUS_INJURY_OLD, (getSeriousInjuryOld() != null) ? getSeriousInjuryOld().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_STATE_NEW, (getPlayerStateNew() != null) ? getPlayerStateNew().getId() : 0);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SERIOUS_INJURY_NEW, (getSeriousInjuryNew() != null) ? getSeriousInjuryNew().getName() : null);
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
    pByteList.addString(getPlayerId());
    pByteList.addSmallInt((getPlayerStateOld() != null) ? getPlayerStateOld().getId() : 0);
    pByteList.addByte((byte) ((getSeriousInjuryOld() != null) ? getSeriousInjuryOld().getId() : 0));
    pByteList.addSmallInt((getPlayerStateNew() != null) ? getPlayerStateNew().getId() : 0);
    pByteList.addByte((byte) ((getSeriousInjuryNew() != null) ? getSeriousInjuryNew().getId() : 0));
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fPlayerId = pByteArray.getString();
    fPlayerStateOld = new PlayerState(pByteArray.getSmallInt());
    fSeriousInjuryOld = new SeriousInjuryFactory().forId(pByteArray.getByte());
    fPlayerStateNew = new PlayerState(pByteArray.getSmallInt());
    fSeriousInjuryNew = new SeriousInjuryFactory().forId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.PLAYER_STATE_OLD.addTo(jsonObject, fPlayerStateOld);
    IJsonOption.SERIOUS_INJURY_OLD.addTo(jsonObject, fSeriousInjuryOld);
    IJsonOption.PLAYER_STATE_NEW.addTo(jsonObject, fPlayerStateNew);
    IJsonOption.SERIOUS_INJURY_NEW.addTo(jsonObject, fSeriousInjuryNew);
    return jsonObject;
  }
  
  public DialogApothecaryChoiceParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fPlayerStateOld = IJsonOption.PLAYER_STATE_OLD.getFrom(jsonObject);
    fSeriousInjuryOld = (SeriousInjury) IJsonOption.SERIOUS_INJURY_OLD.getFrom(jsonObject);
    fPlayerStateNew = IJsonOption.PLAYER_STATE_NEW.getFrom(jsonObject);
    fSeriousInjuryNew = (SeriousInjury) IJsonOption.SERIOUS_INJURY_NEW.getFrom(jsonObject);
    return this;
  }

}
