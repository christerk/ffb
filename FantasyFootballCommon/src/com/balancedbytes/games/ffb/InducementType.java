package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public enum InducementType implements IEnumWithId, IEnumWithName {

  BLOODWEISER_BABES(1, "bloodweiserBabes", "Bloodweiser Babes", "Bloodweiser Babe", "Bloodweiser Babes"),
  BRIBES(2, "bribes", "Bribes", "Bribe", "Bribes"),
  EXTRA_TEAM_TRAINING(3, "extraTeamTraining", "Extra Training", "Extra Team Training", "Extra Team Trainings"),
  MASTER_CHEF(4, "halflingMasterChef", "Halfling Master Chef", "Halfling Master Chef", "Halfling Master Chefs"),
  IGOR(5, "igor", "Igor", "Igor", "Igors"),
  WANDERING_APOTHECARIES(6, "wanderingApothecaries", "Wandering Apo.", "Wandering Apothecary", "Wandering Apothecaries"),
  WIZARD(7, "wizard", "Wizard", "Wizard", "Wizards"),
  STAR_PLAYERS(8, "starPlayers", "Star Players", "Star Player", "Star Players"),
  MERCENARIES(9, "mercenaries", "Mercenaries", "Mercenary", "Mercenaries"),
  
  CARD(10, "card", null, null, null);
  
  private int fId;
  private String fName;
  private String fDescription;
  private String fSingular;
  private String fPlural;

  private InducementType(int pValue, String pName, String pDescription, String pSingular, String pPlural) {
    fId = pValue;
    fName = pName;
    fDescription = pDescription;
    fSingular = pSingular;
    fPlural = pPlural;
  }

  public int getId() {
    return fId;
  }

  public String getDescription() {
    return fDescription;
  }
  
  public String getSingular() {
    return fSingular;
  }
  
  public String getPlural() {
    return fPlural;
  }

  public String getName() {
    return fName;
  }
  
}
