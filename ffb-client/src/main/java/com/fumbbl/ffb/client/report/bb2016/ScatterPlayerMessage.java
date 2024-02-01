package com.fumbbl.ffb.client.report.bb2016;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportScatterPlayer;
import com.fumbbl.ffb.util.ArrayTool;

@ReportMessageType(ReportId.SCATTER_PLAYER)
@RulesCollection(Rules.BB2016)
public class ScatterPlayerMessage extends ReportMessageBase<ReportScatterPlayer> {

    @Override
    protected void render(ReportScatterPlayer report) {
  		int[] rolls = report.getRolls();
  		if (ArrayTool.isProvided(rolls)) {
  			StringBuilder status = new StringBuilder();
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
  				status.append(mapToLocal(directions[i]).getName());
  			}
  			println(getIndent(), TextStyle.ROLL, status.toString());
  			status = new StringBuilder();
  			status.append("Player scatters from square (");
  			status.append(report.getStartCoordinate().getX()).append(",").append(report.getStartCoordinate().getY());
  			status.append(") to square (");
  			status.append(report.getEndCoordinate().getX()).append(",").append(report.getEndCoordinate().getY());
  			status.append(").");
  			println(getIndent() + 1, status.toString());
  		}
    }
}
