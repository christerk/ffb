package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public class SeriousInjuryFactory implements IEnumWithNameFactory {

  public SeriousInjury forName(String pName) {
    for (SeriousInjury seriousInjury : SeriousInjury.values()) {
      if (seriousInjury.getName().equals(pName)) {
        return seriousInjury;
      }
    }
    return null;
  }

}
