package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportCatchOfTheDayRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.CATCH_OF_THE_DAY)
@RulesCollection(RulesCollection.Rules.BB2025)
public class CatchOfTheDayMessage extends ReportMessageBase<ReportCatchOfTheDayRoll> {
	@Override
	protected void render(ReportCatchOfTheDayRoll report) {

		Player<?> player = game.getPlayerById(report.getPlayerId());

		if (!report.isReRolled()) {
			print(getIndent(), true, player);
			println(getIndent(), TextStyle.BOLD, " tries to get the ball from the ground:");
		}

		println(getIndent() + 1, TextStyle.ROLL, "Catch of the Day Roll [ " + report.getRoll() + " ]");

		print(getIndent() + 2, false, player);
		StringBuilder neededRoll = null;
		if (report.isSuccessful()) {

				println(getIndent() + 2, " gets the ball.");
			if (!report.isReRolled()) {
				neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll()).append("+");
			}
		} else {
			println(getIndent() + 2, " fails to get the ball.");
			if (!report.isReRolled()) {
				neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
			}
		}
		if (neededRoll != null) {
			println(getIndent() +2, neededRoll.toString());
		}
	}
}
