package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportPuntDistance;

@ReportMessageType(ReportId.PUNT_DISTANCE_ROLL)
@RulesCollection(Rules.BB2025)
public class PuntDistanceMessage extends ReportMessageBase<ReportPuntDistance> {

    @Override
    protected void render(ReportPuntDistance report) {
  		int distanceRoll = report.getRoll();
  		if (distanceRoll > 0) {
			  println(getIndent(), TextStyle.ROLL, "Punt Distance Roll [ " + distanceRoll + " ]");
				StringBuilder status = new StringBuilder("The ball is punted ");
				status.append(distanceRoll);
				status.append(" squares");
			  if (report.isOutOfBounds()) {
				  status.append(" putting it out of bounds");
			  }
  			println(getIndent() + 1, status.toString());
  		}
    }
}
