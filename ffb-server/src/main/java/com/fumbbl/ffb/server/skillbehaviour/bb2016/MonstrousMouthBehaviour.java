package com.fumbbl.ffb.server.skillbehaviour.bb2016;

import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.bb2016.StepCatchScatterThrowIn;
import com.fumbbl.ffb.server.step.bb2016.StepCatchScatterThrowIn.StepState;
import com.fumbbl.ffb.skill.bb2016.MonstrousMouth;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.BB2016)
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
