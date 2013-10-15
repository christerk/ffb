package com.balancedbytes.games.ffb.model.change;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class ModelChangeList implements IByteArraySerializable, IJsonSerializable {
  
  private List<ModelChange> fChanges;
  
  public ModelChangeList() {
  	this(16);
  }

  public ModelChangeList(int pInitialCapacity) {
  	fChanges = new ArrayList<ModelChange>(pInitialCapacity);
  }

  public void add(ModelChange pChange) {
    fChanges.add(pChange);
  }
  
  public void add(ModelChangeList pChangeList) {
    if (pChangeList != null) {
      for (ModelChange change : pChangeList.getChanges()) {
        add(change);
      }
    }
  }
  
  public ModelChange[] getChanges() {
    return fChanges.toArray(new ModelChange[fChanges.size()]);
  }
  
  public void clear() {
    fChanges.clear();
  }
  
  public int size() {
    return fChanges.size();
  }
  
  public void applyTo(Game pGame) {
    ModelChangeProcessor processor = new ModelChangeProcessor();
    for (ModelChange change : getChanges()) {
    	processor.apply(pGame, change);
    }
  }
  
  // transformation
  
  public ModelChangeList transform() {
    ModelChangeList transformedList = new ModelChangeList(size());
    ModelChangeProcessor processor = new ModelChangeProcessor();
    for (ModelChange change : getChanges()) {
    	transformedList.add(processor.transform(change));
    }
    return transformedList;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addSmallInt(fChanges.size());
    for (ModelChange modelChange : fChanges) {
      modelChange.addTo(pByteList);
    }
  }
  
  public int initFrom(ByteArray pByteArray) {
    clear();
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    int size = pByteArray.getSmallInt();
    for (int i = 0; i < size; i++) {
      ModelChange modelChange = new ModelChange();
      modelChange.initFrom(pByteArray);
      add(modelChange);
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    JsonArray modelChanges = new JsonArray();
    for (ModelChange change : fChanges) {
      modelChanges.add(change.toJsonValue());
    }
    IJsonOption.MODEL_CHANGES.addTo(jsonObject, modelChanges);
    return jsonObject;
  }
  
  public ModelChangeList initFrom(JsonValue pJsonValue) {
    clear();
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    JsonArray modelChanges = IJsonOption.MODEL_CHANGES.getFrom(jsonObject);
    for (int i = 0; i < modelChanges.size(); i++) {
      add(new ModelChange().initFrom(modelChanges.get(i)));
    }
    return this;
  }
     
}
