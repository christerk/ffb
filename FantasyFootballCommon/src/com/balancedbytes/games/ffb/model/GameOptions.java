package com.balancedbytes.games.ffb.model;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.IByteArrayReadable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeId;
import com.balancedbytes.games.ffb.old.GameOptionValueOld;
import com.balancedbytes.games.ffb.option.GameOptionBoolean;
import com.balancedbytes.games.ffb.option.GameOptionFactory;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.GameOptionIdFactory;
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

  private Map<GameOptionId, IGameOption> fOptionById;

  private transient Game fGame;
  private transient GameOptionFactory fGameOptionFactory;

  public GameOptions(Game pGame) {
    fGame = pGame;
    fOptionById = new HashMap<GameOptionId, IGameOption>();
    fGameOptionFactory = new GameOptionFactory();
  }

  public Game getGame() {
    return fGame;
  }

  public void addOption(IGameOption pOption) {
    if (pOption != null) {
      addOptionInternal(pOption);
      // handle mutually exclusive options
      switch (pOption.getId()) {
        case PILING_ON_ARMOR_ONLY:
          if (((GameOptionBoolean) pOption).isEnabled()) {
            GameOptionBoolean pilingOnInjuryOnly = (GameOptionBoolean) getOptionWithDefault(GameOptionId.PILING_ON_INJURY_ONLY);
            if (pilingOnInjuryOnly.isEnabled()) {
              addOptionInternal(pilingOnInjuryOnly.setValue(false));
            }
          }
          break;
        case PILING_ON_INJURY_ONLY:
          if (((GameOptionBoolean) pOption).isEnabled()) {
            GameOptionBoolean pilingOnArmorOnly = (GameOptionBoolean) getOptionWithDefault(GameOptionId.PILING_ON_ARMOR_ONLY);
            if (pilingOnArmorOnly.isEnabled()) {
              addOptionInternal(pilingOnArmorOnly.setValue(false));
            }
          }
          break;
        case FOUL_BONUS:
          if (((GameOptionBoolean) pOption).isEnabled()) {
            GameOptionBoolean foulBonusOutsideTacklezone = (GameOptionBoolean) getOptionWithDefault(GameOptionId.FOUL_BONUS_OUTSIDE_TACKLEZONE);
            if (foulBonusOutsideTacklezone.isEnabled()) {
              addOptionInternal(foulBonusOutsideTacklezone.setValue(false));
            }
          }
          break;
        case FOUL_BONUS_OUTSIDE_TACKLEZONE:
          if (((GameOptionBoolean) pOption).isEnabled()) {
            GameOptionBoolean foulBonus = (GameOptionBoolean) getOptionWithDefault(GameOptionId.FOUL_BONUS);
            if (foulBonus.isEnabled()) {
              addOptionInternal(foulBonus.setValue(false));
            }
          }
          break;
        default:
          break;
      }
    }
  }
  
  private void addOptionInternal(IGameOption pOption) {
    fOptionById.put(pOption.getId(), pOption);
    notifyObservers(ModelChangeId.GAME_OPTIONS_ADD_OPTION, pOption);
  }

  public IGameOption getOption(GameOptionId pOptionId) {
    return fOptionById.get(pOptionId);
  }
  
  public IGameOption getOptionWithDefault(GameOptionId pOptionId) {
    IGameOption option = getOption(pOptionId);
    if (option == null) {
      option = fGameOptionFactory.createGameOption(pOptionId);
    }
    return option;
  }

  public IGameOption[] getOptions() {
    return fOptionById.values().toArray(new IGameOption[fOptionById.size()]);
  }

  public void init(GameOptions pOtherOptions) {
    if (pOtherOptions == null) {
      return;
    }
    for (IGameOption otherOption : pOtherOptions.getOptions()) {
      IGameOption myOption = fGameOptionFactory.createGameOption(otherOption.getId());
      myOption.setValue(otherOption.getValueAsString());
      addOption(myOption);
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
    for (IGameOption option : getOptions()) {
      option.addToXml(pHandler);
    }
    UtilXml.endElement(pHandler, XML_TAG);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  public IXmlSerializable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    if (IGameOption.XML_TAG.equals(pXmlTag)) {
      IGameOption gameOption = new GameOptionFactory().fromXmlElement(pXmlTag, pXmlAttributes);
      if (gameOption != null) {
        gameOption.startXmlElement(pXmlTag, pXmlAttributes);
        addOption(gameOption);
        return gameOption;
      }
    }
    return this;
  }

  public boolean endXmlElement(String pXmlTag, String pValue) {
    return XML_TAG.equals(pXmlTag);
  }

  // ByteArray serialization

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    int nrOfOptions = pByteArray.getSmallInt();
    GameOptionIdFactory idFactory = new GameOptionIdFactory();
    GameOptionFactory optionFactory = new GameOptionFactory();
    for (int i = 0; i < nrOfOptions; i++) {
      GameOptionValueOld gameOptionValue = new GameOptionValueOld();
      gameOptionValue.initFrom(pByteArray);
      // convert to new system
      GameOptionId optionId = idFactory.forName(gameOptionValue.getOption().getName());
      IGameOption gameOption = optionFactory.createGameOption(optionId);
      gameOption.setValue(Integer.toString(gameOptionValue.getValue()));
      addOption(gameOption);
    }
    return byteArraySerializationVersion;
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    JsonArray optionArray = new JsonArray();
    for (IGameOption option : getOptions()) {
      optionArray.add(option.toJsonValue());
    }
    IJsonOption.GAME_OPTION_ARRAY.addTo(jsonObject, optionArray);
    return jsonObject;
  }

  public GameOptions initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    JsonArray optionArray = IJsonOption.GAME_OPTION_ARRAY.getFrom(jsonObject);
    int nrOfOptions = optionArray.size();
    GameOptionFactory optionFactory = new GameOptionFactory();
    for (int i = 0; i < nrOfOptions; i++) {
      IGameOption gameOption = optionFactory.fromJsonValue(optionArray.get(i));
      addOption(gameOption);
    }
    return this;
  }
  
}
