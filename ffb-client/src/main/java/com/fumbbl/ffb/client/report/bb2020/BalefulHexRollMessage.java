package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportBalefulHexRoll;

@ReportMessageType(ReportId.BALEFUL_HEX)
@RulesCollection(Rules.BB2020)
public class BalefulHexRollMessage extends ReportMessageBase<ReportBalefulHexRoll> {

	@Override
	protected void render(ReportBalefulHexRoll report) {
		StringBuilder status = new StringBuilder();
		ActingPlayer actingPlayer = game.getActingPlayer();
		status.append("Baleful Hex Roll [ ").append(report.getRoll()).append(" ]");
		println(getIndent(), TextStyle.ROLL, status.toString());

		print(getIndent() + 1, false, actingPlayer.getPlayer());
		status = new StringBuilder();
		if (report.isSuccessful()) {
			status.append(" makes ");
		} else {
			status.append(" fails to make ");
		}
		print(getIndent() + 1, status.toString());
		print(getIndent() + 1, false, game.getPlayerById(report.getTarget()));
		println(getIndent() + 1, " miss a turn.");
	}

}
