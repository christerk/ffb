package com.fumbbl.ffb.server.skillbehaviour.bb2016;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeBallAndChain;
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

@RulesCollection(Rules.BB2016)
public class WrestleBehaviour extends SkillBehaviour<Wrestle> {
	public WrestleBehaviour() {
		super();

		registerModifier(new StepModifier<StepWrestle, StepWrestle.StepState>() {

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
			public boolean handleExecuteStepHook(StepWrestle step, StepWrestle.StepState state) {
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

			private StepAction performWrestle(StepWrestle step, StepWrestle.StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				
				if (state.usingWrestleAttacker) {
					step.getResult()
							.addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skill, true, SkillUse.BRING_DOWN_OPPONENT));
				} else if (state.usingWrestleDefender) {
					step.getResult()
							.addReport(new ReportSkillUse(game.getDefenderId(), skill, true, SkillUse.BRING_DOWN_OPPONENT));
				} else {
					if (UtilCards.hasSkill(actingPlayer, skill) || UtilCards.hasSkill(game.getDefender(), skill)) {
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

			private StepAction askDefenderForWrestleUse(StepWrestle step, StepWrestle.StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
				boolean defenderCanUseSkill = UtilCards.hasSkill(game.getDefender(), skill) && !defenderState.isRooted();
				boolean actingPlayerIsBlitzing = actingPlayer.getPlayerAction() == PlayerAction.BLITZ;
				if (!state.usingWrestleAttacker && defenderCanUseSkill
						&& !(actingPlayerIsBlitzing && UtilCards.cancelsSkill(actingPlayer.getPlayer(), skill))) {
					UtilServerDialog.showDialog(step.getGameState(), new DialogSkillUseParameter(game.getDefenderId(), skill, 0),
							true);
					return StepAction.CONTINUE;
				} else {
					state.usingWrestleDefender = false;
					return StepAction.REPEAT;
				}
			}

			private StepAction askAttackerForWrestleUse(StepWrestle step, StepWrestle.StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				PlayerState attackerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
				boolean attackerCanUseSkill = UtilCards.hasSkill(actingPlayer, skill) && !attackerState.isRooted();
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