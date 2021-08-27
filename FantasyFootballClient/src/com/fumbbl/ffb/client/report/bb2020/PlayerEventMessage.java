package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportPlayerEvent;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.PLAYER_EVENT)
public class PlayerEventMessage extends ReportMessageBase<ReportPlayerEvent> {
	@Override
	protected void render(ReportPlayerEvent report) {
		print(getIndent() + 1, false, game.getPlayerById(report.getPlayerId()));
		println(getIndent() + 1, TextStyle.NONE, " " + report.getEventMessage());
	}
}
