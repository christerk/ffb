package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.ZappedPlayer;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.IRegistrationAwareModifier;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.modifiers.bb2020.CasualtyModifier;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportInjury;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ReportMessageType(ReportId.INJURY)
@RulesCollection(Rules.BB2020)
public class InjuryMessage extends ReportMessageBase<ReportInjury> {

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
			Optional<Skill> cancelingClaws = Arrays.stream(report.getArmorModifiers())
				.map(IRegistrationAwareModifier::getRegisteredTo)
				.filter(Objects::nonNull)
				.filter(skill -> skill.canCancel(NamedProperties.reducesArmourToFixedValue)).findFirst();

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
				println(getIndent() + 1, " uses Claws to reduce opponent's armour to 8+.");
			}
			if (defender != null) {
				if (cancelingClaws.isPresent()) {
					print(getIndent() + 1, false, defender);
					println(getIndent() + 1, " uses " + cancelingClaws.get().getName() + " to cancel opponent's Claws.");
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
				int rolledTotal = injuryRoll[0] + injuryRoll[1];
				status.append("Rolled Total of ").append(rolledTotal);
				int injuryModifierTotal = 0;
				for (InjuryModifier injuryModifier : report.getInjuryModifiers()) {
					int modifierValue = injuryModifier.getModifier(attacker, defender);
					injuryModifierTotal += modifierValue;
					if (modifierValue == 0) {
						thickSkullUsed = injuryModifier.isRegisteredToSkillWithProperty(NamedProperties.convertKOToStunOn8);
						stuntyUsed = injuryModifier.isRegisteredToSkillWithProperty(NamedProperties.isHurtMoreEasily);
					} else if (modifierValue > 0) {
						status.append(" + ").append(modifierValue).append(" ").append(injuryModifier.getName());
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
					if (defender instanceof ZappedPlayer) {
						status.append(defender.getName()).append(" is badly hurt automatically because ")
							.append(defender.getPlayerGender().getNominative()).append(" has been zapped.");
						println(getIndent(), TextStyle.NONE, status.toString());
					} else {
						int[] casualtyRoll = report.getCasualtyRoll();
						status = new StringBuilder();
						status.append("Casualty Roll [ ").append(casualtyRoll[0]).append(" ]");
						println(getIndent(), TextStyle.ROLL, status.toString());
						if (!report.getCasualtyModifiers().isEmpty()) {
							int modifiers = 0;
							status = new StringBuilder("Rolled ").append(casualtyRoll[0]);
							List<String> reportStrings = new ArrayList<>();
							for (CasualtyModifier modifier : report.getCasualtyModifiers()) {
								reportStrings.add(modifier.reportString());
								modifiers += modifier.getModifier();
							}
							reportStrings.sort(Comparator.naturalOrder());
							for (String reportString : reportStrings) {
								status.append(" + ");
								status.append(reportString);
							}
							status.append(" = ").append(casualtyRoll[0] + modifiers);
							println(getIndent() + 1, TextStyle.NONE, status.toString());
						}
						reportInjury(defender, report.getInjury(), report.getSeriousInjury(), casualtyRoll[1], report.getOriginalInjury());
					}
				} else {
					reportInjury(defender, report.getInjury(), null, 0, null);
				}
			}
		}
	}

	private void reportInjury(Player<?> pDefender, PlayerState pInjury, SeriousInjury pSeriousInjury, int siRoll, SeriousInjury originalInury) {
		StringBuilder status = new StringBuilder();
		print(getIndent() + 1, false, pDefender);
		status.append(" ").append(pInjury.getDescription()).append(".");
		println(getIndent() + 1, status.toString());
		if (pSeriousInjury != null) {
			if (pSeriousInjury.showSiRoll()) {
				status = new StringBuilder();
				status.append("Lasting Injury Roll [ ").append(siRoll).append(" ]");
				println(getIndent(), TextStyle.ROLL, status.toString());
			}
			if (originalInury != null) {
				status = new StringBuilder().append(pDefender.getName()).append(" would have ")
					.append(originalInury.getDescription())
					.append(" but that stat cannot be reduced any further. So a different injury has been chosen randomly.");
				println(getIndent() + 1, TextStyle.EXPLANATION, status.toString());
			}
			print(getIndent() + 1, false, pDefender);
			status = new StringBuilder();
			status.append(" ").append(pSeriousInjury.getDescription()).append(".");
			println(getIndent() + 1, status.toString());
		}
	}
}
