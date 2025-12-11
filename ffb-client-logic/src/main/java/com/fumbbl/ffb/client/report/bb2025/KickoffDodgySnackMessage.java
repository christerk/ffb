package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportKickoffDodgySnack;

@ReportMessageType(ReportId.KICKOFF_DODGY_SNACK)
@RulesCollection(Rules.BB2025)
public class KickoffDodgySnackMessage extends ReportMessageBase<ReportKickoffDodgySnack> {

    @Override
    protected void render(ReportKickoffDodgySnack report) {
  		StringBuilder status = new StringBuilder();
  		status.append("Dodgy Snack Roll Home Team [ ").append(report.getRollHome()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());

  		status = new StringBuilder();
  		status.append("Dodgy Snack Roll Away Team [ ").append(report.getRollAway()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());

  		for (String playerId : report.getPlayerIds()) {
  			Player<?> player = game.getPlayerById(playerId);
  			print(getIndent() + 1, false, player);
  			println(getIndent() + 1, " had a dodgy snack.");
  		}
    }
}
