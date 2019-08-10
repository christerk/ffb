package com.balancedbytes.games.ffb;


import com.balancedbytes.games.ffb.option.GameOptionId;

/**
 * 
 * @author Kalimar
 */
public enum CardType implements INamedObject {

  MISCELLANEOUS_MAYHEM("miscellaneousMayhem", "Miscellaneous Mayhem Deck", "Miscellaneous Mayhem Card",
    "Miscellaneous Mayhem Cards", GameOptionId.CARDS_MISCELLANEOUS_MAYHEM_MAX, GameOptionId.CARDS_MISCELLANEOUS_MAYHEM_COST),
  SPECIAL_TEAM_PLAY("specialTeamPlay", "Special Team Plays Deck", "Special Team Play Card",
    "Special Team Play Cards", GameOptionId.CARDS_SPECIAL_TEAM_PLAY_MAX, GameOptionId.CARDS_SPECIAL_TEAM_PLAY_COST),
  MAGIC_ITEM("magicItem", "Magic Items Deck", "Magic Item Card",
    "Magic Item Cards", GameOptionId.CARDS_MAGIC_ITEM_MAX, GameOptionId.CARDS_MAGIC_ITEM_COST),
  DIRTY_TRICK("dirtyTrick", "Dirty Tricks Deck", "Dirty Trick Card",
    "Dirty Trick Cards", GameOptionId.CARDS_DIRTY_TRICK_MAX, GameOptionId.CARDS_DIRTY_TRICK_COST),
  GOOD_KARMA("goodKarma", "Good Karma Deck", "Good Karma Card",
    "Good Karma Cards", GameOptionId.CARDS_GOOD_KARMA_MAX, GameOptionId.CARDS_GOOD_KARMA_COST),
  RANDOM_EVENT("randomEvent", "Random Events Deck", "Random Event Card",
    "Random Event Cards", GameOptionId.CARDS_RANDOM_EVENT_MAX, GameOptionId.CARDS_RANDOM_EVENT_COST),
  DESPERATE_MEASURE("desperateMeasure", "Desperate Measures Deck", "Desperate Measure Card",
    "Desperate Measure Cards", GameOptionId.CARDS_DESPERATE_MEASURE_MAX, GameOptionId.CARDS_DESPERATE_MEASURE_COST);
  
  private String fName;
  private String fDeckName;
  private String fInducementNameSingle;
  private String fInducementNameMultiple;
  private GameOptionId maxId;
  private GameOptionId costId;

  private CardType(String pName, String pDeckName, String pInducementNameSingle, String pInducementNameMultiple, GameOptionId maxId, GameOptionId costId) {
    fName = pName;
    fDeckName = pDeckName;
    fInducementNameSingle = pInducementNameSingle;
    fInducementNameMultiple = pInducementNameMultiple;
    this.maxId = maxId;
    this.costId = costId;
  }
  
  public String getName() {
    return fName;
  }
  
  public String getDeckName() {
	  return fDeckName;
  }
  
  public String getInducementNameSingle() {
	  return fInducementNameSingle;
  }
  
  public String getInducementNameMultiple() {
	  return fInducementNameMultiple;
  }
  
  public GameOptionId getMaxId() {
    return maxId;
  }

  public GameOptionId getCostId() {
    return costId;
  }

  public String toString() {
    return getName();
  }

}
