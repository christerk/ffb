package com.balancedbytes.games.ffb.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * 
 * @author Kalimar
 */
public abstract class JsonHandler {
	
	private JsonHandlerMode fMode;
	private Charset fCharset;
	private FieldNamingStrategy fFieldNamingStrategy;
	private Set<GsonAdapter> fGsonAdapters;
	
	public JsonHandler() {
		setMode(JsonHandlerMode.COMPACT_JSON);
		setCharset(Charset.forName("UTF-8"));
		fGsonAdapters = new HashSet<GsonAdapter>();
		setFieldNamingStrategy(
			new FieldNamingStrategy() {
	  		// by coding convention all our fields start with 'f' + Uppercase
	  		// we don't need to transfer that to the client side, so we filter it here
	    	public String translateName(Field pField) {
	    		String fieldName = pField.getName();
	    		if ((fieldName != null) && (fieldName.length() >= 2) && (fieldName.charAt(0) == 'f') && Character.isUpperCase(fieldName.charAt(1))) {
	    			StringBuilder translated = new StringBuilder();
	    			translated.append(Character.toLowerCase(fieldName.charAt(1)));
	    			if (fieldName.length() > 2) {
	    				translated.append(fieldName.substring(2));
	    			}
	    			return translated.toString(); 
	    		}
	    		return fieldName;
	    	}
	  	}
		);
	}

	public void setMode(JsonHandlerMode pMode) {
	  fMode = pMode;
  }
	
	public JsonHandlerMode getMode() {
	  return fMode;
  }
	
	public void setCharset(Charset pCharset) {
	  fCharset = pCharset;
  }

	public Charset getCharset() {
	  return fCharset;
  }
	
	public void addGsonAdapter(GsonAdapter pGsonAdapter) {
		pGsonAdapter.setJsonHandler(this);
		fGsonAdapters.add(pGsonAdapter);
	}
	
	public GsonAdapter[] getGsonAdapters() {
		return fGsonAdapters.toArray(new GsonAdapter[fGsonAdapters.size()]);
	}
	
	public void setFieldNamingStrategy(FieldNamingStrategy pFieldNamingStrategy) {
	  fFieldNamingStrategy = pFieldNamingStrategy;
  }
	
	public FieldNamingStrategy getFieldNamingStrategy() {
	  return fFieldNamingStrategy;
  }
	
	public boolean isPrettyPrinting() {
		return (JsonHandlerMode.PRETTY_JSON == getMode());
	}

	public String toJson(Object pSource) {
		if (getMode() == null) {
			return null;
		}
		switch (getMode()) {
  		case PRETTY_JSON:
  		case COMPACT_JSON:
  			return createGsonBuilder().create().toJson(pSource);
  		case BASE64_DEFLATED_JSON:
  			return Base64.encodeToString(toDeflatedJson(pSource), true);
			default:
				return null;
		}
	}

	protected byte[] toDeflatedJson(Object pSource) {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		try {
			DeflaterOutputStream deflaterOut = new DeflaterOutputStream(byteOut, new Deflater(Deflater.BEST_COMPRESSION));
  		JsonWriter jsonWriter = new JsonWriter(new BufferedWriter(new OutputStreamWriter(deflaterOut, getCharset())));
  		createGsonBuilder().create().toJson(pSource, pSource.getClass(), jsonWriter);
  		jsonWriter.close();
		} catch (IOException pIoException) {
			throw new FantasyFootballException(pIoException);
		}
		return byteOut.toByteArray();
	}

	public <T> T fromJson(String pJson, Class<T> pClass) {
		if (getMode() == null) {
			return null;
		}
		switch (getMode()) {
  		case PRETTY_JSON:
  		case COMPACT_JSON:
  			return createGsonBuilder().create().fromJson(pJson, pClass);
  		case BASE64_DEFLATED_JSON:
  			return fromDeflatedJson(Base64.decode(pJson), pClass);
			default:
				return null;
		}
	}

	protected <T> T fromDeflatedJson(byte[] pDeflatedJson, Class<T> pClass) {
		ByteArrayInputStream byteIn = new ByteArrayInputStream(pDeflatedJson);
		JsonReader jsonReader = new JsonReader(new BufferedReader(new InputStreamReader(new InflaterInputStream(byteIn), getCharset())));
		return createGsonBuilder().create().fromJson(jsonReader, pClass);
	}

	protected GsonBuilder createGsonBuilder() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		if (isPrettyPrinting()) {
			gsonBuilder.setPrettyPrinting();
		}
		gsonBuilder.setFieldNamingStrategy(getFieldNamingStrategy());
		for (GsonAdapter gsonAdapter : getGsonAdapters()) {
			gsonBuilder.registerTypeHierarchyAdapter(gsonAdapter.getType(), gsonAdapter);
		}
		return gsonBuilder;
	}

}
