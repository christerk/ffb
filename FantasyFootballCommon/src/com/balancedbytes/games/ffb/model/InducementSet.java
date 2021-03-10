package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.inducement.Inducement;
import com.balancedbytes.games.ffb.inducement.InducementType;
import com.balancedbytes.games.ffb.factory.CardFactory;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.change.ModelChange;
import com.balancedbytes.games.ffb.model.change.ModelChangeId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.IXmlReadable;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Kalimar
 */
public class InducementSet implements IXmlSerializable, IJsonSerializable {

	public static final String XML_TAG = "inducementSet";

	private static final String _XML_TAG_STAR_PLAYER_SET = "starPlayerSet";
	private static final String _XML_TAG_STAR_PLAYER = "starPlayer";

	private static final String _XML_TAG_CARD_SET = "cardSet";
	private static final String _XML_TAG_CARD = "card";

	private static final String _XML_ATTRIBUTE_POSITION_ID = "positionId";
	private static final String _XML_ATTRIBUTE_NAME = "name";

	private Map<InducementType, Inducement> fInducements;
	private Set<Card> fCardsAvailable;
	private Set<Card> fCardsActive;
	private Set<Card> fCardsDeactivated;
	private Set<String> fStarPlayerPositionIds;

	private transient TurnData fTurnData;

	public InducementSet() {
		fInducements = new HashMap<>();
		fCardsAvailable = new HashSet<>();
		fCardsActive = new HashSet<>();
		fCardsDeactivated = new HashSet<>();
		fStarPlayerPositionIds = new HashSet<>();
	}

	public InducementSet(TurnData pTurnData) {
		this();
		fTurnData = pTurnData;
	}

	public TurnData getTurnData() {
		return fTurnData;
	}

	public Inducement get(InducementType pType) {
		if (pType != null) {
			return fInducements.get(pType);
		} else {
			return null;
		}
	}

	public Map<InducementType, Inducement> getInducementMapping() {
		return fInducements;
	}

	public Set<InducementType> getInducementTypes() {
		return fInducements.keySet();
	}

	public Inducement[] getInducements() {
		return fInducements.values().toArray(new Inducement[fInducements.size()]);
	}

	public void addInducement(Inducement pInducement) {
		if (pInducement == null) {
			return;
		}
		fInducements.put(pInducement.getType(), pInducement);
		notifyObservers(ModelChangeId.INDUCEMENT_SET_ADD_INDUCEMENT, pInducement);
	}

	public void removeInducement(Inducement pInducement) {
		if (pInducement == null) {
			return;
		}
		fInducements.remove(pInducement.getType());
		notifyObservers(ModelChangeId.INDUCEMENT_SET_REMOVE_INDUCEMENT, pInducement);
	}

	public boolean hasUsesLeft(InducementType pType) {
		Inducement inducement = get(pType);
		return ((inducement != null) && (inducement.getUsesLeft() > 0));
	}

	public void addAvailableCard(Card pCard) {
		if (pCard == null) {
			return;
		}
		fCardsAvailable.add(pCard);
		notifyObservers(ModelChangeId.INDUCEMENT_SET_ADD_AVAILABLE_CARD, pCard);
	}

	public boolean removeAvailableCard(Card pCard) {
		if (pCard == null) {
			return false;
		}
		boolean removed = fCardsAvailable.remove(pCard);
		notifyObservers(ModelChangeId.INDUCEMENT_SET_REMOVE_AVAILABLE_CARD, pCard);
		return removed;
	}

	public Card[] getAvailableCards() {
		return fCardsAvailable.toArray(new Card[fCardsAvailable.size()]);
	}

	public boolean isAvailable(Card pCard) {
		return fCardsAvailable.contains(pCard);
	}

	public boolean activateCard(Card pCard) {
		if (pCard == null) {
			return false;
		}
		boolean removed = fCardsAvailable.remove(pCard);
		if (removed) {
			fCardsActive.add(pCard);
		}
		notifyObservers(ModelChangeId.INDUCEMENT_SET_ACTIVATE_CARD, pCard);
		return removed;
	}

	public boolean deactivateCard(Card pCard) {
		if (pCard == null) {
			return false;
		}
		boolean removed = fCardsActive.remove(pCard);
		if (removed) {
			fCardsDeactivated.add(pCard);
		}
		notifyObservers(ModelChangeId.INDUCEMENT_SET_DEACTIVATE_CARD, pCard);
		return removed;
	}

	public Card[] getActiveCards() {
		return fCardsActive.toArray(new Card[fCardsActive.size()]);
	}

	public Card[] getDeactivatedCards() {
		return fCardsDeactivated.toArray(new Card[fCardsDeactivated.size()]);
	}

	public boolean isDeactivated(Card pCard) {
		return fCardsDeactivated.contains(pCard);
	}

	public Card[] getAllCards() {
		List<Card> allCards = new ArrayList<>();
		for (Card card : getAvailableCards()) {
			allCards.add(card);
		}
		for (Card card : getActiveCards()) {
			allCards.add(card);
		}
		for (Card card : getDeactivatedCards()) {
			allCards.add(card);
		}
		return allCards.toArray(new Card[allCards.size()]);
	}

	public boolean isActive(Card pCard) {
		return fCardsActive.contains(pCard);
	}

	// add cards and standard inducements from given inducementSet
	public void add(InducementSet pInducementSet) {
		if (pInducementSet != null) {
			for (Inducement inducement : pInducementSet.getInducements()) {
				Inducement initInducement = new Inducement(inducement.getType(), inducement.getValue());
				initInducement.setUses(inducement.getUses());
				addInducement(initInducement);
			}
			for (Card card : pInducementSet.getAllCards()) {
				addAvailableCard(card);
				if (pInducementSet.isActive(card) || pInducementSet.isDeactivated(card)) {
					activateCard(card);
					if (pInducementSet.isDeactivated(card)) {
						deactivateCard(card);
					}
				}
			}
		}
	}

	public void clear() {
		fInducements.clear();
		fCardsActive.clear();
		fCardsAvailable.clear();
		fCardsDeactivated.clear();
	}

	public int getNrOfInducements() {
		return fInducements.size();
	}

	public int totalInducements() {
		int total = 0;
		for (Inducement inducement : getInducements()) {
			total += inducement.getValue();
		}
		total += getAllCards().length;
		return total;
	}

	// no change tracking (Fumbbl communication only)
	public String[] getStarPlayerPositionIds() {
		return fStarPlayerPositionIds.toArray(new String[fStarPlayerPositionIds.size()]);
	}

	// no change tracking (Fumbbl communication only)
	public void addStarPlayerPositionId(String pStarPlayerPositionId) {
		fStarPlayerPositionIds.add(pStarPlayerPositionId);
	}

	// change tracking

	private void notifyObservers(ModelChangeId pChangeId, Object pValue) {
		if ((getTurnData() == null) || (pChangeId == null)) {
			return;
		}
		String key = getTurnData().isHomeData() ? ModelChange.HOME : ModelChange.AWAY;
		ModelChange modelChange = new ModelChange(pChangeId, key, pValue);
		getTurnData().getGame().notifyObservers(modelChange);
	}

	// XML serialization

	public void addToXml(TransformerHandler pHandler) {

		UtilXml.startElement(pHandler, XML_TAG);

		for (Inducement inducement : fInducements.values()) {
			inducement.addToXml(pHandler);
		}

		UtilXml.startElement(pHandler, _XML_TAG_STAR_PLAYER_SET);
		for (String positionId : fStarPlayerPositionIds) {
			AttributesImpl attributes = new AttributesImpl();
			UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_POSITION_ID, positionId);
			UtilXml.addEmptyElement(pHandler, _XML_TAG_STAR_PLAYER, attributes);
		}
		UtilXml.endElement(pHandler, _XML_TAG_STAR_PLAYER_SET);

		UtilXml.startElement(pHandler, _XML_TAG_CARD_SET);
		for (Card card : fCardsAvailable) {
			AttributesImpl attributes = new AttributesImpl();
			UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NAME, card.getName());
			UtilXml.addEmptyElement(pHandler, _XML_TAG_CARD, attributes);
		}
		UtilXml.endElement(pHandler, _XML_TAG_CARD_SET);

		UtilXml.endElement(pHandler, XML_TAG);

	}

	public String toXml(boolean pIndent) {
		return UtilXml.toXml(this, pIndent);
	}

	public IXmlReadable startXmlElement(Game game, String pXmlTag, Attributes pXmlAttributes) {
		IXmlReadable xmlElement = this;
		if (Inducement.XML_TAG.equals(pXmlTag)) {
			Inducement inducement = new Inducement();
			inducement.startXmlElement(game, pXmlTag, pXmlAttributes);
			addInducement(inducement);
			xmlElement = inducement;
		}
		if (_XML_TAG_STAR_PLAYER_SET.equals(pXmlTag)) {
			fStarPlayerPositionIds.clear();
		}
		if (_XML_TAG_STAR_PLAYER.equals(pXmlTag)) {
			String positionId = pXmlAttributes.getValue(_XML_ATTRIBUTE_POSITION_ID).trim();
			addStarPlayerPositionId(positionId);
		}
		if (_XML_TAG_CARD_SET.equals(pXmlTag)) {
			fCardsAvailable.clear();
			fCardsActive.clear();
			fCardsDeactivated.clear();
		}
		if (_XML_TAG_CARD.equals(pXmlTag)) {
			String cardName = pXmlAttributes.getValue(_XML_ATTRIBUTE_NAME).trim();
			Card card = game.<CardFactory>getFactory(Factory.CARD).forName(cardName);
			if (card != null) {
				fCardsAvailable.add(card);
			}
		}
		return xmlElement;
	}

	public boolean endXmlElement(Game game, String pXmlTag, String pValue) {
		return XML_TAG.equals(pXmlTag);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		JsonArray inducementsArray = new JsonArray();
		for (Inducement inducement : fInducements.values()) {
			inducementsArray.add(inducement.toJsonValue());
		}
		IJsonOption.INDUCEMENT_ARRAY.addTo(jsonObject, inducementsArray);
		List<String> cardsAvailable = new ArrayList<>();
		for (Card card : getAvailableCards()) {
			cardsAvailable.add(card.getName());
		}
		IJsonOption.CARDS_AVAILABLE.addTo(jsonObject, cardsAvailable);
		List<String> cardsActive = new ArrayList<>();
		for (Card card : getActiveCards()) {
			cardsActive.add(card.getName());
		}
		IJsonOption.CARDS_ACTIVE.addTo(jsonObject, cardsActive);
		List<String> cardsDeactivated = new ArrayList<>();
		for (Card card : getDeactivatedCards()) {
			cardsDeactivated.add(card.getName());
		}
		IJsonOption.CARDS_DEACTIVATED.addTo(jsonObject, cardsDeactivated);
		String[] starPlayerPositionIds = getStarPlayerPositionIds();
		if (ArrayTool.isProvided(starPlayerPositionIds)) {
			IJsonOption.STAR_PLAYER_POSTION_IDS.addTo(jsonObject, starPlayerPositionIds);
		}
		return jsonObject;
	}

	public InducementSet initFrom(IFactorySource source, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		JsonArray inducements = IJsonOption.INDUCEMENT_ARRAY.getFrom(source, jsonObject);
		if (inducements != null) {
			for (int i = 0; i < inducements.size(); i++) {
				Inducement inducement = new Inducement();
				inducement.initFrom(source, inducements.get(i));
				addInducement(inducement);
			}
		}
		CardFactory cardFactory = source.<CardFactory>getFactory(Factory.CARD);
		String[] cardsAvailable = IJsonOption.CARDS_AVAILABLE.getFrom(source, jsonObject);
		if (ArrayTool.isProvided(cardsAvailable)) {
			for (String cardName : cardsAvailable) {
				fCardsAvailable.add(cardFactory.forName(cardName));
			}
		}
		String[] cardsActive = IJsonOption.CARDS_ACTIVE.getFrom(source, jsonObject);
		if (ArrayTool.isProvided(cardsActive)) {
			for (String cardName : cardsActive) {
				fCardsActive.add(cardFactory.forName(cardName));
			}
		}
		String[] cardsDeactivated = IJsonOption.CARDS_DEACTIVATED.getFrom(source, jsonObject);
		if (ArrayTool.isProvided(cardsDeactivated)) {
			for (String cardName : cardsDeactivated) {
				fCardsDeactivated.add(cardFactory.forName(cardName));
			}
		}
		String[] starPlayerPositionIds = IJsonOption.STAR_PLAYER_POSTION_IDS.getFrom(source, jsonObject);
		if (ArrayTool.isProvided(starPlayerPositionIds)) {
			fStarPlayerPositionIds.addAll(Arrays.asList(starPlayerPositionIds));
		}
		return this;
	}

}
