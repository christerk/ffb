package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.InjuryModificationOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Turns the candidates of a decision group into the options offered to the coach: every re-roll (and every
 * modification offered on broken armour) is a single option, modifiers are offered as minimal combinations reaching
 * the goal of the phase. Goals are only judged on visible dice: re-roll options are never judged by their result.
 */
public class InjuryModificationCombinationService {

	public static final int MAX_COMBINED_CANDIDATES = 5;

	private final InjuryNodeEvaluationService evaluationService;
	private final InjuryModificationOrderingService orderingService;

	public InjuryModificationCombinationService() {
		this(new InjuryNodeEvaluationService(), new InjuryModificationOrderingService());
	}

	public InjuryModificationCombinationService(InjuryNodeEvaluationService evaluationService,
		InjuryModificationOrderingService orderingService) {
		this.evaluationService = evaluationService;
		this.orderingService = orderingService;
	}

	public List<BuiltOption> options(InjuryChainContext context, InjuryContext template, InjuryNode node,
		DecisionGroup group, List<InjuryModificationCandidate> candidates) {
		Map<List<InjuryModificationCandidate>, InjuryNode> results = new LinkedHashMap<>();
		List<InjuryModificationCandidate> modifiers = new ArrayList<>();
		boolean brokenArmourPhase = group.getPhase() == InjuryPhase.ARMOUR && node.getContext().isArmorBroken();

		for (InjuryModificationCandidate candidate : unique(candidates)) {
			if (candidate.getModification().effect() == ModificationEffect.RE_ROLL) {
				List<InjuryModificationCandidate> single = singletonList(candidate);
				results.put(single, simulate(context, template, node, group, single));
			} else if (brokenArmourPhase) {
				List<InjuryModificationCandidate> single = singletonList(candidate);
				InjuryNode child = simulate(context, template, node, group, single);
				if (child.getContext().isArmorBroken()) {
					results.put(single, child);
				}
			} else {
				modifiers.add(candidate);
			}
		}

		if (!modifiers.isEmpty()) {
			List<InjuryModificationCandidate> combinable = modifiers.size() > MAX_COMBINED_CANDIDATES
				? new ArrayList<>(modifiers.subList(0, MAX_COMBINED_CANDIDATES)) : modifiers;
			results.putAll(minimalCombinations(context, template, node, group, combinable));
		}

		Comparator<List<InjuryModificationCandidate>> comparator = orderingService.combinationComparator();
		return results.entrySet().stream()
			.sorted((first, second) -> comparator.compare(first.getKey(), second.getKey()))
			.map(entry -> {
				InjuryModificationOption option = option(group, entry.getKey(), entry.getValue());
				entry.getValue().setOption(option);
				return new BuiltOption(option, entry.getValue());
			})
			.collect(Collectors.toList());
	}

	private Map<List<InjuryModificationCandidate>, InjuryNode> minimalCombinations(InjuryChainContext context,
		InjuryContext template, InjuryNode node, DecisionGroup group, List<InjuryModificationCandidate> modifiers) {
		Map<List<InjuryModificationCandidate>, InjuryNode> kept = new LinkedHashMap<>();
		Map<Integer, InjuryNode> keptByMask = new LinkedHashMap<>();
		int count = modifiers.size();
		List<Integer> masks = new ArrayList<>();
		for (int mask = 1; mask < (1 << count); mask++) {
			masks.add(mask);
		}
		masks.sort(Comparator.comparingInt(Integer::bitCount));

		for (int mask : masks) {
			List<InjuryModificationCandidate> subset = new ArrayList<>();
			for (int i = 0; i < count; i++) {
				if ((mask & (1 << i)) != 0) {
					subset.add(modifiers.get(i));
				}
			}
			InjuryNode child = simulate(context, template, node, group, subset);
			if (!reachesGoal(context, node, group, child)) {
				continue;
			}
			int score = score(context, node, group, child);
			boolean dominated = keptByMask.entrySet().stream()
				.anyMatch(entry -> (entry.getKey() & mask) == entry.getKey()
					&& score(context, node, group, entry.getValue()) >= score);
			if (!dominated) {
				keptByMask.put(mask, child);
				kept.put(subset, child);
			}
		}
		return kept;
	}

	public InjuryNode simulate(InjuryChainContext context, InjuryContext template, InjuryNode node, DecisionGroup group,
		List<InjuryModificationCandidate> candidates) {
		InjuryNode child = node.createChild(-1, group.getPhase());
		for (InjuryModificationCandidate candidate : candidates) {
			ChainableInjuryModification modification = candidate.getModification();
			if (modification.effect() == ModificationEffect.RE_ROLL) {
				if (group.getPhase() == InjuryPhase.ARMOUR) {
					child.setArmourReRolled(true);
				} else {
					child.setInjuryReRolled(true);
				}
			}
			modification.apply(context, child, group.getPhase(), candidate.getOwnerId());
			child.getAppliedModifications().add(new AppliedModification(candidate.getSkill(), candidate.getOwnerId(),
				group.getPhase(), modification.skillUse(group.getPhase())));
		}
		evaluationService.evaluate(context, template, child);
		return child;
	}

	private boolean reachesGoal(InjuryChainContext context, InjuryNode node, DecisionGroup group, InjuryNode child) {
		if (group.getPhase() == InjuryPhase.ARMOUR) {
			return !node.getContext().isArmorBroken() && child.getContext().isArmorBroken();
		}
		return score(context, node, group, child) > score(context, node, group, node);
	}

	/**
	 * Higher is better for the owners of the group.
	 */
	private int score(InjuryChainContext context, InjuryNode node, DecisionGroup group, InjuryNode result) {
		if (group.getPhase() == InjuryPhase.ARMOUR) {
			return result.getContext().isArmorBroken() ? 1 : 0;
		}
		int rank = injuryRank(result.getContext());
		return ownersAreDefenderTeam(context, node, group) ? -rank : rank;
	}

	private boolean ownersAreDefenderTeam(InjuryChainContext context, InjuryNode node, DecisionGroup group) {
		Player<?> defender = context.getDefender(node);
		return group.getOwnerIds().stream().map(id -> context.getGame().getPlayerById(id))
			.anyMatch(owner -> owner != null && defender != null && owner.getTeam() == defender.getTeam());
	}

	public int injuryRank(InjuryContext injuryContext) {
		PlayerState injury = injuryContext.getInjury();
		if (injury == null || !injuryContext.isArmorBroken()) {
			return 0;
		}
		switch (injury.getBase()) {
			case PlayerState.STUNNED:
				return 1;
			case PlayerState.KNOCKED_OUT:
				return 2;
			case PlayerState.BADLY_HURT:
			case PlayerState.SERIOUS_INJURY:
			case PlayerState.RIP:
				return 3;
			default:
				return 0;
		}
	}

	private InjuryModificationOption option(DecisionGroup group, List<InjuryModificationCandidate> candidates,
		InjuryNode child) {
		List<Skill> skills = candidates.stream().map(InjuryModificationCandidate::getSkill).collect(Collectors.toList());
		List<String> playerIds = candidates.stream().map(InjuryModificationCandidate::getOwnerId)
			.collect(Collectors.toList());
		return new InjuryModificationOption(skills, playerIds, description(group, candidates, child));
	}

	private String description(DecisionGroup group, List<InjuryModificationCandidate> candidates, InjuryNode child) {
		boolean reRoll = candidates.stream()
			.anyMatch(candidate -> candidate.getModification().effect() == ModificationEffect.RE_ROLL);
		if (group.getPhase() == InjuryPhase.ARMOUR) {
			if (reRoll) {
				return "re-roll armour";
			}
			return child.getContext().isArmorBroken() ? "armour broken" : "armour holds";
		}
		if (reRoll) {
			return "re-roll injury";
		}
		switch (injuryRank(child.getContext())) {
			case 1:
				return "stunned";
			case 2:
				return "knocked out";
			case 3:
				return "casualty";
			default:
				return "placed prone";
		}
	}

	private List<InjuryModificationCandidate> unique(List<InjuryModificationCandidate> candidates) {
		List<InjuryModificationCandidate> unique = new ArrayList<>();
		for (InjuryModificationCandidate candidate : candidates) {
			boolean duplicate = unique.stream().anyMatch(other -> other.getSkill() == candidate.getSkill()
				&& other.getOwnerId().equals(candidate.getOwnerId()));
			if (!duplicate) {
				unique.add(candidate);
			}
		}
		return unique;
	}

	private List<InjuryModificationCandidate> singletonList(InjuryModificationCandidate candidate) {
		List<InjuryModificationCandidate> list = new ArrayList<>();
		list.add(candidate);
		return list;
	}

	public static class BuiltOption {
		private final InjuryModificationOption option;
		private final InjuryNode child;

		public BuiltOption(InjuryModificationOption option, InjuryNode child) {
			this.option = option;
			this.child = child;
		}

		public InjuryModificationOption getOption() {
			return option;
		}

		public InjuryNode getChild() {
			return child;
		}
	}
}
