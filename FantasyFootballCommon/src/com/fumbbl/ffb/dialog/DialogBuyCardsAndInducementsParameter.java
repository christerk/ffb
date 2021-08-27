package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.CardChoices;
import com.fumbbl.ffb.inducement.CardType;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Kalimar
 */
public class DialogBuyCardsAndInducementsParameter implements IDialogParameter {

	private String fTeamId;
	private int treasury, availableGold, cardSlots, cardPrice;
	private final Map<CardType, Integer> fNrOfCardsPerType;
	private CardChoices cardChoices;
	private boolean canBuyCards;

	public DialogBuyCardsAndInducementsParameter() {
		fNrOfCardsPerType = new HashMap<>();
	}

	public DialogBuyCardsAndInducementsParameter(String teamId, boolean canBuyCards, int cardSlots, int treasury, int availableGold, CardChoices cardChoices, int cardPrice) {
		this();
		fTeamId = teamId;
		this.cardSlots = cardSlots;
		this.treasury = treasury;
		this.availableGold = availableGold;
		this.cardChoices = cardChoices;
		this.cardPrice = cardPrice;
		this.canBuyCards = canBuyCards;
	}

	public int getCardPrice() {
		return cardPrice;
	}

	public DialogId getId() {
		return DialogId.BUY_CARDS_AND_INDUCEMENTS;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public int getCardSlots() {
		return cardSlots;
	}

	public int getAvailableGold() {
		return availableGold;
	}

	public int getTreasury() {
		return treasury;
	}

	public void put(CardType pType, int pNrOfCards) {
		fNrOfCardsPerType.put(pType, pNrOfCards);
	}

	public Map<CardType, Integer> getNrOfCardsPerType() {
		return fNrOfCardsPerType;
	}

	public boolean isCanBuyCards() {
		return canBuyCards;
	}

	public CardChoices getCardChoices() {
		return cardChoices;
	}

	public void setCardChoices(CardChoices cardChoices) {
		this.cardChoices = cardChoices;
	}
// transformation

	public IDialogParameter transform() {
		DialogBuyCardsAndInducementsParameter dialogParameter = new DialogBuyCardsAndInducementsParameter(getTeamId(), canBuyCards, getCardSlots(),
			treasury, availableGold, cardChoices, cardPrice);
		fNrOfCardsPerType.forEach(dialogParameter::put);
		return dialogParameter;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.AVAILABLE_CARDS.addTo(jsonObject, cardSlots);
		IJsonOption.AVAILABLE_GOLD.addTo(jsonObject, availableGold);
		IJsonOption.TREASURY.addTo(jsonObject, treasury);
		// build array of inner jsonObjects with cardType + nrOfCards
		JsonArray nrOfCardsPerType = new JsonArray();
		for (CardType type : fNrOfCardsPerType.keySet()) {
			JsonObject nrOfCardsForThisType = new JsonObject();
			IJsonOption.CARD_TYPE.addTo(nrOfCardsForThisType, type);
			IJsonOption.NR_OF_CARDS.addTo(nrOfCardsForThisType, fNrOfCardsPerType.get(type));
			nrOfCardsPerType.add(nrOfCardsForThisType);
		}
		IJsonOption.NR_OF_CARDS_PER_TYPE.addTo(jsonObject, nrOfCardsPerType);

		IJsonOption.CARD_CHOICES.addTo(jsonObject, cardChoices.toJsonValue());

		IJsonOption.CARDS_PRICE.addTo(jsonObject, cardPrice);
		IJsonOption.CAN_BUY_CARDS.addTo(jsonObject, canBuyCards);
		return jsonObject;
	}

	public DialogBuyCardsAndInducementsParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		cardSlots = IJsonOption.AVAILABLE_CARDS.getFrom(game, jsonObject);
		availableGold = IJsonOption.AVAILABLE_GOLD.getFrom(game, jsonObject);
		treasury = IJsonOption.TREASURY.getFrom(game, jsonObject);
		// get nrOfCards and cardType from array of inner jsonObjects
		JsonArray nrOfCardsPerType = IJsonOption.NR_OF_CARDS_PER_TYPE.getFrom(game, jsonObject);
		for (int i = 0; i < nrOfCardsPerType.size(); i++) {
			JsonObject nrOfCardsForThisType = nrOfCardsPerType.get(i).asObject();
			CardType cardType = (CardType) IJsonOption.CARD_TYPE.getFrom(game, nrOfCardsForThisType);
			int nrOfCards = IJsonOption.NR_OF_CARDS.getFrom(game, nrOfCardsForThisType);
			put(cardType, nrOfCards);
		}

		JsonObject choiceObject = IJsonOption.CARD_CHOICES.getFrom(game, jsonObject);
		if (choiceObject != null) {
			cardChoices = new CardChoices().initFrom(game, choiceObject);
		}

		cardPrice = IJsonOption.CARDS_PRICE.getFrom(game, jsonObject);
		canBuyCards = IJsonOption.CAN_BUY_CARDS.getFrom(game, jsonObject);
		return this;
	}

}
