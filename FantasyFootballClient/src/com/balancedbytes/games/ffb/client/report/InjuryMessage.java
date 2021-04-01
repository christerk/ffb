package com.balancedbytes.games.ffb.client.report;

import java.util.Arrays;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.ZappedPlayer;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.ArmorModifier;
import com.balancedbytes.games.ffb.modifiers.InjuryModifier;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportInjury;
import com.balancedbytes.games.ffb.util.ArrayTool;

@ReportMessageType(ReportId.INJURY)
@RulesCollection(Rules.COMMON)
public class InjuryMessage extends ReportMessageBase<ReportInjury> {

    public InjuryMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportInjury report) {
  		Player<?> defender = game.getPlayerById(report.getDefenderId());
  		Player<?> attacker = game.getPlayerById(report.getAttackerId());
  		StringBuilder status = new StringBuilder();

  		// report injury type

  		report.getInjuryType().reportInjuryString(status, attacker, defender);
  		if (status.length() > 0) {
  			println(getIndent() + 1, status.toString());
  			status = new StringBuilder();
  		}

  		// report armour roll

  		int[] armorRoll = report.getArmorRoll();
  		if (ArrayTool.isProvided(armorRoll)) {
  			status.append("Armour Roll [ ").append(armorRoll[0]).append(" ][ ").append(armorRoll[1]).append(" ]");
  			println(getIndent(), TextStyle.ROLL, status.toString());
  			status = new StringBuilder();
  			int rolledTotal = armorRoll[0] + armorRoll[1];
  			status.append("Rolled Total of ").append(rolledTotal);
  			int armorModifierTotal = 0;
  			boolean usingClaws = Arrays.stream(report.getArmorModifiers())
  				.anyMatch(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.reducesArmourToFixedValue));
  			for (ArmorModifier armorModifier : report.getArmorModifiers()) {
  				if (armorModifier.getModifier(attacker) != 0) {
  					armorModifierTotal += armorModifier.getModifier(attacker);
  					if (armorModifier.getModifier(attacker) > 0) {
  						status.append(" + ");
  					} else {
  						status.append(" - ");
  					}
  					if (!armorModifier.isFoulAssistModifier()) {
  						status.append(Math.abs(armorModifier.getModifier(attacker))).append(" ");
  					}
  					status.append(armorModifier.getName());
  				}
  			}
  			if (armorModifierTotal != 0) {
  				status.append(" = ").append(rolledTotal + armorModifierTotal);
  			}
  			println(getIndent() + 1, status.toString());
  			if ((attacker != null) && usingClaws) {
  				print(getIndent() + 1, false, attacker);
  				println(getIndent() + 1, " uses Claws to reduce opponents armour to 7.");
  			}
  			if (report.isArmorBroken()) {
  				print(getIndent() + 1, "The armour of ");
  				print(getIndent() + 1, false, defender);
  				println(getIndent() + 1, " has been broken.");
  			} else {
  				print(getIndent() + 1, false, defender);
  				status = new StringBuilder();
  				status.append(" has been saved by ").append(defender.getPlayerGender().getGenitive()).append(" armour.");
  				println(getIndent() + 1, status.toString());
  			}
  		}

  		// report injury roll
  		if (report.isArmorBroken()) {
  			boolean thickSkullUsed = false;
  			boolean stuntyUsed = false;
  			status = new StringBuilder();
  			int[] injuryRoll = report.getInjuryRoll();
  			if (ArrayTool.isProvided(injuryRoll)) {
  				status.append("Injury Roll [ ").append(injuryRoll[0]).append(" ][ ").append(injuryRoll[1]).append(" ]");
  				println(getIndent(), TextStyle.ROLL, status.toString());
  				status = new StringBuilder();
  				if (defender instanceof ZappedPlayer) {
  					status.append(defender.getName()).append(" is badly hurt automatically because ")
  						.append(defender.getPlayerGender().getNominative()).append(" has been zapped.");
  					println(getIndent(), TextStyle.NONE, status.toString());
  				} else {
  					int rolledTotal = injuryRoll[0] + injuryRoll[1];
  					status.append("Rolled Total of ").append(rolledTotal);
  					int injuryModifierTotal = 0;
  					for (InjuryModifier injuryModifier : report.getInjuryModifiers()) {
  						int modifierValue = injuryModifier.getModifier(attacker, defender);
  						injuryModifierTotal += modifierValue;
  						if (modifierValue == 0) {
  							thickSkullUsed = injuryModifier.isRegisteredToSkillWithProperty(NamedProperties.convertKOToStunOn8);
  							stuntyUsed = injuryModifier.isRegisteredToSkillWithProperty(NamedProperties.isHurtMoreEasily);
  						} else if (injuryModifier.isNigglingInjuryModifier()) {
  							status.append(" +").append(injuryModifier.getName());
  						} else if (modifierValue > 0) {
  							status.append(" +").append(modifierValue).append(" ").append(injuryModifier.getName());
  						} else {
  							status.append(" ").append(modifierValue).append(" ").append(injuryModifier.getName());
  						}
  					}
  					if (injuryModifierTotal != 0) {
  						status.append(" = ").append(rolledTotal + injuryModifierTotal);
  					}
  					println(getIndent() + 1, status.toString());
  					if (stuntyUsed) {
  						print(getIndent() + 1, false, defender);
  						status = new StringBuilder();
  						status.append(" is Stunty and more easily hurt because of that.");
  						println(getIndent() + 1, status.toString());
  					}
  					if (thickSkullUsed) {
  						print(getIndent() + 1, false, defender);
  						status = new StringBuilder();
  						status.append("'s Thick Skull helps ").append(defender.getPlayerGender().getDative())
  							.append(" to stay on the pitch.");
  						println(getIndent() + 1, status.toString());
  					}
  					if (ArrayTool.isProvided(report.getCasualtyRoll())) {
  						print(getIndent() + 1, false, defender);
  						println(getIndent() + 1, " suffers a casualty.");
  						int[] casualtyRoll = report.getCasualtyRoll();
  						status = new StringBuilder();
  						status.append("Casualty Roll [ ").append(casualtyRoll[0]).append(" ][ ").append(casualtyRoll[1])
  							.append(" ]");
  						println(getIndent(), TextStyle.ROLL, status.toString());
  						reportInjury(defender, report.getInjury(), report.getSeriousInjury());
  						if (ArrayTool.isProvided(report.getCasualtyRollDecay())) {
  							print(getIndent() + 1, false, defender);
  							status = new StringBuilder();
  							status.append("'s body is decaying and ").append(defender.getPlayerGender().getNominative())
  								.append(" suffers a 2nd casualty.");
  							println(getIndent() + 1, status.toString());
  							status = new StringBuilder();
  							int[] casualtyRollDecay = report.getCasualtyRollDecay();
  							status.append("Casualty Roll [ ").append(casualtyRollDecay[0]).append(" ][ ").append(casualtyRollDecay[1])
  								.append(" ]");
  							println(getIndent(), TextStyle.ROLL, status.toString());
  							reportInjury(defender, report.getInjuryDecay(), report.getSeriousInjuryDecay());
  						}
  					} else {
  						reportInjury(defender, report.getInjury(), report.getSeriousInjury());
  					}
  				}
  			}
  		}
    }
    
  	private void reportInjury(Player<?> pDefender, PlayerState pInjury, SeriousInjury pSeriousInjury) {
  		StringBuilder status = new StringBuilder();
  		print(getIndent() + 1, false, pDefender);
  		status.append(" ").append(pInjury.getDescription()).append(".");
  		println(getIndent() + 1, status.toString());
  		if (pSeriousInjury != null) {
  			print(getIndent() + 1, false, pDefender);
  			status = new StringBuilder();
  			status.append(" ").append(pSeriousInjury.getDescription()).append(".");
  			println(getIndent() + 1, status.toString());
  		}
  	}
}
