package com.balancedbytes.games.ffb.client.report;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportCardsAndInducementsBought;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.util.StringTool;

@ReportMessageType(ReportId.CARDS_AND_INDUCEMENTS_BOUGHT)
@RulesCollection(Rules.COMMON)
public class CardsAndInducementsBoughtMessage extends ReportMessageBase<ReportCardsAndInducementsBought> {

    public CardsAndInducementsBoughtMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportCardsAndInducementsBought report) {
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
  		int boughtItems = report.getCards() + report.getInducements() + report.getStars() + report.getMercenaries();
  		if (boughtItems == 0) {
  			status.append("no Inducements.");
  		} else {
  			List<String> itemList = new ArrayList<>();
  			if (report.getCards() > 0) {
  				if (report.getCards() == 1) {
  					itemList.add("1 Card");
  				} else {
  					itemList.add(StringTool.bind("$1 Cards", report.getCards()));
  				}
  			}
  			if (report.getInducements() > 0) {
  				if (report.getInducements() == 1) {
  					itemList.add("1 Inducement");
  				} else {
  					itemList.add(StringTool.bind("$1 Inducements", report.getInducements()));
  				}
  			}
  			if (report.getStars() > 0) {
  				if (report.getStars() == 1) {
  					itemList.add("1 Star");
  				} else {
  					itemList.add(StringTool.bind("$1 Stars", report.getStars()));
  				}
  			}
  			if (report.getMercenaries() > 0) {
  				if (report.getMercenaries() == 1) {
  					itemList.add("1 Mercenary");
  				} else {
  					itemList.add(StringTool.bind("$1 Mercenaries", report.getMercenaries()));
  				}
  			}
  			status.append(StringTool.buildEnumeration(itemList.toArray(new String[0])));
  			status.append(" for ").append(StringTool.formatThousands(report.getGold()))
  				.append(" gold total increasing their Team Value to ").append(StringTool.formatThousands(report.getNewTv()));
  		}
  		println(getIndent() + 1, status.toString());
    }
}
