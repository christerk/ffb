package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportQuickSnapEnd;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.QUICK_SNAP_END)
public class QuickSnapEndMessage extends ReportMessageBase<ReportQuickSnapEnd> {
	@Override
	protected void render(ReportQuickSnapEnd report) {
		String message;
		if (report.isLimitReached()) {
			message = "Moved allowed number of players.";
		} else {
			message = "No more open players available.";
		}
		println(getIndent() + 1, TextStyle.EXPLANATION, message);
	}
}
