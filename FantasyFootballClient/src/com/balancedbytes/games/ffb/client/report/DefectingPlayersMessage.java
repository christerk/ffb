package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportDefectingPlayers;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.util.ArrayTool;

@ReportMessageType(ReportId.DEFECTING_PLAYERS)
@RulesCollection(Rules.COMMON)
public class DefectingPlayersMessage extends ReportMessageBase<ReportDefectingPlayers> {

    @Override
    protected void render(ReportDefectingPlayers report) {
  		String[] playerIds = report.getPlayerIds();
  		if (ArrayTool.isProvided(playerIds)) {
  			int[] rolls = report.getRolls();
  			boolean[] defecting = report.getDefectings();
  			for (int i = 0; i < playerIds.length; i++) {
  				StringBuilder status = new StringBuilder();
  				status.append("Defecting Players Roll [ ").append(rolls[i]).append(" ]");
  				println(getIndent(), TextStyle.ROLL, status.toString());
  				Player<?> player = game.getPlayerById(playerIds[i]);
  				print(getIndent() + 1, false, player);
  				if (defecting[i]) {
  					println(getIndent() + 1, TextStyle.NONE, " leaves the team in disgust.");
  				} else {
  					println(getIndent() + 1, TextStyle.NONE, " stays with the team.");
  				}
  			}
  		}
    }
}
