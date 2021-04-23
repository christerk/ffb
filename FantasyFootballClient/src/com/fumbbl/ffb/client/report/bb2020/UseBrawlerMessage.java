package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportUseBrawler;

@ReportMessageType(ReportId.USE_BRAWLER)
@RulesCollection(RulesCollection.Rules.BB2020)
public class UseBrawlerMessage extends ReportMessageBase<ReportUseBrawler> {
	@Override
	protected void render(ReportUseBrawler report) {
		print(getIndent() + 1, false, game.getPlayerById(report.getPlayerId()));
		StringBuilder builder = new StringBuilder(" uses Brawler to re-roll ");
		builder.append(report.getBrawlerCount());
		builder.append( " Both Down result");
		if (report.getBrawlerCount() > 1) {
			builder.append("s");
		}
		builder.append(".");
		println(getIndent() + 1, TextStyle.NONE,  builder.toString());
	}
}
