package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportCardDeactivated extends NoDiceReport {

	private Card fCard;

	public ReportCardDeactivated() {
		super();
	}

	public ReportCardDeactivated(Card pCard) {
		fCard = pCard;
	}

	public ReportId getId() {
		return ReportId.CARD_DEACTIVATED;
	}

	public Card getCard() {
		return fCard;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportCardDeactivated(getCard());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.CARD.addTo(jsonObject, fCard);
		return jsonObject;
	}

	public ReportCardDeactivated initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fCard = (Card) IJsonOption.CARD.getFrom(source, jsonObject);
		return this;
	}

}
