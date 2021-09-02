package com.fumbbl.ffb.server.skillbehaviour.bb2016;

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
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.bb2016.StepPushback;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.skill.StandFirm;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.BB2016)
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
				DebugLog log = step.getGameState().getServer().getDebugLog();
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();

				log.log(IServerLogLevel.TRACE, game.getId(), "Pushback step: " + step.toJsonValue());
				log.log(IServerLogLevel.TRACE, game.getId(), "Passed state: " + state);
				log.log(IServerLogLevel.TRACE, game.getId(), "Step state:   " + step.state);

				Skill cancellingSkill = UtilCards.getSkillCancelling(actingPlayer.getPlayer(), skill);
				// handle auto-stand firm
				PlayerState playerState = game.getFieldModel().getPlayerState(state.defender);
				boolean hasSkill = UtilCards.hasSkill(state.defender, skill);
				if (playerState.isRooted()) {
					log.log(IServerLogLevel.TRACE, game.getId(), "Defender is rooted");
					state.standingFirm.put(state.defender.getId(), true);
				} else if ((playerState.isProne() || playerState.isStunned()) || ((state.oldDefenderState != null) && (state.oldDefenderState.isProne() || state.oldDefenderState.isStunned()))) {
					log.log(IServerLogLevel.TRACE, game.getId(), "Defender is prone or stunned");
					log.log(IServerLogLevel.TRACE, game.getId(), "Old defender state: " + state.oldDefenderState.toString());
					log.log(IServerLogLevel.TRACE, game.getId(), "Current defender state: " + state.oldDefenderState.toString());
					state.standingFirm.put(state.defender.getId(), false);
				} else if ((PlayerAction.BLITZ == actingPlayer.getPlayerAction()) && cancellingSkill != null
					&& hasSkill && game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer())
					.isAdjacent(game.getFieldModel().getPlayerCoordinate(state.defender))) {
					log.log(IServerLogLevel.TRACE, game.getId(), "Attacker cancels stand firm");
					state.standingFirm.put(state.defender.getId(), false);
					step.getResult().addReport(
						new ReportSkillUse(actingPlayer.getPlayerId(), cancellingSkill, true, SkillUse.CANCEL_STAND_FIRM));
				}

				log.log(IServerLogLevel.TRACE, game.getId(), "Defender has skill: " + hasSkill);
				// handle stand firm
				if (hasSkill
					&& state.standingFirm.getOrDefault(state.defender.getId(), true)) {
					log.log(IServerLogLevel.TRACE, game.getId(), "Defender has not declined stand firm yet");
					if (!state.standingFirm.containsKey(state.defender.getId())) {
						log.log(IServerLogLevel.TRACE, game.getId(), "Dialog is set");
						UtilServerDialog.showDialog(step.getGameState(),
							new DialogSkillUseParameter(state.defender.getId(), skill, 0), true);
					}
					if (state.standingFirm.containsKey(state.defender.getId())) {
						log.log(IServerLogLevel.TRACE, game.getId(), "Defender did decide to use stand firm in the dialog");
						state.doPush = true;
						state.pushbackStack.clear();
						step.publishParameter(new StepParameter(StepParameterKey.STARTING_PUSHBACK_SQUARE, null));
						step.publishParameter(new StepParameter(StepParameterKey.FOLLOWUP_CHOICE, false));
						step.getResult().addReport(new ReportSkillUse(state.defender.getId(), skill, true, SkillUse.AVOID_PUSH));
					}
					return true;
				} else {
					log.log(IServerLogLevel.TRACE, game.getId(), "Stand firm is not used, either the skill is not present or defender decided not to use it.");
				}
				return false;
			}
		});
	}
}