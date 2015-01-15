package com.balancedbytes.games.ffb.option;

import org.xml.sax.Attributes;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class GameOptionFactory {
  
  public IGameOption createGameOption(GameOptionId pOptionId) {
    if (pOptionId == null) {
      return null;
    }
    switch (pOptionId) {
      case ALLOW_STAR_ON_BOTH_TEAMS:
        return new GameOptionBoolean(GameOptionId.ALLOW_STAR_ON_BOTH_TEAMS)
          .setDefault(false)
          .setMessageTrue("A star player may play for both teams.");
      case CHECK_OWNERSHIP:
        return new GameOptionBoolean(GameOptionId.CHECK_OWNERSHIP)
          .setDefault(true)
          .setMessageFalse("Team ownership is not checked.");
      case CLAW_DOES_NOT_STACK:
        return new GameOptionBoolean(GameOptionId.CLAW_DOES_NOT_STACK)
          .setDefault(false)
          .setMessageTrue("Claw does not stack with other skills that modify armour rolls.");
      case EXTRA_MVP:
        return new GameOptionBoolean(GameOptionId.EXTRA_MVP)
          .setDefault(false)
          .setMessageTrue("An extra MVP is awarded at the end of the match");
      case FORCE_TREASURY_TO_PETTY_CASH:
        return new GameOptionBoolean(GameOptionId.FORCE_TREASURY_TO_PETTY_CASH)
          .setDefault(false)
          .setMessageTrue("Treasury is automatically transferred to Petty Cash.");
      case FOUL_BONUS:
        return new GameOptionBoolean(GameOptionId.FOUL_BONUS)
          .setDefault(false)
          .setMessageTrue("+1 to armour roll for a foul.");
      case FOUL_BONUS_OUTSIDE_TACKLEZONE:
        return new GameOptionBoolean(GameOptionId.FOUL_BONUS_OUTSIDE_TACKLEZONE)
          .setDefault(false)
          .setMessageTrue("+1 to armour roll for a foul, if fouler is not in an opposing tacklezone.");
      case FREE_CARD_CASH:
        return new GameOptionInt(GameOptionId.FREE_CARD_CASH)
          .setDefault(0)
          .setMessage("Both coaches get $1 extra gold to buy cards with.");
      case FREE_INDUCEMENT_CASH:
        return new GameOptionInt(GameOptionId.FREE_INDUCEMENT_CASH)
          .setDefault(0)
          .setMessage("Both coaches get $1 extra gold to buy inducements with.");
      case INDUCEMENTS:
        return new GameOptionBoolean(GameOptionId.INDUCEMENTS)
          .setDefault(true)
          .setMessageFalse("Inducements are not available.");
      case MAX_NR_OF_CARDS:
        return new GameOptionInt(GameOptionId.MAX_NR_OF_CARDS)
          .setDefault(5)
          .setMessage("A maximum of $1 cards can be bought.");
      case MAX_PLAYERS_IN_WIDE_ZONE:
        return new GameOptionInt(GameOptionId.MAX_PLAYERS_IN_WIDE_ZONE)
          .setDefault(2)
          .setMessage("A maximum of $1 players may be set up in a widezone.");
      case MAX_PLAYERS_ON_FIELD:
        return new GameOptionInt(GameOptionId.MAX_PLAYERS_ON_FIELD)
          .setDefault(11)
          .setMessage("A maximum of $1 players may be set up on the field.");
      case MIN_PLAYERS_ON_LOS:
        return new GameOptionInt(GameOptionId.MIN_PLAYERS_ON_LOS)
          .setDefault(3)
          .setMessage("A minimum of $1 players must be set up on the line of scrimmage.");
      case OVERTIME:
        return new GameOptionBoolean(GameOptionId.OVERTIME)
          .setDefault(false)
          .setMessageTrue("Game will go into overtime if there is a draw after 2nd half.");
      case PETTY_CASH:
        return new GameOptionBoolean(GameOptionId.PETTY_CASH)
          .setDefault(true)
          .setMessageFalse("Petty Cash is not available.");
      case PILING_ON_ARMOR_ONLY:
        return new GameOptionBoolean(GameOptionId.PILING_ON_ARMOR_ONLY)
          .setDefault(false)
          .setMessageTrue("Piling On lets you re-roll armour-rolls only.");
      case PILING_ON_DOES_NOT_STACK:
        return new GameOptionBoolean(GameOptionId.PILING_ON_DOES_NOT_STACK)
          .setDefault(false)
          .setMessageTrue("Piling On does not stack with other skills that modify armour- or injury-rolls.");
      case PILING_ON_INJURY_ONLY:
        return new GameOptionBoolean(GameOptionId.PILING_ON_INJURY_ONLY)
          .setDefault(false)
          .setMessageTrue("Piling On lets you re-roll injury-rolls only.");
      case PILING_ON_TO_KO_ON_DOUBLE:
        return new GameOptionBoolean(GameOptionId.PILING_ON_TO_KO_ON_DOUBLE)
          .setDefault(false)
          .setMessageTrue("Piling On player knocks himself out when rolling a double on armour or injury.");
      case PITCH_URL:
        return new GameOptionString(GameOptionId.PITCH_URL)
          .setMessage("Custom pitch set.");
      case RIGHT_STUFF_CANCELS_TACKLE:
        return new GameOptionBoolean(GameOptionId.RIGHT_STUFF_CANCELS_TACKLE)
          .setDefault(false)
          .setMessageTrue("Right Stuff prevents Tackle from negating Dodge for Pow/Pushback.");
      case SNEAKY_GIT_AS_FOUL_GUARD:
        return new GameOptionBoolean(GameOptionId.SNEAKY_GIT_AS_FOUL_GUARD)
          .setDefault(false)
          .setMessageTrue("Sneaky Git works like Guard for fouling assists.");
      case SNEAKY_GIT_BAN_TO_KO:
        return new GameOptionBoolean(GameOptionId.SNEAKY_GIT_BAN_TO_KO)
          .setDefault(false)
          .setMessageTrue("Sneaky Git players that get banned are sent to the KO box instead.");
      case SPIKED_BALL:
        return new GameOptionBoolean(GameOptionId.SPIKED_BALL)
          .setDefault(false)
          .setMessageTrue("A Spiked Ball is used for play. Any failed Pickup or Catch roll results in the player being stabbed.");
      case STAND_FIRM_NO_DROP_ON_FAILED_DODGE:
        return new GameOptionBoolean(GameOptionId.STAND_FIRM_NO_DROP_ON_FAILED_DODGE)
          .setDefault(false)
          .setMessageTrue("Stand Firm players do not drop on a failed dodge roll but end their move instead.");
      case TEST_MODE:
        return new GameOptionBoolean(GameOptionId.TEST_MODE)
          .setDefault(false)
          .setMessageTrue("Game is in TEST mode. No result will be uploaded. See help for available test commands.");
      case TURNTIME:
        return new GameOptionInt(GameOptionId.TURNTIME)
          .setDefault(240)
          .setMessage("Turntime is $1 sec.");
      default:
        return null;
    }    
  }
  
  // JSON serialization
  
  public IGameOption fromJsonValue(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    GameOptionId optionId = (GameOptionId) IJsonOption.GAME_OPTION_ID.getFrom(jsonObject);
    IGameOption gameOption = createGameOption(optionId);
    if (gameOption != null) {
      gameOption.setValue(IJsonOption.GAME_OPTION_VALUE.getFrom(jsonObject));
    }
    return gameOption;
  }
  
  // XML serialization
  
  public IGameOption fromXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    IGameOption option = null;
    if (IGameOption.XML_TAG.equals(pXmlTag)) {
      String name = UtilXml.getStringAttribute(pXmlAttributes, IGameOption.XML_ATTRIBUTE_NAME);
      String value = UtilXml.getStringAttribute(pXmlAttributes, IGameOption.XML_ATTRIBUTE_VALUE);
      GameOptionId optionId = new GameOptionIdFactory().forName(name);
      option = createGameOption(optionId);
      if (option != null) {
        option.setValue(value);
      }
    }
    return option;
  }

}
