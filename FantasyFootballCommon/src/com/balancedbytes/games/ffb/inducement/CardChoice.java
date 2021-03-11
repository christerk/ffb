package com.balancedbytes.games.ffb.inducement;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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

		if (choiceOne != null && choiceTwo != null) {
			throw new FantasyFootballException("Types of choiceOne " + choiceOne.getType().getName() + " and choiceTwo " + choiceTwo.getType().getName() + " do not match ");
		}
	}

	@Override
	public CardChoice initFrom(IFactorySource game, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		type = (CardType) IJsonOption.CARD_CHOICE_TYPE.getFrom(game, jsonObject);
		choiceOne = (Card) IJsonOption.CARD_CHOICE_ONE.getFrom(game, jsonObject);
		choiceTwo = (Card) IJsonOption.CARD_CHOICE_TWO.getFrom(game, jsonObject);
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
