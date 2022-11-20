package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportHitAndRun;

@ReportMessageType(ReportId.HIT_AND_RUN)
@RulesCollection(Rules.BB2020)
public class HitAndRunMessage extends ReportMessageBase<ReportHitAndRun> {

	@Override
	protected void render(ReportHitAndRun report) {
		Player<?> player = game.getPlayerById(report.getPlayerId());
		int indent = getIndent();

		print(indent, false, player);
		print(indent, TextStyle.NONE, " moves one square ");
		print(indent, TextStyle.NONE, mapToLocal(report.getDirection()).getName());
		println(indent, TextStyle.NONE, ".");

	}
}
