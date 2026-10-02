package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.factory.JumpUpModifierFactory;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.modifiers.JumpUpContext;
import com.fumbbl.ffb.modifiers.JumpUpModifier;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportJumpUpRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.bb2025.action.select.StepJumpUp;
import com.fumbbl.ffb.server.step.bb2025.action.select.StepJumpUp.StepState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.bb2025.JumpUpModifierSelectionService;
import com.fumbbl.ffb.skill.common.JumpUp;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2025)
public class JumpUpBehaviour extends SkillBehaviour<JumpUp> {
	public JumpUpBehaviour() {
		registerModifier(new StepModifier<StepJumpUp, StepState>() {
			@Override
			public StepCommandStatus handleCommandHook(StepJumpUp step, StepState state, ClientCommandUseSkill command) {
				return StepCommandStatus.UNHANDLED_COMMAND;
			}

			@Override
			public boolean handleExecuteStepHook(StepJumpUp step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				if (state.roll > 0 || (actingPlayer.isStandingUp() && !actingPlayer.hasMoved()
					&& UtilCards.hasUnusedSkill(actingPlayer, skill))) {
					game.setConcessionPossible(false);
					if (actingPlayer.getPlayerAction().isBlockOrSpecialAction()
						|| actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK) {
						JumpUpModifierFactory factory = game.getFactory(Factory.JUMP_UP_MODIFIER);
						Set<JumpUpModifier> modifiers = factory.findModifiers(
							new JumpUpContext(actingPlayer, game, state.selectedModifierSkills));
						AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC)
							.forName(Mechanic.Type.AGILITY.name());
						int minimumRoll = mechanic.minimumRollJumpUp(actingPlayer.getPlayer(), modifiers);
						boolean doRoll = state.roll == 0;
						if (doRoll) {
							state.roll = step.getGameState().getDiceRoller().rollSkill();
						}
						boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(state.roll, minimumRoll);
						if (doRoll) {
							step.getResult().addReport(new ReportJumpUpRoll(actingPlayer.getPlayerId(), successful,
								state.roll, minimumRoll, false, modifiers.toArray(new JumpUpModifier[0])));
						}
						actingPlayer.markSkillUsed(skill);
						if (successful) {
							state.awaitingRescue = false;
							actingPlayer.setHasMoved(true);
							actingPlayer.setStandingUp(false);
							step.getResult().setNextAction(StepAction.NEXT_STEP);
						} else if (step.getReRolledAction() == ReRolledActions.JUMP_UP) {
							step.failJumpUp();
						} else {
							JumpUpModifierSelectionService service = new JumpUpModifierSelectionService();
							List<ModifierChoiceOption> options = service.findOptions(game, state.roll);
							if (options.isEmpty()) {
								step.failJumpUp();
							} else {
								state.awaitingRescue = true;
								UtilServerDialog.showDialog(step.getGameState(), new DialogReRollModifierChoiceParameter(
									actingPlayer.getPlayerId(), ReRolledActions.JUMP_UP, minimumRoll, state.roll, options,
									service.findCombinations(game), Collections.emptyList(), false, null, null, null,
									Collections.emptyList()), false);
								step.getResult().setNextAction(StepAction.CONTINUE);
							}
						}
						return false;
					}
				}
				step.getResult().setNextAction(StepAction.NEXT_STEP);
				return false;
			}
		});
	}
}
