package com.balancedbytes.games.ffb.server.skillbehaviour.bb2020;

import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportAnimosityRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.action.pass.StepAnimosity;
import com.balancedbytes.games.ffb.server.step.action.pass.StepAnimosity.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.Animosity;

@RulesCollection(Rules.BB2020)
public class AnimosityBehaviour extends SkillBehaviour<Animosity> {
	public AnimosityBehaviour() {
		super();

		registerModifier(new StepModifier<StepAnimosity, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepAnimosity step, StepState state,
			                                           ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.UNHANDLED_COMMAND;
			}

			@Override
			public boolean handleExecuteStepHook(StepAnimosity step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();

				Player<?> thrower = game.getThrower();
				Player<?> catcher = game.getPlayerById(state.catcherId);
				if (ReRolledActions.ANIMOSITY == step.getReRolledAction()) {
					if ((step.getReRollSource() == null)
						|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), thrower)) {
						actingPlayer.setSufferingAnimosity(true);
					} else {
						state.doRoll = true;
					}
				} else {
					if (catcher != null && thrower != null) {
						state.doRoll = thrower.hasAnimosityTowards(catcher);
					}
				}
				if (state.doRoll) {
					int roll = step.getGameState().getDiceRoller().rollSkill();
					int minimumRoll = DiceInterpreter.getInstance().minimumRollAnimosity();
					boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
					actingPlayer.markSkillUsed(skill);
					if (successful) {
						actingPlayer.setSufferingAnimosity(false);
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					} else {
						if ((ReRolledActions.ANIMOSITY == step.getReRolledAction())
							|| !UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), thrower,
							ReRolledActions.ANIMOSITY, minimumRoll, false)) {
							actingPlayer.setSufferingAnimosity(true);
						}
					}
					boolean reRolled = ((ReRolledActions.ANIMOSITY == step.getReRolledAction())
						&& (step.getReRollSource() != null));
					step.getResult().addReport(new ReportAnimosityRoll(actingPlayer.getPlayerId(),
						successful, roll, minimumRoll, reRolled, null));
				} else {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}
				if (actingPlayer.isSufferingAnimosity()) {
					step.getResult().setNextAction(StepAction.GOTO_LABEL, state.gotoLabelOnFailure);
				}
				return false;
			}
		});
	}
}
