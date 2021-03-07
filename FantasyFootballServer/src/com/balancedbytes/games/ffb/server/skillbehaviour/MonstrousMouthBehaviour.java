package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.bb2016.StepCatchScatterThrowIn;
import com.balancedbytes.games.ffb.server.step.bb2016.StepCatchScatterThrowIn.StepState;
import com.balancedbytes.games.ffb.skill.MonstrousMouth;
import com.balancedbytes.games.ffb.util.UtilCards;

@RulesCollection(Rules.COMMON)
public class MonstrousMouthBehaviour extends SkillBehaviour<MonstrousMouth> {
	public MonstrousMouthBehaviour() {
		super();

		registerModifier(new StepModifier<StepCatchScatterThrowIn, StepCatchScatterThrowIn.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepCatchScatterThrowIn step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return null;
			}

			@Override
			public boolean handleExecuteStepHook(StepCatchScatterThrowIn step, StepState state) {
				Game game = step.getGameState().getGame();
				if (UtilCards.hasSkill(state.catcher, skill)) {
					step.setReRolledAction(ReRolledActions.CATCH);
					step.setReRollSource(skill.getRerollSource(ReRolledActions.CATCH));
					state.rerollCatch = true;
					
					return true;
				}
				
				return false;
			}

		});
	}
}
