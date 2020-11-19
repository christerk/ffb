package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryModifierFactory;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.ZappedPlayer;
import com.balancedbytes.games.ffb.server.step.IStep;

public class InjuryTypeBitten extends InjuryTypeServer {
		public InjuryTypeBitten(IStep step) {
			super(step, "bitten", false, SendToBoxReason.BITTEN);
		}

		@Override
		public InjuryContext handleInjury(Game game, Player<?> pAttacker, Player<?> pDefender,
				FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode) {

			if (!injuryContext.isArmorBroken()) {
				injuryContext.setArmorBroken(true);
			}

			if (injuryContext.isArmorBroken()) {
				injuryContext.setInjuryRoll(diceRoller.rollInjury());
				injuryContext.addInjuryModifier(new InjuryModifierFactory().getNigglingInjuryModifier(pDefender));

				injuryContext.setInjury(
						interpretInjury(diceInterpreter, gameState, injuryContext, pDefender instanceof ZappedPlayer));

				if (injuryContext.getPlayerState() == null) {
					injuryContext.setInjury(new PlayerState(PlayerState.BADLY_HURT));
				}
			} else {
				injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
			}

			return injuryContext;
		}
	}