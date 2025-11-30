package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.factory.CardFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.PrayerFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.inducement.Prayer;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.model.change.ModelChangeId;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.xml.IXmlReadable;
import com.fumbbl.ffb.xml.IXmlSerializable;
import com.fumbbl.ffb.xml.UtilXml;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
public class InducementSet implements IXmlSerializable, IJsonSerializable {

	public static final String XML_TAG = "inducementSet";

	private static final String _XML_TAG_STAR_PLAYER_SET = "starPlayerSet";
	private static final String _XML_TAG_STAR_PLAYER = "starPlayer";

	private static final String _XML_TAG_CARD_SET = "cardSet";
	private static final String _XML_TAG_CARD = "card";

	private static final String _XML_TAG_PRAYER_SET = "prayerSet";
	private static final String _XML_TAG_PRAYER = "prayer";

	private static final String _XML_ATTRIBUTE_POSITION_ID = "positionId";
	private static final String _XML_ATTRIBUTE_NAME = "name";

	private final Map<InducementType, Inducement> fInducements;
	private final Set<Card> fCardsAvailable;
	private final Set<Card> fCardsActive;
	private final Set<Card> fCardsDeactivated;
	private final Set<String> fStarPlayerPositionIds;
	private final Set<Prayer> prayers;

	private transient TurnData fTurnData;

	public InducementSet() {
		fInducements = new HashMap<>();
		fCardsAvailable = new HashSet<>();
		fCardsActive = new HashSet<>();
		fCardsDeactivated = new HashSet<>();
		fStarPlayerPositionIds = new HashSet<>();
		prayers = new HashSet<>();
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
			return new Inducement(fInducements.get(pType));
		} else {
			return null;
		}
	}

	public Map<InducementType, Inducement> getInducementMapping() {
		return copyMap();
	}

	private Map<InducementType, Inducement> copyMap() {
		Map<InducementType, Inducement> copy = new HashMap<>();
		for (Map.Entry<InducementType, Inducement> entry: fInducements.entrySet()) {
			copy.put(entry.getKey(), new Inducement(entry.getValue()));
		}
		return copy;
	}

	public Set<InducementType> getInducementTypes() {
		return fInducements.keySet();
	}

	public Inducement[] getInducements() {
		return copyMap().values().toArray(new Inducement[0]);
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

	public void addPrayer(Prayer prayer) {
		if (prayer == null || prayers.contains(prayer)) {
			return;
		}
		prayers.add(prayer);
		notifyObservers(ModelChangeId.INDUCEMENT_SET_ADD_PRAYER, prayer);
	}

	public void removePrayer(Prayer prayer) {
		if (prayer == null || !prayers.contains(prayer)) {
			return;
		}
		prayers.remove(prayer);
		notifyObservers(ModelChangeId.INDUCEMENT_SET_REMOVE_PRAYER, prayer);
	}

	public Set<Prayer> getPrayers() {
		return prayers;
	}

	public void addAvailableCard(Card pCard) {
		if (pCard == null) {
			return;
		}
		fCardsAvailable.add(pCard);
		notifyObservers(ModelChangeId.INDUCEMENT_SET_ADD_AVAILABLE_CARD, pCard);
	}

	public void removeAvailableCard(Card pCard) {
		if (pCard == null) {
			return;
		}
		fCardsAvailable.remove(pCard);
		notifyObservers(ModelChangeId.INDUCEMENT_SET_REMOVE_AVAILABLE_CARD, pCard);
	}

	public Card[] getAvailableCards() {
		return fCardsAvailable.toArray(new Card[0]);
	}

	public boolean isAvailable(Card pCard) {
		return fCardsAvailable.contains(pCard);
	}

	public void activateCard(Card pCard) {
		if (pCard == null) {
			return;
		}
		boolean removed = fCardsAvailable.remove(pCard);
		if (removed) {
			fCardsActive.add(pCard);
		}
		notifyObservers(ModelChangeId.INDUCEMENT_SET_ACTIVATE_CARD, pCard);
	}

	public void deactivateCard(Card pCard) {
		if (pCard == null) {
			return;
		}
		boolean removed = fCardsActive.remove(pCard);
		if (removed) {
			fCardsDeactivated.add(pCard);
		}
		notifyObservers(ModelChangeId.INDUCEMENT_SET_DEACTIVATE_CARD, pCard);
	}

	public Card[] getActiveCards() {
		return fCardsActive.toArray(new Card[0]);
	}

	public Card[] getDeactivatedCards() {
		return fCardsDeactivated.toArray(new Card[0]);
	}

	public boolean isDeactivated(Card pCard) {
		return fCardsDeactivated.contains(pCard);
	}

	public Card[] getAllCards() {
		List<Card> allCards = new ArrayList<>();
		Collections.addAll(allCards, getAvailableCards());
		Collections.addAll(allCards, getActiveCards());
		Collections.addAll(allCards, getDeactivatedCards());
		return allCards.toArray(new Card[0]);
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
			for (Prayer prayer : pInducementSet.getPrayers()) {
				addPrayer(prayer);
			}
		}
	}

	public void clear() {
		fInducements.clear();
		fCardsActive.clear();
		fCardsAvailable.clear();
		fCardsDeactivated.clear();
		prayers.clear();
	}

	public int totalInducements() {
		int total = 0;
		for (Inducement inducement : getInducements()) {
			if (!Usage.EXCLUDE_FROM_COUNT.containsAll(inducement.getType().getUsages())) {
				total += inducement.getValue();
			}
		}
		total += getAllCards().length;
		return total;
	}

	// no change tracking (Fumbbl communication only)
	public String[] getStarPlayerPositionIds() {
		return fStarPlayerPositionIds.toArray(new String[0]);
	}

	// no change tracking (Fumbbl communication only)
	public void addStarPlayerPositionId(String pStarPlayerPositionId) {
		fStarPlayerPositionIds.add(pStarPlayerPositionId);
	}

	public int value(Usage usage) {
		return getInducementTypes().stream().filter(type -> type.hasUsage(usage))
			.findFirst().map(inducementType -> get(inducementType).getValue()).orElse(0);
	}

	public InducementType forUsage(Usage usage) {
		return getInducementTypes().stream().filter(inducement -> inducement.hasUsage(usage))
			.findFirst().orElse(null);
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

		UtilXml.startElement(pHandler, _XML_TAG_PRAYER_SET);
		for (Prayer prayer : prayers) {
			AttributesImpl attributes = new AttributesImpl();
			UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NAME, prayer.getName());
			UtilXml.addEmptyElement(pHandler, _XML_TAG_PRAYER, attributes);
		}
		UtilXml.endElement(pHandler, _XML_TAG_PRAYER_SET);

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
		if (_XML_TAG_PRAYER_SET.equals(pXmlTag)) {
			prayers.clear();
		}

		if (_XML_TAG_PRAYER.equals(pXmlTag)) {
			String prayerName = pXmlAttributes.getValue(_XML_ATTRIBUTE_NAME).trim();
			Prayer prayer = game.<PrayerFactory>getFactory(Factory.PRAYER).forName(prayerName);
			if (prayer != null) {
				prayers.add(prayer);
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
			IJsonOption.STAR_PLAYER_POSITION_IDS.addTo(jsonObject, starPlayerPositionIds);
		}

		IJsonOption.PRAYERS.addTo(jsonObject, prayers.stream().map(Prayer::getName).collect(Collectors.toList()));
		return jsonObject;
	}

	public InducementSet initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		JsonArray inducements = IJsonOption.INDUCEMENT_ARRAY.getFrom(source, jsonObject);
		if (inducements != null) {
			for (int i = 0; i < inducements.size(); i++) {
				Inducement inducement = new Inducement();
				inducement.initFrom(source, inducements.get(i));
				addInducement(inducement);
			}
		}
		CardFactory cardFactory = source.getFactory(Factory.CARD);
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
		String[] starPlayerPositionIds = IJsonOption.STAR_PLAYER_POSITION_IDS.getFrom(source, jsonObject);
		if (ArrayTool.isProvided(starPlayerPositionIds)) {
			fStarPlayerPositionIds.addAll(Arrays.asList(starPlayerPositionIds));
		}

		String[] prayersArray = IJsonOption.PRAYERS.getFrom(source, jsonObject);
		if (ArrayTool.isProvided(prayersArray)) {
			PrayerFactory prayerFactory = source.getFactory(Factory.PRAYER);
			prayers.addAll(Arrays.stream(prayersArray).map(prayerFactory::forName).collect(Collectors.toSet()));
		}
		return this;
	}

}
