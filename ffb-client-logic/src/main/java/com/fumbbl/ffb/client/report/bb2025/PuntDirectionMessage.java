package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportPuntDirection;

@ReportMessageType(ReportId.PUNT_DIRECTION_ROLL)
@RulesCollection(Rules.BB2025)
public class PuntDirectionMessage extends ReportMessageBase<ReportPuntDirection> {

    @Override
    protected void render(ReportPuntDirection report) {
  		int directionRoll = report.getDirectionRoll();
  		Direction direction = report.getDirection();
  		if (direction != null) {
  			StringBuilder status = new StringBuilder();
			  String directionName = mapToLocal(direction).getName();
			  status.append("Punt Direction Roll [ ").append(directionRoll).append(" ] ").append(directionName);
				println(getIndent(), TextStyle.ROLL, status.toString());
				print(getIndent() +1 , false, game.getPlayerById(report.getPlayerId()));
				status = new StringBuilder(" punts the ball ");
				status.append(directionName);
				if (report.isOutOfBounds()) {
					status.append(" putting it out of bounds");
				}
				status.append(".");
  			println(getIndent() + 1,  status.toString());
  		}
    }
}
