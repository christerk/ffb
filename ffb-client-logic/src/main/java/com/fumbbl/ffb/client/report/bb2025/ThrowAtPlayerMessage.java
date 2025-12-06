package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportThrowAtPlayer;

@ReportMessageType(ReportId.THROW_AT_PLAYER)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ThrowAtPlayerMessage extends ReportMessageBase<ReportThrowAtPlayer> {
	@Override
	protected void render(ReportThrowAtPlayer report) {
		Player<?> player = game.getPlayerById(report.getPlayerId());

		println(getIndent(), TextStyle.ROLL, "Throw a Rock Roll [ " + report.getRoll() + " ]");

		print(getIndent() + 1, "Fans throw a rock at ");
		print(getIndent() + 1, true, player);
		String message;
		if (report.isSuccessful()) {
			message = " knocking " + player.getPlayerGender().getDative() + " down.";
		} else {
			message = " but miss " + player.getPlayerGender().getDative() + ".";
		}
		println(getIndent() + 1, message);
	}
}
