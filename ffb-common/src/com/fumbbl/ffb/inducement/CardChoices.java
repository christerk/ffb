package com.fumbbl.ffb.inducement;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

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
	public CardChoices initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);

		JsonObject choiceObject = IJsonOption.CARD_CHOICE_INITIAL.getFrom(source, jsonObject);
		if (choiceObject != null) {
			initial = new CardChoice().initFrom(source, choiceObject);
		}

		choiceObject = IJsonOption.CARD_CHOICE_REROLLED.getFrom(source, jsonObject);
		if (choiceObject != null) {
			rerolled = new CardChoice().initFrom(source, choiceObject);
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
