package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportCloudBurster;

@ReportMessageType(ReportId.CLOUD_BURSTER)
@RulesCollection(Rules.COMMON)
@RulesCollection(Rules.BB2025)
public class CloudBursterMessage extends ReportMessageBase<ReportCloudBurster> {

    @Override
    protected void render(ReportCloudBurster report) {
  		Player<?> throwingPlayer = game.getPlayerById(report.getThrowerId());
  		Player<?> interceptingPlayer = game.getPlayerById(report.getInterceptorId());
  		String thrower = throwingPlayer.getName();
  		String interceptor = interceptingPlayer.getName();
  		String genitiv = interceptingPlayer.getPlayerGender().getGenitive();
  		boolean homeIsThrowing = game.getTeamHome().getId().equals(report.getThrowerTeamId());
  		TextStyle throwerStyle = homeIsThrowing ? TextStyle.HOME_BOLD : TextStyle.AWAY_BOLD;
  		TextStyle interceptorStyle = homeIsThrowing ? TextStyle.AWAY_BOLD : TextStyle.HOME_BOLD;

  		print(1, throwerStyle, thrower);
  		println(1, TextStyle.BOLD, " uses CloudBurster");
  		print(2, interceptorStyle, interceptor);
  		println(2, TextStyle.NONE, " has to reroll " + genitiv + " successful interception.");
    }
}
