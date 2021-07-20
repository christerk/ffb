package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2020.StepPushback;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.skill.StandFirm;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.BB2020)
public class StandFirmBehaviour extends SkillBehaviour<StandFirm> {
	public StandFirmBehaviour() {
		super();

		registerModifier(new StepModifier<StepPushback, StepPushback.StepState>(2) {

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
				} else if ((state.oldDefenderState != null) && !state.oldDefenderState.hasTacklezones()) {
					step.getResult().addReport(new ReportSkillUse(state.defender.getId(), skill, false, SkillUse.NO_TACKLEZONE));
					state.standingFirm.put(state.defender.getId(), false);
				} else if ((PlayerAction.BLITZ == actingPlayer.getPlayerAction()) && cancellingSkill != null
					&& UtilCards.hasSkill(state.defender, skill) && game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer())
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
					} else {
						state.doPush = true;
						state.pushbackStack.clear();
						step.publishParameter(new StepParameter(StepParameterKey.STARTING_PUSHBACK_SQUARE, null));
						step.publishParameter(new StepParameter(StepParameterKey.FOLLOWUP_CHOICE, false));
						step.getResult().addReport(new ReportSkillUse(state.defender.getId(), skill, true, SkillUse.AVOID_PUSH));
					}

					return true;
				}
				return false;
			}
		});
	}
}