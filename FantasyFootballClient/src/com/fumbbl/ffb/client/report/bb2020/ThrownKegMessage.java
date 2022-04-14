package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportThrownKeg;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.THROWN_KEG)
public class ThrownKegMessage extends ReportMessageBase<ReportThrownKeg> {
	@Override
	protected void render(ReportThrownKeg report) {
		println(getIndent(), TextStyle.ROLL, "[ " + report.getRoll() + "] Beer Barrel Bash Roll");
		Player<?> thrower = game.getPlayerById(report.getPlayerId());
		print(getIndent(), false, thrower);
		print(getIndent(), TextStyle.NONE, "hits ");
		if (report.isFumble()) {
			print(getIndent(), TextStyle.NONE, thrower.getPlayerGender().getSelf());
		} else if (report.isSuccess()) {
			print(getIndent(), false, game.getPlayerById(report.getTargetPlayerId()));
		} else {
			print(getIndent(), TextStyle.NONE, "no one");
		}
		println(getIndent(), TextStyle.NONE, ".");
	}
}
