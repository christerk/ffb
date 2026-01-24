package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportChompRoll;

@ReportMessageType(ReportId.CHOMP_ROLL)
@RulesCollection(Rules.BB2025)
public class ChompRollMessage extends ReportMessageBase<ReportChompRoll> {

	@Override
	protected void render(ReportChompRoll report) {
		Player<?> player = game.getPlayerById(report.getChomper());
		StringBuilder status = new StringBuilder();
		status.append("Chomp Roll [ ").append(report.getRoll()).append(" ]");
		println(getIndent(), TextStyle.ROLL, status.toString());
		print(getIndent() + 1, false, player);
		status = new StringBuilder();
		if (report.isSuccessful()) {
			status.append(" chomped ");
		} else {
			status.append(" failed to chomp ");
		}
		print(getIndent() + 1, status.toString());

		Player<?> defender = game.getPlayerById(report.getChompee());
		print(getIndent() + 1, false, defender);

		println(getIndent() + 1, ".");
	}
}
