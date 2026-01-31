package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2025.block.StepPushback;
import com.fumbbl.ffb.skill.bb2025.MonstrousMouth;

@RulesCollection(Rules.BB2025)
public class MonstrousMouthBehaviour extends SkillBehaviour<MonstrousMouth> {
	public MonstrousMouthBehaviour() {
		super();

		registerModifier(new StepModifier<StepPushback, StepPushback.StepState>(1) {
			@Override
			public StepCommandStatus handleCommandHook(StepPushback step, StepPushback.StepState state, ClientCommandUseSkill useSkillCommand) {
				return null;
			}

			@Override
			public boolean handleExecuteStepHook(StepPushback step, StepPushback.StepState state) {
				Game game = step.getGameState().getGame();
				// use defender as oldDefenderState always refers to the first player in a chainpush
				PlayerState playerState = game.getFieldModel().getPlayerState(state.defender);

				if (playerState.isChomped()) {
					state.doPush = true;
					state.pushbackStack.clear();
					step.publishParameter(new StepParameter(StepParameterKey.STARTING_PUSHBACK_SQUARE, null));
					step.publishParameter(new StepParameter(StepParameterKey.FOLLOWUP_CHOICE, false));
					return true;
				}

				return false;
			}
		});
	}
}
