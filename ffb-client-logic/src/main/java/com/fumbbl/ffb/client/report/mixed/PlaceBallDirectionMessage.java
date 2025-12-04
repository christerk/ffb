package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportPlaceBallDirection;

@ReportMessageType(ReportId.PLACE_BALL_DIRECTION)
@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class PlaceBallDirectionMessage extends ReportMessageBase<ReportPlaceBallDirection> {
	@Override
	protected void render(ReportPlaceBallDirection report) {
		print(getIndent(), false, game.getPlayerById(report.getPlayerId()));
		String builder = " places the ball " + mapToLocal(report.getDirection()).getName() +
			".";
		println(getIndent(), TextStyle.NONE, builder);
	}
}
