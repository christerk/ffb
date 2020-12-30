package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardEffect;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRollSources;
import com.balancedbytes.games.ffb.dialog.DialogPilingOnParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.SkillConstants;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportPilingOn;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeBlock;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeBlockProne;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeBlockStunned;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypePilingOnArmour;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypePilingOnInjury;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypePilingOnKnockedOut;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeServer;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.block.StepDropFallingPlayers;
import com.balancedbytes.games.ffb.server.step.action.block.StepDropFallingPlayers.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.PilingOn;
import com.balancedbytes.games.ffb.util.UtilCards;

public class PilingOnBehaviour extends SkillBehaviour<PilingOn> {
	public PilingOnBehaviour() {
		super();

		registerModifier(new StepModifier<StepDropFallingPlayers, StepDropFallingPlayers.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepDropFallingPlayers step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				state.usingPilingOn = useSkillCommand.isSkillUsed();
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepDropFallingPlayers step, StepState state) {
				boolean doNextStep = true;
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
				FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
				PlayerState attackerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
				FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
				if ((attackerState != null) && (attackerState.getBase() == PlayerState.FALLING) && attackerState.isRooted()) {
					attackerState = attackerState.changeRooted(false);
				}
				if ((defenderState != null) && (defenderState.getBase() == PlayerState.FALLING) && defenderState.isRooted()) {
					defenderState = defenderState.changeRooted(false);
				}
				if (((defenderState != null) && (defenderState.getBase() == PlayerState.FALLING)
						&& (defenderCoordinate != null)) || (state.usingPilingOn != null)) {
					if (state.usingPilingOn != null) {
						boolean reRollInjury = state.injuryResultDefender.injuryContext().isArmorBroken();
						step.getResult()
								.addReport(new ReportPilingOn(actingPlayer.getPlayerId(), state.usingPilingOn, reRollInjury));
						boolean usesATeamReroll = UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_USES_A_TEAM_REROLL);
						if (state.usingPilingOn && (!usesATeamReroll
								|| UtilServerReRoll.useReRoll(step, ReRollSources.TEAM_RE_ROLL, actingPlayer.getPlayer()))) {
							actingPlayer.markSkillUsed(skill);
							step.publishParameters(
									UtilServerInjury.dropPlayer(step, actingPlayer.getPlayer(), ApothecaryMode.ATTACKER));
							boolean rolledDouble;
							if (reRollInjury) {
								state.injuryResultDefender = UtilServerInjury.handleInjury(step, new InjuryTypePilingOnInjury(),
										actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, state.injuryResultDefender,
										ApothecaryMode.DEFENDER);
								rolledDouble = DiceInterpreter.getInstance()
										.isDouble(state.injuryResultDefender.injuryContext().getInjuryRoll());
							} else {
								state.injuryResultDefender = UtilServerInjury.handleInjury(step, new InjuryTypePilingOnArmour(),
										actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, ApothecaryMode.DEFENDER);
								rolledDouble = DiceInterpreter.getInstance()
										.isDouble(state.injuryResultDefender.injuryContext().getArmorRoll());
							}
							if (rolledDouble && UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_TO_KO_ON_DOUBLE)) {
								step.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT,
										UtilServerInjury.handleInjury(step, new InjuryTypePilingOnKnockedOut(step), null,
												actingPlayer.getPlayer(), attackerCoordinate, null, ApothecaryMode.ATTACKER)));
							}
						}
					} else {
						step.publishParameters(UtilServerInjury.dropPlayer(step, game.getDefender(), ApothecaryMode.DEFENDER));
						InjuryTypeServer injuryType = new InjuryTypeBlock();

						if (state.oldDefenderState != null) {
							if (state.oldDefenderState.isStunned()) {
								injuryType = new InjuryTypeBlockStunned();
							} else if (state.oldDefenderState.isProne()) {
								injuryType = new InjuryTypeBlockProne();
							}
						}

						state.injuryResultDefender = UtilServerInjury.handleInjury(step, injuryType, actingPlayer.getPlayer(),
								game.getDefender(), defenderCoordinate, null, ApothecaryMode.DEFENDER);

						if (actingPlayer.getPlayer().hasSkillWithProperty(NamedProperties.appliesPoisonOnBadlyHurt)
								&& state.injuryResultDefender.injuryContext().isBadlyHurt()) {
							boolean success = rollWeepingDagger(actingPlayer.getPlayer(), game.getDefender(), step);
							if (success) {
								step.publishParameter(new StepParameter(StepParameterKey.DEFENDER_POISONED, true));
							}
						}
						boolean usesATeamReroll = UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_USES_A_TEAM_REROLL);
						if ((attackerState.getBase() != PlayerState.FALLING)
								&& UtilCards.hasUnusedSkill(game, actingPlayer, SkillConstants.PILING_ON)
								&& (!usesATeamReroll
										|| UtilServerReRoll.isTeamReRollAvailable(step.getGameState(), actingPlayer.getPlayer()))
								&& attackerCoordinate.isAdjacent(defenderCoordinate)
								&& !state.injuryResultDefender.injuryContext().isCasualty() && !attackerState.isRooted()
								&& (!UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_INJURY_ONLY)
										|| state.injuryResultDefender.injuryContext().isArmorBroken())
								&& (!UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_ARMOR_ONLY)
										|| !state.injuryResultDefender.injuryContext().isArmorBroken())
								&& (!UtilCards.hasCard(game, game.getDefender(), Card.BELT_OF_INVULNERABILITY)
										|| state.injuryResultDefender.injuryContext().isArmorBroken())
								&& !UtilCards.cancelsSkill(actingPlayer.getPlayer(), SkillConstants.PILING_ON)
								&& !UtilCards.hasCard(game, game.getDefender(), Card.GOOD_OLD_MAGIC_CODPIECE)) {
							state.injuryResultDefender.report(step);
							UtilServerDialog.showDialog(step.getGameState(), new DialogPilingOnParameter(actingPlayer.getPlayerId(),
									state.injuryResultDefender.injuryContext().isArmorBroken(), usesATeamReroll), false);
							doNextStep = false;
						}
					}
				}
				if (doNextStep) {
					if (state.injuryResultDefender != null) {
						step.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, state.injuryResultDefender));
					}
					if (state.usingPilingOn != null) {
						step.publishParameter(new StepParameter(StepParameterKey.USING_PILING_ON, state.usingPilingOn));
					}
					// end turn if dropping a player of your own team
					if ((defenderState != null) && (defenderState.getBase() == PlayerState.FALLING)
							&& (game.getDefender().getTeam() == actingPlayer.getPlayer().getTeam())
							&& (state.oldDefenderState != null) && !state.oldDefenderState.isProne()) {
						step.publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
					}
					if ((attackerState != null) && (attackerState.getBase() == PlayerState.FALLING)
							&& (attackerCoordinate != null)) {
						step.publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
						step.publishParameters(
								UtilServerInjury.dropPlayer(step, actingPlayer.getPlayer(), ApothecaryMode.ATTACKER));
						InjuryResult injuryResultAttacker = UtilServerInjury.handleInjury(step, new InjuryTypeBlock(),
								game.getDefender(), actingPlayer.getPlayer(), attackerCoordinate, null, ApothecaryMode.ATTACKER);
						if (game.getDefender().hasSkillWithProperty(NamedProperties.appliesPoisonOnBadlyHurt)
								&& injuryResultAttacker.injuryContext().isBadlyHurt()) {
							boolean success = rollWeepingDagger(game.getDefender(), actingPlayer.getPlayer(), step);
							if (success) {
								step.publishParameter(new StepParameter(StepParameterKey.ATTACKER_POISONED, true));
							}
						}
						step.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultAttacker));
					}
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}
				return false;
			}

		});
	}

	private boolean rollWeepingDagger(Player source, Player target, StepDropFallingPlayers step) {
		Game game = step.getGameState().getGame();
		int minimumRoll = DiceInterpreter.getInstance().minimumRollWeepingDagger();
		int roll = step.getGameState().getDiceRoller().rollWeepingDagger();
		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
		if (successful) {
			game.getFieldModel().addCardEffect(target, CardEffect.POISONED);
		}
		step.getResult().addReport(
				new ReportSkillRoll(ReportId.WEEPING_DAGGER_ROLL, source.getId(), successful, roll, minimumRoll, false, null));
		return successful;
	}
}