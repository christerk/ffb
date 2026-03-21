package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportSwoopDirection;

@ReportMessageType(ReportId.SWOOP_DIRECTION_ROLL)
@RulesCollection(Rules.BB2025)
public class SwoopDirectionMessage extends ReportMessageBase<ReportSwoopDirection> {

	@Override
	protected void render(ReportSwoopDirection report) {
		int directionRoll = report.getDirectionRoll();
		Direction direction = report.getDirection();
		Player<?> player = game.getPlayerById(report.getPlayerId());
		StringBuilder status = new StringBuilder();
		String directionName = mapToLocal(direction).getName();
		status.append("Swoop Direction Roll [ ").append(directionRoll).append(" ] ").append(directionName);
		println(getIndent(), TextStyle.ROLL, status.toString());
		print(getIndent() + 1, false, player);
		status = new StringBuilder(" swoops ");
		status.append(directionName);
		if (report.isOutOfBounds()) {
			status.append(" which takes ");
			status.append(player.getPlayerGender().getDative());
			status.append(" out of bounds");
		}
		status.append(".");
		println(getIndent() + 1, status.toString());
	}
}
