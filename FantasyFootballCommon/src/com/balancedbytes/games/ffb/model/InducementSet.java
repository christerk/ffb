package com.balancedbytes.games.ffb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.Inducement;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.change.old.CommandTurnDataChange;
import com.balancedbytes.games.ffb.model.change.old.ModelChangeTurnData;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.IXmlWriteable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class InducementSet implements IByteArraySerializable, IXmlWriteable {
  
  public static final String XML_TAG = "inducementSet";
  
  private static final String _XML_TAG_INDUCEMENTS = "inducements";
  private static final String _XML_TAG_CARDS = "cards";
  private static final String _XML_TAG_CARD = "card";
  
  private static final String _XML_ATTRIBUTE_NAME = "name";
  private static final String _XML_ATTRIBUTE_ACTIVE = "active";

  private TurnData fTurnData;
  private Map<InducementType, Inducement> fInducements;
  private Set<Card> fCardsAvailable;
  private Set<Card> fCardsActive;
  private Set<Card> fCardsDeactivated;
  
  public InducementSet() {
    fInducements = new HashMap<InducementType, Inducement>();
    fCardsAvailable = new HashSet<Card>();
    fCardsActive = new HashSet<Card>();
    fCardsDeactivated = new HashSet<Card>();
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
  
  public Inducement[] getInducements() {
    return fInducements.values().toArray(new Inducement[fInducements.size()]);
  }
  
  public void addInducement(Inducement pInducement) {
    if (pInducement == null) {
    	return;
    }
    if ((getTurnData() != null) && getTurnData().getGame().isTrackingChanges()) {
      getTurnData().getGame().add(new ModelChangeTurnData(CommandTurnDataChange.ADD_INDUCEMENT, getTurnData().isHomeData(), pInducement));
    }
    fInducements.put(pInducement.getType(), pInducement);
  }
  
  public void removeInducement(Inducement pInducement) {
    if (pInducement == null) {
    	return;
    }
    if ((getTurnData() != null) && getTurnData().getGame().isTrackingChanges()) {
      getTurnData().getGame().add(new ModelChangeTurnData(CommandTurnDataChange.REMOVE_INDUCEMENT, getTurnData().isHomeData(), pInducement));
    }
    fInducements.remove(pInducement.getType());
  }
  
  public boolean hasUsesLeft(InducementType pType) {
    Inducement inducement = get(pType);
    return ((inducement != null) && (inducement.getUsesLeft() > 0));
  }
  
  public void addAvailableCard(Card pCard) {
  	if (pCard == null) {
  		return;
  	}
    if ((getTurnData() != null) && getTurnData().getGame().isTrackingChanges()) {
      getTurnData().getGame().add(new ModelChangeTurnData(CommandTurnDataChange.ADD_AVAILABLE_CARD, getTurnData().isHomeData(), pCard));
    }
    fCardsAvailable.add(pCard);
  }
  
  public boolean removeAvailableCard(Card pCard) {
  	if (pCard == null) {
  		return false;
  	}
    if ((getTurnData() != null) && getTurnData().getGame().isTrackingChanges()) {
      getTurnData().getGame().add(new ModelChangeTurnData(CommandTurnDataChange.REMOVE_AVAILABLE_CARD, getTurnData().isHomeData(), pCard));
    }
    return fCardsAvailable.remove(pCard);
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
    if ((getTurnData() != null) && getTurnData().getGame().isTrackingChanges()) {
      getTurnData().getGame().add(new ModelChangeTurnData(CommandTurnDataChange.ACTIVATE_CARD, getTurnData().isHomeData(), pCard));
    }
    if (fCardsAvailable.remove(pCard)) {
    	fCardsActive.add(pCard);
    	return true;
    } else {
      return false;
    }
  }
    
  public boolean deactivateCard(Card pCard) {
  	if (pCard == null) {
  		return false;
  	}
    if ((getTurnData() != null) && getTurnData().getGame().isTrackingChanges()) {
      getTurnData().getGame().add(new ModelChangeTurnData(CommandTurnDataChange.DEACTIVATE_CARD, getTurnData().isHomeData(), pCard));
    }
  	if (fCardsActive.remove(pCard)) {
      fCardsDeactivated.add(pCard);
  		return true;
  	} else {
  		return false;
  	}
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
  	List<Card> allCards = new ArrayList<Card>();
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
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	
  	UtilXml.startElement(pHandler, XML_TAG);

    Inducement[] inducements = getInducements();
    if (ArrayTool.isProvided(inducements)) {
    	UtilXml.startElement(pHandler, _XML_TAG_INDUCEMENTS);
      for (Inducement inducement : inducements) {
      	inducement.addToXml(pHandler);
      }
    	UtilXml.endElement(pHandler, _XML_TAG_INDUCEMENTS);
    }
    
    Card[] allCards = getAllCards();
    if (ArrayTool.isProvided(allCards)) {
    	UtilXml.startElement(pHandler, _XML_TAG_CARDS);
    	for (Card card : allCards) {
      	AttributesImpl attributes = new AttributesImpl();
      	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NAME, card.getName());
      	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ACTIVE, isActive(card));
      	UtilXml.addEmptyElement(pHandler, _XML_TAG_CARD, attributes);
    	}
    	UtilXml.endElement(pHandler, _XML_TAG_CARDS);
    }
  	
  	UtilXml.endElement(pHandler, XML_TAG);
  	
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 2;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getNrOfInducements());
    Iterator<Inducement> inducementIterator = fInducements.values().iterator();
    while (inducementIterator.hasNext()) {
      Inducement inducement = inducementIterator.next();
      inducement.addTo(pByteList);
    }
    Card[] availableCards = getAvailableCards();
    pByteList.addByte((byte) availableCards.length);
    for (Card card : availableCards) {
    	pByteList.addSmallInt(card.getId()); 
    }
    Card[] activeCards = getActiveCards();
    pByteList.addByte((byte) activeCards.length);
    for (Card card : activeCards) {
    	pByteList.addSmallInt(card.getId());
    }
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    int size = pByteArray.getByte();
    for (int i = 0; i < size; i++) {
      Inducement inducement = new Inducement();
      inducement.initFrom(pByteArray);
      addInducement(inducement);
    }
    if (byteArraySerializationVersion > 1) {
    	int nrOfAvailableCards = pByteArray.getByte();
    	CardFactory cardFactory = new CardFactory();
    	for (int i = 0; i < nrOfAvailableCards; i++) {
    		fCardsAvailable.add(cardFactory.forId(pByteArray.getSmallInt()));
    	}
    	int nrOfActiveCards = pByteArray.getByte();
    	for (int i = 0; i < nrOfActiveCards; i++) {
    		fCardsActive.add(cardFactory.forId(pByteArray.getSmallInt()));
    	}
    }
    return byteArraySerializationVersion;
  }

  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    JsonArray inducementsArray = new JsonArray();
    for (Inducement inducement : fInducements.values()) {
      inducementsArray.add(inducement.toJsonValue());
    }
    IJsonOption.INDUCEMENTS.addTo(jsonObject, inducementsArray);
    List<String> cardsAvailable = new ArrayList<String>();
    for (Card card : getAvailableCards()) {
      cardsAvailable.add(card.getName());
    }
    IJsonOption.CARDS_AVAILABLE.addTo(jsonObject, cardsAvailable);
    List<String> cardsActive = new ArrayList<String>();
    for (Card card : getActiveCards()) {
      cardsActive.add(card.getName());
    }
    IJsonOption.CARDS_ACTIVE.addTo(jsonObject, cardsActive);
    List<String> cardsDeactivated = new ArrayList<String>();
    for (Card card : getDeactivatedCards()) {
      cardsDeactivated.add(card.getName());
    }
    IJsonOption.CARDS_DEACTIVATED.addTo(jsonObject, cardsDeactivated);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    JsonArray inducements = IJsonOption.INDUCEMENTS.getFrom(jsonObject);
    for (int i = 0; i < inducements.size(); i++) {
      Inducement inducement = new Inducement();
      inducement.initFrom(inducements.get(i));
      addInducement(inducement);
    }
    CardFactory cardFactory = new CardFactory();
    String[] cardsAvailable = IJsonOption.CARDS_AVAILABLE.getFrom(jsonObject);
    for (String cardName : cardsAvailable) {
      fCardsAvailable.add(cardFactory.forName(cardName));
    }
    String[] cardsActive = IJsonOption.CARDS_ACTIVE.getFrom(jsonObject);
    for (String cardName : cardsActive) {
      fCardsActive.add(cardFactory.forName(cardName));
    }    
    String[] cardsDeactivated = IJsonOption.CARDS_DEACTIVATED.getFrom(jsonObject);
    for (String cardName : cardsDeactivated) {
      fCardsDeactivated.add(cardFactory.forName(cardName));
    }
  }
  
}
