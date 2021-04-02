package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportScatterPlayer;
import com.balancedbytes.games.ffb.util.ArrayTool;

@ReportMessageType(ReportId.SCATTER_PLAYER)
@RulesCollection(Rules.COMMON)
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
  				status.append(directions[i].getName());
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
