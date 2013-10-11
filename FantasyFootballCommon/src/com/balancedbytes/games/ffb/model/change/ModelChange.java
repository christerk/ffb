package com.balancedbytes.games.ffb.model.change;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ModelChange implements IByteArraySerializable {
	
	public static final String HOME = "home";
	public static final String AWAY = "away";
	
	private ModelChangeId fChangeId;
	private String fKey;
	private Object fValue;

	public ModelChange() {
		super();
	}

	public ModelChange(ModelChangeId pChangeId, String pKey, Object pValue) {
		setChangeId(pChangeId);
		setKey(pKey);
		setValue(pValue);
	}

	public ModelChangeId getChangeId() {
		return fChangeId;
	}
	
	public void setChangeId(ModelChangeId pChangeId) {
		fChangeId = pChangeId;
	}
		
	public String getKey() {
	  return fKey;
  }
	
	public void setKey(String pKey) {
	  fKey = pKey;
  }
	
	public Object getValue() {
	  return fValue;
  }
	
	public void setValue(Object pValue) {
	  fValue = pValue;
  }
	
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(fChangeId.getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(fKey);
    fChangeId.getDataType().addTo(pByteList, getValue());
  }
  
  public int initFrom(ByteArray pByteArray) {
    fChangeId = new ModelChangeIdFactory().forId(pByteArray.getSmallInt());
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fKey = pByteArray.getString();
    fValue = fChangeId.getDataType().initFrom(pByteArray);
    return byteArraySerializationVersion;
  }
	
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.MODEL_CHANGE_ID.addTo(jsonObject, fChangeId);
    IJsonOption.MODEL_CHANGE_KEY.addTo(jsonObject, fKey);
    IJsonOption.MODEL_CHANGE_VALUE.addTo(jsonObject, fChangeId.toJsonValue(fValue));
    return jsonObject;
  }
  
  public ModelChange initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fChangeId = (ModelChangeId) IJsonOption.MODEL_CHANGE_ID.getFrom(jsonObject);
    fKey = IJsonOption.MODEL_CHANGE_KEY.getFrom(jsonObject);
    fValue = fChangeId.fromJsonValue(IJsonOption.MODEL_CHANGE_VALUE.getFrom(jsonObject));
    return this;
  }
	
}
