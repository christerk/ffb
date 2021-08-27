package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportWinnings;
import com.fumbbl.ffb.util.StringTool;

@ReportMessageType(ReportId.WINNINGS)
@RulesCollection(Rules.BB2020)
public class WinningsMessage extends ReportMessageBase<ReportWinnings> {

	@Override
	protected void render(ReportWinnings report) {

		print(getIndent() + 1, TextStyle.HOME_BOLD, game.getTeamHome().getName());
		println(getIndent() + 1, TextStyle.NONE, " earns " + StringTool.formatThousands(report.getWinningsHome()) + " gold.");

		print(getIndent() + 1, TextStyle.AWAY_BOLD, game.getTeamAway().getName());
		println(getIndent() + 1, TextStyle.NONE, " earns " + StringTool.formatThousands(report.getWinningsAway()) + " gold.");

	}
}
