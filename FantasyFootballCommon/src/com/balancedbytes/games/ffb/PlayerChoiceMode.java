package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum PlayerChoiceMode implements IEnumWithId, IEnumWithName {
  
  TENTACLES(1, "tentacles"),
  SHADOWING(2, "shadowing"),
  DIVING_TACKLE(3, "divingTackle"),
  FEED(4, "feed"),
  DIVING_CATCH(5, "divingCatch"),
  CARD(6, "card");
  
  private int fId;
  private String fName;
  
  private PlayerChoiceMode(int pValue, String pName) {
    fId = pValue;
    fName = pName;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public String getDialogHeader(int pNrOfPlayers) {
    StringBuilder header = new StringBuilder();
    switch (this) {
      case DIVING_TACKLE:
        header.append("Select a player to use ").append(Skill.DIVING_TACKLE.getName());
        break;
      case SHADOWING:
        header.append("Select a player to use ").append(Skill.SHADOWING.getName());
        break;
      case TENTACLES:
        header.append("Select a player to use ").append(Skill.TENTACLES.getName());
        break;
      case FEED:
        header.append("Select a player to feed on");
        break;
      case DIVING_CATCH:
        header.append("Select a player to use ").append(Skill.DIVING_CATCH.getName());
        break;
      case CARD:
      	header.append("Select a player to play this card on");
      default:
      	break;
    }
    return header.toString();
  }
  
  public String getStatusTitle() {
    StringBuilder title = new StringBuilder();
    switch (this) {
      case DIVING_TACKLE:
        title.append(Skill.DIVING_TACKLE.getName());
        break;
      case SHADOWING:
        title.append(Skill.SHADOWING.getName());
        break;
      case TENTACLES:
        title.append(Skill.TENTACLES.getName());
        break;
      case FEED:
        title.append("Feed on player");
        break;
      case DIVING_CATCH:
        title.append(Skill.DIVING_CATCH.getName());
        break;
      case CARD:
      	title.append("Play Card");
      	break;
      default:
      	break;
    }
    return title.toString();
  }
  
  public String getStatusMessage() {
    StringBuilder message = new StringBuilder();
    switch (this) {
      case DIVING_TACKLE:
        message.append("Waiting for coach to use ").append(Skill.DIVING_TACKLE.getName()).append(".");
        break;
      case SHADOWING:
        message.append("Waiting for coach to use ").append(Skill.SHADOWING.getName()).append(".");
        break;
      case TENTACLES:
        message.append("Waiting for coach to use ").append(Skill.TENTACLES.getName()).append(".");
        break;
      case FEED:
        message.append("Waiting for coach to choose player to feed on.");
        break;
      case DIVING_CATCH:
        message.append("Waiting for coach to use ").append(Skill.DIVING_CATCH.getName()).append(".");
        break;
      case CARD:
      	message.append("Waiting for coach to play card on player.");
      	break;
      default:
      	break;
    }
    return message.toString();
  }
  
}
