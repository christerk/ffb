package com.fumbbl.ffb.server.mechanic.bb2025;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.bb2025.SeriousInjury;
import com.fumbbl.ffb.dialog.DialogReRollPropertiesParameter;
import com.fumbbl.ffb.factory.mixed.CasualtyModifierFactory;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.bb2020.CasualtyModifier;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.UtilCards;

import java.util.*;

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
		SeriousInjury originalInjury = mapSIRoll(roll);
		InjuryAttribute attribute = originalInjury.getInjuryAttribute();

		if (canBeReduced(attribute, currentValue(attribute, defender))) {
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

	@Override
	public boolean askForReRollIfAvailable(GameState gameState, Player<?> player, ReRolledAction reRolledAction,
	                                              int minimumRoll, boolean fumble, Skill modificationSkill,
	                                              Skill reRollSkill,
	                                              CommonProperty menuProperty, String defaultValueKey,
	                                              List<String> messages) {
		boolean dialogShown = false;
		Game game = gameState.getGame();
		if (minimumRoll >= 0) {
			boolean mascotOption = isMascotAvailable(game);
			boolean teamReRollOption = isTeamReRollAvailable(gameState, player);
			boolean singleUseReRollOption = isSingleUseReRollAvailable(gameState, player);
			boolean proOption = isProReRollAvailable(player, game, gameState.getPassState());
			if (reRollSkill == null) {
				Optional<Skill> reRollOnce =
						UtilCards.getUnusedSkillWithProperty(player, NamedProperties.canRerollSingleDieOncePerPeriod);
				if (reRollOnce.isPresent()) {
					reRollSkill = reRollOnce.get();
				}
			}

			dialogShown =
					(mascotOption || teamReRollOption || proOption || singleUseReRollOption || reRollSkill != null ||
							modificationSkill != null);
			if (dialogShown) {
				Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
				String playerId = player.getId();
				UtilServerDialog.showDialog(gameState,
						new DialogReRollPropertiesParameter(playerId, reRolledAction, minimumRoll, teamReRollOption, proOption,
								fumble, mascotOption, reRollSkill, singleUseReRollOption ? ReRollSources.LORD_OF_CHAOS : null,
								modificationSkill, menuProperty, defaultValueKey, messages),
						!actingTeam.hasPlayer(player));
			}
		}
		return dialogShown;
	}

	private boolean isMascotAvailable(Game game) {
		InducementSet inducementSet = game.isHomePlaying() ?
				game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();

		return Arrays.stream(inducementSet.getInducements())
				.anyMatch(ind -> ind.getType().hasUsage(Usage.CONDITIONAL_REROLL) && ind.getUsesLeft() > 0);

	}
}
