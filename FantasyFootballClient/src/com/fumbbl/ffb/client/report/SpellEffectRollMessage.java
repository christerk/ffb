package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSpecialEffectRoll;

@ReportMessageType(ReportId.SPELL_EFFECT_ROLL)
@RulesCollection(Rules.COMMON)
public class SpellEffectRollMessage extends ReportMessageBase<ReportSpecialEffectRoll> {

    @Override
    protected void render(ReportSpecialEffectRoll report) {
  		StringBuilder status = new StringBuilder();
  		if (report.getSpecialEffect() == SpecialEffect.LIGHTNING) {
  			status.append("Lightning Spell Effect Roll [ ").append(report.getRoll()).append(" ]");
  		}
  		if (report.getSpecialEffect() == SpecialEffect.ZAP) {
  			status.append("Zap! Spell Effect Roll [ ").append(report.getRoll()).append(" ]");
  		}
  		if (report.getSpecialEffect() == SpecialEffect.FIREBALL) {
  			status.append("Fireball Spell Effect Roll [ ").append(report.getRoll()).append(" ]");
  		}
  		if (report.getSpecialEffect() == SpecialEffect.BOMB) {
  			status.append("Bomb Effect Roll [ ");
  			status.append((report.getRoll() > 0) ? report.getRoll() : "automatic success");
  			status.append(" ]");
  		}
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		print(getIndent() + 1, false, game.getPlayerById(report.getPlayerId()));
  		if (report.isSuccessful()) {
  			if (report.getSpecialEffect().isWizardSpell()) {
  				println(getIndent() + 1, " is hit by the spell.");
  			} else {
  				println(getIndent() + 1, " is hit by the explosion.");
  			}
  		} else {
  			if (report.getSpecialEffect().isWizardSpell()) {
  				println(getIndent() + 1, " escapes the spell effect.");
  			} else {
  				println(getIndent() + 1, " escapes the explosion.");
  			}
  		}
    }
}
