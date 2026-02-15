package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportLookIntoMyEyesRoll;

@ReportMessageType(ReportId.LOOK_INTO_MY_EYES_ROLL)
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class LookIntoMyEyesRollMessage extends ReportMessageBase<ReportLookIntoMyEyesRoll> {

	@Override
	protected void render(ReportLookIntoMyEyesRoll report) {
		StringBuilder status = new StringBuilder();
		ActingPlayer actingPlayer = game.getActingPlayer();
		status.append("Look Into My Eyes Roll [ ").append(report.getRoll()).append(" ]");
		println(getIndent(), TextStyle.ROLL, status.toString());

		print(getIndent() + 1, false, actingPlayer.getPlayer());
		status = new StringBuilder();
		if (report.isSuccessful()) {
			status.append(" steals the ball from ").append(actingPlayer.getPlayer().getPlayerGender().getGenitive()).append(" opponent.");
		} else {
			status.append(" fails to steal the ball from ").append(actingPlayer.getPlayer().getPlayerGender().getGenitive()).append(" opponent.");
		}
		println(getIndent() + 1, status.toString());
	}

}
