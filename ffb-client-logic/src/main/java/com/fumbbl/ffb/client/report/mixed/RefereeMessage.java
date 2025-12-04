package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportReferee;

@ReportMessageType(ReportId.REFEREE)
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class RefereeMessage extends ReportMessageBase<ReportReferee> {

	@Override
	protected void render(ReportReferee report) {
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (report.isFoulingPlayerBanned()) {
			print(getIndent(), "The referee spots the foul ");
			if (report.isUnderScrutiny()) {
				print(getIndent(), "because the team is under scrutiny ");
			}
			print(getIndent(), "and bans ");
			print(getIndent(), false, actingPlayer.getPlayer());
			println(getIndent(), " from the game.");
		} else {
			println(getIndent(), "The referee didn't spot the foul.");
		}
	}
}
