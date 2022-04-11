package com.fumbbl.ffb.inducement;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

public class CardChoice implements IJsonSerializable {
	private CardType type;
	private Card choiceOne;
	private Card choiceTwo;

	public CardType getType() {
		return type;
	}

	public CardChoice withType(CardType type) {
		this.type = type;
		validate();
		return this;
	}

	public Card getChoiceOne() {
		return choiceOne;
	}

	public CardChoice withChoiceOne(Card choiceOne) {
		this.choiceOne = choiceOne;
		validate();
		return this;
	}

	public Card getChoiceTwo() {
		return choiceTwo;
	}

	public CardChoice withChoiceTwo(Card choiceTwo) {
		this.choiceTwo = choiceTwo;
		validate();
		return this;
	}

	private void validate() {
		if (type == null) {
			throw new FantasyFootballException("CardChoice has no type set");
		}
		if (choiceOne != null && choiceOne.getType() != type) {
			throw new FantasyFootballException("Type of choiceOne " + choiceOne.getType().getName() + " does not match type " + type.getName());
		}

		if (choiceTwo != null && choiceTwo.getType() != type) {
			throw new FantasyFootballException("Type of choiceTwo " + choiceTwo.getType().getName() + " does not match type " + type.getName());
		}

		if (choiceOne != null && choiceTwo != null && choiceOne.getType() != choiceTwo.getType()) {
			throw new FantasyFootballException("Types of choiceOne " + choiceOne.getType().getName() + " and choiceTwo " + choiceTwo.getType().getName() + " do not match ");
		}
	}

	@Override
	public CardChoice initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		type = (CardType) IJsonOption.CARD_CHOICE_TYPE.getFrom(source, jsonObject);
		choiceOne = (Card) IJsonOption.CARD_CHOICE_ONE.getFrom(source, jsonObject);
		choiceTwo = (Card) IJsonOption.CARD_CHOICE_TWO.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.CARD_CHOICE_TYPE.addTo(jsonObject, type);
		IJsonOption.CARD_CHOICE_ONE.addTo(jsonObject, choiceOne);
		IJsonOption.CARD_CHOICE_TWO.addTo(jsonObject, choiceTwo);
		return jsonObject;
	}
}
