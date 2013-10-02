package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class ReRollSourceFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public ReRollSource forName(String pName) {
    for (ReRollSource source : ReRollSource.values()) {
      if (source.getName().equalsIgnoreCase(pName)) {
        return source;
      }
    }
    return null;
  }

  public ReRollSource forId(int pId) {
    for (ReRollSource source : ReRollSource.values()) {
      if (pId == source.getId()) {
        return source;
      }
    }
    return null;
  }

}
