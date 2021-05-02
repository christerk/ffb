package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportFumblerooskie;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.FUMBLEROOSKIE)
public class UseFumblerooskieMessage extends ReportMessageBase<ReportFumblerooskie> {
	@Override
	protected void render(ReportFumblerooskie report) {
		Player<?> player = game.getPlayerById(report.getPlayerId());

		print(getIndent(), false, player);
		if (report.isUsed()) {
			println(getIndent(), TextStyle.NONE, " drops the ball using Fumblerooskie.");
		} else {
			println(getIndent(), TextStyle.NONE, " did not vacant the square and thus keeps the ball.");
		}
	}
}
