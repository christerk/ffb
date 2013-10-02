package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ClientCommandFollowupChoice extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_CHOICE_FOLLOWUP = "choiceFollowup";
  
  private boolean fChoiceFollowup;
  
  public ClientCommandFollowupChoice() {
    super();
  }

  public ClientCommandFollowupChoice(boolean pChoiceReceive) {
    fChoiceFollowup = pChoiceReceive;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_FOLLOWUP_CHOICE;
  }
  
  public boolean isChoiceFollowup() {
    return fChoiceFollowup;
  }
  
  // XML serialization
    
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CHOICE_FOLLOWUP, isChoiceFollowup());
    UtilXml.addEmptyElement(pHandler, getId().getName(), attributes);
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
    pByteList.addBoolean(isChoiceFollowup());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fChoiceFollowup = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.CHOICE_FOLLOWUP.addTo(jsonObject, fChoiceFollowup);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fChoiceFollowup = IJsonOption.CHOICE_FOLLOWUP.getFrom(jsonObject);
  }
    
}
