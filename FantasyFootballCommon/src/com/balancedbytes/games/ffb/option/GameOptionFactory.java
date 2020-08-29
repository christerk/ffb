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

  // LRB6 ------------------------------
  // ARGUE_THE_CALL                false
  // FORCE_TREASURY_TO_PETTY_CASH  false
  // MVP_NOMINATIONS               0
  // PETTY_CASH_AFFECTS_TV         true
  // PILING_ON_USES_A_TEAM_REROLL  false
  // WIZARD_AVAILABLE              true
  // -----------------------------------

  public IGameOption createGameOption(GameOptionId pOptionId) {
    if (pOptionId == null) {
      return null;
    }
    switch (pOptionId) {
      case ALLOW_KTM_REROLL:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("Kick Team-Mate can be rerolled.");
      case ALLOW_STAR_ON_BOTH_TEAMS:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("A star player may play for both teams.");
      case ARGUE_THE_CALL:
        return new GameOptionBoolean(pOptionId)
          .setDefault(true)
          .setMessageFalse("Calls may not be argued.");
      case CARDS_DESPERATE_MEASURE_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(400000)
          .setMessage("Desperate Measure cards can be bought for $1 gps each.");
      case CARDS_DESPERATE_MEASURE_MAX:
        return new GameOptionInt(pOptionId)
          .setDefault(Integer.MAX_VALUE)
          .setMessage("Coaches may purchase up to $1 Desperate Measure cards.");
      case CARDS_DIRTY_TRICK_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(50000)
          .setMessage("Dirty Trick cards can be bought for $1 gps each.");
      case CARDS_DIRTY_TRICK_MAX:
        return new GameOptionInt(pOptionId)
          .setDefault(Integer.MAX_VALUE)
          .setMessage("Coaches may purchase up to $1 Dirty Trick cards.");
      case CARDS_GOOD_KARMA_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(100000)
          .setMessage("Good Karma cards can be bought for $1 gps each.");
      case CARDS_GOOD_KARMA_MAX:
        return new GameOptionInt(pOptionId)
          .setDefault(Integer.MAX_VALUE)
          .setMessage("Coaches may purchase up to $1 Good Karma cards.");
      case CARDS_MAGIC_ITEM_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(50000)
          .setMessage("Magic Item cards can be bought for $1 gps each.");
      case CARDS_MAGIC_ITEM_MAX:
        return new GameOptionInt(pOptionId)
          .setDefault(Integer.MAX_VALUE)
          .setMessage("Coaches may purchase up to $1 Magic Item cards.");
      case CARDS_MISCELLANEOUS_MAYHEM_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(50000)
          .setMessage("Miscellaneous Mayhem cards can be bought for $1 gps each.");
      case CARDS_MISCELLANEOUS_MAYHEM_MAX:
        return new GameOptionInt(pOptionId)
          .setDefault(Integer.MAX_VALUE)
          .setMessage("Coaches may purchase up to $1 Miscellaneous Mayhem cards.");
      case CARDS_RANDOM_EVENT_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(200000)
          .setMessage("Random Event cards can be bought for $1 gps each.");
      case CARDS_RANDOM_EVENT_MAX:
        return new GameOptionInt(pOptionId)
          .setDefault(Integer.MAX_VALUE)
          .setMessage("Coaches may purchase up to $1 Random Event cards.");
      case CARDS_SPECIAL_TEAM_PLAY_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(50000)
          .setMessage("Special Team Play cards can be bought for $1 gps each.");
      case CARDS_SPECIAL_TEAM_PLAY_MAX:
        return new GameOptionInt(pOptionId)
          .setDefault(Integer.MAX_VALUE)
          .setMessage("Coaches may purchase up to $1 Special Team Play cards.");
      case CHECK_OWNERSHIP:
        return new GameOptionBoolean(pOptionId)
          .setDefault(true)
          .setMessageFalse("Team ownership is not checked.");
      case CLAW_DOES_NOT_STACK:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("Claw does not stack with other skills that modify armour rolls.");
      case EXTRA_MVP:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("An extra MVP is awarded at the end of the match");
      case FORCE_TREASURY_TO_PETTY_CASH:
        return new GameOptionBoolean(pOptionId)
          .setDefault(true)
          .setMessageFalse("Treasury is not automatically transferred to Petty Cash.");
      case FOUL_BONUS:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("+1 to armour roll for a foul.");
      case FOUL_BONUS_OUTSIDE_TACKLEZONE:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("+1 to armour roll for a foul, if fouler is not in an opposing tacklezone.");
      case FREE_CARD_CASH:
        return new GameOptionInt(pOptionId)
          .setDefault(0)
          .setMessage("Both coaches get $1 extra gold to buy cards with.");
      case FREE_INDUCEMENT_CASH:
        return new GameOptionInt(pOptionId)
          .setDefault(0)
          .setMessage("Both coaches get $1 extra gold to buy inducements with.");
      case INDUCEMENTS:
        return new GameOptionBoolean(pOptionId)
          .setDefault(true)
          .setMessageFalse("Inducements are not available.");
      case INDUCEMENT_APOS_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(100000)
          .setMessage("Wandering apothecaries can be purchased for $1 gps each.");
      case INDUCEMENT_APOS_MAX:
        return new GameOptionInt(pOptionId)
          .setDefault(2)
          .setMessage("Coaches may purchase up to $1 wandering apothecarie(s).");
      case INDUCEMENT_BRIBES_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(100000)
          .setMessage("Bribes can be purchased for $1 gps each.");
      case INDUCEMENT_BRIBES_REDUCED_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(50000)
          .setMessage("Bribes for reduced price can be purchased for $1 gps each.");
      case INDUCEMENT_BRIBES_MAX:
        return new GameOptionInt(pOptionId)
          .setDefault(3)
          .setMessage("Coaches may purchase up to $1 bribe(s).");
      case INDUCEMENT_CHEFS_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(300000)
          .setMessage("Halfling Master Chefs can be purchased for $1 gps each.");
      case INDUCEMENT_CHEFS_REDUCED_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(100000)
          .setMessage("Halfling Master Chefs for reduced price can be purchased for $1 gps each.");
      case INDUCEMENT_CHEFS_MAX:
        return new GameOptionInt(pOptionId)
          .setDefault(1)
          .setMessage("Coaches may purchase up to $1 Halfling Master Chef(s).");
      case INDUCEMENT_IGORS_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(100000)
          .setMessage("Igors can be purchased for $1 gps each.");
      case INDUCEMENT_IGORS_MAX:
        return new GameOptionInt(pOptionId)
          .setDefault(1)
          .setMessage("Coaches may purchase up to $1 Igor(s).");
      case INDUCEMENT_KEGS_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(50000)
          .setMessage("Bloodweiser Kegs can be purchased for $1 gps each.");
      case INDUCEMENT_KEGS_MAX:
        return new GameOptionInt(pOptionId)
          .setDefault(2)
          .setMessage("Coaches may purchase up to $1 Bloodweiser Keg(s).");
      case INDUCEMENT_MERCENARIES_EXTRA_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(30000)
          .setMessage("Mercenaries can be purchased for an extra $1 gps each.");
      case INDUCEMENT_MERCENARIES_SKILL_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(50000)
          .setMessage("Mercenaries can can gain an extra skill for $1 gps.");
      case INDUCEMENT_MERCENARIES_MAX:
        return new GameOptionInt(pOptionId)
          .setDefault(Integer.MAX_VALUE)
          .setMessage("Coaches may purchase up to $1 Mercenaries.");
      case INDUCEMENT_EXTRA_TRAINING_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(100000)
          .setMessage("Rerolls can be purchased for $1 gps each.");
      case INDUCEMENT_EXTRA_TRAINING_MAX:
        return new GameOptionInt(pOptionId)
          .setDefault(4)
          .setMessage("Coaches may purchase up to $1 reroll(s).");
      case INDUCEMENT_STARS_MAX:
        return new GameOptionInt(pOptionId)
          .setDefault(2)
          .setMessage("Coaches may purchase up to $1 star(s).");
      case INDUCEMENT_WIZARDS_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(150000)
          .setMessage("Wizards can be purchased for $1 gps each.");
      case INDUCEMENT_WIZARDS_MAX:
        return new GameOptionInt(pOptionId)
          .setDefault(0)
          .setMessage("Coaches may purchase up to $1 wizard(s).");
      case MAX_NR_OF_CARDS:
        return new GameOptionInt(pOptionId)
          .setDefault(5)
          .setMessage("A maximum of $1 cards can be bought.");
      case MAX_PLAYERS_IN_WIDE_ZONE:
        return new GameOptionInt(pOptionId)
          .setDefault(2)
          .setMessage("A maximum of $1 players may be set up in a widezone.");
      case MAX_PLAYERS_ON_FIELD:
        return new GameOptionInt(pOptionId)
          .setDefault(11)
          .setMessage("A maximum of $1 players may be set up on the field.");
      case MIN_PLAYERS_ON_LOS:
        return new GameOptionInt(pOptionId)
          .setDefault(3)
          .setMessage("A minimum of $1 players must be set up on the line of scrimmage.");
      case MVP_NOMINATIONS:
        return new GameOptionInt(pOptionId)
          .setDefault(3)
          .setMessage("$1 players may be nominated to receice the MVP award.");
      case OVERTIME:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("Game will go into overtime if there is a draw after 2nd half.");
      case PETTY_CASH:
        return new GameOptionBoolean(pOptionId)
          .setDefault(true)
          .setMessageFalse("Petty Cash is not available.");
      case PETTY_CASH_AFFECTS_TV:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("Petty Cash affects Team Value.");
      case PILING_ON_ARMOR_ONLY:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("Piling On lets you re-roll armour-rolls only.");
      case PILING_ON_DOES_NOT_STACK:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("Piling On does not stack with other skills that modify armour- or injury-rolls.");
      case PILING_ON_INJURY_ONLY:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("Piling On lets you re-roll injury-rolls only.");
      case PILING_ON_TO_KO_ON_DOUBLE:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("Piling On player knocks himself out when rolling a double on armour or injury.");
      case PILING_ON_USES_A_TEAM_REROLL:
        return new GameOptionBoolean(pOptionId)
          .setDefault(true)
          .setMessageFalse("Piling On does not cost a Team Re-roll to use.");
      case PITCH_URL:
        return new GameOptionString(pOptionId)
          .setMessage("Custom pitch set.");
      case RIGHT_STUFF_CANCELS_TACKLE:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("Right Stuff prevents Tackle from negating Dodge for Pow/Pushback.");
      case SNEAKY_GIT_AS_FOUL_GUARD:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("Sneaky Git works like Guard for fouling assists.");
      case SNEAKY_GIT_BAN_TO_KO:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("Sneaky Git players that get banned are sent to the KO box instead.");
      case SPIKED_BALL:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("A Spiked Ball is used for play. Any failed Pickup or Catch roll results in the player being stabbed.");
      case STAND_FIRM_NO_DROP_ON_FAILED_DODGE:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("Stand Firm players do not drop on a failed dodge roll but end their move instead.");
      case TEST_MODE:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("Game is in TEST mode. No result will be uploaded. See help for available test commands.");
      case TURNTIME:
        return new GameOptionInt(pOptionId)
          .setDefault(240)
          .setMessage("Turntime is $1 sec.");
      case USE_PREDEFINED_INDUCEMENTS:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("Inducements are predefined.");
      case WIZARD_AVAILABLE:
        return new GameOptionBoolean(pOptionId)
          .setDefault(false)
          .setMessageTrue("A wizard may be bought as an inducement.");
      case INDUCEMENT_RIOTOUS_ROOKIES_MAX:
        return new GameOptionInt(pOptionId)
          .setDefault(1)
          .setMessage("Coaches my hire $1 groups of Riotous Rookies.");
      case INDUCEMENT_RIOTOUS_ROOKIES_COST:
        return new GameOptionInt(pOptionId)
          .setDefault(100000)
          .setMessage("Groups of Riotous Rookies can be purchased for $1 gps each.");
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
