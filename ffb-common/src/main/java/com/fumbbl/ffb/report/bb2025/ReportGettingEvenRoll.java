package com.fumbbl.ffb.report.bb2025;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportGettingEvenRoll extends ReportSkillRoll {

	private Keyword keyword;

	@SuppressWarnings("unused")
	public ReportGettingEvenRoll() {
	}

	public ReportGettingEvenRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                      boolean pReRolled, Keyword keyword) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, new RollModifier[0]);
		this.keyword = keyword;
	}

	public Keyword getKeyword() {
		return keyword;
	}

	@Override
	public ReportId getId() {
		return ReportId.GETTING_EVEN_ROLL;
	}

	@Override
	public ReportGettingEvenRoll transform(IFactorySource source) {
		return new ReportGettingEvenRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			keyword);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.KEYWORD.addTo(jsonObject, keyword.getName());
		return jsonObject;
	}

	@Override
	public ReportSkillRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		keyword = Keyword.forName(IJsonOption.KEYWORD.getFrom(source, UtilJson.toJsonObject(jsonValue)));
		return this;
	}
}
