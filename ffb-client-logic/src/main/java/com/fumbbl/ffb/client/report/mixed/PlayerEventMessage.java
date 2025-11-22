package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportPlayerEvent;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.PLAYER_EVENT)
@RulesCollection(RulesCollection.Rules.BB2025)
public class PlayerEventMessage extends ReportMessageBase<ReportPlayerEvent> {
	@Override
	protected void render(ReportPlayerEvent report) {
		print(getIndent() + 1, false, game.getPlayerById(report.getPlayerId()));
		println(getIndent() + 1, TextStyle.NONE, " " + report.getEventMessage());
	}
}
