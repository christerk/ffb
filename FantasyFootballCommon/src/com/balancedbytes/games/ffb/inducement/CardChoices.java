package com.balancedbytes.games.ffb.inducement;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class CardChoices implements IJsonSerializable {
	private CardChoice initial, rerolled;

	public CardChoices() {
	}

	public CardChoices(CardChoice initial, CardChoice rerolled) {
		this.initial = initial;
		this.rerolled = rerolled;
	}

	public CardChoice getInitial() {
		return initial;
	}

	public CardChoice getRerolled() {
		return rerolled;
	}

	@Override
	public CardChoices initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);

		JsonObject choiceObject = IJsonOption.CARD_CHOICE_INITIAL.getFrom(game, jsonObject);
		if (choiceObject != null) {
			initial = new CardChoice().initFrom(game, choiceObject);
		}

		choiceObject = IJsonOption.CARD_CHOICE_REROLLED.getFrom(game, jsonObject);
		if (choiceObject != null) {
			rerolled = new CardChoice().initFrom(game, choiceObject);
		}

		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		if (initial != null) {
			IJsonOption.CARD_CHOICE_INITIAL.addTo(jsonObject, initial.toJsonValue());
		}
		if (rerolled != null) {
			IJsonOption.CARD_CHOICE_REROLLED.addTo(jsonObject, rerolled.toJsonValue());
		}

		return jsonObject;
	}
}
