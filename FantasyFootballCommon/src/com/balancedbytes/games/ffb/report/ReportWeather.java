package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.WeatherFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.xml.UtilXml;



/**
 * 
 * @author Kalimar
 */
public class ReportWeather implements IReport {
  
  private static final String _XML_ATTRIBUTE_WEATHER = "weather";
  private static final String _XML_ATTRIBUTE_ROLL = "roll";
  
  private Weather fWeather;
  private int[] fRoll;
  
  public ReportWeather() {
    super();
  }

  public ReportWeather(Weather pWeather, int[] pRoll) {
    fWeather = pWeather;
    fRoll = pRoll;
  }
  
  public ReportId getId() {
    return ReportId.WEATHER;
  }
  
  public Weather getWeather() {
    return fWeather;
  }
  
  public int[] getRoll() {
    return fRoll;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportWeather(getWeather(), getRoll());
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_WEATHER, (getWeather() != null) ? getWeather().getName() : null);    
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL, getRoll());
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
    if (getWeather() != null) {
      pByteList.addByte((byte) getWeather().getId());
    } else {
      pByteList.addByte((byte) 0);
    }
    pByteList.addByteArray(getRoll());
  }
  
  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fWeather = new WeatherFactory().forId(pByteArray.getByte());
    fRoll = pByteArray.getByteArrayAsIntArray();
    return byteArraySerializationVersion;
  }
    
}
