package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportAllYouCanEatRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.ALL_YOU_CAN_EAT)
@RulesCollection(RulesCollection.Rules.BB2025)
public class AllYouCanEatMessage extends ReportMessageBase<ReportAllYouCanEatRoll> {
	@Override
	protected void render(ReportAllYouCanEatRoll report) {

		Player<?> player = game.getPlayerById(report.getPlayerId());
		if (!report.isReRolled()) {
			print(getIndent(), true, player);
			println(getIndent(), TextStyle.BOLD, " hopes the ref did not spot " + player.getPlayerGender().getDative() + ".");
		}

		StringBuilder message = new StringBuilder();

		message.append("All You Can Eat Roll [ ").append(report.getRoll()).append(" ]");
		println(getIndent() + 1, TextStyle.ROLL, message.toString());

		print(getIndent(), false, player);

		message = new StringBuilder();

		message.append(" ");

		if (report.isSuccessful()) {
			message.append("goes unnoticed.");
		} else {
			message.append("is spotted.");
		}
		println(getIndent() + 2, TextStyle.NONE, message.toString());

		if (!report.isReRolled()) {
			message = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
			println(getIndent() + 2, TextStyle.NEEDED_ROLL, message.toString());

		}
	}
}
