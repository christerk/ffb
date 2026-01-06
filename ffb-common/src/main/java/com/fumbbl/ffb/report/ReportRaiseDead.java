package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.util.StringTool;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportRaiseDead extends NoDiceReport {

	private String fPlayerId, position;
	private boolean fNurglesRot;

	@SuppressWarnings("unused")
	public ReportRaiseDead() {
		super();
	}

	public ReportRaiseDead(String pPlayerId, String position, boolean pNurglesRot) {
		fPlayerId = pPlayerId;
		this.position = position;
		fNurglesRot = pNurglesRot;
	}

	public ReportId getId() {
		return ReportId.RAISE_DEAD;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public boolean isNurglesRot() {
		return fNurglesRot;
	}

	public String getPosition() {
		return position;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportRaiseDead(getPlayerId(), position, isNurglesRot());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.NURGLES_ROT.addTo(jsonObject, fNurglesRot);
		if (StringTool.isProvided(position)) {
			IJsonOption.POSITION_NAME.addTo(jsonObject, position);
		}
		return jsonObject;
	}

	public ReportRaiseDead initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fNurglesRot = IJsonOption.NURGLES_ROT.getFrom(source, jsonObject);
		if (IJsonOption.POSITION_NAME.isDefinedIn(jsonObject)) {
			position = IJsonOption.POSITION_NAME.getFrom(source, jsonObject);
		}
		return this;
	}

}
