package com.balancedbytes.games.ffb;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.CommandGameAttributeChange;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.ModelChangeGameAttribute;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class GameOptions implements IByteArraySerializable, IXmlSerializable {
  
  public static final String XML_TAG = "options";
  
  private static final String _XML_TAG_OVERTIME = "overtime";
  private static final String _XML_TAG_TURNTIME = "turntime";
  private static final String _XML_TAG_PETTY_CASH = "pettyCash";
  private static final String _XML_TAG_INDUCEMENTS = "inducements";
  private static final String _XML_TAG_CHECK_OWNERSHIP = "checkOwnership";
  private static final String _XML_TAG_TEST_MODE = "testMode";
  private static final String _XML_TAG_SNEAKY_GIT_AS_FOUL_GUARD = "sneakyGitAsFoulGuard";
  private static final String _XML_TAG_FOUL_BONUS_OUTSIDE_TACKLEZONE = "foulBonusOutsideTacklezone";
  private static final String _XML_TAG_RIGHT_STUFF_CANCELS_TACKLE = "rightStuffCancelsTackle";
  private static final String _XML_TAG_PILING_ON_WITHOUT_MODIFIER = "pilingOnWithoutModifier";

  private Game fGame;
  private Map<GameOption, GameOptionValue> fOptionValueByName;

  public GameOptions(Game pGame) {
  	fGame = pGame;
  	fOptionValueByName = new HashMap<GameOption, GameOptionValue>();
  }
  
  public Game getGame() {
    return fGame;
  } 

  public void addOption(GameOptionValue pGameOption) {
  	if ((pGameOption != null) && (pGameOption.getOption() != null)) {
  		GameOptionValue oldOption = getOptionValue(pGameOption.getOption());
  		if ((oldOption == null) || (oldOption.getValue() != pGameOption.getValue())) {
	      oldOption = null;
  			// check for other options of the same group, reset if found
  			if ((pGameOption.getGroup() != null) && pGameOption.isChanged()) {
	      	GameOptionValue[] oldValues = getOptionValues();
	      	for (GameOptionValue oldValue : oldValues) {
	      		if (pGameOption.getGroup().equals(oldValue.getGroup())) {
	      			oldOption = oldValue;
	      			oldOption.setValue(0);
	      			break;
	      		}
	      	}
	      }
	      if ((getGame() != null) && getGame().isTrackingChanges()) {
	        if (oldOption != null) {
		        getGame().add(new ModelChangeGameAttribute(CommandGameAttributeChange.ADD_OPTION, oldOption));
	        }
	        getGame().add(new ModelChangeGameAttribute(CommandGameAttributeChange.ADD_OPTION, pGameOption));
	      }
	      if (oldOption != null) {
		      fOptionValueByName.put(oldOption.getOption(), oldOption);
	      }
	      fOptionValueByName.put(pGameOption.getOption(), pGameOption);
  		}
  	}
  }

  public GameOptionValue getOptionValue(GameOption pOptionName) {
  	GameOptionValue value = fOptionValueByName.get(pOptionName);
  	if (value == null) {
  		return new GameOptionValue(pOptionName, pOptionName.getDefaultValue());
  	} else {
  		return value;
  	}
  }
  
  public GameOptionValue[] getOptionValues() {
  	return fOptionValueByName.values().toArray(new GameOptionValue[fOptionValueByName.size()]);
  }
  
  public void init(GameOptions pOtherOptions) {
  	if (pOtherOptions != null) {
  		for (GameOptionValue option : pOtherOptions.getOptionValues()) {
  			addOption(option);
  		}
  	}
  }  

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	UtilXml.startElement(pHandler, XML_TAG);
	for (GameOptionValue option : getOptionValues()) {
		option.addToXml(pHandler);
	}
	UtilXml.endElement(pHandler, XML_TAG);
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }  
  
  public IXmlSerializable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
  	if (GameOptionValue.XML_TAG.equals(pXmlTag)) {
  		GameOptionValue gameOption = new GameOptionValue();
  		gameOption.startXmlElement(pXmlTag, pXmlAttributes);
  		addOption(gameOption);
  		return gameOption;
  	}
    return this;
  }
  
  public boolean endXmlElement(String pXmlTag, String pValue) {
  	// backward compatibility - can be removed later
    if (_XML_TAG_OVERTIME.equals(pXmlTag)) {
    	boolean booleanFlag = Boolean.parseBoolean(pValue);
    	addOption(new GameOptionValue(GameOption.OVERTIME, booleanFlag ? 1 : 0));
    }
  	// backward compatibility - can be removed later
    if (_XML_TAG_TURNTIME.equals(pXmlTag)) {
    	int turntime = Integer.parseInt(pValue);
    	addOption(new GameOptionValue(GameOption.TURNTIME, turntime));
    }
  	// backward compatibility - can be removed later
    if (_XML_TAG_PETTY_CASH.equals(pXmlTag)) {
    	boolean booleanFlag = Boolean.parseBoolean(pValue);
    	addOption(new GameOptionValue(GameOption.PETTY_CASH, booleanFlag ? 1 : 0));
    }
  	// backward compatibility - can be removed later
    if (_XML_TAG_INDUCEMENTS.equals(pXmlTag)) {
    	boolean booleanFlag = Boolean.parseBoolean(pValue);
    	addOption(new GameOptionValue(GameOption.INDUCEMENTS, booleanFlag ? 1 : 0));
    }
  	// backward compatibility - can be removed later
    if (_XML_TAG_CHECK_OWNERSHIP.equals(pXmlTag)) {
    	boolean booleanFlag = Boolean.parseBoolean(pValue);
    	addOption(new GameOptionValue(GameOption.CHECK_OWNERSHIP, booleanFlag ? 1 : 0));
    }
  	// backward compatibility - can be removed later
    if (_XML_TAG_TEST_MODE.equals(pXmlTag)) {
    	boolean booleanFlag = Boolean.parseBoolean(pValue);
    	addOption(new GameOptionValue(GameOption.TEST_MODE, booleanFlag ? 1 : 0));
    }
  	// backward compatibility - can be removed later
    if (_XML_TAG_TEST_MODE.equals(pXmlTag)) {
    	boolean booleanFlag = Boolean.parseBoolean(pValue);
    	addOption(new GameOptionValue(GameOption.TEST_MODE, booleanFlag ? 1 : 0));
    }
  	// backward compatibility - can be removed later
    if (_XML_TAG_SNEAKY_GIT_AS_FOUL_GUARD.equals(pXmlTag)) {
    	boolean testMode = Boolean.parseBoolean(pValue);
    	addOption(new GameOptionValue(GameOption.SNEAKY_GIT_AS_FOUL_GUARD, testMode ? 1 : 0));
    }
  	// backward compatibility - can be removed later
    if (_XML_TAG_FOUL_BONUS_OUTSIDE_TACKLEZONE.equals(pXmlTag)) {
    	boolean booleanFlag = Boolean.parseBoolean(pValue);
    	addOption(new GameOptionValue(GameOption.FOUL_BONUS_OUTSIDE_TACKLEZONE, booleanFlag ? 1 : 0));
    }
  	// backward compatibility - can be removed later
    if (_XML_TAG_RIGHT_STUFF_CANCELS_TACKLE.equals(pXmlTag)) {
    	boolean booleanFlag = Boolean.parseBoolean(pValue);
    	addOption(new GameOptionValue(GameOption.RIGHT_STUFF_CANCELS_TACKLE, booleanFlag ? 1 : 0));
    }
  	// backward compatibility - can be removed later
    if (_XML_TAG_PILING_ON_WITHOUT_MODIFIER.equals(pXmlTag)) {
    	boolean booleanFlag = Boolean.parseBoolean(pValue);
    	addOption(new GameOptionValue(GameOption.PILING_ON_DOES_NOT_STACK, booleanFlag ? 1 : 0));
    }
    return XML_TAG.equals(pXmlTag);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    GameOptionValue[] options = getOptionValues();
    pByteList.addSmallInt(options.length);
    for (GameOptionValue option : options) {
    	option.addTo(pByteList);
    }
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    int nrOfOptions = pByteArray.getSmallInt();
    for (int i = 0; i < nrOfOptions; i++) {
    	GameOptionValue gameOption = new GameOptionValue();
    	gameOption.initFrom(pByteArray);
    	addOption(gameOption);
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    JsonArray optionArray = new JsonArray();
    GameOptionValue[] options = getOptionValues();
    for (GameOptionValue option : options) {
      optionArray.add(option.toJsonValue());
    }
    IJsonOption.GAME_OPTIONS.addTo(jsonObject, optionArray);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    JsonArray optionArray = IJsonOption.GAME_OPTIONS.getFrom(jsonObject);
    int nrOfOptions = optionArray.size();
    for (int i = 0; i < nrOfOptions; i++) {
      GameOptionValue gameOption = new GameOptionValue();
      gameOption.initFrom(optionArray.get(i));
      addOption(gameOption);
    }
  }

}
