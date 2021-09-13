package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportPrayerAmount;
import com.fumbbl.ffb.util.StringTool;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.PRAYER_AMOUNT)
public class PrayerAmountMessage extends ReportMessageBase<ReportPrayerAmount> {

	@Override
	protected void render(ReportPrayerAmount report) {
		println(getIndent(), TextStyle.BOLD, "Praying to Nuffle");
		print(getIndent() + 1, TextStyle.HOME_BOLD, game.getTeamHome().getName());
		println(getIndent() + 1, TextStyle.NONE, getTvText(report.getTvHome()));
		print(getIndent() + 1, TextStyle.AWAY_BOLD, game.getTeamAway().getName());
		println(getIndent() + 1, TextStyle.NONE, getTvText(report.getTvAway()));
		if (report.isHomeTeamReceivesPrayers()) {
			print(getIndent() + 2, TextStyle.HOME, game.getTeamHome().getName());
		} else {
			print(getIndent() + 2, TextStyle.AWAY, game.getTeamAway().getName());
		}
		String prayers = report.getPrayerAmount() == 1 ? "Prayer" : "Prayers";
		println(getIndent() + 2, TextStyle.EXPLANATION, " is granted " + report.getPrayerAmount() + " " + prayers + " to Nuffle");
	}

	private String getTvText(int tv) {
		return " has a TV of " +
			StringTool.formatThousands(tv) +
			" after buying inducements.";
	}
}
