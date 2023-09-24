package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportShowStarReRoll;

@ReportMessageType(ReportId.SHOW_STAR_RE_ROLL)
@RulesCollection(Rules.BB2020)
public class ShowStarReRollMessage extends ReportMessageBase<ReportShowStarReRoll> {

	@Override
	protected void render(ReportShowStarReRoll report) {

		Player<?> player = game.getPlayerById(report.getPlayerId());
		print(getIndent(), false, player);

		print(getIndent(), " is the Star of the Show and ");
		Team team = player.getTeam();
		if (team == game.getTeamHome()) {
			print(getIndent(), TextStyle.HOME, game.getTeamHome().getName());
		} else {
			print(getIndent(), TextStyle.AWAY, game.getTeamAway().getName());
		}
		println(getIndent(), " gains a Re-Roll only available for this drive.");
		println(getIndent(), TextStyle.EXPLANATION, "Will be added for the next drive.");
	}
}
