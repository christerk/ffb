package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportWizardUse;

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
