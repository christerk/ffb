package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.SpecialEffectFactory;
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
public class ReportSpecialEffectRoll implements IReport {

  private static final String _XML_ATTRIBUTE_SPECIAL_EFFECT = "specialEffect";
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_ROLL = "roll";
  private static final String _XML_ATTRIBUTE_SUCCESSFUL = "successful";

  private SpecialEffect fSpecialEffect;
  private String fPlayerId;
  private int fRoll;
  private boolean fSuccessful;

  public ReportSpecialEffectRoll() {
    super();
  }

  public ReportSpecialEffectRoll(SpecialEffect pSpecialEffect, String pPlayerId, int pRoll, boolean pSuccessful) {
    fSpecialEffect = pSpecialEffect;
    fPlayerId = pPlayerId;
    fRoll = pRoll;
    fSuccessful = pSuccessful;
  }

  public ReportId getId() {
    return ReportId.SPELL_EFFECT_ROLL;
  }

  public SpecialEffect getSpecialEffect() {
    return fSpecialEffect;
  }

  public String getPlayerId() {
    return fPlayerId;
  }

  public int getRoll() {
    return fRoll;
  }

  public boolean isSuccessful() {
    return fSuccessful;
  }

  // transformation

  public IReport transform() {
    return new ReportSpecialEffectRoll(getSpecialEffect(), getPlayerId(), getRoll(), isSuccessful());
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SPECIAL_EFFECT, (getSpecialEffect() != null) ? getSpecialEffect().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL, getRoll());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SUCCESSFUL, isSuccessful());
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
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) ((getSpecialEffect() != null) ? getSpecialEffect().getId() : 0));
    pByteList.addString(getPlayerId());
    pByteList.addByte((byte) getRoll());
    pByteList.addBoolean(isSuccessful());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fSpecialEffect = new SpecialEffectFactory().forId(pByteArray.getByte());
    fPlayerId = pByteArray.getString();
    fRoll = pByteArray.getByte();
    fSuccessful = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.SPECIAL_EFFECT.addTo(jsonObject, fSpecialEffect);
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.ROLL.addTo(jsonObject, fRoll);
    IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
    return jsonObject;
  }
  
  public ReportSpecialEffectRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fSpecialEffect = (SpecialEffect) IJsonOption.SPECIAL_EFFECT.getFrom(jsonObject);
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fRoll = IJsonOption.ROLL.getFrom(jsonObject);
    fSuccessful = IJsonOption.SUCCESSFUL.getFrom(jsonObject);
    return this;
  }

}
