package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.report.ReportReferee;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.action.foul.StepEjectPlayer;
import com.balancedbytes.games.ffb.server.step.action.foul.StepEjectPlayer.StepState;
import com.balancedbytes.games.ffb.server.step.action.foul.StepReferee;
import com.balancedbytes.games.ffb.skill.SneakyGit;
import com.balancedbytes.games.ffb.util.UtilCards;

@RulesCollection(Rules.COMMON)
public class SneakyGitBehaviour extends SkillBehaviour<SneakyGit> {
	public SneakyGitBehaviour() {
		super();

		registerModifier(new StepModifier<StepEjectPlayer, StepEjectPlayer.StepState>() {

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

				if ((state.argueTheCallSuccessful != null) && state.argueTheCallSuccessful) {
					game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeBase(PlayerState.RESERVE));
				} else if (UtilCards.hasSkill(game, actingPlayer, skill)
						&& UtilGameOption.isOptionEnabled(game, GameOptionId.SNEAKY_GIT_BAN_TO_KO)) {
					game.getFieldModel().setPlayerState(actingPlayer.getPlayer(),
							playerState.changeBase(PlayerState.KNOCKED_OUT));
					attackerResult.setSendToBoxReason(SendToBoxReason.FOUL_BAN);
					attackerResult.setSendToBoxTurn(game.getTurnData().getTurnNr());
					attackerResult.setSendToBoxHalf(game.getHalf());
				} else {
					game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeBase(PlayerState.BANNED));
					attackerResult.setSendToBoxReason(SendToBoxReason.FOUL_BAN);
					attackerResult.setSendToBoxTurn(game.getTurnData().getTurnNr());
					attackerResult.setSendToBoxHalf(game.getHalf());
				}
				return false;
			}

		});

		registerModifier(new StepModifier<StepReferee, StepReferee.StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepReferee step,
					com.balancedbytes.games.ffb.server.step.action.foul.StepReferee.StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepReferee step,
					com.balancedbytes.games.ffb.server.step.action.foul.StepReferee.StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				boolean refereeSpotsFoul = false;
				if (!UtilCards.isCardActive(game, Card.BLATANT_FOUL) && (!UtilCards.hasSkill(game, actingPlayer, skill)
						|| state.injuryResultDefender.injuryContext().isArmorBroken()
						|| ((UtilCards.hasSkill(game, actingPlayer, skill)
								&& UtilGameOption.isOptionEnabled(game, GameOptionId.SNEAKY_GIT_BAN_TO_KO))))) {
					int[] armorRoll = state.injuryResultDefender.injuryContext().getArmorRoll();
					refereeSpotsFoul = (armorRoll[0] == armorRoll[1]);
				}
				if (!refereeSpotsFoul && state.injuryResultDefender.injuryContext().isArmorBroken()) {
					int[] injuryRoll = state.injuryResultDefender.injuryContext().getInjuryRoll();
					refereeSpotsFoul = (injuryRoll[0] == injuryRoll[1]);
				}
				step.getResult().addReport(new ReportReferee(refereeSpotsFoul));
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