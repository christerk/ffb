package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportWizardUse;

@ReportMessageType(ReportId.WIZARD_USE)
@RulesCollection(Rules.COMMON)
public class WizardUseMessage extends ReportMessageBase<ReportWizardUse> {

    @Override
    protected void render(ReportWizardUse report) {
  		print(getIndent(), TextStyle.BOLD, "The team wizard of ");
  		if (game.getTeamHome().getId().equals(report.getTeamId())) {
  			print(getIndent(), TextStyle.HOME_BOLD, game.getTeamHome().getName());
  		} else {
  			print(getIndent(), TextStyle.AWAY_BOLD, game.getTeamAway().getName());
  		}
  		if (report.getWizardSpell() == SpecialEffect.LIGHTNING) {
  			println(getIndent(), TextStyle.BOLD, " casts a Lightning spell.");
  		} else if (report.getWizardSpell() == SpecialEffect.ZAP) {
  			println(getIndent(), TextStyle.BOLD, " casts a Zap! spell.");
  		} else {
  			println(getIndent(), TextStyle.BOLD, " casts a Fireball spell.");
  		}
    }
}
