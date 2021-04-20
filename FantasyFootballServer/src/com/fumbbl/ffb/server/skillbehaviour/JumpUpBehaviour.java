package com.fumbbl.ffb.server.skillbehaviour;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportJumpUpRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.action.select.StepJumpUp;
import com.fumbbl.ffb.server.step.action.select.StepJumpUp.StepState;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.JumpUp;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.COMMON)
public class JumpUpBehaviour extends SkillBehaviour<JumpUp> {
	public JumpUpBehaviour() {
		super();

		registerModifier(new StepModifier<StepJumpUp, StepJumpUp.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepJumpUp step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepJumpUp step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
				if ((actingPlayer.isStandingUp() && !actingPlayer.hasMoved()
						&& UtilCards.hasUnusedSkill(actingPlayer, skill))
						|| (ReRolledActions.JUMP_UP == step.getReRolledAction())) {
					actingPlayer.setHasMoved(true);
					game.setConcessionPossible(false);
					actingPlayer.markSkillUsed(skill);
					if ((PlayerAction.BLOCK == actingPlayer.getPlayerAction())
							|| (PlayerAction.MULTIPLE_BLOCK == actingPlayer.getPlayerAction())) {
						if (ReRolledActions.JUMP_UP == step.getReRolledAction()) {
							if ((step.getReRollSource() == null)
									|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
								game.getFieldModel().setPlayerState(actingPlayer.getPlayer(),
										playerState.changeBase(PlayerState.PRONE).changeActive(false));
								step.publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
								step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
								return false;
							}
						}
						AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
						int minimumRoll = mechanic.minimumRollJumpUp(actingPlayer.getPlayer());
						int roll = step.getGameState().getDiceRoller().rollSkill();
						boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
						boolean reRolled = ((step.getReRolledAction() == ReRolledActions.JUMP_UP)
								&& (step.getReRollSource() != null));
						step.getResult().addReport(new ReportJumpUpRoll(actingPlayer.getPlayerId(),
								successful, roll, minimumRoll, reRolled, null));
						if (successful) {
							actingPlayer.setStandingUp(false);
							step.getResult().setNextAction(StepAction.NEXT_STEP);
							return false;
						} else {
							if ((step.getReRolledAction() == ReRolledActions.JUMP_UP)
									|| !UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(),
											ReRolledActions.JUMP_UP, minimumRoll, false)) {
								game.getFieldModel().setPlayerState(actingPlayer.getPlayer(),
										playerState.changeBase(PlayerState.PRONE).changeActive(false));
								step.publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
								step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
							} else {
								step.getResult().setNextAction(StepAction.CONTINUE);
							}
							return false;
						}
					}
				}
				step.getResult().setNextAction(StepAction.NEXT_STEP);
				return false;
			}

		});
	}
}