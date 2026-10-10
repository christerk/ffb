package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.factory.DodgeModifierFactory;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.DodgeContext;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.action.move.StepDivingTackle;
import com.fumbbl.ffb.server.step.action.move.StepDivingTackle.StepState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.skill.mixed.DivingTackle;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Set;
import java.util.stream.Collectors;

@RulesCollection(Rules.BB2025)
public class DivingTackleBehaviour extends SkillBehaviour<DivingTackle> {
	public DivingTackleBehaviour() {
		super();

		registerModifier(new StepModifier<StepDivingTackle, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepDivingTackle step, StepState state,
																								 ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepDivingTackle step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				AgilityMechanic mechanic =
					(AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());

				if (state.usingDivingTackle == null) {
					game.setDefenderId(null);
					state.usingDivingTackle = false;
					if (game.getFieldModel().getPlayer(state.coordinateFrom) == null) {
						Player<?>[] divingTacklers = UtilPlayer.findEligibleDivingTacklers(game, state.coordinateFrom,
							state.coordinateTo, NamedProperties.canAttemptToTackleDodgingPlayer);
						if (ArrayTool.isProvided(divingTacklers) && (state.dodgeRoll > 0)) {
							DodgeModifierFactory modifierFactory = game.getFactory(Factory.DODGE_MODIFIER);
							Set<DodgeModifier> dodgeModifiers = modifierFactory.findModifiers(new DodgeContext(game, actingPlayer,
								state.coordinateFrom, state.coordinateTo, state.selectedModifierSkills));
							dodgeModifiers.addAll(modifierFactory.forType(ModifierType.DIVING_TACKLE));

							int minimumRollWithDivingTackle =
								mechanic.minimumRollDodge(game, actingPlayer.getPlayer(), dodgeModifiers);
							boolean tripsDodger = !DiceInterpreter.getInstance()
								.isSkillRollSuccessful(state.dodgeRoll, minimumRollWithDivingTackle);

							promptForDT(game, step, divingTacklers, new String[]{description(state, tripsDodger)}, state);
						}
					}
				}
				if (state.usingDivingTackle != null) {
					step.publishParameter(new StepParameter(StepParameterKey.USING_DIVING_TACKLE, state.usingDivingTackle));
					if (state.usingDivingTackle) {
						step.getResult()
							.addReport(new ReportSkillUse(game.getDefender().getId(), skill, true, SkillUse.STOP_OPPONENT));
						step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnSuccess);
					} else {
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					}
				}
				return false;
			}

			private String description(StepState state, boolean tripsDodger) {
				if (tripsDodger) {
					return "This will trip the dodger.";
				}
				if (state.selectedModifierSkills.isEmpty()) {
					return "This will NOT trip the dodger, the dodge will still succeed.";
				}
				return "This will NOT trip the dodger, but will force the use of "
					+ state.selectedModifierSkills.stream().map(Skill::getName).collect(Collectors.joining(" + ")) + ".";
			}

			private void promptForDT(Game game, StepDivingTackle step, Player<?>[] divingTacklers, String[] descriptions,
															 StepState state) {
				String teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
				UtilServerDialog.showDialog(step.getGameState(), new DialogPlayerChoiceParameter(teamId,
					PlayerChoiceMode.DIVING_TACKLE, divingTacklers, descriptions, 1), true);
				state.usingDivingTackle = null;
			}

		});
	}
}
