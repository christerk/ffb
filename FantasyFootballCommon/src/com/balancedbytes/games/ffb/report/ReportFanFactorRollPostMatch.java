package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 *
 * @author Kalimar
 */
public class ReportFanFactorRollPostMatch implements IReport {

	private int[] fFanFactorRollHome;
	private int fFanFactorModifierHome;
	private int[] fFanFactorRollAway;
	private int fFanFactorModifierAway;

	public ReportFanFactorRollPostMatch() {
		super();
	}

	public ReportFanFactorRollPostMatch(int[] pFanFactorRollHome, int pFanFactorModifierHome, int[] pFanFactorRollAway,
	                                    int pFanFactorModifierAway) {
		fFanFactorRollHome = pFanFactorRollHome;
		fFanFactorModifierHome = pFanFactorModifierHome;
		fFanFactorRollAway = pFanFactorRollAway;
		fFanFactorModifierAway = pFanFactorModifierAway;
	}

	public ReportId getId() {
		return ReportId.FAN_FACTOR_ROLL_POST_MATCH;
	}

	public int[] getFanFactorRollHome() {
		return fFanFactorRollHome;
	}

	public int getFanFactorModifierHome() {
		return fFanFactorModifierHome;
	}

	public int[] getFanFactorRollAway() {
		return fFanFactorRollAway;
	}

	public int getFanFactorModifierAway() {
		return fFanFactorModifierAway;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportFanFactorRollPostMatch(getFanFactorRollAway(), getFanFactorModifierAway(), getFanFactorRollHome(),
				getFanFactorModifierHome());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.FAN_FACTOR_ROLL_HOME.addTo(jsonObject, fFanFactorRollHome);
		IJsonOption.FAN_FACTOR_MODIFIER_HOME.addTo(jsonObject, fFanFactorModifierHome);
		IJsonOption.FAN_FACTOR_ROLL_AWAY.addTo(jsonObject, fFanFactorRollAway);
		IJsonOption.FAN_FACTOR_MODIFIER_AWAY.addTo(jsonObject, fFanFactorModifierAway);
		return jsonObject;
	}

	public ReportFanFactorRollPostMatch initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fFanFactorRollHome = IJsonOption.FAN_FACTOR_ROLL_HOME.getFrom(game, jsonObject);
		fFanFactorModifierHome = IJsonOption.FAN_FACTOR_MODIFIER_HOME.getFrom(game, jsonObject);
		fFanFactorRollAway = IJsonOption.FAN_FACTOR_ROLL_AWAY.getFrom(game, jsonObject);
		fFanFactorModifierAway = IJsonOption.FAN_FACTOR_MODIFIER_AWAY.getFrom(game, jsonObject);
		return this;
	}

}
