package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.option.GameOptionId;

/**
 * 
 * @author Kalimar
 */
public enum InducementType implements INamedObject {

  BLOODWEISER_BABES("bloodweiserBabes", "Bloodweiser Babes", "Bloodweiser Babe",
    "Bloodweiser Babes", GameOptionId.INDUCEMENT_KEGS_MAX, GameOptionId.INDUCEMENT_KEG_COST),
  BRIBES("bribes", "Bribes", "Bribe", "Bribes", GameOptionId.INDUCEMENT_BRIBES_MAX,
    GameOptionId.INDUCEMENT_BRIBE_COST, GameOptionId.INDUCEMENT_BRIBE_REDUCED_COST),
  EXTRA_TEAM_TRAINING("extraTeamTraining", "Extra Training", "Extra Team Training",
    "Extra Team Trainings", GameOptionId.INDUCEMENT_REROLLS_MAX, GameOptionId.INDUCEMENT_REROLL_COST),
  MASTER_CHEF("halflingMasterChef", "Halfling Master Chef", "Halfling Master Chef",
    "Halfling Master Chefs", GameOptionId.INDUCEMENT_CHEFS_MAX, GameOptionId.INDUCEMENT_CHEF_COST, GameOptionId.INDUCEMENT_CHEF_REDUCED_COST),
  IGOR("igor", "Igor", "Igor", "Igors", GameOptionId.INDUCEMENT_IGORS_MAX, GameOptionId.INDUCEMENT_IGOR_COST),
  WANDERING_APOTHECARIES("wanderingApothecaries", "Wandering Apo.", "Wandering Apothecary",
    "Wandering Apothecaries", GameOptionId.INDUCEMENT_APOS_MAX, GameOptionId.INDUCEMENT_APO_COST),
  WIZARD("wizard", "Wizard", "Wizard", "Wizards", GameOptionId.INDUCEMENT_WIZARDS_MAX, GameOptionId.INDUCEMENT_WIZARD_COST),
  STAR_PLAYERS("starPlayers", "Star Players", "Star Player", "Star Players", GameOptionId.INDUCEMENT_STARS_MAX, null),
  MERCENARIES("mercenaries", "Mercenaries", "Mercenary", "Mercenaries", null, null),
 
  CARD("card", null, null, null, null, null);
  
  private String fName;
  private String fDescription;
  private String fSingular;
  private String fPlural;
  private GameOptionId maxId;
  private GameOptionId costId;
  private GameOptionId reducedCostId;

  private InducementType(String pName, String pDescription, String pSingular, String pPlural, GameOptionId maxId, GameOptionId costId) {
    this(pName, pDescription, pSingular, pPlural, maxId, costId, costId);
  }

  private InducementType(String pName, String pDescription, String pSingular, String pPlural, GameOptionId maxId, GameOptionId costId, GameOptionId reducedCostId) {
    fName = pName;
    fDescription = pDescription;
    fSingular = pSingular;
    fPlural = pPlural;
    this.maxId = maxId;
    this.costId = costId;
    this.reducedCostId = reducedCostId;
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

  public GameOptionId getMaxId() {
    return maxId;
  }

  public GameOptionId getCostId() {
    return costId;
  }

  public GameOptionId getReducedCostId() {
    return reducedCostId;
  }
}
