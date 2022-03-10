package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportTwoForOne;

@ReportMessageType(ReportId.TWO_FOR_ONE)
@RulesCollection(Rules.BB2020)
public class TwoForOneMessage extends ReportMessageBase<ReportTwoForOne> {

	@Override
	protected void render(ReportTwoForOne report) {
		Player<?> player = game.getPlayerById(report.getPlayerId());
		Player<?> partner = game.getPlayerById(report.getPartnerId());
		int indent = getIndent();
		String verb, reason;
		if (report.isUsed()) {
			verb = "gains";
			reason = "is injured";
		} else {
			verb = "loses";
			reason = "has recovered";
		}
		print(indent, false, player);
		print(indent, TextStyle.NONE, " " + verb + " Loner (2+) because");
		print(indent, false, partner);
		println(indent, TextStyle.NONE, " " + reason + ".");
	}
}
