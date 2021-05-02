package com.fumbbl.ffb.report.bb2016;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public class ReportSpectators implements IReport {

	private int[] fSpectatorRollHome;
	private int fSpectatorsHome;
	private int fFameHome;
	private int[] fSpectatorRollAway;
	private int fSpectatorsAway;
	private int fFameAway;

	public ReportSpectators() {
		super();
	}

	public ReportSpectators(int[] pRollHome, int pSupportersHome, int pFameHome, int[] pRollAway, int pSupportersAway,
			int pFameAway) {
		fSpectatorRollHome = pRollHome;
		fSpectatorsHome = pSupportersHome;
		fFameHome = pFameHome;
		fSpectatorRollAway = pRollAway;
		fSpectatorsAway = pSupportersAway;
		fFameAway = pFameAway;
	}

	public ReportId getId() {
		return ReportId.SPECTATORS;
	}

	public int[] getSpectatorRollHome() {
		return fSpectatorRollHome;
	}

	public int getSpectatorsHome() {
		return fSpectatorsHome;
	}

	public int getFameHome() {
		return fFameHome;
	}

	public int[] getSpectatorRollAway() {
		return fSpectatorRollAway;
	}

	public int getSpectatorsAway() {
		return fSpectatorsAway;
	}

	public int getFameAway() {
		return fFameAway;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportSpectators(getSpectatorRollAway(), getSpectatorsAway(), getFameAway(), getSpectatorRollHome(),
				getSpectatorsHome(), getFameHome());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.SPECTATOR_ROLL_HOME.addTo(jsonObject, fSpectatorRollHome);
		IJsonOption.SPECTATORS_HOME.addTo(jsonObject, fSpectatorsHome);
		IJsonOption.FAME_HOME.addTo(jsonObject, fFameHome);
		IJsonOption.SPECTATOR_ROLL_AWAY.addTo(jsonObject, fSpectatorRollAway);
		IJsonOption.SPECTATORS_AWAY.addTo(jsonObject, fSpectatorsAway);
		IJsonOption.FAME_AWAY.addTo(jsonObject, fFameAway);
		return jsonObject;
	}

	public ReportSpectators initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		fSpectatorRollHome = IJsonOption.SPECTATOR_ROLL_HOME.getFrom(game, jsonObject);
		fSpectatorsHome = IJsonOption.SPECTATORS_HOME.getFrom(game, jsonObject);
		fFameHome = IJsonOption.FAME_HOME.getFrom(game, jsonObject);
		fSpectatorRollAway = IJsonOption.SPECTATOR_ROLL_AWAY.getFrom(game, jsonObject);
		fSpectatorsAway = IJsonOption.SPECTATORS_AWAY.getFrom(game, jsonObject);
		fFameAway = IJsonOption.FAME_AWAY.getFrom(game, jsonObject);
		return this;
	}

}