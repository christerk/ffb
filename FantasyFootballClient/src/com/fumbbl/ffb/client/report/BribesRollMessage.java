package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.PlayerGender;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportBribesRoll;
import com.fumbbl.ffb.report.ReportId;

@ReportMessageType(ReportId.BRIBES_ROLL)
@RulesCollection(Rules.COMMON)
public class BribesRollMessage extends ReportMessageBase<ReportBribesRoll> {

    @Override
    protected void render(ReportBribesRoll report) {
  		Player<?> player = game.getPlayerById(report.getPlayerId());
  		StringBuilder status = new StringBuilder();
  		status.append("Bribes Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		if (report.isSuccessful()) {
  			PlayerGender gender = player.getPlayerGender();
  			print(getIndent() + 1, TextStyle.NONE, "The ref refrains from penalizing ");
  			print(getIndent() + 1, false, player);
  			status = new StringBuilder();
  			status.append(" and ").append(gender.getNominative()).append(" "+gender.getVerbForm("remains",  "remain")+" in the game.");
  			println(getIndent() + 1, TextStyle.NONE, status.toString());
  		} else {
  			print(getIndent() + 1, TextStyle.NONE, "The ref appears to be unimpressed and ");
  			print(getIndent() + 1, false, player);
  			println(getIndent() + 1, TextStyle.NONE, " must leave the game.");
  		}
    }
}
