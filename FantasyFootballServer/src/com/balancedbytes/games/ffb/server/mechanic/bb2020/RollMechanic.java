package com.balancedbytes.games.ffb.server.mechanic.bb2020;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.bb2020.SeriousInjury;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.ZappedPlayer;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.bb2020.CasualtyModifier;
import com.balancedbytes.games.ffb.modifiers.bb2020.CasualtyModifierFactory;
import com.balancedbytes.games.ffb.server.DiceRoller;

import java.util.Arrays;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2020)
public class RollMechanic extends com.balancedbytes.games.ffb.server.mechanic.RollMechanic {
	@Override
	public int[] rollCasualty(DiceRoller diceRoller) {
		return new int[] { diceRoller.rollDice(16), diceRoller.rollDice(6) };
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
				}  else if ((total == 9) && isStunty) {
					playerState = new PlayerState(PlayerState.BADLY_HURT);
				} else if (total > 9) {
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
	public PlayerState interpretCasualtyRollAndAddModifiers(Game game, InjuryContext injuryContext, Player<?> player) {
		if (player instanceof ZappedPlayer) {
			return new PlayerState(PlayerState.BADLY_HURT);
		}
		int[] roll = injuryContext.getCasualtyRoll();
		CasualtyModifierFactory factory = game.getFactory(FactoryType.Factory.CASUALTY_MODIFIER);
		Set<CasualtyModifier> casualtyModifiers = factory.findModifiers(player);
		injuryContext.setCasualtyModifiers(casualtyModifiers);
		int modifierSum = casualtyModifiers.stream().mapToInt(CasualtyModifier::getModifier).sum();
		return new PlayerState(mapCasualtyRoll(roll[0] + modifierSum));
	}

	@Override
	public SeriousInjury interpretSeriousInjuryRoll(InjuryContext injuryContext, boolean useDecay) {
		return interpretSeriousInjuryRoll(injuryContext);
	}

	@Override
	public SeriousInjury interpretSeriousInjuryRoll(InjuryContext injuryContext) {
		int casModifier = injuryContext.casualtyModifiers.stream().mapToInt(CasualtyModifier::getModifier).sum();
		return interpretSeriousInjuryRoll(injuryContext.getCasualtyRoll()[0] + casModifier, injuryContext.getCasualtyRoll()[1]);
	}

	@Override
	public SeriousInjury interpretSeriousInjuryRoll(int[] roll) {
		return interpretSeriousInjuryRoll(roll[0], roll[1]);
	}

	private SeriousInjury interpretSeriousInjuryRoll(int casRoll, int siRoll) {
		if (isSI(casRoll)) {
			return mapSIRoll(siRoll);
		}

		if (casRoll >= 10 && casRoll <= 12) {
			return SeriousInjury.SERIOUS_INJURY;
		}

		if (casRoll >= 7 && casRoll <= 9) {
			return SeriousInjury.SERIOUSLY_HURT;
		}

		return null;
	}

	private SeriousInjury mapSIRoll(int roll) {
		switch (roll) {
			case 6:
				return SeriousInjury.DISLOCATED_SHOULDER;
			case 5:
				return SeriousInjury.NECK_INJURY;
			case 4:
				return SeriousInjury.BROKEN_ARM;
			case 3:
				return SeriousInjury.SMASHED_KNEE;
			default:
				return SeriousInjury.HEAD_INJURY;
		}
	}

	private int mapCasualtyRoll(int roll) {
		if (roll >= 15) {
			return PlayerState.RIP;
		}
		if (roll >= 7) {
			return PlayerState.SERIOUS_INJURY;
		}

		return PlayerState.BADLY_HURT;
	}

	private boolean isSI(int roll) {
		return roll == 13 || roll == 14;
	}
}
