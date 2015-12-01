package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class ReRollSourceFactory implements IEnumWithNameFactory {
  
  public ReRollSource forName(String pName) {
    for (ReRollSource source : ReRollSource.values()) {
      if (source.getName().equalsIgnoreCase(pName)) {
        return source;
      }
    }
    return null;
  }

}
