package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportCoinThrow implements IReport {

	private boolean fCoinThrowHeads;
	private String fCoach;
	private boolean fCoinChoiceHeads;

	public ReportCoinThrow() {
		super();
	}

	public ReportCoinThrow(boolean pCoinThrowHeads, String pCoach, boolean pCoinChoiceHeads) {
		fCoinThrowHeads = pCoinThrowHeads;
		fCoach = pCoach;
		fCoinChoiceHeads = pCoinChoiceHeads;
	}

	public ReportId getId() {
		return ReportId.COIN_THROW;
	}

	public boolean isCoinThrowHeads() {
		return fCoinThrowHeads;
	}

	public String getCoach() {
		return fCoach;
	}

	public boolean isCoinChoiceHeads() {
		return fCoinChoiceHeads;
	}

	// transformation

	public IReport transform(Game game) {
		return new ReportCoinThrow(isCoinThrowHeads(), getCoach(), isCoinChoiceHeads());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.COACH.addTo(jsonObject, fCoach);
		IJsonOption.COIN_THROW_HEADS.addTo(jsonObject, fCoinThrowHeads);
		IJsonOption.COIN_CHOICE_HEADS.addTo(jsonObject, fCoinChoiceHeads);
		return jsonObject;
	}

	public ReportCoinThrow initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fCoach = IJsonOption.COACH.getFrom(game, jsonObject);
		fCoinThrowHeads = IJsonOption.COIN_THROW_HEADS.getFrom(game, jsonObject);
		fCoinChoiceHeads = IJsonOption.COIN_CHOICE_HEADS.getFrom(game, jsonObject);
		return this;
	}

}