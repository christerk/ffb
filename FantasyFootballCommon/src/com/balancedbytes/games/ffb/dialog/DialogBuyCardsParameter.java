package com.balancedbytes.games.ffb.dialog;

import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.CardType;
import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogBuyCardsParameter implements IDialogParameter {

	private String fTeamId;
	private int fAvailableGold;
	private int fAvailableCards;
	private Map<CardType, Integer> fNrOfCardsPerType;

	public DialogBuyCardsParameter() {
		fNrOfCardsPerType = new HashMap<CardType, Integer>();
	}

	public DialogBuyCardsParameter(String pTeamId, int pAvailableCards, int pAvailableGold) {
		this();
		fTeamId = pTeamId;
		fAvailableCards = pAvailableCards;
		fAvailableGold = pAvailableGold;
	}

	public DialogId getId() {
		return DialogId.BUY_CARDS;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public int getAvailableCards() {
		return fAvailableCards;
	}

	public int getAvailableGold() {
		return fAvailableGold;
	}

	public void put(CardType pType, int pNrOfCards) {
		fNrOfCardsPerType.put(pType, pNrOfCards);
	}

	public int getNrOfCards(CardType pType) {
		Integer nrOfCards = fNrOfCardsPerType.get(pType);
		return ((nrOfCards != null) ? nrOfCards : 0);
	}

	// transformation

	public IDialogParameter transform() {
		DialogBuyCardsParameter dialogParameter = new DialogBuyCardsParameter(getTeamId(), getAvailableCards(),
				getAvailableGold());
		for (CardType type : CardType.values()) {
			dialogParameter.put(type, getNrOfCards(type));
		}
		return dialogParameter;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.AVAILABLE_CARDS.addTo(jsonObject, fAvailableCards);
		IJsonOption.AVAILABLE_GOLD.addTo(jsonObject, fAvailableGold);
		// build array of inner jsonObjects with cardType + nrOfCards
		JsonArray nrOfCardsPerType = new JsonArray();
		for (CardType type : fNrOfCardsPerType.keySet()) {
			JsonObject nrOfCardsForThisType = new JsonObject();
			IJsonOption.CARD_TYPE.addTo(nrOfCardsForThisType, type);
			IJsonOption.NR_OF_CARDS.addTo(nrOfCardsForThisType, fNrOfCardsPerType.get(type));
			nrOfCardsPerType.add(nrOfCardsForThisType);
		}
		IJsonOption.NR_OF_CARDS_PER_TYPE.addTo(jsonObject, nrOfCardsPerType);
		return jsonObject;
	}

	public DialogBuyCardsParameter initFrom(JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
		fAvailableCards = IJsonOption.AVAILABLE_CARDS.getFrom(jsonObject);
		fAvailableGold = IJsonOption.AVAILABLE_GOLD.getFrom(jsonObject);
		// get nrOfCards and cardType from array of inner jsonObjects
		JsonArray nrOfCardsPerType = IJsonOption.NR_OF_CARDS_PER_TYPE.getFrom(jsonObject);
		for (int i = 0; i < nrOfCardsPerType.size(); i++) {
			JsonObject nrOfCardsForThisType = nrOfCardsPerType.get(i).asObject();
			CardType cardType = (CardType) IJsonOption.CARD_TYPE.getFrom(nrOfCardsForThisType);
			int nrOfCards = IJsonOption.NR_OF_CARDS.getFrom(nrOfCardsForThisType);
			put(cardType, nrOfCards);
		}
		return this;
	}

}
