package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportPrayersAndInducementsBought;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.List;

@ReportMessageType(ReportId.PRAYERS_AND_INDUCEMENTS_BOUGHT)
@RulesCollection(Rules.BB2025)
public class PrayersAndInducementsBoughtMessage extends ReportMessageBase<ReportPrayersAndInducementsBought> {

    @Override
    protected void render(ReportPrayersAndInducementsBought report) {
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
  		int boughtItems = report.getInducements() + report.getStars() + report.getMercenaries();
  		if (boughtItems == 0) {
  			status.append("no Inducements.");
  		} else {
  			List<String> itemList = new ArrayList<>();
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
