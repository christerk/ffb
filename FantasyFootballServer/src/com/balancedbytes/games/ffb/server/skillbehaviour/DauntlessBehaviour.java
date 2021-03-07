package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportDauntlessRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.action.block.StepDauntless;
import com.balancedbytes.games.ffb.server.step.action.block.StepDauntless.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.Dauntless;
import com.balancedbytes.games.ffb.util.UtilCards;

@RulesCollection(Rules.COMMON)
public class DauntlessBehaviour extends SkillBehaviour<Dauntless> {
	public DauntlessBehaviour() {
		super();

		registerModifier(new StepModifier<StepDauntless, StepDauntless.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepDauntless step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepDauntless step, StepState state) {
				boolean doNextStep = true;
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				boolean lessStrengthThanDefender = (actingPlayer.getStrength() < game.getDefender().getStrengthWithModifiers());
				boolean usesSpecialBlockingRules = actingPlayer.getPlayer().hasSkillProperty(NamedProperties.useSpecialBlockRules);

				if (UtilCards.hasSkill(actingPlayer, skill) && lessStrengthThanDefender
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
							game.getDefender().getStrengthWithModifiers());
						boolean successful = (dauntlessRoll >= minimumRoll);
						boolean reRolled = ((step.getReRolledAction() == ReRolledActions.DAUNTLESS)
								&& (step.getReRollSource() != null));
						step.getResult().addReport(new ReportDauntlessRoll(actingPlayer.getPlayerId(), successful, dauntlessRoll,
								minimumRoll, reRolled, game.getDefender().getStrengthWithModifiers()));
						if (successful) {
							actingPlayer.setStrength(game.getDefender().getStrengthWithModifiers());
							actingPlayer.markSkillUsed(skill);
						} else {
							if (UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(),
									ReRolledActions.DAUNTLESS, minimumRoll, false)) {
								doNextStep = false;
							}
						}
					}
				}
				if (doNextStep) {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}
				return false;
			}
		});
	}
}