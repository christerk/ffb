package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportScatterBall;
import com.fumbbl.ffb.util.ArrayTool;

@ReportMessageType(ReportId.SCATTER_BALL)
@RulesCollection(Rules.COMMON)
public class ScatterBallMessage extends ReportMessageBase<ReportScatterBall> {

    @Override
    protected void render(ReportScatterBall report) {
  		StringBuilder status = new StringBuilder();
  		if (report.isGustOfWind()) {
  			setIndent(getIndent() + 1);
  			status.append("A gust of wind scatters the ball.");
  			println(getIndent(), status.toString());
  			status = new StringBuilder();
  		}
  		int[] rolls = report.getRolls();
  		if (ArrayTool.isProvided(rolls)) {
  			if (rolls.length > 1) {
  				status.append("Scatter Rolls [ ");
  			} else {
  				status.append("Scatter Roll [ ");
  			}
  			for (int i = 0; i < rolls.length; i++) {
  				if (i > 0) {
  					status.append(", ");
  				}
  				status.append(rolls[i]);
  			}
  			status.append(" ] ");
  			Direction[] directions = report.getDirections();
  			for (int i = 0; i < directions.length; i++) {
  				if (i > 0) {
  					status.append(", ");
  				}
  				status.append(directions[i].getName());
  			}
  			println(getIndent(), TextStyle.ROLL, status.toString());
  		}
  		if (report.isGustOfWind()) {
  			setIndent(getIndent() - 1);
  		}
		}
}
