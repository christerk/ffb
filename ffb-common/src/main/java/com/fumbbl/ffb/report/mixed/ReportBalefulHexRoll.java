package com.fumbbl.ffb.report.mixed;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportBalefulHexRoll extends ReportSkillRoll {

	private String target;

	public ReportBalefulHexRoll() {
	}

	public ReportBalefulHexRoll(String pPlayerId, String target, boolean pSuccessful, int pRoll,
															boolean pReRolled) {
		super(pPlayerId, pSuccessful, pRoll, 2, pReRolled, new RollModifier[0]);
		this.target = target;
	}

	public String getTarget() {
		return target;
	}

	@Override
	public ReportId getId() {
		return ReportId.BALEFUL_HEX;
	}

	@Override
	public ReportBalefulHexRoll transform(IFactorySource source) {
		return new ReportBalefulHexRoll(getPlayerId(), target, isSuccessful(), getRoll(), isReRolled());
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.TARGET_PLAYER_ID.addTo(jsonObject, target);
		return jsonObject;
	}

	@Override
	public ReportSkillRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		target = IJsonOption.TARGET_PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}
}
