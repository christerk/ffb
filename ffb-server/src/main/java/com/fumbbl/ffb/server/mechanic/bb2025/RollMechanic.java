package com.fumbbl.ffb.server.mechanic.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.bb2025.SeriousInjury;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.ZappedPlayer;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.bb2020.CasualtyModifier;
import com.fumbbl.ffb.factory.mixed.CasualtyModifierFactory;
import com.fumbbl.ffb.server.DiceRoller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2025)
public class RollMechanic extends com.fumbbl.ffb.server.mechanic.RollMechanic {

	private final Map<InjuryAttribute, Integer> reductionThresholds = new HashMap<InjuryAttribute, Integer>() {{
		put(InjuryAttribute.MA, 1);
		put(InjuryAttribute.ST, 1);
		put(InjuryAttribute.AG, 6);
		put(InjuryAttribute.PA, 6);
		put(InjuryAttribute.AV, 3);
	}};

	private final List<SeriousInjury> orderedInjuries = new ArrayList<SeriousInjury>() {{
		add(SeriousInjury.HEAD_INJURY);
		add(SeriousInjury.HEAD_INJURY);
		add(SeriousInjury.SMASHED_KNEE);
		add(SeriousInjury.BROKEN_ARM);
		add(SeriousInjury.DISLOCATED_HIP);
		add(SeriousInjury.DISLOCATED_SHOULDER);
	}};

	@Override
	public int[] rollCasualty(DiceRoller diceRoller) {
		return new int[]{diceRoller.rollDice(16), diceRoller.rollDice(6)};
	}

	@Override
	public PlayerState interpretInjuryRoll(Game game, InjuryContext pInjuryContext) {
		PlayerState playerState = null;
		if ((game != null) && (pInjuryContext != null)) {
			int[] injuryRoll = pInjuryContext.getInjuryRoll();
			Player<?> defender = game.getPlayerById(pInjuryContext.getDefenderId());
			if ((defender != null) && defender.hasSkillProperty(NamedProperties.preventDamagingInjuryModifications)) {
				pInjuryContext.clearInjuryModifiers();
			}
			if (injuryRoll == null) {
				// This is a forced injury, for example triggered by the player being eaten
				// We expect an injury being available in the injury context
				playerState = pInjuryContext.getInjury();
			} else {
				boolean isStunty = Arrays.stream(pInjuryContext.getInjuryModifiers()).anyMatch(injuryModifier -> injuryModifier.isRegisteredToSkillWithProperty(NamedProperties.isHurtMoreEasily));
				int total = injuryRoll[0] + injuryRoll[1] + pInjuryContext.getInjuryModifierTotal(game);
				boolean hasThickSkull = defender != null && defender.hasSkillProperty(NamedProperties.convertKOToStunOn8);

				if (total == 7 && isStunty) {
					if (hasThickSkull) {
						playerState = new PlayerState(PlayerState.STUNNED);
						defender.getSkillWithProperty(NamedProperties.convertKOToStunOn8).getInjuryModifiers()
							.forEach(pInjuryContext::addInjuryModifier);
					} else {
						playerState = new PlayerState(PlayerState.KNOCKED_OUT);
					}
				} else if ((total == 8) && hasThickSkull && !isStunty) {
					playerState = new PlayerState(PlayerState.STUNNED);
					defender.getSkillWithProperty(NamedProperties.convertKOToStunOn8).getInjuryModifiers()
						.forEach(pInjuryContext::addInjuryModifier);
				} else if ((total == 9) && isStunty) {
					playerState = new PlayerState(PlayerState.BADLY_HURT);
				} else if (total > 9) {
					//noinspection DataFlowIssue
					playerState = null;
				} else if (total > 7) {
					playerState = new PlayerState(PlayerState.KNOCKED_OUT);
				} else {
					playerState = new PlayerState(PlayerState.STUNNED);
				}
			}
		}
		return playerState;
	}

	@Override
	public PlayerState interpretCasualtyRollAndAddModifiers(Game game, InjuryContext injuryContext, Player<?> player, boolean useDecayRoll) {
		if (player instanceof ZappedPlayer) {
			return new PlayerState(PlayerState.BADLY_HURT);
		}
		int[] roll = injuryContext.getCasualtyRoll();
		CasualtyModifierFactory factory = game.getFactory(FactoryType.Factory.CASUALTY_MODIFIER);
		Set<CasualtyModifier> casualtyModifiers = factory.findModifiers(player);
		injuryContext.addCasualtyModifiers(casualtyModifiers);
		int modifierSum = casualtyModifiers.stream().mapToInt(CasualtyModifier::getModifier).sum();
		return new PlayerState(mapCasualtyRoll(roll[0] + modifierSum));
	}

	@Override
	public SeriousInjury interpretSeriousInjuryRoll(Game game, InjuryContext injuryContext, boolean useDecay) {
		return interpretSeriousInjuryRoll(game, injuryContext);
	}

	@Override
	public SeriousInjury interpretSeriousInjuryRoll(Game game, InjuryContext injuryContext) {
		int casModifier = injuryContext.casualtyModifiers.stream().mapToInt(CasualtyModifier::getModifier).sum();
		return interpretSeriousInjuryRoll(game, injuryContext, injuryContext.getCasualtyRoll()[0] + casModifier, injuryContext.getCasualtyRoll()[1]);
	}

	@Override
	public SeriousInjury interpretSeriousInjuryRoll(Game game, InjuryContext injuryContext, int[] roll) {
		return interpretSeriousInjuryRoll(game, injuryContext, roll[0], roll[1]);
	}

	@Override
	public int multiBlockAttackerModifier() {
		return -2;
	}

	@Override
	public int multiBlockDefenderModifier() {
		return 0;
	}

	private SeriousInjury interpretSeriousInjuryRoll(Game game, InjuryContext injuryContext, int casRoll, int siRoll) {
		if (isSI(casRoll)) {
			return mapSIRoll(game, injuryContext, siRoll);
		}

		if (casRoll >= 11 && casRoll <= 12) {
			return SeriousInjury.SERIOUS_INJURY;
		}

		if (casRoll >= 9 && casRoll <= 10) {
			return SeriousInjury.SERIOUSLY_HURT;
		}

		return null;
	}

	private SeriousInjury mapSIRoll(Game game, InjuryContext injuryContext, int roll) {
		Player<?> defender = game.getPlayerById(injuryContext.getDefenderId());
		List<SeriousInjury> injuriesWithReduceableStats = orderedInjuries.stream().filter(
			injury -> {
				InjuryAttribute attribute = injury.getInjuryAttribute();
				return canBeReduced(attribute, currentValue(attribute, defender));
			}).collect(Collectors.toList());

		SeriousInjury originalInjury = mapSIRoll(roll);

		if (injuriesWithReduceableStats.isEmpty() || injuriesWithReduceableStats.contains(originalInjury)) {
			return originalInjury;
		}

		injuryContext.setOriginalSeriousInjury(originalInjury);

		return  SeriousInjury.SERIOUSLY_HURT;
	}

	/**
	 * @return current stat value WITHOUT temporary modifiers
	 */
	private int currentValue(InjuryAttribute attribute, Player<?> player) {
		switch (attribute) {
			case MA:
				return player.getMovement();
			case ST:
				return player.getStrength();
			case AG:
				return player.getAgility();
			case PA:
				return player.getPassing();
			case AV:
				return player.getArmour();
			default:
				return 0;
		}
	}

	private boolean canBeReduced(InjuryAttribute attribute, int currentValue) {
		return currentValue > 0 && reductionThresholds.get(attribute) != currentValue;
	}

	private SeriousInjury mapSIRoll(int roll) {
		return orderedInjuries.get(roll - 1);
	}

	private int mapCasualtyRoll(int roll) {
		if (roll >= 15) {
			return PlayerState.RIP;
		}
		if (roll >= 9) {
			return PlayerState.SERIOUS_INJURY;
		}

		return PlayerState.BADLY_HURT;
	}

	private boolean isSI(int roll) {
		return roll == 13 || roll == 14;
	}

	@Override
	public int minimumLonerRoll(Player<?> player) {
		return player.getSkillIntValue(NamedProperties.hasToRollToUseTeamReroll);
	}

	@Override
	public int minimumProRoll() {
		return 3;
	}

}
