package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.CardType;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandBuyCard extends ClientCommand {

	private CardType fCardType;

	public ClientCommandBuyCard() {
		super();
	}

	public ClientCommandBuyCard(CardType pCardType) {
		fCardType = pCardType;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_BUY_CARD;
	}

	public CardType getCardType() {
		return fCardType;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.CARD_TYPE.addTo(jsonObject, fCardType);
		return jsonObject;
	}

	public ClientCommandBuyCard initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fCardType = (CardType) IJsonOption.CARD_TYPE.getFrom(source, jsonObject);
		return this;
	}

}
