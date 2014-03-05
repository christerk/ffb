package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class WeatherFactory implements IEnumWithIdFactory, IEnumWithNameFactory {

  public Weather forId(int pId) {
    if (pId > 0) {
      for (Weather weather : Weather.values()) {
        if (pId == weather.getId()) {
          return weather;
        }
      }
    }
    return null;
  }
    
  public Weather forName(String pName) {
    for (Weather weather : Weather.values()) {
      if (weather.getName().equalsIgnoreCase(pName)) {
        return weather;
      }
    }
    return null;
  }
  
  public Weather forShortName(String pShortName) {
    for (Weather weather : Weather.values()) {
      if (weather.getShortName().equalsIgnoreCase(pShortName)) {
        return weather;
      }
    }
    return null;
  }

}
