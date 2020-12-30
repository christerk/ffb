package com.balancedbytes.games.ffb.model;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.factory.GameOptionFactory;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeId;
import com.balancedbytes.games.ffb.option.GameOptionBoolean;
import com.balancedbytes.games.ffb.option.GameOptionId;
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
public class GameOptions implements IXmlSerializable, IJsonSerializable {

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

	public RulesCollection.Rules getRulesVersion() {
		String rulesVersion = getOptionWithDefault(GameOptionId.RULESVERSION).getValueAsString();
		RulesCollection.Rules rules = Rules.BB2020;
		try {
			rules = RulesCollection.Rules.valueOf(rulesVersion);
		} catch (IllegalArgumentException e) { }
		return rules;
	}
	
	public GameOptionFactory getFactory() {
		return fGameOptionFactory;
	}
	
	public void addOption(IGameOption pOption) {
		if (pOption != null) {
			addOptionInternal(pOption);
			// handle mutually exclusive options
			switch (pOption.getId()) {
			case PILING_ON_ARMOR_ONLY:
				if (((GameOptionBoolean) pOption).isEnabled()) {
					GameOptionBoolean pilingOnInjuryOnly = (GameOptionBoolean) getOptionWithDefault(
							GameOptionId.PILING_ON_INJURY_ONLY);
					if (pilingOnInjuryOnly.isEnabled()) {
						addOptionInternal(pilingOnInjuryOnly.setValue(false));
					}
				}
				break;
			case PILING_ON_INJURY_ONLY:
				if (((GameOptionBoolean) pOption).isEnabled()) {
					GameOptionBoolean pilingOnArmorOnly = (GameOptionBoolean) getOptionWithDefault(
							GameOptionId.PILING_ON_ARMOR_ONLY);
					if (pilingOnArmorOnly.isEnabled()) {
						addOptionInternal(pilingOnArmorOnly.setValue(false));
					}
				}
				break;
			case FOUL_BONUS:
				if (((GameOptionBoolean) pOption).isEnabled()) {
					GameOptionBoolean foulBonusOutsideTacklezone = (GameOptionBoolean) getOptionWithDefault(
							GameOptionId.FOUL_BONUS_OUTSIDE_TACKLEZONE);
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

	public IXmlSerializable startXmlElement(Game game, String pXmlTag, Attributes pXmlAttributes) {
		if (IGameOption.XML_TAG.equals(pXmlTag)) {
			addOption(new GameOptionFactory().fromXmlElement(game, pXmlTag, pXmlAttributes));
		}
		return this;
	}

	public boolean endXmlElement(Game game, String pXmlTag, String pValue) {
		return XML_TAG.equals(pXmlTag);
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

	public GameOptions initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		JsonArray optionArray = IJsonOption.GAME_OPTION_ARRAY.getFrom(game, jsonObject);
		int nrOfOptions = optionArray.size();
		GameOptionFactory optionFactory = new GameOptionFactory();
		for (int i = 0; i < nrOfOptions; i++) {
			IGameOption gameOption = optionFactory.fromJsonValue(game, optionArray.get(i));
			addOption(gameOption);
		}
		return this;
	}

}
