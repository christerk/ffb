package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportDodgySnackRoll;

@ReportMessageType(ReportId.DODGY_SNACK_ROLL)
@RulesCollection(Rules.BB2025)
public class DodgySnackRollMessage extends ReportMessageBase<ReportDodgySnackRoll> {

    @Override
    protected void render(ReportDodgySnackRoll report) {
	    println(getIndent(), TextStyle.ROLL, "Dodgy Snack Effect Roll [ " + report.getRoll() + " ]");
  		Player<?> player = game.getPlayerById(report.getPlayerId());
  		print(getIndent() + 1, false, player);
  		String message = report.getRoll() == 1 ? " is sent to reserves." : " suffers -MA and -AV for this drive.";
  		println(getIndent() + 1, TextStyle.NONE, message);
    }
}
