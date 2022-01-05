package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.GameOptionFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.model.change.ModelChangeId;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.IGameOption;
import com.fumbbl.ffb.xml.IXmlSerializable;
import com.fumbbl.ffb.xml.UtilXml;
import org.xml.sax.Attributes;

import javax.xml.transform.sax.TransformerHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kalimar
 */
public class GameOptions implements IXmlSerializable, IJsonSerializable {

	public static final String XML_TAG = "options";

	private final Map<GameOptionId, IGameOption> fOptionById;

	private final transient Game fGame;
	private final transient GameOptionFactory fGameOptionFactory;

	public GameOptions(Game pGame) {
		fGame = pGame;
		fOptionById = new HashMap<>();
		fGameOptionFactory = new GameOptionFactory();
	}

	public Game getGame() {
		return fGame;
	}

	public RulesCollection.Rules getRulesVersion() {
		String rulesVersion = getOptionWithDefault(GameOptionId.RULESVERSION).getValueAsString();
		try {
			return RulesCollection.Rules.valueOf(rulesVersion);
		} catch (IllegalArgumentException e) {
			throw new FantasyFootballException("Could not create a rules version for value '" + rulesVersion + "'");
		}
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

	private IGameOption getOption(GameOptionId pOptionId) {
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
		if (!fOptionById.containsKey(GameOptionId.RULESVERSION)) {
			addOption(optionFactory.createGameOption(GameOptionId.RULESVERSION).setValue("BB2016"));
		}
		return this;
	}

}
