package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeStab;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.action.block.StepStab;
import com.fumbbl.ffb.server.step.action.block.StepStab.StepState;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.skill.bb2020.Stab;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.BB2020)
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
				if (UtilCards.hasSkill(actingPlayer, skill) && (state.usingStab != null) && state.usingStab) {
					step.getResult().setSound(SoundId.STAB);
					FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
					InjuryResult injuryResultDefender = UtilServerInjury.handleInjury(step, new InjuryTypeStab(),
							actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, ApothecaryMode.DEFENDER);
					if (injuryResultDefender.injuryContext().isArmorBroken()) {
						step.publishParameters(UtilServerInjury.dropPlayer(step, game.getDefender(), ApothecaryMode.DEFENDER));
					}
					step.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultDefender));
					step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnSuccess);
				} else {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}

				return false;
			}

		});
	}
}
