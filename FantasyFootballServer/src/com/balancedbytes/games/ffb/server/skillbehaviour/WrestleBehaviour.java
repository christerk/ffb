package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeBallAndChain;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.block.StepWrestle;
import com.balancedbytes.games.ffb.server.step.action.block.StepWrestle.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;
import com.balancedbytes.games.ffb.skill.Wrestle;
import com.balancedbytes.games.ffb.util.UtilCards;

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
				StepAction nextAction = StepAction.NEXT_STEP;
				
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
					if (UtilCards.hasSkill(game, actingPlayer, skill) || UtilCards.hasSkill(game, game.getDefender(), skill)) {
						step.getResult().addReport(new ReportSkillUse(null, skill, false, null));
					}
				}
				
				if (state.usingWrestleAttacker || state.usingWrestleDefender) {
					step.publishParameters(UtilServerInjury.dropPlayer(step, game.getDefender(), ApothecaryMode.DEFENDER));
					step.publishParameters(UtilServerInjury.dropPlayer(step, actingPlayer.getPlayer(), ApothecaryMode.ATTACKER));

					if (UtilCards.hasSkillWithProperty(game.getDefender(), NamedProperties.placedProneCausesInjuryRoll)) {
						FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
						step.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT,
								UtilServerInjury.handleInjury(step, new InjuryTypeBallAndChain(), actingPlayer.getPlayer(),
										game.getDefender(), defenderCoordinate, null, ApothecaryMode.DEFENDER)));
					}
				}
				return StepAction.NEXT_STEP;
			}

			private StepAction askDefenderForWrestleUse(StepWrestle step, StepWrestle.StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
				boolean defenderCanUseSkill = UtilCards.hasSkill(game, game.getDefender(), skill) && !defenderState.isRooted();
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
				boolean attackerCanUseSkill = UtilCards.hasSkill(game, actingPlayer, skill) && !attackerState.isRooted();
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