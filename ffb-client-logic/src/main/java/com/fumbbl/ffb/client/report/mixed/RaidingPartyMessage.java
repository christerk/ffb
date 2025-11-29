package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportRaidingParty;

@ReportMessageType(ReportId.RAIDING_PARTY)
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class RaidingPartyMessage extends ReportMessageBase<ReportRaidingParty> {

	@Override
	protected void render(ReportRaidingParty report) {
		Player<?> player = game.getPlayerById(report.getPlayerId());
		Player<?> otherPlayer = game.getPlayerById(report.getOtherPlayerId());
		int indent = getIndent();

		print(indent, false, player);
		print(indent, TextStyle.NONE, " allows ");
		print(indent, false, otherPlayer);
		print(indent, TextStyle.NONE, " to move one square ");
		print(indent, TextStyle.NONE, mapToLocal(report.getDirection()).getName());
		println(indent, TextStyle.NONE, ".");

	}
}
