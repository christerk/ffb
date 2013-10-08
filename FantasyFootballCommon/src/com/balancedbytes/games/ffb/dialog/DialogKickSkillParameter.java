package com.balancedbytes.games.ffb.dialog;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.FieldCoordinate;
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
public class DialogKickSkillParameter implements IDialogParameter {
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";

  private static final String _XML_ATTRIBUTE_X = "x";
  private static final String _XML_ATTRIBUTE_Y = "y";
  private static final String _XML_TAG_BALL_COORDINATE = "ballCoordinate";
  private static final String _XML_TAG_BALL_COORDINATE_WITH_KICK = "ballCoordinateWithKick";
  
  private String fPlayerId;
  private FieldCoordinate fBallCoordinate;
  private FieldCoordinate fBallCoordinateWithKick;

  public DialogKickSkillParameter() {
    super();
  }
  
  public DialogKickSkillParameter(String pPlayerId, FieldCoordinate pBallCoordinate, FieldCoordinate pBallCoordinateWithKick) {
    fPlayerId = pPlayerId;
    fBallCoordinate = pBallCoordinate;
    fBallCoordinateWithKick = pBallCoordinateWithKick;
  }
  
  public DialogId getId() {
    return DialogId.KICK_SKILL;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public FieldCoordinate getBallCoordinate() {
    return fBallCoordinate;
  }

  public FieldCoordinate getBallCoordinateWithKick() {
    return fBallCoordinateWithKick;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogKickSkillParameter(getPlayerId(), FieldCoordinate.transform(getBallCoordinate()), FieldCoordinate.transform(getBallCoordinateWithKick()));
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    if (getBallCoordinate() != null) {
      attributes = new AttributesImpl();
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, getBallCoordinate().getX());
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, getBallCoordinate().getY());
      UtilXml.addEmptyElement(pHandler, _XML_TAG_BALL_COORDINATE, attributes);
    }
    if (getBallCoordinateWithKick() != null) {
      attributes = new AttributesImpl();
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, getBallCoordinateWithKick().getX());
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, getBallCoordinateWithKick().getY());
      UtilXml.addEmptyElement(pHandler, _XML_TAG_BALL_COORDINATE_WITH_KICK, attributes);
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
    pByteList.addString(getPlayerId());
    pByteList.addFieldCoordinate(getBallCoordinateWithKick());
    pByteList.addFieldCoordinate(getBallCoordinate());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fPlayerId = pByteArray.getString();
    fBallCoordinateWithKick = pByteArray.getFieldCoordinate();
    fBallCoordinate = pByteArray.getFieldCoordinate();
    return byteArraySerializationVersion;
  }

  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.BALL_COORDINATE.addTo(jsonObject, fBallCoordinate);
    IJsonOption.BALL_COORDINATE_WITH_KICK.addTo(jsonObject, fBallCoordinateWithKick);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fBallCoordinate = IJsonOption.BALL_COORDINATE.getFrom(jsonObject);
    fBallCoordinateWithKick = IJsonOption.BALL_COORDINATE_WITH_KICK.getFrom(jsonObject);
  }

}
