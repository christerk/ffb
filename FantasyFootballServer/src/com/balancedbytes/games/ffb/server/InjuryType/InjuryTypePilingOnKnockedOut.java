package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.step.IStep;

public class InjuryTypePilingOnKnockedOut extends InjuryTypeServer {
		public InjuryTypePilingOnKnockedOut(IStep step) {
			super(step, "pilingOnKnockedOut", false, SendToBoxReason.KO_ON_PILING_ON);
		}

		@Override
		public boolean canUseApo() {
			return false;
		}

		@Override
		public boolean isCausedByOpponent() {
			return true;
		}


		@Override
		public InjuryContext handleInjury(Game game, Player<?> pAttacker, Player<?> pDefender,
				FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode) {

			if (!injuryContext.isArmorBroken()) {
				injuryContext.setArmorBroken(true);
			}

			if (injuryContext.isArmorBroken()) {
				injuryContext.setInjury(new PlayerState(PlayerState.KNOCKED_OUT));
			} else {
				injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
			}

			return injuryContext;
		}
	}