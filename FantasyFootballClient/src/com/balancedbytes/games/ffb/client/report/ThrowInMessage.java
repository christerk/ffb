package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportThrowIn;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.THROW_IN)
@RulesCollection(Rules.COMMON)
public class ThrowInMessage extends ReportMessageBase<ReportThrowIn> {

    public ThrowInMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportThrowIn report) {
  		int directionRoll = report.getDirectionRoll();
  		int[] distanceRoll = report.getDistanceRoll();
  		Direction direction = report.getDirection();
  		if ((distanceRoll != null) && (distanceRoll.length > 1) && (direction != null)) {
  			StringBuilder status = new StringBuilder();
  			status.append("Throw In Direction Roll [ ").append(directionRoll).append(" ] ").append(direction.getName());
  			println(getIndent(), TextStyle.ROLL, status.toString());
  			status = new StringBuilder();
  			status.append("Throw In Distance Roll [ ").append(distanceRoll[0]).append(" ][ ").append(distanceRoll[1])
  				.append(" ]");
  			println(getIndent(), TextStyle.ROLL, status.toString());
  			println(getIndent() + 1, "The fans throw the ball back onto the pitch.");
  			status = new StringBuilder();
  			int distance = distanceRoll[0] + distanceRoll[1];
  			status.append("It lands ").append(distance).append(" squares ").append(direction.getName());
  			println(getIndent() + 1, status.toString());
  		}
    }
}
