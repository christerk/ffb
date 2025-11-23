package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportThenIStartedBlastin;
import com.fumbbl.ffb.util.StringTool;

/**
 * This is the same as "Then I Started Blastin'!"" in 2020, its just a skill rename.
 */
@RulesCollection(RulesCollection.Rules.BB2025)
@ReportMessageType(ReportId.THEN_I_STARTED_BLASTIN)
public class BlastinSolvesEverythingMessage extends ReportMessageBase<ReportThenIStartedBlastin> {
	@Override
	protected void render(ReportThenIStartedBlastin report) {
		if (report.getRoll() > 0) {
			println(getIndent(), TextStyle.ROLL, "\"Blastin' Solves Everything\" Roll [ " + report.getRoll() + " ]");
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
