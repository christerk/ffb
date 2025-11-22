package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportTrapDoor;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.TRAP_DOOR)
@RulesCollection(RulesCollection.Rules.BB2025)
public class TrapDoorMessage extends ReportMessageBase<ReportTrapDoor> {
	@Override
	protected void render(ReportTrapDoor report) {
		Player<?> player = game.getPlayerById(report.getPlayerId());

		println(getIndent(), TextStyle.ROLL, "Trapdoor Roll [ " + report.getRoll() + " ]");
		print(getIndent(), false, player);
		if (report.isEscaped()) {
			println(getIndent(), TextStyle.NONE, " escapes the trapdoor.");
		} else {
			println(getIndent(), TextStyle.NONE, " falls down the trapdoor.");
		}
	}
}
