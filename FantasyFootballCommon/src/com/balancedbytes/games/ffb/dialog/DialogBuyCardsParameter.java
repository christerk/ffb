package com.balancedbytes.games.ffb.dialog;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.CardType;
import com.balancedbytes.games.ffb.CardTypeFactory;
import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogBuyCardsParameter implements IDialogParameter {

  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_AVAILABLE_CARDS = "availableCards";
  private static final String _XML_ATTRIBUTE_AVAILABLE_GOLD = "availableGold";
  private static final String _XML_ATTRIBUTE_TYPE = "type";
  private static final String _XML_ATTRIBUTE_CARDS = "cards";
  
  private static final String _XML_TAG_DECK = "deck";
  

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
  	DialogBuyCardsParameter dialogParameter = new DialogBuyCardsParameter(getTeamId(), getAvailableCards(), getAvailableGold());
  	for (CardType type : CardType.values()) {
  		dialogParameter.put(type, getNrOfCards(type));
  	}
  	return dialogParameter;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_AVAILABLE_CARDS, getAvailableCards());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_AVAILABLE_GOLD, getAvailableGold());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    if (fNrOfCardsPerType.size() > 0) {
      for (CardType type : fNrOfCardsPerType.keySet()) {
        attributes = new AttributesImpl();
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TYPE, type.getName());
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CARDS, fNrOfCardsPerType.get(type));
        UtilXml.addEmptyElement(pHandler, _XML_TAG_DECK, attributes);
      }
    }
    UtilXml.endElement(pHandler, XML_TAG);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getId().getId());
    pByteList.addString(getTeamId());
    pByteList.addByte((byte) getAvailableCards());
    pByteList.addInt(getAvailableGold());
    pByteList.addByte((byte) fNrOfCardsPerType.size());
  	for (CardType type : fNrOfCardsPerType.keySet()) {
  		pByteList.addByte((byte) type.getId());
  		pByteList.addByte(fNrOfCardsPerType.get(type).byteValue());
  	}
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fTeamId = pByteArray.getString();
    fAvailableCards = pByteArray.getByte();
    fAvailableGold = pByteArray.getInt();
    fNrOfCardsPerType.clear();
    int nrOfEntries = pByteArray.getByte();
    CardTypeFactory cardTypeFactory = new CardTypeFactory();
    for (int i = 0; i < nrOfEntries; i++) {
    	CardType type = cardTypeFactory.forId(pByteArray.getByte());
    	int nrOfCards = pByteArray.getByte();
    	put(type, nrOfCards);
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
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
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
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
  }

}
