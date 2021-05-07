package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportQuickSnapCount;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.QUICK_SNAP_COUNT)
public class QuickSnapCountMessage extends ReportMessageBase<ReportQuickSnapCount> {
	@Override
	protected void render(ReportQuickSnapCount report) {
		String builder = "Moved " +
			report.getAmount() +
			" of the allowed " +
			report.getLimit() +
			" players (" +
			report.getAvailable() +
			" still open).";
		println(getIndent() + 1, TextStyle.EXPLANATION, builder);
	}
}
