package com.fumbbl.ffb.report.bb2025;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportChompRoll extends ReportSkillRoll {
	private String chomper;
	private String chompee;

	@SuppressWarnings("unused")
	public ReportChompRoll() {
	}

	public ReportChompRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
		boolean pReRolled, String chomper, String chompee) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, null);
		this.chompee = chompee;
		this.chomper = chomper;
	}

	@Override
	public ReportId getId() {
		return ReportId.CHOMP_ROLL;
	}

	@Override
	public ReportChompRoll transform(IFactorySource source) {
		return new ReportChompRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(), chomper,
			chompee);
	}

	public String getChompee() {
		return chompee;
	}

	public String getChomper() {
		return chomper;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.DEFENDER_ID.addTo(jsonObject, chompee);
		IJsonOption.ATTACKER_ID.addTo(jsonObject, chomper);
		return jsonObject;
	}

	@Override
	public ReportChompRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		chompee = IJsonOption.DEFENDER_ID.getFrom(source, UtilJson.toJsonObject(jsonValue));
		chomper = IJsonOption.ATTACKER_ID.getFrom(source, UtilJson.toJsonObject(jsonValue));
		return this;
	}
}
