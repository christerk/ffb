package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportPrayerEnd;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.PRAYER_END)
public class PrayerEndMessage extends ReportMessageBase<ReportPrayerEnd> {

	@Override
	protected void render(ReportPrayerEnd report) {
		println(getIndent(), TextStyle.ROLL, "Prayer effect ended: " + report.getPrayer().getName());
		println(getIndent() + 2, TextStyle.EXPLANATION, "Effect was: " + report.getPrayer().getDescription());
	}
}
