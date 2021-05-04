package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.action.pass.StepBombardier;
import com.fumbbl.ffb.server.step.action.pass.StepBombardier.StepState;
import com.fumbbl.ffb.skill.bb2020.Bombardier;

@RulesCollection(Rules.BB2020)
public class BombardierBehaviour extends SkillBehaviour<Bombardier> {
	public BombardierBehaviour() {
		super();

		registerModifier(new StepModifier<StepBombardier, StepState>() {

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
