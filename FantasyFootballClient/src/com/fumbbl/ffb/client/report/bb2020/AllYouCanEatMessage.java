package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportAllYouCanEatRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.ALL_YOU_CAN_EAT)
public class AllYouCanEatMessage extends ReportMessageBase<ReportAllYouCanEatRoll> {
	@Override
	protected void render(ReportAllYouCanEatRoll report) {

		Player<?> player = game.getPlayerById(report.getPlayerId());
		if (!report.isReRolled()) {
			print(getIndent(), true, player);
			println(getIndent(), TextStyle.BOLD, " hopes the ref did not spot " + player.getPlayerGender().getDative() + ".");
		}

		StringBuilder message = new StringBuilder();

		message.append(player.getName()).append(" ");

		if (report.isSuccessful()) {
			message.append("goes unnoticed.");
		} else {
			message.append("is spotted.");
		}

		println(getIndent(), TextStyle.NONE, message.toString());
	}
}
