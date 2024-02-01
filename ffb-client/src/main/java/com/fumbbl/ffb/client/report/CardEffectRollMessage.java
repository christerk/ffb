package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportCardEffectRoll;
import com.fumbbl.ffb.report.ReportId;

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
