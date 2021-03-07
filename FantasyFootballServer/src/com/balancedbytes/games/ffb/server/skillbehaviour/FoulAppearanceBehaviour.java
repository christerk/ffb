package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.action.block.StepFoulAppearance;
import com.balancedbytes.games.ffb.server.step.action.block.StepFoulAppearance.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.FoulAppearance;
import com.balancedbytes.games.ffb.util.UtilCards;

@RulesCollection(Rules.COMMON)
public class FoulAppearanceBehaviour extends SkillBehaviour<FoulAppearance> {
	public FoulAppearanceBehaviour() {
		super();

		registerModifier(new StepModifier<StepFoulAppearance, StepFoulAppearance.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepFoulAppearance step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepFoulAppearance step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				if ((game.getDefender() != null) && UtilCards.hasSkill(game.getDefender(), skill)
						&& !UtilCards.hasSkillToCancelProperty(actingPlayer.getPlayer(), NamedProperties.forceRollBeforeBeingBlocked)) {
					boolean doRoll = true;
					if (ReRolledActions.FOUL_APPEARANCE == step.getReRolledAction()) {
						if ((step.getReRollSource() == null)
								|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
							doRoll = false;
							actingPlayer.setHasBlocked(true);
							game.getTurnData().setTurnStarted(true);
							step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
						}
					}
					if (doRoll) {
						int foulAppearanceRoll = step.getGameState().getDiceRoller().rollSkill();
						int minimumRoll = DiceInterpreter.getInstance().minimumRollResistingFoulAppearance();
						boolean mayBlock = DiceInterpreter.getInstance().isSkillRollSuccessful(foulAppearanceRoll, minimumRoll);
						boolean reRolled = ((step.getReRolledAction() == ReRolledActions.FOUL_APPEARANCE)
								&& (step.getReRollSource() != null));
						step.getResult().addReport(new ReportSkillRoll(ReportId.FOUL_APPEARANCE_ROLL, actingPlayer.getPlayerId(),
								mayBlock, foulAppearanceRoll, minimumRoll, reRolled));
						if (mayBlock) {
							step.getResult().setNextAction(StepAction.NEXT_STEP);
						} else {
							if (!UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(),
									ReRolledActions.FOUL_APPEARANCE, minimumRoll, false)) {
								actingPlayer.setHasBlocked(true);
								game.getTurnData().setTurnStarted(true);
								step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
							}
						}
						if (!mayBlock && !reRolled) {
							step.getResult().setSound(SoundId.EW);
						}
					}
				} else {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}
				return false;
			}

		});
	}
}