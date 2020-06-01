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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.INamedObjectFactory;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.util.ArrayTool;
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
  
  public static INamedObject toEnumWithName(INamedObjectFactory pFactory, JsonValue pJsonValue) {
    if (pFactory == null) {
      throw new IllegalArgumentException("Parameter factory must not be null.");
    }
    if ((pJsonValue == null) || pJsonValue.isNull()) {
      return null;
    }
    return pFactory.forName(pJsonValue.asString());
  }

  public static JsonValue toJsonValue(INamedObject pEnumWithName) {
    if (pEnumWithName == null) {
      return JsonValue.NULL;
    }
    return JsonValue.valueOf(pEnumWithName.getName());
  }
  
  public static byte[] gzip(JsonValue pJsonValue) throws IOException {
    if (pJsonValue == null) {
      return new byte[0];
    }
    ByteArrayOutputStream byteOut = null;
    GZIPOutputStream gzipOut = null;
    BufferedWriter out = null;
    OutputStreamWriter outWriter = null;
    try {
      byteOut = new ByteArrayOutputStream();
      gzipOut = new GZIPOutputStream(byteOut);
      outWriter = new OutputStreamWriter(gzipOut, _CHARSET);
      out = new BufferedWriter(outWriter);
      out.write(pJsonValue.toString());
      return byteOut.toByteArray();
    } finally {
      if (byteOut != null) {
        byteOut.close();
      }
      if (gzipOut != null) {
        gzipOut.close();
      }
      if (outWriter != null) {
        outWriter.close();
      }
      if (out != null) {
        out.close();
      }
    }
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
