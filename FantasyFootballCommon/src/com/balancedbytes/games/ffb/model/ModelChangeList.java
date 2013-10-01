package com.balancedbytes.games.ffb.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.xml.IXmlWriteable;
import com.balancedbytes.games.ffb.xml.UtilXml;


/**
 * 
 * @author Kalimar
 */
public class ModelChangeList implements IByteArraySerializable, IXmlWriteable {
  
  public static final String XML_TAG = "modelChangeList";

  private List<IModelChange> fChanges;
  
  private ModelChangeList(int pInitialCapacity) {
    fChanges = new ArrayList<IModelChange>(pInitialCapacity);
  }

  public ModelChangeList() {
    this(20);
  }

  public void add(IModelChange pChange) {
    fChanges.add(pChange);
  }
  
  public void add(ModelChangeList pChanges) {
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
  
  public ModelChangeList copy() {
    ModelChangeList copiedList = new ModelChangeList(size());
    Iterator<IModelChange> changeIterator = fChanges.iterator();
    while (changeIterator.hasNext()) {
      IModelChange change = changeIterator.next();
      copiedList.add(change);
    }
    return copiedList;
  }
  
  // transformation
  
  public ModelChangeList transform() {
    ModelChangeList transformedList = new ModelChangeList(size());
    Iterator<IModelChange> changeIterator = fChanges.iterator();
    while (changeIterator.hasNext()) {
      IModelChange change = changeIterator.next();
      transformedList.add(change.transform());
    }
    return transformedList;
  }
  
  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    UtilXml.startElement(pHandler, XML_TAG);
    if (size() > 0) {
      Iterator<IModelChange> changeIterator = fChanges.iterator();
      while (changeIterator.hasNext()) {
        IModelChange change = changeIterator.next();
        change.addToXml(pHandler);
      }
    }
    UtilXml.endElement(pHandler, XML_TAG);
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
    pByteList.addSmallInt(fChanges.size());
    Iterator<IModelChange> changeIterator = fChanges.iterator();
    while (changeIterator.hasNext()) {
      IModelChange change = changeIterator.next();
      change.addTo(pByteList);
    }
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    int size = pByteArray.getSmallInt();
    for (int i = 0; i < size; i++) {
      ModelChangeId modelChangeId = ModelChangeId.fromId(pByteArray.getByte(pByteArray.getPosition()));
      if (modelChangeId != null) {
        IModelChange modelChange = modelChangeId.createModelChange();
        modelChange.initFrom(pByteArray);
        add(modelChange);
      }
    }
    return byteArraySerializationVersion;
  }
  
}
