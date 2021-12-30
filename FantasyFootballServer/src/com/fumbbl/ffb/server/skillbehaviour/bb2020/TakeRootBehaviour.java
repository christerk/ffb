package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.factory.ReRolledActionFactory;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportConfusionRoll;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.skillbehaviour.bb2020.StepTakeRoot.StepState;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2020.StepPushback;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.bb2020.TakeRoot;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.BB2020)
public class TakeRootBehaviour extends SkillBehaviour<TakeRoot> {
	public TakeRootBehaviour() {
		super();

		registerModifier(new StepModifier<StepTakeRoot, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepTakeRoot step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return null;
			}

			@Override
			public boolean handleExecuteStepHook(StepTakeRoot step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());

				if (!playerState.isRooted()) {
					boolean doRoll = true;
					ReRolledAction reRolledAction = new ReRolledActionFactory().forSkill(game, skill);
					if ((reRolledAction != null) && (reRolledAction == step.getReRolledAction())) {
						if ((step.getReRollSource() == null)
								|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
							doRoll = false;
							state.status = ActionStatus.FAILURE;
							step.cancelPlayerAction();
						}
					} else {
						doRoll = UtilCards.hasUnusedSkill(actingPlayer, skill);
					}
					if (doRoll) {
						int roll = step.getGameState().getDiceRoller().rollSkill();
						int minimumRoll = DiceInterpreter.getInstance().minimumRollConfusion(true);
						boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
						actingPlayer.markSkillUsed(skill);
						if (successful) {
							// read player state again as it might have been altered for pro in UtilServerReRoll#useReRoll
							playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer()).recoverTacklezones();
							game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState);

						} else {
							state.status = ActionStatus.FAILURE;
							if (((reRolledAction == null) || (reRolledAction != step.getReRolledAction()))
									&& UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(),
											reRolledAction, minimumRoll, false)) {
								state.status = ActionStatus.WAITING_FOR_RE_ROLL;
							} else {
								step.cancelPlayerAction();
							}
						}
						boolean reRolled = ((reRolledAction != null) && (reRolledAction == step.getReRolledAction())
							&& (step.getReRollSource() != null));
						step.getResult().addReport(
							new ReportConfusionRoll(actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled, skill));
					}
				}

				return false;
			}

		});

		registerModifier(new StepModifier<StepPushback, StepPushback.StepState>(1) {
			@Override
			public StepCommandStatus handleCommandHook(StepPushback step, StepPushback.StepState state, ClientCommandUseSkill useSkillCommand) {
				return null;
			}

			@Override
			public boolean handleExecuteStepHook(StepPushback step, StepPushback.StepState state) {
				Game game = step.getGameState().getGame();
				// use defender as oldDefenderState always refers to the first player in a chainpush
				PlayerState playerState = game.getFieldModel().getPlayerState(state.defender);

				if (playerState.isRooted()) {
					state.doPush = true;
					state.pushbackStack.clear();
					step.publishParameter(new StepParameter(StepParameterKey.STARTING_PUSHBACK_SQUARE, null));
					step.publishParameter(new StepParameter(StepParameterKey.FOLLOWUP_CHOICE, false));
					return true;
				}

				return false;
			}
		});
	}
}
