package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.select.StepJumpUp;
import com.balancedbytes.games.ffb.server.step.action.select.StepJumpUp.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.JumpUp;
import com.balancedbytes.games.ffb.util.UtilCards;

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
						&& UtilCards.hasUnusedSkill(game, actingPlayer, skill))
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
						int minimumRoll = DiceInterpreter.getInstance().minimumRollJumpUp(actingPlayer.getPlayer());
						int roll = step.getGameState().getDiceRoller().rollSkill();
						boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
						boolean reRolled = ((step.getReRolledAction() == ReRolledActions.JUMP_UP)
								&& (step.getReRollSource() != null));
						step.getResult().addReport(new ReportSkillRoll(ReportId.JUMP_UP_ROLL, actingPlayer.getPlayerId(),
								successful, roll, minimumRoll, reRolled));
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