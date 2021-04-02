package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.bb2020.ReportCloudBurster;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.CLOUD_BURSTER)
@RulesCollection(Rules.COMMON)
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
