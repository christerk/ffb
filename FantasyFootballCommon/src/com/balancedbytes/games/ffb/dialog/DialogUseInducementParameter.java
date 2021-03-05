package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.factory.CardFactory;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.factory.InducementTypeFactory;
import com.balancedbytes.games.ffb.inducement.InducementType;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class DialogUseInducementParameter implements IDialogParameter {

	private String fTeamId;
	private InducementType[] fInducementTypes;
	private Card[] fCards;

	public DialogUseInducementParameter() {
		super();
	}

	public DialogUseInducementParameter(String pTeamId, InducementType[] pInducementTypes, Card[] pCards) {
		fTeamId = pTeamId;
		fInducementTypes = pInducementTypes;
		fCards = pCards;
	}

	public DialogId getId() {
		return DialogId.USE_INDUCEMENT;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public InducementType[] getInducementTypes() {
		return fInducementTypes;
	}

	public Card[] getCards() {
		return fCards;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogUseInducementParameter(getTeamId(), getInducementTypes(), getCards());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		List<String> inducementTypeNames = new ArrayList<>();
		for (InducementType inducementType : getInducementTypes()) {
			inducementTypeNames.add(inducementType.getName());
		}
		IJsonOption.INDUCEMENT_TYPE_ARRAY.addTo(jsonObject, inducementTypeNames);
		List<String> cardNames = new ArrayList<>();
		for (Card card : getCards()) {
			cardNames.add(card.getName());
		}
		IJsonOption.CARDS.addTo(jsonObject, cardNames);
		return jsonObject;
	}

	public DialogUseInducementParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		String[] inducementTypeNames = IJsonOption.INDUCEMENT_TYPE_ARRAY.getFrom(game, jsonObject);
		fInducementTypes = new InducementType[inducementTypeNames.length];
		InducementTypeFactory inducementTypeFactory = game.getFactory(Factory.INDUCEMENT_TYPE);
		for (int i = 0; i < fInducementTypes.length; i++) {
			fInducementTypes[i] = inducementTypeFactory.forName(inducementTypeNames[i]);
		}
		String[] cardNames = IJsonOption.CARDS.getFrom(game, jsonObject);
		fCards = new Card[cardNames.length];
		CardFactory cardFactory = game.<CardFactory>getFactory(Factory.CARD);
		for (int i = 0; i < fCards.length; i++) {
			fCards[i] = cardFactory.forName(cardNames[i]);
		}
		return this;
	}

}
