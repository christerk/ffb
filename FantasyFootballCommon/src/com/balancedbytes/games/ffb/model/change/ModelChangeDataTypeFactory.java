package com.balancedbytes.games.ffb.model.change;

import com.balancedbytes.games.ffb.IEnumWithIdFactory;
import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class ModelChangeDataTypeFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public ModelChangeDataType forName(String pName) {
    for (ModelChangeDataType type : ModelChangeDataType.values()) {
      if (type.getName().equalsIgnoreCase(pName)) {
        return type;
      }
    }
    return null;
  }

  public ModelChangeDataType forId(int pId) {
    for (ModelChangeDataType type : ModelChangeDataType.values()) {
      if (type.getId() == pId) {
        return type;
      }
    }
    return null;
  }

}
