package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum Weather implements INamedObject {
  
  SWELTERING_HEAT("Sweltering Heat", "heat", "Each player on the pitch may suffer from heat exhaustion on a roll of 1 before the next kick-off."),
  VERY_SUNNY("Very Sunny", "sunny", "A -1 modifier applies to all passing rolls."),
  NICE("Nice Weather", "nice", "Perfect Fantasy Football weather."),
  POURING_RAIN("Pouring Rain", "rain", "A -1 modifier applies to all catch, intercept, or pick-up rolls."),
  BLIZZARD("Blizzard", "blizzard", "Going For It fails on a roll of 1 or 2 and only quick or short passes can be attempted."),
  INTRO("Intro", "intro", "No weather at all, but the intro screen shown by the client.");
  
  private String fName;
  private String fShortName;
  private String fDescription;
  
  private Weather(String pName, String pShortName, String pDescription) {
    fName = pName;
    fShortName = pShortName;
    fDescription = pDescription;
  }
  
  public String getName() {
    return fName;
  }
  
  public String getShortName() {
    return fShortName;
  }
  
  public String getDescription() {
    return fDescription;
  }
  
}
