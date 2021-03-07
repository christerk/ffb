package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.factory.LeapModifierFactory;
import com.balancedbytes.games.ffb.mechanics.AgilityMechanic;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.modifiers.LeapContext;
import com.balancedbytes.games.ffb.modifiers.LeapModifier;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.ActionStatus;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeDropLeap;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.move.StepLeap;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.Leap;
import com.balancedbytes.games.ffb.util.UtilCards;

import java.util.Set;

@RulesCollection(Rules.COMMON)
public class LeapBehaviour extends SkillBehaviour<Leap> {
	public LeapBehaviour() {
		super();

		registerModifier(new StepModifier<StepLeap, StepLeap.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepLeap step,
					com.balancedbytes.games.ffb.server.step.action.move.StepLeap.StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepLeap step,
					com.balancedbytes.games.ffb.server.step.action.move.StepLeap.StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				boolean doLeap = (actingPlayer.isLeaping() && UtilCards.hasUnusedSkill(actingPlayer, skill));
				if (doLeap) {
					if (ReRolledActions.LEAP == step.getReRolledAction()) {
						if ((step.getReRollSource() == null)
								|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
							step.publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, new InjuryTypeDropLeap()));
							step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
							doLeap = false;
						}
					}
					if (doLeap) {
						switch (leap(step)) {
						case SUCCESS:
							actingPlayer.setLeaping(false);
							actingPlayer.markSkillUsed(skill);
							step.getResult().setNextAction(StepAction.NEXT_STEP);
							break;
						case FAILURE:
							actingPlayer.setLeaping(false);
							actingPlayer.markSkillUsed(skill);
							step.publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, new InjuryTypeDropLeap()));
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

	private ActionStatus leap(StepLeap step) {
		ActionStatus status = null;
		Game game = step.getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer());

		LeapModifierFactory modifierFactory = new LeapModifierFactory();
		Set<LeapModifier> leapModifiers = modifierFactory.findModifiers(new LeapContext(game, actingPlayer.getPlayer()));
		AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
		int minimumRoll = mechanic.minimumRollLeap(actingPlayer.getPlayer(), leapModifiers);
		int roll = step.getGameState().getDiceRoller().rollSkill();
		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
		boolean reRolled = ((step.getReRolledAction() == ReRolledActions.LEAP) && (step.getReRollSource() != null));
		step.getResult().addReport(new ReportSkillRoll(ReportId.LEAP_ROLL, actingPlayer.getPlayerId(), successful, roll,
				minimumRoll, reRolled, leapModifiers.toArray(new LeapModifier[0])));
		if (successful) {
			status = ActionStatus.SUCCESS;
		} else {
			status = ActionStatus.FAILURE;
			if (step.getReRolledAction() != ReRolledActions.LEAP) {
				step.setReRolledAction(ReRolledActions.LEAP);
				if (UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(),
						ReRolledActions.LEAP, minimumRoll, false)) {
					status = ActionStatus.WAITING_FOR_RE_ROLL;
				}
			}
		}
		return status;
	}
}
