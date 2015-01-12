package com.balancedbytes.games.ffb.dialog;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.InducementTypeFactory;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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
    List<String> inducementTypeNames = new ArrayList<String>();
    for (InducementType inducementType : getInducementTypes()) {
      inducementTypeNames.add(inducementType.getName());
    }
    IJsonOption.INDUCEMENT_TYPE_ARRAY.addTo(jsonObject, inducementTypeNames);
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
    String[] inducementTypeNames = IJsonOption.INDUCEMENT_TYPE_ARRAY.getFrom(jsonObject);
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
