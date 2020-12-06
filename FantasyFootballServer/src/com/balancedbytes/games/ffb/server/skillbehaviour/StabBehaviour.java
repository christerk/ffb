package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeStab;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.block.StepStab;
import com.balancedbytes.games.ffb.server.step.action.block.StepStab.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;
import com.balancedbytes.games.ffb.skill.Stab;
import com.balancedbytes.games.ffb.util.UtilCards;

public class StabBehaviour extends SkillBehaviour<Stab> {
	public StabBehaviour() {
		super();

		registerModifier(new StepModifier<StepStab, StepStab.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepStab step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepStab step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				if (UtilCards.hasSkill(game, actingPlayer, skill) && (state.usingStab != null) && state.usingStab) {
					step.getResult().setSound(SoundId.STAB);
					FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
					InjuryResult injuryResultDefender = UtilServerInjury.handleInjury(step, new InjuryTypeStab(),
							actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, ApothecaryMode.DEFENDER);
					if (injuryResultDefender.injuryContext().isArmorBroken()) {
						step.publishParameters(UtilServerInjury.dropPlayer(step, game.getDefender(), ApothecaryMode.DEFENDER));
					}
					step.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultDefender));
					step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
				} else {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}

				return false;
			}

		});
	}
}
