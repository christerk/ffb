package com.balancedbytes.games.ffb.client.report.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.client.report.ReportMessageBase;
import com.balancedbytes.games.ffb.client.report.ReportMessageType;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.bb2016.ReportInducementsBought;
import com.balancedbytes.games.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.List;

@ReportMessageType(ReportId.INDUCEMENTS_BOUGHT)
@RulesCollection(Rules.COMMON)
public class InducementsBoughtMessage extends ReportMessageBase<ReportInducementsBought> {

    @Override
    protected void render(ReportInducementsBought report) {
  		if (!statusReport.inducementsBoughtReportReceived) {
  			statusReport.inducementsBoughtReportReceived = true;
  			println(getIndent(), TextStyle.BOLD, "Buy Inducements");
  		}
  		print(getIndent() + 1, "Team ");
  		if (game.getTeamHome().getId().equals(report.getTeamId())) {
  			print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
  		} else {
  			print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
  		}
  		StringBuilder status = new StringBuilder();
  		status.append(" buys ");
  		if ((report.getNrOfInducements() == 0) && (report.getNrOfStars() == 0) && (report.getNrOfMercenaries() == 0)) {
  			status.append("no Inducements.");
  		} else {
  			List<String> itemList = new ArrayList<>();
  			if (report.getNrOfInducements() > 0) {
  				if (report.getNrOfInducements() == 1) {
  					itemList.add("1 Inducement");
  				} else {
  					itemList.add(StringTool.bind("$1 Inducements", report.getNrOfInducements()));
  				}
  			}
  			if (report.getNrOfStars() > 0) {
  				if (report.getNrOfStars() == 1) {
  					itemList.add("1 Star");
  				} else {
  					itemList.add(StringTool.bind("$1 Stars", report.getNrOfStars()));
  				}
  			}
  			if (report.getNrOfMercenaries() > 0) {
  				if (report.getNrOfMercenaries() == 1) {
  					itemList.add("1 Mercenary");
  				} else {
  					itemList.add(StringTool.bind("$1 Mercenaries", report.getNrOfMercenaries()));
  				}
  			}
  			status.append(StringTool.buildEnumeration(itemList.toArray(new String[itemList.size()])));
  			status.append(" for ").append(StringTool.formatThousands(report.getGold())).append(" gold total.");
  		}
  		println(getIndent() + 1, status.toString());
    }
}
