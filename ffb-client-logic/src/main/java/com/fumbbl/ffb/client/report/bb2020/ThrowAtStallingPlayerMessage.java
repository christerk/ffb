package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportThrowAtStallingPlayer;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.THROW_AT_STALLING_PLAYER)
public class ThrowAtStallingPlayerMessage extends ReportMessageBase<ReportThrowAtStallingPlayer> {
	@Override
	protected void render(ReportThrowAtStallingPlayer report) {
		println(getIndent(), TextStyle.ROLL, "Throw a Rock Roll [ " + report.getRoll() + " ]");

		print(getIndent() + 1, true, game.getPlayerById(report.getPlayerId()));
		String message;
		if (report.isSuccessful()) {
			message = " is hit by a rock.";
		} else {
			message = " is not punished for stalling.";
		}
		println(getIndent() + 1, message);
	}
}
