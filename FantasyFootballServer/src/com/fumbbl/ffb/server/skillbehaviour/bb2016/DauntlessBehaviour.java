package com.fumbbl.ffb.server.skillbehaviour.bb2016;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportDauntlessRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.action.block.StepDauntless;
import com.fumbbl.ffb.server.step.action.block.StepDauntless.StepState;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.Dauntless;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.BB2016)
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
				int defenderStrength = game.getDefender().getStrengthWithModifiers();
				if (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK) {
					RollMechanic mechanic = (RollMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name());
					defenderStrength += mechanic.multiBlockDefenderModifier();
				}
				boolean lessStrengthThanDefender = (actingPlayer.getStrength() < defenderStrength);
				boolean usesSpecialBlockingRules = actingPlayer.getPlayer().hasSkillProperty(NamedProperties.makesStrengthTestObsolete);

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
							defenderStrength);
						boolean successful = (dauntlessRoll >= minimumRoll);
						boolean reRolled = ((step.getReRolledAction() == ReRolledActions.DAUNTLESS)
								&& (step.getReRollSource() != null));
						step.getResult().addReport(new ReportDauntlessRoll(actingPlayer.getPlayerId(), successful, dauntlessRoll,
								minimumRoll, reRolled, defenderStrength));
						if (successful) {
							actingPlayer.markSkillUsed(skill);
							step.publishParameter(new StepParameter(StepParameterKey.SUCCESSFUL_DAUNTLESS, true));
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