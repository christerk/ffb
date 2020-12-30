package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.CardType;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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

	public ClientCommandBuyCard initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fCardType = (CardType) IJsonOption.CARD_TYPE.getFrom(game, jsonObject);
		return this;
	}

}
