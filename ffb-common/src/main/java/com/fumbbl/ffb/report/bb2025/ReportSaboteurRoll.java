package com.fumbbl.ffb.report.bb2025;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

@RulesCollection(Rules.BB2025)
public class ReportSaboteurRoll extends ReportSkillRoll {

	@SuppressWarnings("unused")
	public ReportSaboteurRoll() {
	}

	public ReportSaboteurRoll(String playerId, boolean successful, int roll, int minimumRoll, boolean reRolled) {
		super(playerId, successful, roll, minimumRoll, reRolled, new RollModifier[0]);
	}

	@Override
	public ReportId getId() {
		return ReportId.SABOTEUR_ROLL;
	}

	@Override
	public ReportSaboteurRoll transform(IFactorySource source) {
		return new ReportSaboteurRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled());
	}

	@Override
	public ReportSkillRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		return super.initFrom(source, jsonValue);
	}
}
