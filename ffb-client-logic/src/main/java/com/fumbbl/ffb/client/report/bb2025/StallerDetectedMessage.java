package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportStallerDetected;

@ReportMessageType(ReportId.STALLER_DETECTED)
@RulesCollection(RulesCollection.Rules.BB2025)
public class StallerDetectedMessage extends ReportMessageBase<ReportStallerDetected> {
	@Override
	protected void render(ReportStallerDetected report) {
		println(getIndent(), TextStyle.BOLD, "Stalling Detection");
		print(getIndent() + 1, true, game.getPlayerById(report.getPlayerId()));
		println(getIndent() + 1, TextStyle.NONE, " could stall");
	}
}
