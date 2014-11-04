package com.balancedbytes.games.ffb.model.change.old;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.IByteArrayReadable;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.change.ModelChangeList;


/**
 * 
 * @author Kalimar
 */
public class ModelChangeListOld implements IByteArrayReadable {
  
  private List<IModelChange> fChanges;
  
  private ModelChangeListOld(int pInitialCapacity) {
    fChanges = new ArrayList<IModelChange>(pInitialCapacity);
  }

  public ModelChangeListOld() {
    this(20);
  }

  public void add(IModelChange pChange) {
    fChanges.add(pChange);
  }
  
  public void add(ModelChangeListOld pChanges) {
    if (pChanges != null) {
      for (IModelChange change : pChanges.getChanges()) {
        add(change);
      }
    }
  }
  
  public IModelChange[] getChanges() {
    return fChanges.toArray(new IModelChange[fChanges.size()]);
  }
  
  public void clear() {
    fChanges.clear();
  }
  
  public int size() {
    return fChanges.size();
  }
  
  public void applyTo(Game pGame) {
    Iterator<IModelChange> changeIterator = fChanges.iterator();
    while (changeIterator.hasNext()) {
      IModelChange change = changeIterator.next();
      change.applyTo(pGame);
    }
  }
  
  public ModelChangeListOld copy() {
    ModelChangeListOld copiedList = new ModelChangeListOld(size());
    Iterator<IModelChange> changeIterator = fChanges.iterator();
    while (changeIterator.hasNext()) {
      IModelChange change = changeIterator.next();
      copiedList.add(change);
    }
    return copiedList;
  }
  
  public ModelChangeList convert() {
    ModelChangeList changeList = new ModelChangeList();
    for (IModelChange modelChange : getChanges()) {
      changeList.add(modelChange.convert());
    }
    return changeList;
  }
  
  // transformation
  
  public ModelChangeListOld transform() {
    ModelChangeListOld transformedList = new ModelChangeListOld(size());
    Iterator<IModelChange> changeIterator = fChanges.iterator();
    while (changeIterator.hasNext()) {
      IModelChange change = changeIterator.next();
      transformedList.add(change.transform());
    }
    return transformedList;
  }
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    int size = pByteArray.getSmallInt();
    for (int i = 0; i < size; i++) {
      ModelChangeIdOld modelChangeId = ModelChangeIdOld.fromId(pByteArray.getByte(pByteArray.getPosition()));
      if (modelChangeId != null) {
        IModelChange modelChange = modelChangeId.createModelChange();
        modelChange.initFrom(pByteArray);
        add(modelChange);
      }
    }
    return byteArraySerializationVersion;
  }
  
}
