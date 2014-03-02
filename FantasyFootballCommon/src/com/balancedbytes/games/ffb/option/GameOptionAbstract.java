package com.balancedbytes.games.ffb.option;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.IXmlReadable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public abstract class GameOptionAbstract implements IGameOption {
  
  private GameOptionId fId;

  public GameOptionAbstract(GameOptionId pId) {
    fId = pId;
  }

  @Override
  public GameOptionId getId() {
    return fId;
  }
  
  protected void setId(GameOptionId pId) {
    fId = pId;
  }
  
  @Override
  public GameOptionAbstract reset() {
    setValue(getDefaultAsString());
    return this;
  }

  @Override
  public boolean isChanged() {
    return !StringTool.print(getDefaultAsString()).equals(getValueAsString());
  }

  protected abstract String getDefaultAsString();
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.GAME_OPTION_ID.addTo(jsonObject, getId());
    IJsonOption.GAME_OPTION_VALUE.addTo(jsonObject, getValueAsString());
    return jsonObject;
  }
  
  public GameOptionAbstract initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    setId((GameOptionId) IJsonOption.GAME_OPTION_ID.getFrom(jsonObject));
    setValue(IJsonOption.GAME_OPTION_VALUE.getFrom(jsonObject));
    return this;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_NAME, (getId() != null) ? getId().getName() : null);
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_VALUE, getValueAsString());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    UtilXml.endElement(pHandler, XML_TAG);
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    if (XML_TAG.equals(pXmlTag)) {
      String name = UtilXml.getStringAttribute(pXmlAttributes, XML_ATTRIBUTE_NAME);
      setId(new GameOptionIdFactory().forName(name));
      String value = UtilXml.getStringAttribute(pXmlAttributes, XML_ATTRIBUTE_VALUE);
      setValue(value);
    }
    return this;
  }
  
  public boolean endXmlElement(String pXmlTag, String pValue) {
    return XML_TAG.equals(pXmlTag);
  }

}
