package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.block.StepPushback;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.skill.StandFirm;
import com.balancedbytes.games.ffb.util.UtilCards;

@RulesCollection(Rules.COMMON)
public class StandFirmBehaviour extends SkillBehaviour<StandFirm> {
	public StandFirmBehaviour() {
		super();

		registerModifier(new StepModifier<StepPushback, StepPushback.StepState>(1) {

			@Override
			public StepCommandStatus handleCommandHook(StepPushback step, StepPushback.StepState state,
					ClientCommandUseSkill useSkillCommand) {
				state.standingFirm.put(useSkillCommand.getPlayerId(), useSkillCommand.isSkillUsed());
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepPushback step, StepPushback.StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();

				Skill cancellingSkill = UtilCards.getSkillCancelling(actingPlayer.getPlayer(), skill);
				// handle auto-stand firm
				PlayerState playerState = game.getFieldModel().getPlayerState(state.defender);
				if (playerState.isRooted()) {
					state.standingFirm.put(state.defender.getId(), true);
				} else if (playerState.isProne() || ((state.oldDefenderState != null) && state.oldDefenderState.isProne())) {
					state.standingFirm.put(state.defender.getId(), false);
				} else if ((PlayerAction.BLITZ == actingPlayer.getPlayerAction()) && cancellingSkill != null
						&& game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer())
								.isAdjacent(game.getFieldModel().getPlayerCoordinate(state.defender))) {
					state.standingFirm.put(state.defender.getId(), false);
					step.getResult().addReport(
							new ReportSkillUse(actingPlayer.getPlayerId(), cancellingSkill, true, SkillUse.CANCEL_STAND_FIRM));
				}

				// handle stand firm
				if (UtilCards.hasSkill(state.defender, skill)
						&& state.standingFirm.getOrDefault(state.defender.getId(), true)) {
					if (!state.standingFirm.containsKey(state.defender.getId())) {
						UtilServerDialog.showDialog(step.getGameState(),
								new DialogSkillUseParameter(state.defender.getId(), skill, 0), true);
					}
					if (state.standingFirm.containsKey(state.defender.getId())) {
						if (state.standingFirm.containsKey(state.defender.getId())) {
							state.doPush = true;
							state.pushbackStack.clear();
							step.publishParameter(new StepParameter(StepParameterKey.STARTING_PUSHBACK_SQUARE, null));
							step.publishParameter(new StepParameter(StepParameterKey.FOLLOWUP_CHOICE, false));
							step.getResult().addReport(new ReportSkillUse(state.defender.getId(), skill, true, SkillUse.AVOID_PUSH));
						} else {
							step.getResult().addReport(new ReportSkillUse(state.defender.getId(), skill, false, null));
						}

					}
					return true;
				}
				return false;
			}
		});
	}
}