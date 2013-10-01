package com.balancedbytes.games.ffb.dialog;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;

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
  private boolean fProOption;
  private boolean fFumble;

  public DialogReRollParameter() {
    super();
  }
  
  public DialogReRollParameter(String pPlayerId, ReRolledAction pReRolledAction, int pMinimumRoll, boolean pTeamReRollOption, boolean pProOption, boolean pFumble) {
    fPlayerId = pPlayerId;
    fReRolledAction = pReRolledAction;
    fMinimumRoll = pMinimumRoll;
    fTeamReRollOption = pTeamReRollOption;
    fProOption = pProOption;
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
  
  public boolean isProOption() {
    return fProOption;
  }
  
  public boolean isFumble() {
	  return fFumble;
  }

  // transformation

  public IDialogParameter transform() {
    return new DialogReRollParameter(getPlayerId(), getReRolledAction(), getMinimumRoll(), isTeamReRollOption(), isProOption(), isFumble());
  }
    
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_RE_ROLLED_ACTION, (getReRolledAction() != null) ? getReRolledAction().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MINIMUM_ROLL, getMinimumRoll());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_RE_ROLL_OPTION, isTeamReRollOption());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PRO_OPTION, isProOption());
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
    pByteList.addBoolean(isProOption());
    pByteList.addBoolean(isFumble());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    DialogId dialogId = DialogId.fromId(pByteArray.getByte());
    if (getId() != dialogId) {
      throw new IllegalStateException("Wrong dialog id. Expected " + getId().getName() + " received " + ((dialogId != null) ? dialogId.getName() : "null"));
    }
    fPlayerId = pByteArray.getString();
    fReRolledAction = ReRolledAction.fromId(pByteArray.getByte());
    fMinimumRoll = pByteArray.getByte();
    fTeamReRollOption = pByteArray.getBoolean();
    fProOption = pByteArray.getBoolean();
    if (byteArraySerializationVersion > 1) {
    	fFumble = pByteArray.getBoolean();
    }
    return byteArraySerializationVersion;
  }

}
