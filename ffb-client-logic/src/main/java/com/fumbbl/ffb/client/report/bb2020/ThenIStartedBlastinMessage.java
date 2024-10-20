package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportThenIStartedBlastin;
import com.fumbbl.ffb.report.bb2020.ReportThrownKeg;
import com.fumbbl.ffb.util.StringTool;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.THEN_I_STARTED_BLASTIN)
public class ThenIStartedBlastinMessage extends ReportMessageBase<ReportThenIStartedBlastin> {
	@Override
	protected void render(ReportThenIStartedBlastin report) {
		if (report.getRoll() > 0) {
			println(getIndent(), TextStyle.ROLL, "\"Then I Started Blastin'!\" Roll [ " + report.getRoll() + " ]");
		}
		Player<?> thrower = game.getPlayerById(report.getPlayerId());
		print(getIndent(), false, thrower);
		if (StringTool.isProvided(report.getTargetPlayerId())) {
			print(getIndent(), TextStyle.NONE, " hits ");
			if (report.isFumble()) {
				print(getIndent(), TextStyle.NONE, thrower.getPlayerGender().getSelf());
			} else if (report.isSuccess()) {
				print(getIndent(), false, game.getPlayerById(report.getTargetPlayerId()));
			} else {
				print(getIndent(), TextStyle.NONE, "a player chosen by the opposing coach");
			}
		} else {
			print(getIndent(), TextStyle.NONE, " starts blastin' ");
		}
		println(getIndent(), TextStyle.NONE, ".");
	}
}
