package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportDefectingPlayers;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.util.ArrayTool;

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
				  println(getIndent(), TextStyle.ROLL, "Defecting Players Roll [ " + rolls[i] + " ]");
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
