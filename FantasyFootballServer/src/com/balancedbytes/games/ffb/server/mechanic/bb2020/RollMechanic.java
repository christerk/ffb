package com.balancedbytes.games.ffb.server.mechanic.bb2020;

import com.balancedbytes.games.ffb.InjuryAttribute;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.server.DiceRoller;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2020)
public class RollMechanic extends com.balancedbytes.games.ffb.server.mechanic.RollMechanic {
	@Override
	public int[] rollCasualty(DiceRoller diceRoller) {
		return new int[] { diceRoller.rollDice(16) };
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
	public PlayerState interpretCasualtyRoll(int[] roll, Player<?> player) {
		int nigglings = (int) Arrays.stream(player.getLastingInjuries()).filter(injury -> injury.getInjuryAttribute() == InjuryAttribute.NI).count();
		return null;
	}

	@Override
	public SeriousInjury interpretSeriousInjuryRoll(int[] roll) {
		return null;
	}
}
