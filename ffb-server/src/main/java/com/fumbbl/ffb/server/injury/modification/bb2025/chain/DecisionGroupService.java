package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rule 3: which coach is asked in which order on a node.
 * <ul>
 * <li>armour holds: attacker (armour)</li>
 * <li>armour broken: defender (armour), attacker (armour, only modifications offered on broken armour), injury</li>
 * <li>injury node: injury</li>
 * </ul>
 * The injury group is asked to the attacker, without attacker to the defender.
 */
public class DecisionGroupService {

	public List<DecisionGroup> groups(InjuryTree tree, InjuryNode node) {
		List<DecisionGroup> groups = new ArrayList<>();
		List<String> attackerIds = tree.getAttackerRoleIds();
		String defenderId = node.getContext().fDefenderId;
		boolean broken = node.getContext().isArmorBroken();
		if (node.getPhase() == InjuryPhase.ARMOUR) {
			if (broken) {
				groups.add(new DecisionGroup(InjuryPhase.ARMOUR, OwnerSide.DEFENDER, Collections.singletonList(defenderId)));
			}
			if (!attackerIds.isEmpty()) {
				groups.add(new DecisionGroup(InjuryPhase.ARMOUR, OwnerSide.ATTACKER, attackerIds));
			}
		}
		if (broken) {
			if (!attackerIds.isEmpty()) {
				groups.add(new DecisionGroup(InjuryPhase.INJURY, OwnerSide.ATTACKER, attackerIds));
			} else {
				groups.add(new DecisionGroup(InjuryPhase.INJURY, OwnerSide.DEFENDER, Collections.singletonList(defenderId)));
			}
		}
		return groups;
	}
}
