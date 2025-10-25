package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportPrayerWasted;
import com.fumbbl.ffb.util.StringTool;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.PRAYER_WASTED)
public class PrayerWastedMessage extends ReportMessageBase<ReportPrayerWasted> {

	@Override
	protected void render(ReportPrayerWasted report) {
		if (StringTool.isProvided(report.getPlayerId())) {
			println(getIndent() + 1, TextStyle.EXPLANATION, "Prayer " + report.getPrayerName() + " is wasted since there are no eligible skills.");
			print(getIndent() + 1, true, game.getPlayerById(report.getPlayerId()));
			println(getIndent() + 1, TextStyle.EXPLANATION, " was the selected player");
		} else {
			println(getIndent() + 1, TextStyle.EXPLANATION, "Prayer " + report.getPrayerName() + " is wasted since there are no eligible players.");
		}
	}
}
