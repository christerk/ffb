package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportPlayCard implements IReport {

	private String fTeamId;
	private Card fCard;
	private String fPlayerId;

	public ReportPlayCard() {
		super();
	}

	public ReportPlayCard(String pTeamId, Card pCard) {
		fTeamId = pTeamId;
		fCard = pCard;
	}

	public ReportPlayCard(String pTeamId, Card pCard, String pCatcherId) {
		this(pTeamId, pCard);
		fPlayerId = pCatcherId;
	}

	public ReportId getId() {
		return ReportId.PLAY_CARD;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public Card getCard() {
		return fCard;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportPlayCard(getTeamId(), getCard(), getPlayerId());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.CARD.addTo(jsonObject, fCard);
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		return jsonObject;
	}

	public ReportPlayCard initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fTeamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		fCard = (Card) IJsonOption.CARD.getFrom(game, jsonObject);
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		return this;
	}

}
