package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.factory.PassModifierFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.mechanics.TtmMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.PassContext;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.bb2020.ReportThrowTeamMateRoll;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2020.ttm.StepThrowTeamMate;
import com.fumbbl.ffb.server.step.bb2020.ttm.StepThrowTeamMate.StepState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.bb2020.ThrowTeamMate;

import java.util.Set;

@RulesCollection(Rules.BB2020)
public class ThrowTeamMateBehaviour extends SkillBehaviour<ThrowTeamMate> {
	public ThrowTeamMateBehaviour() {
		super();

		registerModifier(new StepModifier<StepThrowTeamMate, StepState>(2) {

			@Override
			public StepCommandStatus handleCommandHook(StepThrowTeamMate step, StepState state,
			                                           ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepThrowTeamMate step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				actingPlayer.setHasPassed(true);
				game.setThrowerId(actingPlayer.getPlayerId());
				game.setConcessionPossible(false);
				ReRolledAction rerolledAction;
				if (state.kicked) {
					game.getTurnData().setKtmUsed(true);
					rerolledAction = ReRolledActions.KICK_TEAM_MATE;
				} else {
					game.getTurnData().setPassUsed(true);
					rerolledAction = ReRolledActions.THROW_TEAM_MATE;
				}
				UtilServerDialog.hideDialog(step.getGameState());
				Player<?> thrower = game.getActingPlayer().getPlayer();
				boolean doRoll = true;
				if (rerolledAction == step.getReRolledAction()) {
					if ((step.getReRollSource() == null) || !UtilServerReRoll.useReRoll(step, step.getReRollSource(), thrower)) {
						handlePassResult(state.passResult, step);
						doRoll = false;
					}
				}
				if (doRoll) {
					PassModifierFactory passModifierFactory = game.getFactory(FactoryType.Factory.PASS_MODIFIER);
					FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(thrower);
					PassMechanic mechanic = (PassMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
					PassingDistance passingDistance = mechanic.findPassingDistance(game, throwerCoordinate,
						game.getPassCoordinate(), true);
					Set<PassModifier> passModifiers = passModifierFactory.findModifiers(new PassContext(game, thrower, passingDistance, true));
					TtmMechanic ttmMechanic = (TtmMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());
					int minimumRoll = ttmMechanic.minimumRoll(passingDistance, passModifiers);
					int roll = step.getGameState().getDiceRoller().rollSkill();
					boolean playerCanPass = thrower.getPassing() != 0;
					state.passResult = evaluatePass(playerCanPass, thrower.getPassingWithModifiers(), roll, ttmMechanic.modifierSum(passingDistance, passModifiers));
					boolean reRolled = ((step.getReRolledAction() == rerolledAction)
						&& (step.getReRollSource() != null));
					boolean successful = state.passResult == PassResult.ACCURATE || state.passResult == PassResult.INACCURATE;

					step.getResult().addReport(new ReportThrowTeamMateRoll(thrower.getId(), successful, roll, minimumRoll,
						reRolled, passModifiers.toArray(new PassModifier[0]), passingDistance, state.thrownPlayerId, state.passResult, state.kicked));
					if (successful) {
						handlePassResult(state.passResult, step);
					} else {
						if (step.getReRolledAction() != rerolledAction && playerCanPass) {
							step.setReRolledAction(rerolledAction);
							if (reRolled || !UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer,
								rerolledAction, minimumRoll, false)) {
								handlePassResult(state.passResult, step);
							}
						} else {
							handlePassResult(state.passResult, step);
						}
					}
				}

				return false;
			}

			private void handlePassResult(PassResult passResult, StepThrowTeamMate step) {
				step.publishParameter(StepParameter.from(StepParameterKey.PASS_RESULT, passResult));
				step.getResult().setNextAction(StepAction.NEXT_STEP);
			}

			private PassResult evaluatePass(boolean playerCanPass, int passValue, int roll, int modifierSum) {
				if (!playerCanPass) {
					return PassResult.FUMBLE;
				}
				if (passValue <= 0) {
					return PassResult.FUMBLE;
				}
				int resultAfterModifiers = roll - modifierSum;
				if (roll == 1) {
					return PassResult.FUMBLE;
				} else if (roll == 6 || resultAfterModifiers >= passValue) {
					return PassResult.ACCURATE;
				} else if (resultAfterModifiers <= 1) {
					return PassResult.WILDLY_INACCURATE;
				} else {
					return PassResult.INACCURATE;
				}
			}
		});
	}
}
