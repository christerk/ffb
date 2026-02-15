package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportChompRemoved;

@ReportMessageType(ReportId.CHOMP_REMOVED)
@RulesCollection(Rules.BB2025)
public class ChompRemovedMessage extends ReportMessageBase<ReportChompRemoved> {

	@Override
	protected void render(ReportChompRemoved report) {
		Player<?> player = game.getPlayerById(report.getPlayer());
		print(getIndent() + 1, false, player);
		StringBuilder status = new StringBuilder();
		status.append(" got unchomped ");
		if (report.isSuccessful()) {
			status.append("and is free to move again.");
		} else {
			status.append("but is still held by another player.");
		}
		println(getIndent() + 1, status.toString());
	}
}
