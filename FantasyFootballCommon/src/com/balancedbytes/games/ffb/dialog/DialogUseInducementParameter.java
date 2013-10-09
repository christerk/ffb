package com.balancedbytes.games.ffb.dialog;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.InducementTypeFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogUseInducementParameter implements IDialogParameter {

  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_TAG_INDUCEMENT = "inducement";
  private static final String _XML_TAG_CARD = "card";

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

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    for (InducementType inducement : getInducementTypes()) {
      UtilXml.addValueElement(pHandler, _XML_TAG_INDUCEMENT, inducement.getName());
    }
    for (Card card : getCards()) {
      UtilXml.addValueElement(pHandler, _XML_TAG_CARD, card.getName());
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
    pByteList.addByte((byte) getId().getId());
    pByteList.addString(getTeamId());
    InducementType[] inducements = getInducementTypes();
    byte[] inducementIds = null;
    if (ArrayTool.isProvided(inducements)) {
      inducementIds = new byte[inducements.length];
      for (int i = 0; i < inducementIds.length; i++) {
        inducementIds[i] = (byte) inducements[i].getId();
      }
    }
    pByteList.addByteArray(inducementIds);
    Card[] cards = getCards();
    pByteList.addByte((byte) cards.length);
    for (int i = 0; i < cards.length; i++) {
      pByteList.addSmallInt(cards[i].getId());
    }
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fTeamId = pByteArray.getString();
    byte[] inducementIds = pByteArray.getByteArray();
    fInducementTypes = new InducementType[inducementIds.length];
    InducementTypeFactory inducementTypeFactory = new InducementTypeFactory();
    for (int i = 0; i < fInducementTypes.length; i++) {
      fInducementTypes[i] = inducementTypeFactory.forId(inducementIds[i]);
    }
    if (byteArraySerializationVersion > 1) {
      fCards = new Card[pByteArray.getByte()];
      CardFactory cardFactory = new CardFactory();
      for (int i = 0; i < fCards.length; i++) {
        fCards[i] = cardFactory.forId(pByteArray.getSmallInt());
      }
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    List<String> inducementTypeNames = new ArrayList<String>();
    for (InducementType inducementType : getInducementTypes()) {
      inducementTypeNames.add(inducementType.getName());
    }
    IJsonOption.INDUCEMENT_TYPES.addTo(jsonObject, inducementTypeNames);
    List<String> cardNames = new ArrayList<String>();
    for (Card card : getCards()) {
      cardNames.add(card.getName());
    }
    IJsonOption.CARDS.addTo(jsonObject, cardNames);
    return jsonObject;
  }
  
  public DialogUseInducementParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    String[] inducementTypeNames = IJsonOption.INDUCEMENT_TYPES.getFrom(jsonObject);
    fInducementTypes = new InducementType[inducementTypeNames.length];
    InducementTypeFactory inducementTypeFactory = new InducementTypeFactory();
    for (int i = 0; i < fInducementTypes.length; i++) {
      fInducementTypes[i] = inducementTypeFactory.forName(inducementTypeNames[i]);
    }
    String[] cardNames = IJsonOption.CARDS.getFrom(jsonObject);
    fCards = new Card[cardNames.length];
    CardFactory cardFactory = new CardFactory();
    for (int i = 0; i < fCards.length; i++) {
      fCards[i] = cardFactory.forName(cardNames[i]);
    }
    return this;
  }

}
