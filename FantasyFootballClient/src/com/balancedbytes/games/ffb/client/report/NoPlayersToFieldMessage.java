package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.ParagraphStyle;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportNoPlayersToField;
import com.balancedbytes.games.ffb.util.StringTool;

@ReportMessageType(ReportId.NO_PLAYERS_TO_FIELD)
@RulesCollection(Rules.COMMON)
public class NoPlayersToFieldMessage extends ReportMessageBase<ReportNoPlayersToField> {

    @Override
    protected void render(ReportNoPlayersToField report) {
  		setIndent(0);
  		if (StringTool.isProvided(report.getTeamId())) {
  			StringBuilder status = new StringBuilder();
  			if (game.getTeamHome().getId().equals(report.getTeamId())) {
  				status.append(game.getTeamHome().getName()).append(" can field no players.");
  				println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN_HOME, status.toString());
  			} else {
  				status.append(game.getTeamAway().getName()).append(" can field no players.");
  				println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN_AWAY, status.toString());
  			}
  		} else {
  			println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN, "Both teams can field no players.");
  		}
  		if (StringTool.isProvided(report.getTeamId())) {
  			println(getIndent(), TextStyle.BOLD, "The opposing team is awarded a touchdown.");
  		}
  		println(ParagraphStyle.SPACE_BELOW, TextStyle.BOLD, "The turn counter is advanced 2 steps.");
    }
}
