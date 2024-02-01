package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportPumpUpTheCrowdReRoll;

@ReportMessageType(ReportId.PUMP_UP_THE_CROWD_RE_ROLL)
@RulesCollection(Rules.BB2020)
public class PumpUpTheCrowdReRollMessage extends ReportMessageBase<ReportPumpUpTheCrowdReRoll> {

	@Override
	protected void render(ReportPumpUpTheCrowdReRoll report) {

		Player<?> player = game.getPlayerById(report.getPlayerId());
		print(getIndent(), false, player);

		print(getIndent(), " Pumps Up The Crowd so ");
		Team team = player.getTeam();
		if (team == game.getTeamHome()) {
			print(getIndent(), TextStyle.HOME, game.getTeamHome().getName());
		} else {
			print(getIndent(), TextStyle.AWAY, game.getTeamAway().getName());
		}
		println(getIndent(), " gains a Re-Roll only available for this drive.");
	}
}
