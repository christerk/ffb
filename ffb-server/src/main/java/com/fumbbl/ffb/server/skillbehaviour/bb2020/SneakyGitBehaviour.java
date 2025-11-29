package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.mixed.ReportBiasedRef;
import com.fumbbl.ffb.report.mixed.ReportReferee;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.action.foul.StepReferee;
import com.fumbbl.ffb.server.step.bb2020.foul.StepEjectPlayer;
import com.fumbbl.ffb.server.step.bb2020.foul.StepEjectPlayer.StepState;
import com.fumbbl.ffb.skill.bb2020.SneakyGit;
import com.fumbbl.ffb.util.UtilCards;

@RulesCollection(Rules.BB2020)
public class SneakyGitBehaviour extends SkillBehaviour<SneakyGit> {
	public SneakyGitBehaviour() {
		super();

		registerModifier(new StepModifier<StepEjectPlayer, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepEjectPlayer step, StepState state,
																								 ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepEjectPlayer step, StepState state) {
				Game game = step.getGameState().getGame();
				GameResult gameResult = game.getGameResult();
				ActingPlayer actingPlayer = game.getActingPlayer();
				PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
				PlayerResult attackerResult = gameResult.getPlayerResult(actingPlayer.getPlayer());
				boolean wasCased = game.getFieldModel().getPlayerState(actingPlayer.getPlayer()).isCasualty();

				SendToBoxReason reason = SendToBoxReason.FOUL_BAN;

				if (state.officiousRef) {
					reason = SendToBoxReason.OFFICIOUS_REF;
				} else if (actingPlayer.getPlayerId().equals(step.getGameState().getPassState().getOriginalBombardier())) {
					reason = SendToBoxReason.THREW_TWO_BOMBS;
				}

				if (UtilCards.hasSkill(actingPlayer, skill)
					&& UtilGameOption.isOptionEnabled(game, GameOptionId.SNEAKY_GIT_BAN_TO_KO)) {
					game.getFieldModel().setPlayerState(actingPlayer.getPlayer(),
						playerState.changeBase(PlayerState.KNOCKED_OUT));
					attackerResult.setSendToBoxReason(reason);
					attackerResult.setSendToBoxTurn(game.getTurnData().getTurnNr());
					attackerResult.setSendToBoxHalf(game.getHalf());
				} else if ((state.argueTheCallSuccessful == null || !state.argueTheCallSuccessful) && !wasCased) {
					game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeBase(PlayerState.BANNED));
					attackerResult.setSendToBoxReason(reason);
					attackerResult.setSendToBoxTurn(game.getTurnData().getTurnNr());
					attackerResult.setSendToBoxHalf(game.getHalf());
				}
				return false;
			}

		});

		registerModifier(new StepModifier<StepReferee, StepReferee.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepReferee step,
																								 StepReferee.StepState state,
																								 ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepReferee step,
																					 StepReferee.StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				boolean refereeSpotsFoul = false;
				if (!game.isActive(NamedProperties.foulBreaksArmourWithoutRoll) && (!UtilCards.hasSkill(actingPlayer, skill)
					|| state.injuryResultDefender.injuryContext().isArmorBroken()
					|| ((UtilCards.hasSkill(actingPlayer, skill) && UtilGameOption.isOptionEnabled(game, GameOptionId.SNEAKY_GIT_BAN_TO_KO))))) {
					int[] armorRoll = state.injuryResultDefender.injuryContext().getArmorRoll();
					refereeSpotsFoul = (armorRoll[0] == armorRoll[1]) && !UtilCards.hasSkill(actingPlayer, skill);
				}
				if (!refereeSpotsFoul && state.injuryResultDefender.injuryContext().isArmorBroken()) {
					int[] injuryRoll = state.injuryResultDefender.injuryContext().getInjuryRoll();
					refereeSpotsFoul = (injuryRoll[0] == injuryRoll[1]);
				}
				boolean underScrutiny = step.getGameState().getPrayerState().isUnderScrutiny(actingPlayer.getPlayer().getTeam());
				refereeSpotsFoul |= underScrutiny;
				step.getResult().addReport(new ReportReferee(refereeSpotsFoul, underScrutiny));

				if (!refereeSpotsFoul) {
					InducementSet opponentInducementSet = game.isHomePlaying() ? game.getTurnDataAway().getInducementSet() : game.getTurnDataHome().getInducementSet();
					for (int i = 0; i < opponentInducementSet.value(Usage.SPOT_FOUL); i++) {
						int roll = step.getGameState().getDiceRoller().rollSkill();
						refereeSpotsFoul = roll > 4;

						step.getResult().addReport(new ReportBiasedRef(roll, refereeSpotsFoul));

						if (refereeSpotsFoul) {
							break;
						}
					}
				}

				if (refereeSpotsFoul) {
					step.getResult().setSound(SoundId.WHISTLE);
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				} else {
					step.getResult().setNextAction(StepAction.GOTO_LABEL, state.gotoLabelOnEnd);
				}
				return false;
			}

		});
	}
}