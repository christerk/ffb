package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportPickMeUp;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.PICK_ME_UP)
public class PickMeUpMessage extends ReportMessageBase<ReportPickMeUp> {
	@Override
	protected void render(ReportPickMeUp report) {
		Player<?> player = game.getPlayerById(report.getPlayerId());

		println(getIndent(), TextStyle.ROLL, "Pick-me-up Roll [ " + report.getRoll() + " ]");
		print(getIndent(), false, player);
		if (report.isSuccess()) {
			println(getIndent(), TextStyle.NONE, " is picked up.");
		} else {
			println(getIndent(), TextStyle.NONE, " is not picked up.");
		}
	}
}
