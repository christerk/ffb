package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.action.pass.StepBombardier;
import com.balancedbytes.games.ffb.server.step.action.pass.StepBombardier.StepState;
import com.balancedbytes.games.ffb.skill.Bombardier;

public class BombardierBehaviour extends SkillBehaviour<Bombardier> {
	public BombardierBehaviour() {
		super();

		registerModifier(new StepModifier<StepBombardier, StepBombardier.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepBombardier step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean handleExecuteStepHook(StepBombardier step, StepState state) {

				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				if (!game.getTurnMode().isBombTurn() && ((actingPlayer.getPlayerAction() == PlayerAction.THROW_BOMB)
						|| (actingPlayer.getPlayerAction() == PlayerAction.HAIL_MARY_BOMB))) {
					// mark skill used to set active=false when changing players
					actingPlayer.markSkillUsed(skill);
					if (game.getTeamHome().hasPlayer(actingPlayer.getPlayer())) {
						if (TurnMode.BLITZ == game.getTurnMode()) {
							game.setTurnMode(TurnMode.BOMB_HOME_BLITZ);
						} else {
							game.setTurnMode(TurnMode.BOMB_HOME);
						}
					} else {
						if (TurnMode.BLITZ == game.getTurnMode()) {
							game.setTurnMode(TurnMode.BOMB_AWAY_BLITZ);
						} else {
							game.setTurnMode(TurnMode.BOMB_AWAY);
						}
					}
				}

				return false;
			}

		});
	}
}
