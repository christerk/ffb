package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportTrapDoor;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.TRAP_DOOR)
public class TrapDoorMessage extends ReportMessageBase<ReportTrapDoor> {
	@Override
	protected void render(ReportTrapDoor report) {
		Player<?> player = game.getPlayerById(report.getPlayerId());

		println(getIndent(), TextStyle.ROLL, "Trap Door Roll [ " + report.getRoll() + " ]");
		print(getIndent(), false, player);
		if (report.isEscaped()) {
			println(getIndent(), TextStyle.NONE, " escapes the trap door.");
		} else {
			println(getIndent(), TextStyle.NONE, " falls down the trap door.");
		}
	}
}
