package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportSaboteurRoll;

@ReportMessageType(ReportId.SABOTEUR_ROLL)
@RulesCollection(Rules.BB2025)
public class SaboteurRollMessage extends ReportMessageBase<ReportSaboteurRoll> {

	@Override
	protected void render(ReportSaboteurRoll report) {
		println(getIndent(), TextStyle.ROLL, "Saboteur Roll [ " + report.getRoll() + " ]");
		Player<?> player = game.getPlayerById(report.getPlayerId());
		print(getIndent() + 1, false, player);
		if (report.isSuccessful()) {
			println(getIndent() + 1, TextStyle.NONE, " sabotages their weapon! They are KO'd and the blocker is knocked down.");
		} else {
			println(getIndent() + 1, TextStyle.NONE, " fails to detonate the weapon.");
		}
	}
}
