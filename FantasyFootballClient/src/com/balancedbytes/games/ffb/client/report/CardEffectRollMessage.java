package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportCardEffectRoll;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.CARD_EFFECT_ROLL)
@RulesCollection(Rules.COMMON)
public class CardEffectRollMessage extends ReportMessageBase<ReportCardEffectRoll> {

    @Override
    protected void render(ReportCardEffectRoll report) {
  		report.getCard().cardReport(report.getCardEffect(), report.getRoll()).ifPresent(
  				effectReport -> {
  					println(getIndent(), TextStyle.ROLL, effectReport.getRoll());
  					println(getIndent() + 1, effectReport.getDescription());
  				}
  			);
    }
}
