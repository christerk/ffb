package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportBretonnianPastriesRoll;

@ReportMessageType(ReportId.BRETONNIAN_PASTRIES_ROLL)
@RulesCollection(Rules.BB2025)
public class BretonnianPastriesRollMessage extends ReportMessageBase<ReportBretonnianPastriesRoll> {

	@Override
	protected void render(ReportBretonnianPastriesRoll report) {
		println(getIndent(), TextStyle.ROLL, "Bretonnian Pastries Roll [ " + report.getRoll() + " ]");
		Player<?> player = game.getPlayerById(report.getPlayerId());
		print(getIndent() + 1, false, player);
		if (report.isSuccessful()) {
			println(getIndent() + 1, TextStyle.NONE, " is revived by the pastries and is placed in the Reserves box.");
		} else {
			println(getIndent() + 1, TextStyle.NONE, " eats the pastry and enters a food coma. The player stays Knocked Out.");
		}
	}
}
