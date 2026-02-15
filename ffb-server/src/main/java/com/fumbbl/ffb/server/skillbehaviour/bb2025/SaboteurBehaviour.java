package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.bb2025.ReportSaboteurRoll;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.bb2025.shared.StepDropFallingPlayers;
import com.fumbbl.ffb.server.step.bb2025.shared.StepDropFallingPlayers.StepState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.skill.bb2025.Saboteur;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.BB2025)
public class SaboteurBehaviour extends SkillBehaviour<Saboteur> {

	public SaboteurBehaviour() {
		super();

		registerModifier(new StepModifier<StepDropFallingPlayers, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepDropFallingPlayers step, StepState state,
				ClientCommandUseSkill useSkillCommand) {

				if (!useSkillCommand.getSkill().hasSkillProperty(NamedProperties.canSabotageBlockerOnKnockdown)) {
					return StepCommandStatus.UNHANDLED_COMMAND;
				}

				Game game = step.getGameState().getGame();
				Player<?> defender = game.getDefender();
				Player<?> attacker = game.getActingPlayer() != null ? game.getActingPlayer().getPlayer() : null;

				if (defender != null && defender.getId().equals(useSkillCommand.getPlayerId())) {
					state.usingSaboteurDefender = useSkillCommand.isSkillUsed();
					return StepCommandStatus.EXECUTE_STEP;
				}
				if (attacker != null && attacker.getId().equals(useSkillCommand.getPlayerId())) {
					state.usingSaboteurAttacker = useSkillCommand.isSkillUsed();
					return StepCommandStatus.EXECUTE_STEP;
				}

				return StepCommandStatus.UNHANDLED_COMMAND;
			}

			@Override
			public boolean handleExecuteStepHook(StepDropFallingPlayers step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				Player<?> attacker = actingPlayer != null ? actingPlayer.getPlayer() : null;
				Player<?> defender = game.getDefender();
				if (actingPlayer == null || defender == null) {
					return false;
				}

				FieldModel field = game.getFieldModel();
				PlayerState attackerState = field.getPlayerState(attacker);
				PlayerState defenderState = field.getPlayerState(defender);
				PlayerState oldDefenderState = state.oldDefenderState;
				PlayerAction action = actingPlayer.getPlayerAction();

				boolean blockAction = action == PlayerAction.BLOCK
					|| action == PlayerAction.BLITZ 
					|| action == PlayerAction.MULTIPLE_BLOCK;

				// attacker has Saboteur and is falling (skull/both-down)
				boolean attackerEligible = blockAction
					&& !state.saboteurTriggeredAttacker
					&& attacker.hasSkillProperty(NamedProperties.canSabotageBlockerOnKnockdown)
					&& attackerState.getBase() == PlayerState.FALLING;

				if (attackerEligible) {
					if (state.usingSaboteurAttacker == null) {
						UtilCards.getSkillWithProperty(attacker, NamedProperties.canSabotageBlockerOnKnockdown)
							.ifPresent(skill -> UtilServerDialog.showDialog(step.getGameState(),
								new DialogSkillUseParameter(attacker.getId(), skill, 0), false));
						step.getResult().setNextAction(StepAction.CONTINUE);
						return true;
					}
					if (state.usingSaboteurAttacker) {
						int roll = step.getGameState().getDiceRoller().rollSkill();
						boolean success = roll >= 4;
						UtilCards.getSkillWithProperty(attacker, NamedProperties.canSabotageBlockerOnKnockdown)
							.ifPresent(skill -> step.getResult().addReport(new ReportSaboteurRoll(attacker.getId(), success, roll, 4, false)));
						if (success) {
							state.saboteurTriggeredAttacker = true;
							if (defenderState != null && defenderState.getBase() != PlayerState.FALLING) {
								field.setPlayerState(defender, defenderState.changeBase(PlayerState.FALLING));
							}
						}
					}
					if (state.usingSaboteurAttacker == null) {
						return true;
					}
				}

				// defender has Saboteur (standing and not distracted) and was knocked down
				boolean defenderEligible = blockAction
					&& !state.saboteurTriggeredDefender
					&& defender.hasUsableSkillProperty(NamedProperties.canSabotageBlockerOnKnockdown, oldDefenderState)
					&& defenderState.getBase() == PlayerState.FALLING;

				if (defenderEligible) {
					if (state.usingSaboteurDefender == null) {
						UtilCards.getSkillWithProperty(defender, NamedProperties.canSabotageBlockerOnKnockdown)
							.ifPresent(skill -> UtilServerDialog.showDialog(step.getGameState(),
								new DialogSkillUseParameter(defender.getId(), skill, 0), true));
						step.getResult().setNextAction(StepAction.CONTINUE);
						return true;
					}
					if (state.usingSaboteurDefender) {
						int roll = step.getGameState().getDiceRoller().rollSkill();
						boolean success = roll >= 4;
						UtilCards.getSkillWithProperty(defender, NamedProperties.canSabotageBlockerOnKnockdown)
							.ifPresent(skill -> step.getResult().addReport(new ReportSaboteurRoll(defender.getId(), success, roll, 4, false)));
						if (success) {
							state.saboteurTriggeredDefender = true;
							if (attackerState != null && attackerState.getBase() != PlayerState.FALLING) {
								field.setPlayerState(attacker, attackerState.changeBase(PlayerState.FALLING));
							}
						}
					}
					if (state.usingSaboteurDefender == null) {
						return true;
					}
				}

				return false;
			}
		});
	}
}
