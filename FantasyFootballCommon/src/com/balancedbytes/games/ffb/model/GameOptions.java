package com.balancedbytes.games.ffb.model;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArrayReadable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeId;
import com.balancedbytes.games.ffb.old.GameOptionOld;
import com.balancedbytes.games.ffb.old.GameOptionValueOld;
import com.balancedbytes.games.ffb.option.GameOptionBoolean;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.GameOptionInt;
import com.balancedbytes.games.ffb.option.GameOptionString;
import com.balancedbytes.games.ffb.option.IGameOption;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class GameOptions implements IXmlSerializable, IByteArrayReadable, IJsonSerializable {

  public static final String XML_TAG = "options";

  public static final String XML_TAG_OPTION = "option";

  private static final String _XML_ATTRIBUTE_NAME = "name";
  private static final String _XML_ATTRIBUTE_VALUE = "value";

  private Map<String, IGameOption> fOptionByName;

  private transient Game fGame;

  public GameOptions(Game pGame) {
    fGame = pGame;
    fOptionByName = new HashMap<String, IGameOption>();
  }

  public Game getGame() {
    return fGame;
  }

  public void addOption(GameOptionValueOld pGameOption) {
    if ((pGameOption != null) && (pGameOption.getOption() != null)) {
      GameOptionValueOld oldOption = getOptionValue(pGameOption.getOption());
      if ((oldOption == null) || (oldOption.getValue() != pGameOption.getValue())) {
        oldOption = null;
        // check for other options of the same group, reset if found
        if ((pGameOption.getGroup() != null) && pGameOption.isChanged()) {
          GameOptionValueOld[] oldValues = getOptionValues();
          for (GameOptionValueOld oldValue : oldValues) {
            if (pGameOption.getGroup().equals(oldValue.getGroup())) {
              oldOption = oldValue;
              oldOption.setValue(0);
              break;
            }
          }
        }
        if (oldOption != null) {
          fOptionValueByName.put(oldOption.getOption(), oldOption);
          notifyObservers(ModelChangeId.GAME_OPTIONS_ADD_OPTION, oldOption);
        }
        fOptionValueByName.put(pGameOption.getOption(), pGameOption);
        notifyObservers(ModelChangeId.GAME_OPTIONS_ADD_OPTION, pGameOption);
      }
    }
  }

  public GameOptionValueOld getOptionValue(GameOptionOld pOptionName) {
    GameOptionValueOld value = fOptionValueByName.get(pOptionName);
    if (value == null) {
      return new GameOptionValueOld(pOptionName, pOptionName.getDefaultValue());
    } else {
      return value;
    }
  }

  public GameOptionValueOld[] getOptionValues() {
    return fOptionValueByName.values().toArray(new GameOptionValueOld[fOptionValueByName.size()]);
  }

  public void init(GameOptions pOtherOptions) {
    if (pOtherOptions != null) {
      for (GameOptionValueOld option : pOtherOptions.getOptionValues()) {
        addOption(option);
      }
    }
  }

  // change tracking

  private void notifyObservers(ModelChangeId pChangeId, Object pValue) {
    if ((getGame() == null) || (pChangeId == null)) {
      return;
    }
    getGame().notifyObservers(new ModelChange(pChangeId, null, pValue));
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    UtilXml.startElement(pHandler, XML_TAG);
    for (GameOptionValueOld option : getOptionValues()) {
      option.addToXml(pHandler);
    }
    UtilXml.endElement(pHandler, XML_TAG);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  public IXmlSerializable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    if (GameOptionValueOld.XML_TAG.equals(pXmlTag)) {
      GameOptionValueOld gameOption = new GameOptionValueOld();
      gameOption.startXmlElement(pXmlTag, pXmlAttributes);
      addOption(gameOption);
      return gameOption;
    }
    return this;
  }

  public boolean endXmlElement(String pXmlTag, String pValue) {
    return XML_TAG.equals(pXmlTag);
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 1;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    GameOptionValueOld[] options = getOptionValues();
    pByteList.addSmallInt(options.length);
    for (GameOptionValueOld option : options) {
      option.addTo(pByteList);
    }
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    int nrOfOptions = pByteArray.getSmallInt();
    for (int i = 0; i < nrOfOptions; i++) {
      GameOptionValueOld gameOption = new GameOptionValueOld();
      gameOption.initFrom(pByteArray);
      addOption(gameOption);
    }
    return byteArraySerializationVersion;
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    JsonArray optionArray = new JsonArray();
    GameOptionValueOld[] options = getOptionValues();
    for (GameOptionValueOld option : options) {
      optionArray.add(option.toJsonValue());
    }
    IJsonOption.GAME_OPTION_ARRAY.addTo(jsonObject, optionArray);
    return jsonObject;
  }

  public GameOptions initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    JsonArray optionArray = IJsonOption.GAME_OPTION_ARRAY.getFrom(jsonObject);
    int nrOfOptions = optionArray.size();
    for (int i = 0; i < nrOfOptions; i++) {
      GameOptionValueOld gameOption = new GameOptionValueOld();
      gameOption.initFrom(optionArray.get(i));
      addOption(gameOption);
    }
    return this;
  }
  
  private IGameOption createDefault(GameOptionId pOptionId) {
    if (pOptionId == null) {
      return null;
    }
    switch (pOptionId) {
      case CHECK_OWNERSHIP:
        return new GameOptionBoolean(GameOptionId.CHECK_OWNERSHIP)
          .setDefault(true)
          .setMessageTrue("Team ownership is checked.")
          .setMessageFalse("Team ownership is not checked.");
      case CLAW_DOES_NOT_STACK:
//      CLAW_DOES_NOT_STACK("clawDoesNotStack", 0, null, "Claw does not stack with other skills that modify armour rolls."),
      case FOUL_BONUS:
//      FOUL_BONUS("foulBonus", 0, "foul", "+1 to armour roll for a foul."),
      case FOUL_BONUS_OUTSIDE_TACKLEZONE:
//      FOUL_BONUS_OUTSIDE_TACKLEZONE("foulBonusOutsideTacklezone", 0, "foul", "+1 to armour roll for a foul, if fouler is not in an opposing tacklezone."),
      case FREE_CARD_CASH:
//      FREE_CARD_CASH("freeCardCash", 0, null, "Both coaches get $1 extra gold to buy cards with."),
      case FREE_INDUCEMENT_CASH:
//      FREE_INDUCEMENT_CASH("freeInducementCash", 0, null, "Both coaches get $1 extra gold to buy inducements with."),
      case INDUCEMENTS:
//      INDUCEMENTS("inducements", 1, null, "Inducements are not available."),
      case MAX_NR_OF_CARDS:
//      MAX_NR_OF_CARDS("maxNrOfCards", 5, null, "A maximum of $1 cards can be bought."),
      case MAX_PLAYERS_IN_WIDE_ZONE:
//      MAX_PLAYERS_IN_WIDE_ZONE("maxPlayersInWideZone", 2, null, "A maximum of $1 players may be set up in a widezone."),
      case MAX_PLAYERS_ON_FIELD:
//      MAX_PLAYERS_ON_FIELD("maxPlayersOnField", 11, null, "A maximum of $1 players may be set up on the field."),
      case MIN_PLAYERS_ON_LOS:
//      MIN_PLAYERS_ON_LOS("minPlayersOnLos", 3, null, "A minimum of $1 players must be set up on the line of scrimmage."),
      case OVERTIME:
        return new GameOptionBoolean(GameOptionId.OVERTIME)
          .setDefault(false)
          .setMessageTrue("Game ends after 2nd half.")
          .setMessageFalse("Game will go into overtime if there is a draw after 2nd half.");
      case PETTY_CASH:
//      PETTY_CASH("pettyCash", 1, null, "Petty Cash is not available."),
      case PILING_ON_ARMOR_ONLY:
//      PILING_ON_ARMOR_ONLY("pilingOnArmorOnly", 0, "pilingOn", "Piling On lets you re-roll armour-rolls only."),
      case PILING_ON_DOES_NOT_STACK:
//      PILING_ON_DOES_NOT_STACK("pilingOnDoesNotStack", 0, "pilingOn", "Piling On does not stack with other skills that modify armour- or injury-rolls."),
      case PILING_ON_INJURY_ONLY:
//      PILING_ON_INJURY_ONLY("pilingOnInjuryOnly", 0, "pilingOn", "Piling On lets you re-roll injury-rolls only."),
      case PILING_ON_TO_KO_ON_DOUBLE:
//      PILING_ON_TO_KO_ON_DOUBLE("pilingOnToKoOnDouble", 0, "pilingOn", "Piling On player knocks himself out when rolling a double on armour or injury."),
      case PITCH_URL_BLIZZARD:
        return new GameOptionString(GameOptionId.PITCH_URL_BLIZZARD);
      case PITCH_URL_HEAT:
        return new GameOptionString(GameOptionId.PITCH_URL_HEAT);
      case PITCH_URL_NICE:
        return new GameOptionString(GameOptionId.PITCH_URL_NICE);
      case PITCH_URL_RAIN:
        return new GameOptionString(GameOptionId.PITCH_URL_RAIN);
      case PITCH_URL_SUNNY:
        return new GameOptionString(GameOptionId.PITCH_URL_SUNNY);
      case RIGHT_STUFF_CANCELS_TACKLE:
//      RIGHT_STUFF_CANCELS_TACKLE("rightStuffCancelsTackle", 0, null, "Right Stuff prevents Tackle from negating Dodge for Pow/Pushback."),
      case SNEAKY_GIT_AS_FOUL_GUARD:
//      SNEAKY_GIT_AS_FOUL_GUARD("sneakyGitAsFoulGuard", 0, "sneakyGit", "Sneaky Git works like Guard for fouling assists."),
      case SNEAKY_GIT_BAN_TO_KO:
//      SNEAKY_GIT_BAN_TO_KO("sneakyGitBanToKo", 0, "sneakyGit", "Sneaky Git players that get banned are sent to the KO box instead."),
      case SPIKED_BALL:
//      SPIKED_BALL("spikedBall", 0, null, "A Spiked Ball is used for play. Any failed Pickup or Catch roll results in the player being stabbed.");
      case STAND_FIRM_NO_DROP_ON_FAILED_DODGE:
//      STAND_FIRM_NO_DROP_ON_FAILED_DODGE("standFirmNoDropOnFailedDodge", 0, null, "Stand Firm players do not drop on a failed dodge roll but end their move instead."),
      case TEST_MODE:
//      TEST_MODE("testMode", 0, null, "Game is in TEST mode. No result will be uploaded. See help for available test commands."),
      case TURNTIME:
        return new GameOptionInt(GameOptionId.TURNTIME)
          .setDefault(240)
          .setMessage("Turntime is $1 sec.");
      default:
        return null;
    }
    
  }

}
