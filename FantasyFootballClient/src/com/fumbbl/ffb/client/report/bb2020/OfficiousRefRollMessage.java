package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportOfficiousRefRoll;

@ReportMessageType(ReportId.OFFICIOUS_REF_ROLL)
@RulesCollection(Rules.BB2020)
public class OfficiousRefRollMessage extends ReportMessageBase<ReportOfficiousRefRoll> {

    @Override
    protected void render(ReportOfficiousRefRoll report) {
	    println(getIndent(), TextStyle.ROLL, "Officious Ref Effect Roll [ " + report.getRoll() + " ]");
  		Player<?> player = game.getPlayerById(report.getPlayerId());
  		print(getIndent() + 1, false, player);
  		String message = report.getRoll() == 1 ? " is sent off." : " is stunned";
  		println(getIndent() + 1, TextStyle.NONE, message);
    }
}
