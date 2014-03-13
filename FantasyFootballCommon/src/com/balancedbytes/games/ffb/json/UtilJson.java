package com.balancedbytes.games.ffb.json;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.IEnumWithName;
import com.balancedbytes.games.ffb.IEnumWithNameFactory;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class UtilJson {
  
  private static final DateFormat _TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // 2001-07-04T12:08:56.235
  
  private static final Charset _CHARSET = Charset.forName("UTF-8");
  
  public static JsonObject toJsonObject(JsonValue pJsonValue) {
    if ((pJsonValue == null) || !pJsonValue.isObject()) {
      throw new IllegalArgumentException("JsonValue is not an object.");
    }
    return pJsonValue.asObject();
  }

  public static JsonArray toJsonArray(JsonValue pJsonValue) {
    if ((pJsonValue == null) || !pJsonValue.isArray()) {
      throw new IllegalArgumentException("JsonValue is not an array.");
    }
    return pJsonValue.asArray();
  }
  
  public static FieldCoordinate toFieldCoordinate(JsonValue pJsonValue) {
    if ((pJsonValue == null) || pJsonValue.isNull()) {
      return null;
    }
    JsonArray jsonArray = pJsonValue.isArray() ? pJsonValue.asArray() : null;
    if ((jsonArray == null) || (jsonArray.size() != 2)) {
      throw new IllegalArgumentException("JsonValue is not a valid FieldCoordinate object.");
    }
    return new FieldCoordinate(jsonArray.get(0).asInt(), jsonArray.get(1).asInt());
  }
  
  public static JsonValue toJsonValue(FieldCoordinate pFieldCoordinate) {
    if (pFieldCoordinate == null) {
      return JsonValue.NULL;
    }
    JsonArray jsonArray = new JsonArray();
    jsonArray.add(pFieldCoordinate.getX());
    jsonArray.add(pFieldCoordinate.getY());
    return jsonArray;
  }
  
  public static Date toDate(JsonValue pValue) {
    if (!pValue.isNull() && pValue.isString()) {
      try {
        return _TIMESTAMP_FORMAT.parse(pValue.asString());
      } catch (ParseException pParseException) {
        return null;
      }
    }
    return null;
  }

  public static JsonValue toJsonValue(Date pDate) {
    if (pDate != null) {
      return JsonValue.valueOf(_TIMESTAMP_FORMAT.format(pDate));
    }
    return JsonValue.NULL;
  }
  
  public static PlayerState toPlayerState(JsonValue pJsonValue) {
    if ((pJsonValue == null) || pJsonValue.isNull()) {
      return null;
    }
    return new PlayerState(pJsonValue.asInt());
  }
  
  public static JsonValue toJsonValue(PlayerState pPlayerState) {
    if (pPlayerState == null) {
      return JsonValue.NULL;
    }
    return JsonValue.valueOf(pPlayerState.getId());
  }
  
  public static IEnumWithName toEnumWithName(IEnumWithNameFactory pFactory, JsonValue pJsonValue) {
    if (pFactory == null) {
      throw new IllegalArgumentException("Parameter factory must not be null.");
    }
    if ((pJsonValue == null) || pJsonValue.isNull()) {
      return null;
    }
    return pFactory.forName(pJsonValue.asString());
  }

  public static JsonValue toJsonValue(IEnumWithName pEnumWithName) {
    if (pEnumWithName == null) {
      return JsonValue.NULL;
    }
    return JsonValue.valueOf(pEnumWithName.getName());
  }
  
  public static String deflateToBase64(JsonValue pJsonValue) throws IOException {
    if (pJsonValue == null) {
      return null;
    }
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    DeflaterOutputStream deflaterOut = new DeflaterOutputStream(byteOut, new Deflater(Deflater.BEST_COMPRESSION));
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(deflaterOut, _CHARSET));
    out.write(pJsonValue.toString());
    out.close();
    return Base64.encodeToString(byteOut.toByteArray(), false);
  }
  
  public static byte[] gzip(JsonValue pJsonValue) throws IOException {
    if (pJsonValue == null) {
      return new byte[0];
    }
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut);
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(gzipOut, _CHARSET));
    out.write(pJsonValue.toString());
    out.close();
    return byteOut.toByteArray();
  }
  
  public static JsonValue inflateFromBase64(String pBase64DeflatedJson) throws IOException {
    if (!StringTool.isProvided(pBase64DeflatedJson)) {
      return null;
    }    
    ByteArrayInputStream byteIn = new ByteArrayInputStream(Base64.decodeFast(pBase64DeflatedJson));
    InputStreamReader in = new InputStreamReader(new InflaterInputStream(byteIn), _CHARSET);
    return JsonValue.readFrom(in);  // no bufferedReader necessary
  }
  
  public static JsonValue gunzip(byte[] pGzippedJson) throws IOException {
    if (!ArrayTool.isProvided(pGzippedJson)) {
      return null;
    }
    ByteArrayInputStream byteIn = new ByteArrayInputStream(pGzippedJson);
    InputStreamReader in = new InputStreamReader(new GZIPInputStream(byteIn), _CHARSET);
    return JsonValue.readFrom(in);  // no bufferedReader necessary
  }

}
