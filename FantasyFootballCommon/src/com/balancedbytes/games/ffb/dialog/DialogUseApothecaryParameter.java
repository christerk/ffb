package com.balancedbytes.games.ffb.dialog;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class DialogUseApothecaryParameter implements IDialogParameter {
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_PLAYER_STATE = "playerState";
  private static final String _XML_ATTRIBUTE_SERIOUS_INJURY = "seriousInjury";
  
  private String fPlayerId;
  private PlayerState fPlayerState;
  private SeriousInjury fSeriousInjury;

  public DialogUseApothecaryParameter() {
    super();
  }
  
  public DialogUseApothecaryParameter(String pPlayerId, PlayerState pPlayerState, SeriousInjury pSeriousInjury) {
    fPlayerId = pPlayerId;
    fPlayerState = pPlayerState;
    fSeriousInjury = pSeriousInjury;
  }
  
  public DialogId getId() {
    return DialogId.USE_APOTHECARY;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public PlayerState getPlayerState() {
    return fPlayerState;
  }
  
  public SeriousInjury getSeriousInjury() {
    return fSeriousInjury;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogUseApothecaryParameter(getPlayerId(), getPlayerState(), getSeriousInjury());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_STATE, (getPlayerState() != null) ? getPlayerState().getId() : 0);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SERIOUS_INJURY, (getSeriousInjury() != null) ? getSeriousInjury().getName() : null);
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
    pByteList.addSmallInt((getPlayerState() != null) ? getPlayerState().getId() : 0);
    pByteList.addByte((byte) ((getSeriousInjury() != null) ? getSeriousInjury().getId() : 0));
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt(); 
    DialogId dialogId = DialogId.fromId(pByteArray.getByte());
    if (getId() != dialogId) {
      throw new IllegalStateException("Wrong dialog id. Expected " + getId().getName() + " received " + ((dialogId != null) ? dialogId.getName() : "null"));
    }
    fPlayerId = pByteArray.getString();
    fPlayerState = new PlayerState(pByteArray.getSmallInt());
    fSeriousInjury = SeriousInjury.fromId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }

}
