package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportDauntlessRoll;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.action.block.StepDauntless;
import com.fumbbl.ffb.server.step.action.block.StepDauntless.StepState;
import com.fumbbl.ffb.server.step.bb2020.multiblock.StepDauntlessMultiple;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.Dauntless;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.BB2020)
public class DauntlessBehaviour extends SkillBehaviour<Dauntless> {
	public DauntlessBehaviour() {
		super();

		registerModifier(new StepModifier<StepDauntless, StepState>(2) {

			@Override
			public StepCommandStatus handleCommandHook(StepDauntless step, StepState state,
			                                           ClientCommandUseSkill useSkillCommand) {
				if (useSkillCommand.getSkill().hasSkillProperty(NamedProperties.canDoubleStrengthAfterDauntless)) {
					state.status = useSkillCommand.isSkillUsed() ? ActionStatus.SKILL_CHOICE_YES : ActionStatus.SKILL_CHOICE_NO;
				}
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepDauntless step, StepState state) {
				boolean doNextStep = true;
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				int defenderStrength = game.getDefender().getStrengthWithModifiers();
				boolean lessStrengthThanDefender = (actingPlayer.getStrength() < defenderStrength);
				boolean usesSpecialBlockingRules = actingPlayer.getPlayer().hasSkillProperty(NamedProperties.makesStrengthTestObsolete);

				if (state.status == null && UtilCards.hasSkill(actingPlayer, skill) && lessStrengthThanDefender
					&& ((state.usingStab == null) || !state.usingStab) && !usesSpecialBlockingRules) {
					boolean doDauntless = true;
					if (ReRolledActions.DAUNTLESS == step.getReRolledAction()) {
						if ((step.getReRollSource() == null)
							|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
							doDauntless = false;
						}
					}
					if (doDauntless) {
						int dauntlessRoll = step.getGameState().getDiceRoller().rollDauntless();
						int minimumRoll = DiceInterpreter.getInstance().minimumRollDauntless(actingPlayer.getStrength(),
							defenderStrength);
						boolean successful = (dauntlessRoll >= minimumRoll);
						boolean reRolled = ((step.getReRolledAction() == ReRolledActions.DAUNTLESS)
							&& (step.getReRollSource() != null));
						step.getResult().addReport(new ReportDauntlessRoll(actingPlayer.getPlayerId(), successful, dauntlessRoll,
							minimumRoll, reRolled, defenderStrength));
						if (successful) {
							actingPlayer.markSkillUsed(skill);
							step.publishParameter(new StepParameter(StepParameterKey.SUCCESSFUL_DAUNTLESS, true));
							Skill indomitable = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canDoubleStrengthAfterDauntless);
							if (indomitable != null) {
								doNextStep = false;
								state.status = ActionStatus.WAITING_FOR_SKILL_USE;
								Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
								UtilServerDialog.showDialog(step.getGameState(),
									new DialogSkillUseParameter(actingPlayer.getPlayerId(), indomitable, 0),
									actingTeam.hasPlayer(actingPlayer.getPlayer()));
							}
						} else {
							if (!reRolled) {
								ReRollSource reRollSource = UtilCards.getUnusedRerollSource(actingPlayer, ReRolledActions.DAUNTLESS);

								if (reRollSource != null) {
									Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
									UtilServerDialog.showDialog(step.getGameState(),
										new DialogSkillUseParameter(actingPlayer.getPlayerId(), reRollSource.getSkill(game), minimumRoll),
										actingTeam.hasPlayer(actingPlayer.getPlayer()));
									doNextStep = false;
								} else if (UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(),
									ReRolledActions.DAUNTLESS, minimumRoll, false)) {
									doNextStep = false;
								}
							}
						}
					}
				} else if (state.status != null) {
					switch (state.status) {
						case SKILL_CHOICE_YES:
							actingPlayer.markSkillUsed(NamedProperties.canDoubleStrengthAfterDauntless);
							step.publishParameter(new StepParameter(StepParameterKey.DOUBLE_TARGET_STRENGTH, true));
							break;
						case WAITING_FOR_SKILL_USE:
							doNextStep = false;
							break;
						default:
							break;
					}
				}
				if (doNextStep) {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}
				return false;
			}
		});

		registerModifier(new AbstractStepModifierMultipleBlock<StepDauntlessMultiple, StepStateMultipleRolls>() {

			@Override
			protected boolean requiresRoll(Player<?> actingPlayer, Player<?> opponentPlayer) {
				return Math.max(1, actingPlayer.getStrengthWithModifiers() - 2) < opponentPlayer.getStrengthWithModifiers();
			}

			@Override
			protected boolean canBeSkipped(Player<?> actingPlayer) {
				return !actingPlayer.hasSkillProperty(NamedProperties.canRollToMatchOpponentsStrength);
			}

			@Override
			protected int skillRoll(StepDauntlessMultiple step) {
				return step.getGameState().getDiceRoller().rollSkill();
			}

			@Override
			protected int minimumRoll(Game game, Player<?> actingPlayer, Player<?> opponentPlayer) {
				RollMechanic mechanic = (RollMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name());
				int attackerStrength = ServerUtilBlock.getAttackerStrength(game, actingPlayer, opponentPlayer, true);
				return DiceInterpreter.getInstance().minimumRollDauntless(attackerStrength, opponentPlayer.getStrength() + mechanic.multiBlockDefenderModifier());
			}

			@Override
			protected IReport report(Game game, String playerId, boolean mayBlock, int actualRoll, int minimumRoll, boolean reRolling, String currentTargetId) {
				int strength = game.getPlayerById(currentTargetId).getStrengthWithModifiers();
				return new ReportDauntlessRoll(playerId, mayBlock, actualRoll, minimumRoll, reRolling, strength, currentTargetId);
			}

			@Override
			protected void unhandledTargetsCallback(StepDauntlessMultiple step, StepStateMultipleRolls state) {

			}

			@Override
			protected void cleanUp(StepDauntlessMultiple step, StepStateMultipleRolls state) {

			}

			@Override
			protected void successFulRollCallback(StepDauntlessMultiple step, String successfulId) {
				step.publishParameter(new StepParameter(StepParameterKey.PLAYER_ID_DAUNTLESS_SUCCESS, successfulId));
			}

			@Override
			protected void failedRollEffect(StepDauntlessMultiple step) {}

			@Override
			protected ReRolledAction reRolledAction() {
				return ReRolledActions.DAUNTLESS;
			}
		});

	}
}