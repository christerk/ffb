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
public class ClientCommandInterceptorChoice extends NetCommand {

  private static final String _XML_ATTRIBUTE_INTERCEPTOR_ID = "interceptorId";

  private String fInterceptorId;

  public ClientCommandInterceptorChoice() {
    super();
  }

  public ClientCommandInterceptorChoice(String pInterceptorId) {
    fInterceptorId = pInterceptorId;
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_INTERCEPTOR_CHOICE;
  }

  public String getInterceptorId() {
    return fInterceptorId;
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_INTERCEPTOR_ID, getInterceptorId());
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
    pByteList.addString(getInterceptorId());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fInterceptorId = pByteArray.getString();
    return byteArraySerializationVersion;
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.INTERCEPTOR_ID.addTo(jsonObject, fInterceptorId);
    return jsonObject;
  }

  public ClientCommandInterceptorChoice initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fInterceptorId = IJsonOption.INTERCEPTOR_ID.getFrom(jsonObject);
    return this;
  }

}
