package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportSwoopPlayer;

@ReportMessageType(ReportId.SWOOP_PLAYER)
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class SwoopPlayerMessage extends ReportMessageBase<ReportSwoopPlayer> {

	@Override
	protected void render(ReportSwoopPlayer report) {
		StringBuilder status = new StringBuilder();
		status.append("Swoop Roll [ ");
		status.append(report.getDistance());
		status.append(" ] in direction ");
		status.append(mapToLocal(report.getDirection()).getName());
		println(getIndent(), TextStyle.ROLL, status.toString());
		status = new StringBuilder();
		status.append("Player swoops from square (");
		status.append(report.getStartCoordinate().getX()).append(",").append(report.getStartCoordinate().getY());
		status.append(") to square (");
		status.append(report.getEndCoordinate().getX()).append(",").append(report.getEndCoordinate().getY());
		status.append(").");
		println(getIndent() + 1, status.toString());
	}
}
