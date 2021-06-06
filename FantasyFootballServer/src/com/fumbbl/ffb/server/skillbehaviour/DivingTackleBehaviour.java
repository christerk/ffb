package com.fumbbl.ffb.server.skillbehaviour;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.TurnMode;
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
import com.fumbbl.ffb.skill.DivingTackle;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Optional;
import java.util.Set;

@RulesCollection(Rules.COMMON)
public class DivingTackleBehaviour extends SkillBehaviour<DivingTackle> {
	public DivingTackleBehaviour() {
		super();

		registerModifier(new StepModifier<StepDivingTackle, StepDivingTackle.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepDivingTackle step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepDivingTackle step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
				if (state.usingDivingTackle == null) {
					game.setDefenderId(null);
					state.usingDivingTackle = false;
					if (game.getFieldModel().getPlayer(state.coordinateFrom) == null) {
						Player<?>[] divingTacklers = UtilPlayer.findAdjacentOpposingPlayersWithProperty(game, state.coordinateFrom,
								NamedProperties.canAttemptToTackleDodgingPlayer, true);
						divingTacklers = UtilPlayer.filterThrower(game, divingTacklers);
						if (game.getTurnMode() == TurnMode.DUMP_OFF) {
							divingTacklers = UtilPlayer.filterAttackerAndDefender(game, divingTacklers);
						}
						if (ArrayTool.isProvided(divingTacklers) && (state.dodgeRoll > 0)) {
							DodgeModifierFactory modifierFactory = game.getFactory(Factory.DODGE_MODIFIER);
							Set<DodgeModifier> dodgeModifiers = modifierFactory.findModifiers(new DodgeContext(game, actingPlayer, state.coordinateFrom,
									state.coordinateTo, state.usingBreakTackle));
							dodgeModifiers.addAll(modifierFactory.forType(ModifierType.DIVING_TACKLE));
							int minimumRoll = mechanic.minimumRollDodge(game, actingPlayer.getPlayer(),
									dodgeModifiers);
							int minimumRollWithoutBreakTackle = minimumRoll;
							Optional<DodgeModifier> strengthModifier = dodgeModifiers.stream().filter(DodgeModifier::isUseStrength).findFirst();
							if (strengthModifier.isPresent() && dodgeModifiers.remove(strengthModifier.get())) {
								minimumRollWithoutBreakTackle = mechanic.minimumRollDodge(game,
										actingPlayer.getPlayer(), dodgeModifiers);
								dodgeModifiers.add(strengthModifier.get());
							}
							if (!DiceInterpreter.getInstance().isSkillRollSuccessful(state.dodgeRoll, minimumRoll)) {
								String teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
								UtilServerDialog.showDialog(step.getGameState(),
										new DialogPlayerChoiceParameter(teamId, PlayerChoiceMode.DIVING_TACKLE, divingTacklers, null, 1),
										true);
								state.usingDivingTackle = null;
							} else if (!DiceInterpreter.getInstance().isSkillRollSuccessful(state.dodgeRoll,
									minimumRollWithoutBreakTackle)) {
								// Ask if Diving tackle is going to be used strictly to trigger Break Tackle.
								// The dodge will still succeed.
								String teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
								String[] descriptions = new String[] {
										"This will NOT trip the dodger, but will force the use of BREAK TACKLE." };
								UtilServerDialog.showDialog(step.getGameState(), new DialogPlayerChoiceParameter(teamId,
										PlayerChoiceMode.DIVING_TACKLE, divingTacklers, descriptions, 1), true);
								state.usingDivingTackle = null;
							} else {
								step.getResult().addReport(new ReportSkillUse(null, skill, false, SkillUse.WOULD_NOT_HELP));
							}
						}
					}
				}
				if (state.usingDivingTackle != null) {
					step.publishParameter(new StepParameter(StepParameterKey.USING_DIVING_TACKLE, state.usingDivingTackle));
					if (state.usingDivingTackle) {
						// Implicitly, a DT use is normally only triggered if it makes the dodge fail.

						// Check if the dodge is successful with BT (ie. DT was used only to trigger BT)
						DodgeModifierFactory modifierFactory = game.getFactory(Factory.DODGE_MODIFIER);
						Set<DodgeModifier> dodgeModifiers = modifierFactory.findModifiers(new DodgeContext(game, actingPlayer, state.coordinateFrom,
							state.coordinateTo, false));
						int minimumRoll = mechanic.minimumRollDodge(game, actingPlayer.getPlayer(),
							dodgeModifiers);

						Optional<DodgeModifier> strengthModifier = dodgeModifiers.stream().filter(DodgeModifier::isUseStrength).findFirst();

						Optional<Skill> strengthSkill = actingPlayer.getPlayer().getSkillsIncludingTemporaryOnes().stream().filter(skill -> strengthModifier.isPresent() && skill.getDodgeModifiers().contains(strengthModifier.get())).findFirst();

						if (strengthModifier.isPresent()
							&& strengthSkill.isPresent()
							&& DiceInterpreter.getInstance().isSkillRollSuccessful(state.dodgeRoll, minimumRoll)) {
							// This dodge will be successful with Break Tackle triggered, so mark it as
							// used.
							state.usingBreakTackle = true;
							actingPlayer.markSkillUsed(strengthSkill.get());
							step.publishParameter(new StepParameter(StepParameterKey.USING_BREAK_TACKLE, state.usingBreakTackle));
						}

						step.getResult()
								.addReport(new ReportSkillUse(game.getDefender().getId(), skill, true, SkillUse.STOP_OPPONENT));
						step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnSuccess);
					} else {
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					}
				}
				return false;
			}

		});
	}
}
