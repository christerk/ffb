package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeBallAndChain;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.action.block.StepWrestle;
import com.fumbbl.ffb.server.step.action.block.StepWrestle.StepState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.skill.Wrestle;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.BB2020)
public class WrestleBehaviour extends SkillBehaviour<Wrestle> {
	public WrestleBehaviour() {
		super();

		registerModifier(new StepModifier<StepWrestle, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepWrestle step, StepState state,
																								 ClientCommandUseSkill useSkillCommand) {

				if (state.usingWrestleAttacker == null) {
					state.usingWrestleAttacker = useSkillCommand.isSkillUsed();
				} else {
					state.usingWrestleDefender = useSkillCommand.isSkillUsed();
				}
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepWrestle step, StepState state) {
				StepAction nextAction;

				if (state.usingWrestleAttacker == null) {
					nextAction = askAttackerForWrestleUse(step, state);
				} else if (state.usingWrestleDefender == null) {
					nextAction = askDefenderForWrestleUse(step, state);
				} else {
					nextAction = performWrestle(step, state);
				}

				step.getResult().setNextAction(nextAction);
				return false;
			}

			private StepAction performWrestle(StepWrestle step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				boolean defenderHasTacklezones = state.oldDefenderState.hasTacklezones();

				if (state.usingWrestleAttacker) {
					step.getResult()
						.addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skill, true, SkillUse.BRING_DOWN_OPPONENT));
				} else if (state.usingWrestleDefender) {
					step.getResult()
						.addReport(new ReportSkillUse(game.getDefenderId(), skill, true, SkillUse.BRING_DOWN_OPPONENT));
				} else {
					if (!defenderHasTacklezones && UtilCards.hasSkill(game.getDefender(), skill)) {
						step.getResult().addReport(new ReportSkillUse(game.getDefenderId(), skill, false, SkillUse.NO_TACKLEZONE));
					} else if (UtilCards.hasSkill(actingPlayer, skill) || UtilCards.hasSkill(game.getDefender(), skill)) {
						step.getResult().addReport(new ReportSkillUse(null, skill, false, null));
					}
				}

				if (state.usingWrestleAttacker || state.usingWrestleDefender) {
					step.publishParameters(UtilServerInjury.dropPlayer(step, game.getDefender(), ApothecaryMode.DEFENDER, true));
					step.publishParameters(UtilServerInjury.dropPlayer(step, actingPlayer.getPlayer(), ApothecaryMode.ATTACKER, true));

					if (game.getDefender().hasSkillProperty(NamedProperties.placedProneCausesInjuryRoll)) {
						FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
						step.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT,
							UtilServerInjury.handleInjury(step, new InjuryTypeBallAndChain(), actingPlayer.getPlayer(),
								game.getDefender(), defenderCoordinate, null, null, ApothecaryMode.DEFENDER)));
					}
				}
				return StepAction.NEXT_STEP;
			}

			private StepAction askDefenderForWrestleUse(StepWrestle step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				boolean defenderCanUseSkill = UtilCards.hasSkill(game.getDefender(), skill) && state.oldDefenderState.hasTacklezones();
				boolean actingPlayerIsBlitzing = actingPlayer.getPlayerAction() == PlayerAction.BLITZ;
				boolean wrestlePrevented = actingPlayerIsBlitzing && UtilCards.cancelsSkill(actingPlayer.getPlayer(), skill);
				if (!state.usingWrestleAttacker && defenderCanUseSkill
					&& !wrestlePrevented) {
					UtilServerDialog.showDialog(step.getGameState(), new DialogSkillUseParameter(game.getDefenderId(), skill, 0),
						true);
					return StepAction.CONTINUE;
				} else {
					if (wrestlePrevented) {
						step.getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(),
							UtilCards.getSkillCancelling(actingPlayer.getPlayer(), skill), true, SkillUse.CANCEL_WRESTLE));
					}
					state.usingWrestleDefender = false;
					return StepAction.REPEAT;
				}
			}

			private StepAction askAttackerForWrestleUse(StepWrestle step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				boolean attackerCanUseSkill = UtilCards.hasSkill(actingPlayer, skill);
				if (attackerCanUseSkill) {
					UtilServerDialog.showDialog(step.getGameState(),
						new DialogSkillUseParameter(actingPlayer.getPlayer().getId(), skill, 0), false);
					return StepAction.CONTINUE;
				} else {
					state.usingWrestleAttacker = false;
					return StepAction.REPEAT;
				}
			}

		});
	}
}