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
	private int treasury, availableGold, availableCards;
	private final Map<CardType, Integer> fNrOfCardsPerType;
	private CardChoice initialChoice, rerolledChoice;

	public DialogBuyCardsAndInducementsParameter() {
		fNrOfCardsPerType = new HashMap<>();
	}

	public DialogBuyCardsAndInducementsParameter(String teamId, int availableCards, int treasury, int availableGold, CardChoice initialChoice, CardChoice rerolledChoice) {
		this();
		fTeamId = teamId;
		this.availableCards = availableCards;
		this.treasury = treasury;
		this.availableGold = availableGold;
		this.initialChoice = initialChoice;
		this.rerolledChoice = rerolledChoice;
	}

	public DialogId getId() {
		return DialogId.BUY_CARDS_AND_INDUCEMENTS;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public int getAvailableCards() {
		return availableCards;
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
// transformation

	public IDialogParameter transform() {
		DialogBuyCardsAndInducementsParameter dialogParameter = new DialogBuyCardsAndInducementsParameter(getTeamId(), getAvailableCards(),
			treasury, availableGold, initialChoice, rerolledChoice);
		fNrOfCardsPerType.forEach(dialogParameter::put);
		return dialogParameter;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.AVAILABLE_CARDS.addTo(jsonObject, availableCards);
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

		return jsonObject;
	}

	public DialogBuyCardsAndInducementsParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		availableCards = IJsonOption.AVAILABLE_CARDS.getFrom(game, jsonObject);
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
		return this;
	}

}
