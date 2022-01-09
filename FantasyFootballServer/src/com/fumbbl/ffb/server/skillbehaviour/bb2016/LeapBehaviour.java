package com.fumbbl.ffb.server.skillbehaviour.bb2016;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.factory.JumpModifierFactory;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.JumpMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.modifiers.JumpContext;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportJumpRoll;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeDropJump;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2016.move.StepJump;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.bb2016.Leap;

import java.util.Set;

@RulesCollection(Rules.BB2016)
public class LeapBehaviour extends SkillBehaviour<Leap> {
	public LeapBehaviour() {
		super();

		registerModifier(new StepModifier<StepJump, StepJump.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepJump step,
			                                           StepJump.StepState state,
			                                           ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepJump step,
			                                     StepJump.StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				JumpMechanic mechanic = (JumpMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.JUMP.name());
				boolean doLeap = (actingPlayer.isJumping() && mechanic.canStillJump(game, actingPlayer));
				if (doLeap) {
					if (ReRolledActions.JUMP == step.getReRolledAction()) {
						if ((step.getReRollSource() == null)
								|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
							step.publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, new InjuryTypeDropJump()));
							step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
							doLeap = false;
						}
					}
					if (doLeap) {
						switch (leap(step)) {
						case SUCCESS:
							actingPlayer.setJumping(false);
							actingPlayer.markSkillUsed(skill);
							step.getResult().setNextAction(StepAction.NEXT_STEP);
							break;
						case FAILURE:
							actingPlayer.setJumping(false);
							actingPlayer.markSkillUsed(skill);
							step.publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, new InjuryTypeDropJump()));
							step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
							break;
						default:
							break;
						}
					}
				} else {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}
				return false;
			}

		});

	}

	private ActionStatus leap(StepJump step) {
		ActionStatus status = null;
		Game game = step.getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		JumpModifierFactory modifierFactory = game.getFactory(FactoryType.Factory.JUMP_MODIFIER);
		Set<JumpModifier> jumpModifiers = modifierFactory.findModifiers(new JumpContext(game, actingPlayer.getPlayer(), null, null));
		AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
		int minimumRoll = mechanic.minimumRollJump(actingPlayer.getPlayer(), jumpModifiers);
		int roll = step.getGameState().getDiceRoller().rollSkill();
		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
		boolean reRolled = ((step.getReRolledAction() == ReRolledActions.JUMP) && (step.getReRollSource() != null));
		step.getResult().addReport(new ReportJumpRoll(actingPlayer.getPlayerId(), successful, roll,
				minimumRoll, reRolled, jumpModifiers.toArray(new JumpModifier[0])));
		if (successful) {
			status = ActionStatus.SUCCESS;
		} else {
			status = ActionStatus.FAILURE;
			if (step.getReRolledAction() != ReRolledActions.JUMP) {
				step.setReRolledAction(ReRolledActions.JUMP);
				if (UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(),
						ReRolledActions.JUMP, minimumRoll, false)) {
					status = ActionStatus.WAITING_FOR_RE_ROLL;
				}
			}
		}
		return status;
	}
}
