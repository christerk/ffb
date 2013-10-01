package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ServerCommandVersion extends ServerCommand {
  
  private static final String _XML_ATTRIBUTE_SERVER_VERSION = "serverVersion";
  private static final String _XML_ATTRIBUTE_CLIENT_VERSION = "clientVersion";
  private static final String _XML_ATTRIBUTE_NAME = "name";
  private static final String _XML_ATTRIBUTE_VALUE = "value";
  
  private static final String _XML_TAG_CLIENT_PROPERTIES = "clientProperties";
  private static final String _XML_TAG_PROPERTY = "property";
  
  private String fServerVersion;
  private String fClientVersion;
  private Map<String, String> fClientProperties;
  
  public ServerCommandVersion() {
    fClientProperties = new HashMap<String, String>();
  }
  
  public ServerCommandVersion(String pServerVersion, String pClientVersion, String[] pClientProperties, String[] pClientPropertyValues) {
    this();
    fServerVersion = pServerVersion;
    fClientVersion = pClientVersion;
    if (ArrayTool.isProvided(pClientProperties) && ArrayTool.isProvided(pClientPropertyValues)) {
      for (int i = 0; i < pClientProperties.length; i++) {
        fClientProperties.put(pClientProperties[i], pClientPropertyValues[i]);
      }
    }
  }
  
  public NetCommandId getId() {
    return NetCommandId.SERVER_VERSION;
  }
  
  public String getServerVersion() {
    return fServerVersion;
  }
  
  public String getClientVersion() {
    return fClientVersion;
  }
  
  public String[] getClientProperties() {
    return fClientProperties.keySet().toArray(new String[fClientProperties.size()]);
  }
  
  public String getClientPropertyValue(String pClientProperty) {
    return fClientProperties.get(pClientProperty);
  }
    
  public boolean isReplayable() {
    return false;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    if (getCommandNr() > 0) {
      UtilXml.addAttribute(attributes, XML_ATTRIBUTE_COMMAND_NR, getCommandNr());
    }
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SERVER_VERSION, getServerVersion());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CLIENT_VERSION, getClientVersion());
    UtilXml.startElement(pHandler, getId().getName(), attributes);
    if (fClientProperties.size() > 0) {
      UtilXml.startElement(pHandler, _XML_TAG_CLIENT_PROPERTIES);
      for (Map.Entry<String, String> property : fClientProperties.entrySet()) {
        attributes = new AttributesImpl();
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NAME, property.getKey());
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_VALUE, property.getValue());
        UtilXml.addEmptyElement(pHandler, _XML_TAG_PROPERTY, attributes);
      }
      UtilXml.endElement(pHandler, _XML_TAG_CLIENT_PROPERTIES);
    }
    UtilXml.endElement(pHandler, getId().getName());
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
    pByteList.addSmallInt(getCommandNr());
    pByteList.addString(getServerVersion());
    pByteList.addString(getClientVersion());
    String[] clientProperies = getClientProperties();
    pByteList.addStringArray(clientProperies);
    List<String> clientPropertyValues = new ArrayList<String>();
    for (String clientProperty: clientProperies) {
      clientPropertyValues.add(getClientPropertyValue(clientProperty));
    }
    pByteList.addStringArray(clientPropertyValues.toArray(new String[clientPropertyValues.size()]));
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    fServerVersion = pByteArray.getString();
    fClientVersion = pByteArray.getString();
    String[] clientProperties = pByteArray.getStringArray();
    String[] clientPropertyValues = pByteArray.getStringArray();
    if (ArrayTool.isProvided(clientProperties) && ArrayTool.isProvided(clientPropertyValues)) {
      for (int i = 0; i < clientProperties.length; i++) {
        fClientProperties.put(clientProperties[i], clientPropertyValues[i]);
      }
    }
    return byteArraySerializationVersion;
  }
    
}
