package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportStallerDetected;
import com.fumbbl.ffb.util.StringTool;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.STALLER_DETECTED)
public class StallerDetectedMessage extends ReportMessageBase<ReportStallerDetected> {
	@Override
	protected void render(ReportStallerDetected report) {
		println(getIndent(), TextStyle.BOLD, "Stalling Detection");
		if (StringTool.isProvided(report.getPlayerId())) {
			print(getIndent() + 1, true, game.getPlayerById(report.getPlayerId()));
		} else {
			print(getIndent() + 1, TextStyle.NONE, "Nobody");
		}
		println(getIndent() + 1, TextStyle.NONE, " is stalling");
	}
}
