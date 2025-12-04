package com.fumbbl.ffb.report.bb2025;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.NoDiceReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportPrayersAndInducementsBought extends NoDiceReport {

	private String teamId;
	private int inducements, stars, mercenaries, gold, newTv;

	public ReportPrayersAndInducementsBought() {
		super();
	}

	public ReportPrayersAndInducementsBought(String teamId, int inducements, int stars, int mercenaries, int gold, int newTv) {
		this.teamId = teamId;
		this.inducements = inducements;
		this.stars = stars;
		this.mercenaries = mercenaries;
		this.gold = gold;
		this.newTv = newTv;
	}

	public ReportId getId() {
		return ReportId.PRAYERS_AND_INDUCEMENTS_BOUGHT;
	}

	public String getTeamId() {
		return teamId;
	}

	public int getInducements() {
		return inducements;
	}

	public int getStars() {
		return stars;
	}

	public int getMercenaries() {
		return mercenaries;
	}

	public int getGold() {
		return gold;
	}

	public int getNewTv() {
		return newTv;
	}
// transformation

	public IReport transform(IFactorySource source) {
		return new ReportPrayersAndInducementsBought(getTeamId(), getInducements(), getStars(), getMercenaries(),
				getGold(), newTv);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.NR_OF_INDUCEMENTS.addTo(jsonObject, inducements);
		IJsonOption.NR_OF_STARS.addTo(jsonObject, stars);
		IJsonOption.NR_OF_MERCENARIES.addTo(jsonObject, mercenaries);
		IJsonOption.GOLD.addTo(jsonObject, gold);
		IJsonOption.TEAM_VALUE.addTo(jsonObject, newTv);
		return jsonObject;
	}

	public ReportPrayersAndInducementsBought initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		inducements = IJsonOption.NR_OF_INDUCEMENTS.getFrom(source, jsonObject);
		stars = IJsonOption.NR_OF_STARS.getFrom(source, jsonObject);
		mercenaries = IJsonOption.NR_OF_MERCENARIES.getFrom(source, jsonObject);
		gold = IJsonOption.GOLD.getFrom(source, jsonObject);
		newTv = IJsonOption.TEAM_VALUE.getFrom(source, jsonObject);
		return this;
	}

}
