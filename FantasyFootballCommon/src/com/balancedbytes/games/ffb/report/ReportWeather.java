package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.WeatherFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ReportWeather implements IReport {
  
  private Weather fWeather;
  private int[] fWeatherRoll;
  
  public ReportWeather() {
    super();
  }

  public ReportWeather(Weather pWeather, int[] pRoll) {
    fWeather = pWeather;
    fWeatherRoll = pRoll;
  }
  
  public ReportId getId() {
    return ReportId.WEATHER;
  }
  
  public Weather getWeather() {
    return fWeather;
  }
  
  public int[] getWeatherRoll() {
    return fWeatherRoll;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportWeather(getWeather(), getWeatherRoll());
  }

  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fWeather = new WeatherFactory().forId(pByteArray.getByte());
    fWeatherRoll = pByteArray.getByteArrayAsIntArray();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.WEATHER.addTo(jsonObject, fWeather);
    IJsonOption.WEATHER_ROLL.addTo(jsonObject, fWeatherRoll);
    return jsonObject;
  }
  
  public ReportWeather initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fWeather = (Weather) IJsonOption.WEATHER.getFrom(jsonObject);
    fWeatherRoll = IJsonOption.WEATHER_ROLL.getFrom(jsonObject);
    return this;
  }
    
}
