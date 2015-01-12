package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardEffect;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ReportCardEffectRoll implements IReport {
  
  private Card fCard;
  private CardEffect fCardEffect;
  private int fRoll;
  
  public ReportCardEffectRoll() {
    super();
  }

  public ReportCardEffectRoll(Card pCard, int pRoll) {
    fCard = pCard;
    fRoll = pRoll;
  }
  
  public ReportId getId() {
    return ReportId.CARD_EFFECT_ROLL;
  }
  
  public Card getCard() {
    return fCard;
  }
  
  public int getRoll() {
    return fRoll;
  }
  
  public void setCardEffect(CardEffect pCardEffect) {
    fCardEffect = pCardEffect;
  }
  
  public CardEffect getCardEffect() {
    return fCardEffect;
  }
  
  // transformation
  
  public IReport transform() {
    ReportCardEffectRoll transformedReport = new ReportCardEffectRoll(getCard(), getRoll());
    transformedReport.setCardEffect(getCardEffect());
    return transformedReport;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.CARD.addTo(jsonObject, fCard);
    IJsonOption.ROLL.addTo(jsonObject, fRoll);
    if (fCardEffect != null) {
      IJsonOption.CARD_EFFECT.addTo(jsonObject, fCardEffect);
    }
    return jsonObject;
  }
  
  public ReportCardEffectRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fCard = (Card) IJsonOption.CARD.getFrom(jsonObject);
    fRoll = IJsonOption.ROLL.getFrom(jsonObject);
    fCardEffect = (CardEffect) IJsonOption.CARD_EFFECT.getFrom(jsonObject);
    return this;
  }
    
}
