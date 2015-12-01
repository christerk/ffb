package com.balancedbytes.games.ffb.model.change;

import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class ModelChangeDataTypeFactory implements IEnumWithNameFactory {
  
  public ModelChangeDataType forName(String pName) {
    for (ModelChangeDataType type : ModelChangeDataType.values()) {
      if (type.getName().equalsIgnoreCase(pName)) {
        return type;
      }
    }
    return null;
  }

}
