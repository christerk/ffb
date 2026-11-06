package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ISkillBehaviour;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.model.SkillBehaviour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Collects the modifications the coach of a decision group can apply on a node (rule 3 owner, rule 2 re-roll limit,
 * not yet applied on the path, unused, applicable) in rule 4 order.
 */
public class InjuryModificationCandidateService {

	private final InjuryModificationOrderingService orderingService;

	public InjuryModificationCandidateService() {
		this(new InjuryModificationOrderingService());
	}

	public InjuryModificationCandidateService(InjuryModificationOrderingService orderingService) {
		this.orderingService = orderingService;
	}

	public List<InjuryModificationCandidate> candidates(InjuryChainContext context, InjuryNode node, DecisionGroup group,
		EligibilityMode mode) {
		Game game = context.getGame();
		List<InjuryModificationCandidate> candidates = new ArrayList<>();
		for (String ownerId : group.getOwnerIds()) {
			Player<?> owner = game.getPlayerById(ownerId);
			if (owner == null) {
				continue;
			}
			for (Skill skill : owner.getSkillsIncludingTemporaryOnes()) {
				findEligible(context, node, group, skill, ownerId, mode)
					.ifPresent(modification -> candidates.add(
						new InjuryModificationCandidate(modification, ownerId, group.getPhase())));
			}
		}
		candidates.sort(orderingService.candidateComparator());
		return candidates;
	}

	/**
	 * The first chain modification of the skill eligible for the owner in the group, if any.
	 */
	public Optional<ChainableInjuryModification> findEligible(InjuryChainContext context, InjuryNode node,
		DecisionGroup group, Skill skill, String ownerId, EligibilityMode mode) {
		Player<?> owner = context.getGame().getPlayerById(ownerId);
		if (owner == null || skill == null || !group.getOwnerIds().contains(ownerId) || !owner.has(skill)
			|| owner.isUsed(skill) || node.isApplied(skill, ownerId)) {
			return Optional.empty();
		}
		return modifications(skill).stream()
			.filter(modification -> isEligible(context, node, group, modification, ownerId, mode))
			.findFirst();
	}

	private boolean isEligible(InjuryChainContext context, InjuryNode node, DecisionGroup group,
		ChainableInjuryModification modification, String ownerId, EligibilityMode mode) {
		InjuryPhase phase = group.getPhase();
		if (modification.ownerSide() != group.getSide() || !modification.phases().contains(phase)
			|| !modification.isValidType(node.getContext().fInjuryType)) {
			return false;
		}
		if (modification.effect() == ModificationEffect.RE_ROLL && node.isReRolled(phase)) {
			return false;
		}
		if (phase == InjuryPhase.ARMOUR && node.getContext().isArmorBroken() && group.getSide() == OwnerSide.ATTACKER
			&& !modification.offeredOnBrokenArmour()) {
			return false;
		}
		return modification.isApplicable(context, node, phase, ownerId, mode);
	}

	public List<ChainableInjuryModification> modifications(Skill skill) {
		ISkillBehaviour behaviour = skill.getSkillBehaviour();
		if (behaviour instanceof SkillBehaviour) {
			return ((SkillBehaviour<?>) behaviour).getChainableInjuryModifications();
		}
		return Collections.emptyList();
	}
}
