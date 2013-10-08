package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum Weather implements IEnumWithId, IEnumWithName {
  
  SWELTERING_HEAT(1, "Sweltering Heat", "Each player on the pitch may suffer from heat exhaustion on a roll of 1 before the next kick-off."),
  VERY_SUNNY(2, "Very Sunny", "A -1 modifier applies to all passing rolls."),
  NICE(3, "Nice Weather", "Perfect Fantasy Football weather."),
  POURING_RAIN(4, "Pouring Rain", "A -1 modifier applies to all catch, intercept, or pick-up rolls."),
  BLIZZARD(5, "Blizzard", "Going For It fails on a roll of 1 or 2 and only quick or short passes can be attempted."),
  INTRO(6, "Intro", "No weather at all, but the intro screen shown by the client.");
  
  private int fId;
  private String fName;
  private String fDescription;
  
  private Weather(int pId, String pName, String pDescription) {
    fId = pId;
    fName = pName;
    fDescription = pDescription;
  }
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public String getDescription() {
    return fDescription;
  }
  
}
