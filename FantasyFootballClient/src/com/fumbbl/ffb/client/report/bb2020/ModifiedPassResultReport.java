package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportModifiedPassResult;

@ReportMessageType(ReportId.MODIFIED_PASS_RESULT)
@RulesCollection(RulesCollection.Rules.BB2020)
public class ModifiedPassResultReport extends ReportMessageBase<ReportModifiedPassResult> {

	@Override
	protected void render(ReportModifiedPassResult report) {
		println(getIndent() + 1, TextStyle.EXPLANATION, "Using " + report.getSkill().getName() +
			" would change the result to " + report.getPassResult().getName());
	}
}

