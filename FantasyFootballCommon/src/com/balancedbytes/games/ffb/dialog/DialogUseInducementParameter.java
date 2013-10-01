package com.balancedbytes.games.ffb.dialog;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.InducementTypeFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class DialogUseInducementParameter implements IDialogParameter {

  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_TAG_INDUCEMENT = "inducement";
  private static final String _XML_TAG_CARD = "card";

  private String fTeamId;
  private InducementType[] fInducements;
  private Card[] fCards;

  public DialogUseInducementParameter() {
    super();
  }

  public DialogUseInducementParameter(String pTeamId, InducementType[] pInducements, Card[] pCards) {
    fTeamId = pTeamId;
    fInducements = pInducements;
    fCards = pCards;
  }

  public DialogId getId() {
    return DialogId.USE_INDUCEMENT;
  }

  public String getTeamId() {
    return fTeamId;
  }

  public InducementType[] getInducements() {
    return fInducements;
  }

  public Card[] getCards() {
    return fCards;
  }

  // transformation

  public IDialogParameter transform() {
    return new DialogUseInducementParameter(getTeamId(), getInducements(), getCards());
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    for (InducementType inducement : getInducements()) {
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
    InducementType[] inducements = getInducements();
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
    DialogId dialogId = DialogId.fromId(pByteArray.getByte());
    if (getId() != dialogId) {
      throw new IllegalStateException("Wrong dialog id. Expected " + getId().getName() + " received " + ((dialogId != null) ? dialogId.getName() : "null"));
    }
    fTeamId = pByteArray.getString();
    byte[] inducementIds = pByteArray.getByteArray();
    fInducements = new InducementType[inducementIds.length];
    InducementTypeFactory inducementTypeFactory = new InducementTypeFactory();
    for (int i = 0; i < fInducements.length; i++) {
      fInducements[i] = inducementTypeFactory.forId(inducementIds[i]);
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

}
