package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportProjectileVomit extends ReportSkillRoll {
	private String defenderId;

	public ReportProjectileVomit() {
	}

	public ReportProjectileVomit(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                             boolean pReRolled, String defenderId) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, null);
		this.defenderId = defenderId;
	}

	@Override
	public ReportId getId() {
		return ReportId.PROJECTILE_VOMIT;
	}

	@Override
	public ReportProjectileVomit transform(IFactorySource source) {
		return new ReportProjectileVomit(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			defenderId);
	}

	public String getDefenderId() {
		return defenderId;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.DEFENDER_ID.addTo(jsonObject, defenderId);
		return jsonObject;
	}

	@Override
	public ReportProjectileVomit initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		defenderId = IJsonOption.DEFENDER_ID.getFrom(source, UtilJson.toJsonObject(pJsonValue));
		return this;
	}
}
