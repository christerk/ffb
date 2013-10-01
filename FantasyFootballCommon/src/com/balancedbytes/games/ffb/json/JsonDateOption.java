package com.balancedbytes.games.ffb.json;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class JsonDateOption extends JsonAbstractOption {
  
  private static final DateFormat _TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // 2001-07-04T12:08:56.235
  
  public JsonDateOption(String pKey) {
    super(pKey);
  }
  
  public Date getFrom(JsonObject pJsonObject) {
    return toDate(getValueFrom(pJsonObject).asString());
  }
  
  public Date getFrom(JsonObject pJsonObject, Date pDefault) {
    return toDate(getValueFrom(pJsonObject, JsonValue.valueOf(toString(pDefault))).asString());
  }

  public void addTo(JsonObject pJsonObject, Date pValue) {
    addValueTo(pJsonObject, JsonValue.valueOf(toString(pValue)));
  }
  
  private Date toDate(String pDateString) {
    if (StringTool.isProvided(pDateString)) {
      try {
        return _TIMESTAMP_FORMAT.parse(pDateString);
      } catch (ParseException pParseException) {
        return null;
      }
    }
    return null;
  }

  private String toString(Date pDate) {
    if (pDate != null) {
      return _TIMESTAMP_FORMAT.format(pDate);
    }
    return null;
  }

}
