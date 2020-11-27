package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.game.ffb.injury.Stab;
import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.ArmorModifier;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.model.ServerSkill;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.util.UtilCards;

public class InjuryTypeStab extends InjuryTypeServer<Stab>  {
		public InjuryTypeStab() {
			super(new Stab());
		}

		@Override
		public InjuryContext handleInjury(IStep step, Game game,GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender,
				FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode) {
			
			DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();
					
			if (!injuryContext.isArmorBroken()) {
				Team otherTeam = game.getTeamHome().hasPlayer(pDefender) ? game.getTeamHome() : game.getTeamAway();
				if ((pAttacker != null) && UtilCards.hasSkill(game, pAttacker, ServerSkill.STAKES)
						&& (otherTeam.getRoster().isUndead()
								|| ((pDefender != null) && pDefender.getPosition().isUndead()))) {
					injuryContext.addArmorModifier(ArmorModifier.STAKES);
				}
				injuryContext.setArmorRoll(diceRoller.rollArmour());
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			}

			if (injuryContext.isArmorBroken()) {
				injuryContext.setInjuryRoll(diceRoller.rollInjury());

				setInjury(pDefender, gameState, diceRoller);
			} else {
				injuryContext.setInjury(null);
			}

			return injuryContext;
		}
	}