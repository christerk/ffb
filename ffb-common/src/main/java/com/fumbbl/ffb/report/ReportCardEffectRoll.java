package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CardEffect;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportCardEffectRoll extends NoDiceReport {

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

	public IReport transform(IFactorySource source) {
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

	public ReportCardEffectRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fCard = (Card) IJsonOption.CARD.getFrom(source, jsonObject);
		fRoll = IJsonOption.ROLL.getFrom(source, jsonObject);
		fCardEffect = (CardEffect) IJsonOption.CARD_EFFECT.getFrom(source, jsonObject);
		return this;
	}

}
