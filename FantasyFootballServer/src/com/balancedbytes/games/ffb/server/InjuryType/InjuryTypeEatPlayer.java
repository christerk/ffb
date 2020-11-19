package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.step.IStep;

public class InjuryTypeEatPlayer extends InjuryTypeServer {
		public InjuryTypeEatPlayer(IStep step) {
			super(step, "eatPlayer", false, SendToBoxReason.EATEN);
		}

		@Override
		public boolean canUseApo() {
			return false;
		}


		@Override
		public InjuryContext handleInjury(Game game, Player<?> pAttacker, Player<?> pDefender,
				FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode) {

			if (!injuryContext.isArmorBroken()) {
				injuryContext.setArmorBroken(true);
			}

			if (injuryContext.isArmorBroken()) {
				injuryContext.setInjury(new PlayerState(PlayerState.RIP));
				setInjury(pDefender);
			} else {
				injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
			}

			return injuryContext;
		}
	}