package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.inducement.CardChoice;
import com.balancedbytes.games.ffb.inducement.CardType;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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
	private CardChoice initialChoice, rerolledChoice;
	private boolean canBuyCards;

	public DialogBuyCardsAndInducementsParameter() {
		fNrOfCardsPerType = new HashMap<>();
	}

	public DialogBuyCardsAndInducementsParameter(String teamId, boolean canBuyCards, int cardSlots, int treasury, int availableGold, CardChoice initialChoice, CardChoice rerolledChoice, int cardPrice) {
		this();
		fTeamId = teamId;
		this.cardSlots = cardSlots;
		this.treasury = treasury;
		this.availableGold = availableGold;
		this.initialChoice = initialChoice;
		this.rerolledChoice = rerolledChoice;
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

	public CardChoice getInitialChoice() {
		return initialChoice;
	}

	public CardChoice getRerolledChoice() {
		return rerolledChoice;
	}

	public boolean isCanBuyCards() {
		return canBuyCards;
	}

	// transformation

	public IDialogParameter transform() {
		DialogBuyCardsAndInducementsParameter dialogParameter = new DialogBuyCardsAndInducementsParameter(getTeamId(), canBuyCards, getCardSlots(),
			treasury, availableGold, initialChoice, rerolledChoice, cardPrice);
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

		if (initialChoice != null) {
			IJsonOption.CARD_CHOICE_INITIAL.addTo(jsonObject, initialChoice.toJsonValue());
		}
		if (rerolledChoice != null) {
			IJsonOption.CARD_CHOICE_REROLLED.addTo(jsonObject, rerolledChoice.toJsonValue());
		}

		IJsonOption.CARDS_PRICE.addTo(jsonObject, cardPrice);
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
		JsonObject choiceObject = IJsonOption.CARD_CHOICE_INITIAL.getFrom(game, jsonObject);
		if (choiceObject != null) {
			initialChoice = new CardChoice().initFrom(game, choiceObject);
		}

		choiceObject = IJsonOption.CARD_CHOICE_REROLLED.getFrom(game, jsonObject);
		if (choiceObject != null) {
			rerolledChoice = new CardChoice().initFrom(game, choiceObject);
		}

		cardPrice = IJsonOption.CARDS_PRICE.getFrom(game, jsonObject);
		return this;
	}

}
