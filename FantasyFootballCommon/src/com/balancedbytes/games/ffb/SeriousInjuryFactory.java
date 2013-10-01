package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public class SeriousInjuryFactory implements IEnumWithIdFactory, IEnumWithNameFactory {

  public SeriousInjury forId(int pId) {
    for (SeriousInjury seriousInjury : SeriousInjury.values()) {
      if (seriousInjury.getId() == pId) {
        return seriousInjury;
      }
    }
    return null;
  }
  
  public SeriousInjury forName(String pName) {
    for (SeriousInjury seriousInjury : SeriousInjury.values()) {
      if (seriousInjury.getName().equals(pName)) {
        return seriousInjury;
      }
    }
    return null;
  }

}
