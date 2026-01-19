package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.ApothecaryMode;
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
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeSaboteur;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.bb2025.shared.StepDropFallingPlayers;
import com.fumbbl.ffb.server.step.bb2025.shared.StepDropFallingPlayers.StepState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInjury;
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

				Player<?> defender = step.getGameState().getGame().getDefender();
				if (defender != null && defender.getId().equals(useSkillCommand.getPlayerId())) {
					state.usingSaboteur = useSkillCommand.isSkillUsed();
					return StepCommandStatus.EXECUTE_STEP;
				}

				return StepCommandStatus.UNHANDLED_COMMAND;
			}

			@Override
			public boolean handleExecuteStepHook(StepDropFallingPlayers step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				Player<?> defender = game.getDefender();
				if (actingPlayer == null || defender == null) {
					return false;
				}

				FieldModel field = game.getFieldModel();
				PlayerState defenderState = field.getPlayerState(defender);
				PlayerState oldState = state.oldDefenderState;
				PlayerAction action = actingPlayer.getPlayerAction();

				boolean blockAction = action == PlayerAction.BLOCK
					|| action == PlayerAction.BLITZ 
					|| action == PlayerAction.MULTIPLE_BLOCK;

				boolean eligible = !state.saboteurTriggered
					&& defender.hasSkillProperty(NamedProperties.canSabotageBlockerOnKnockdown)
					&& defenderState.getBase() == PlayerState.FALLING
					&& !oldState.isProneOrStunned()
					&& blockAction
					&& actingPlayer.getPlayer().getTeam() != defender.getTeam();

				if (!eligible) {
					return false;
				}

				if (state.usingSaboteur == null) {
					UtilCards.getSkillWithProperty(defender, NamedProperties.canSabotageBlockerOnKnockdown)
						.ifPresent(skill -> UtilServerDialog.showDialog(step.getGameState(),
							new DialogSkillUseParameter(defender.getId(), skill, 0), true));
					step.getResult().setNextAction(StepAction.CONTINUE);
					return true;
				}

				if (!state.usingSaboteur) {
					return false;
				}

				int roll = step.getGameState().getDiceRoller().rollSkill();
				boolean success = roll >= 4;
				UtilCards.getSkillWithProperty(defender, NamedProperties.canSabotageBlockerOnKnockdown)
					.ifPresent(skill -> step.getResult().addReport(
						new ReportSaboteurRoll(defender.getId(), success, roll, 4, false)));

				if (!success) {
					return false;
				}

				state.saboteurTriggered = true;

				state.injuryResultDefender = UtilServerInjury.handleInjury(step, new InjuryTypeSaboteur(), null,
					defender, field.getPlayerCoordinate(defender), null, null, ApothecaryMode.DEFENDER);

				Player<?> attacker = actingPlayer.getPlayer();
				PlayerState attackerState = field.getPlayerState(attacker);
				if (attackerState != null && attackerState.getBase() != PlayerState.FALLING) {
					field.setPlayerState(attacker, attackerState.changeBase(PlayerState.FALLING));
				}

				return false;
			}
		});
	}
}
