package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.CardEffect;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.dialog.DialogPilingOnParameter;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.ReportPilingOn;
import com.fumbbl.ffb.report.ReportWeepingDaggerRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeBlock;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeBlockProne;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeBlockStunned;
import com.fumbbl.ffb.server.InjuryType.InjuryTypePilingOnArmour;
import com.fumbbl.ffb.server.InjuryType.InjuryTypePilingOnInjury;
import com.fumbbl.ffb.server.InjuryType.InjuryTypePilingOnKnockedOut;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeServer;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.action.block.StepDropFallingPlayers;
import com.fumbbl.ffb.server.step.action.block.StepDropFallingPlayers.StepState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.skill.bb2020.PilingOn;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.BB2020)
public class PilingOnBehaviour extends SkillBehaviour<PilingOn> {
	public PilingOnBehaviour() {
		super();

		registerModifier(new StepModifier<StepDropFallingPlayers, StepState>() {

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
										actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, state.injuryResultDefender,
										ApothecaryMode.DEFENDER);
								rolledDouble = DiceInterpreter.getInstance()
										.isDouble(state.injuryResultDefender.injuryContext().getInjuryRoll());
							} else {
								state.injuryResultDefender = UtilServerInjury.handleInjury(step, new InjuryTypePilingOnArmour(),
										actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, null, ApothecaryMode.DEFENDER);
								rolledDouble = DiceInterpreter.getInstance()
										.isDouble(state.injuryResultDefender.injuryContext().getArmorRoll());
							}
							if (rolledDouble && UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_TO_KO_ON_DOUBLE)) {
								step.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT,
										UtilServerInjury.handleInjury(step, new InjuryTypePilingOnKnockedOut(step), null,
												actingPlayer.getPlayer(), attackerCoordinate, null, null, ApothecaryMode.ATTACKER)));
							}
						}
					} else {
						step.publishParameters(UtilServerInjury.dropPlayer(step, game.getDefender(), ApothecaryMode.DEFENDER, true));

						InjuryTypeServer<?> injuryType = new InjuryTypeBlock();

						if (state.oldDefenderState != null) {
							if (state.oldDefenderState.isStunned()) {
								injuryType = new InjuryTypeBlockStunned();
							} else if (state.oldDefenderState.isProne()) {
								injuryType = new InjuryTypeBlockProne();
							}
						}

						state.injuryResultDefender = UtilServerInjury.handleInjury(step, injuryType, actingPlayer.getPlayer(),
								game.getDefender(), defenderCoordinate, null, null, ApothecaryMode.DEFENDER);

						if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.appliesPoisonOnBadlyHurt)
								&& state.injuryResultDefender.injuryContext().isBadlyHurt()) {
							boolean success = rollWeepingDagger(actingPlayer.getPlayer(), game.getDefender(), step);
							if (success) {
								step.publishParameter(new StepParameter(StepParameterKey.DEFENDER_POISONED, true));
							}
						}
						boolean usesATeamReroll = UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_USES_A_TEAM_REROLL);
						if ((attackerState.getBase() != PlayerState.FALLING)
								&& UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canPileOnOpponent)
								&& (!usesATeamReroll
										|| UtilServerReRoll.isTeamReRollAvailable(step.getGameState(), actingPlayer.getPlayer()))
								&& attackerCoordinate.isAdjacent(defenderCoordinate)
								&& !state.injuryResultDefender.injuryContext().isCasualty() && !attackerState.isRooted()
								&& (!UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_INJURY_ONLY)
										|| state.injuryResultDefender.injuryContext().isArmorBroken())
								&& (!UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_ARMOR_ONLY)
										|| !state.injuryResultDefender.injuryContext().isArmorBroken())
								&& (!game.getDefender().hasSkillProperty(NamedProperties.preventArmourModifications)
										|| state.injuryResultDefender.injuryContext().isArmorBroken())
								&& !UtilCards.hasSkillToCancelProperty(actingPlayer.getPlayer(), NamedProperties.canPileOnOpponent)
								&& !game.getDefender().hasSkillProperty(NamedProperties.preventDamagingInjuryModifications)) {
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
								UtilServerInjury.dropPlayer(step, actingPlayer.getPlayer(), ApothecaryMode.ATTACKER, true));

						InjuryResult injuryResultAttacker = UtilServerInjury.handleInjury(step, new InjuryTypeBlock(),
								game.getDefender(), actingPlayer.getPlayer(), attackerCoordinate, null, null, ApothecaryMode.ATTACKER);
						if (game.getDefender().hasSkillProperty(NamedProperties.appliesPoisonOnBadlyHurt)
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

	private boolean rollWeepingDagger(Player<?> source, Player<?> target, StepDropFallingPlayers step) {
		Game game = step.getGameState().getGame();
		int minimumRoll = DiceInterpreter.getInstance().minimumRollWeepingDagger();
		int roll = step.getGameState().getDiceRoller().rollWeepingDagger();
		boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
		if (successful) {
			game.getFieldModel().addCardEffect(target, CardEffect.POISONED);
		}
		step.getResult().addReport(
				new ReportWeepingDaggerRoll(source.getId(), successful, roll, minimumRoll, false, null));
		return successful;
	}
}
