package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportNervesOfSteel;

@ReportMessageType(ReportId.NERVES_OF_STEEL)
@RulesCollection(Rules.BB2020)
public class NervesOfSteelMessage extends ReportMessageBase<ReportNervesOfSteel> {

    @Override
    protected void render(ReportNervesOfSteel report) {
    	Player<?> player = game.getPlayerById(report.getPlayerId());
    	String ballAction = report.getBallAction();
    	
  		if (player != null) {
  			print(getIndent(), false, player);
  			print(getIndent(), " is using Nerves of Steel to ");
				if (report.isBomb()) {
					println(getIndent(), "throw the bomb.");
				} else {
					println(getIndent(), ballAction + " the ball.");
				}
  		}
    }
}
