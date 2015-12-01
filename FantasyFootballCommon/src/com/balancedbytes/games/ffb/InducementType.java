package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public enum InducementType implements IEnumWithName {

  BLOODWEISER_BABES("bloodweiserBabes", "Bloodweiser Babes", "Bloodweiser Babe", "Bloodweiser Babes"),
  BRIBES("bribes", "Bribes", "Bribe", "Bribes"),
  EXTRA_TEAM_TRAINING("extraTeamTraining", "Extra Training", "Extra Team Training", "Extra Team Trainings"),
  MASTER_CHEF("halflingMasterChef", "Halfling Master Chef", "Halfling Master Chef", "Halfling Master Chefs"),
  IGOR("igor", "Igor", "Igor", "Igors"),
  WANDERING_APOTHECARIES("wanderingApothecaries", "Wandering Apo.", "Wandering Apothecary", "Wandering Apothecaries"),
  WIZARD("wizard", "Wizard", "Wizard", "Wizards"),
  STAR_PLAYERS("starPlayers", "Star Players", "Star Player", "Star Players"),
  MERCENARIES("mercenaries", "Mercenaries", "Mercenary", "Mercenaries"),
 
  CARD("card", null, null, null);
  
  private String fName;
  private String fDescription;
  private String fSingular;
  private String fPlural;

  private InducementType(String pName, String pDescription, String pSingular, String pPlural) {
    fName = pName;
    fDescription = pDescription;
    fSingular = pSingular;
    fPlural = pPlural;
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
