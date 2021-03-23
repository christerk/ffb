package com.balancedbytes.games.ffb.server.skillbehaviour.bb2016;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.factory.JumpModifierFactory;
import com.balancedbytes.games.ffb.mechanics.AgilityMechanic;
import com.balancedbytes.games.ffb.mechanics.JumpMechanic;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.modifiers.JumpContext;
import com.balancedbytes.games.ffb.modifiers.JumpModifier;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.ActionStatus;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeDropJump;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.bb2016.StepJump;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.bb2016.Leap;

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
		Set<JumpModifier> jumpModifiers = modifierFactory.findModifiers(new JumpContext(game, actingPlayer.getPlayer()));
		AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
		int minimumRoll = mechanic.minimumRollJump(actingPlayer.getPlayer(), jumpModifiers);
		int roll = step.getGameState().getDiceRoller().rollSkill();
		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
		boolean reRolled = ((step.getReRolledAction() == ReRolledActions.JUMP) && (step.getReRollSource() != null));
		step.getResult().addReport(new ReportSkillRoll(ReportId.JUMP_ROLL, actingPlayer.getPlayerId(), successful, roll,
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
