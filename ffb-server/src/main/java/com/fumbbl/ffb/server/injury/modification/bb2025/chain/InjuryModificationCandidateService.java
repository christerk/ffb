package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ISkillBehaviour;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Determines the coach asked in the current resolution state and the modifications that coach can use, sorted for
 * display.
 * <ul>
 *   <li>Armour phase, armour not broken: attacker.</li>
 *   <li>Armour phase, armour broken: defender, or the attacker for modifications that are
 *   {@link ChainableInjuryModification#offeredOnBrokenArmour()} if the defender has nothing to offer or declined.</li>
 *   <li>Injury phase: attacker, without an attacker the defender.</li>
 * </ul>
 * Injuries from a player's own chainsaw or vomit-like attack have no attacker, the defender takes the attacker's
 * role then.
 */
public class InjuryModificationCandidateService {

	private final InjuryModificationOrderingService orderingService;

	public InjuryModificationCandidateService() {
		this(new InjuryModificationOrderingService());
	}

	InjuryModificationCandidateService(InjuryModificationOrderingService orderingService) {
		this.orderingService = orderingService;
	}

	public List<InjuryModificationCandidate> candidates(GameState gameState, InjuryResolutionState state) {
		Game game = gameState.getGame();
		InjuryContext injuryContext = state.getInjuryContext();
		Player<?> attacker = attackingPlayer(game, injuryContext);
		Player<?> defender = game.getPlayerById(injuryContext.getDefenderId());

		switch (state.getPhase()) {
			case ARMOUR:
				if (!injuryContext.isArmorBroken()) {
					return candidatesFor(gameState, state, attacker, modification -> true);
				}
				List<InjuryModificationCandidate> defenderCandidates =
					candidatesFor(gameState, state, defender, modification -> true);
				if (!defenderCandidates.isEmpty()) {
					return defenderCandidates;
				}
				return candidatesFor(gameState, state, attacker, ChainableInjuryModification::offeredOnBrokenArmour);
			case INJURY:
				return candidatesFor(gameState, state, attacker != null ? attacker : defender, modification -> true);
			default:
				return Collections.emptyList();
		}
	}

	Player<?> attackingPlayer(Game game, InjuryContext injuryContext) {
		if (StringTool.isProvided(injuryContext.getAttackerId())) {
			return game.getPlayerById(injuryContext.getAttackerId());
		}
		InjuryType injuryType = injuryContext.getInjuryType();
		if (injuryType != null && (injuryType.isChainsaw() || injuryType.isVomitLike())) {
			return game.getPlayerById(injuryContext.getDefenderId());
		}
		return null;
	}

	private List<InjuryModificationCandidate> candidatesFor(GameState gameState, InjuryResolutionState state,
		Player<?> player, Predicate<ChainableInjuryModification> filter) {
		if (player == null || state.hasDeclined(player.getId())) {
			return Collections.emptyList();
		}
		String ownerCheckId = player.getId();
		return unusedModifications(player).stream()
			.filter(filter)
			.filter(modification -> ownerCheckId.equals(modification.ownerId(state.getInjuryContext())))
			.filter(modification -> modification.isApplicable(gameState, state))
			.map(modification -> new InjuryModificationCandidate(player, modification))
			.sorted(orderingService.comparator())
			.collect(Collectors.toList());
	}

	private List<ChainableInjuryModification> unusedModifications(Player<?> player) {
		List<ChainableInjuryModification> modifications = new ArrayList<>();
		for (Skill skill : player.getSkillsIncludingTemporaryOnes()) {
			ISkillBehaviour<? extends Skill> behaviour = skill.getSkillBehaviour();
			if (!player.isUsed(skill) && behaviour instanceof SkillBehaviour) {
				modifications.addAll(((SkillBehaviour<?>) behaviour).getChainableInjuryModifications());
			}
		}
		return modifications;
	}
}
