package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportModifiedDodgeResultSuccessful;

@ReportMessageType(ReportId.MODIFIED_DODGE_RESULT_SUCCESSFUL)
@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ModifiedDodgeResultSuccessfulMessage extends ReportMessageBase<ReportModifiedDodgeResultSuccessful> {

	@Override
	protected void render(ReportModifiedDodgeResultSuccessful report) {
		println(getIndent() + 1, TextStyle.EXPLANATION, "Using " + report.getSkill().getName() +
			" would result in a successful dodge");
	}
}

