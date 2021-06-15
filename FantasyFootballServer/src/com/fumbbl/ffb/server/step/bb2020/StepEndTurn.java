package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.HeatExhaustion;
import com.fumbbl.ffb.KnockoutRecovery;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogArgueTheCallParameter;
import com.fumbbl.ffb.dialog.DialogBribesParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementDuration;
import com.fumbbl.ffb.inducement.InducementPhase;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.ZappedPlayer;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandArgueTheCall;
import com.fumbbl.ffb.net.commands.ClientCommandUseInducement;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.ReportArgueTheCallRoll;
import com.fumbbl.ffb.report.ReportBribesRoll;
import com.fumbbl.ffb.report.ReportSecretWeaponBan;
import com.fumbbl.ffb.report.bb2020.ReportBrilliantCoachingReRollsLost;
import com.fumbbl.ffb.report.bb2020.ReportTurnEnd;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.ServerMode;
import com.fumbbl.ffb.server.factory.PrayerHandlerFactory;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestUpdateGamestate;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.EndGame;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.Inducement.SequenceParams;
import com.fumbbl.ffb.server.step.generator.common.Kickoff;
import com.fumbbl.ffb.server.util.UtilServerCards;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerInducementUse;
import com.fumbbl.ffb.server.util.UtilServerTimer;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilBox;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Step in any sequence to end a turn.
 *
 * May push another sequence on the stack (endGame, startGame or kickoff)
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepEndTurn extends AbstractStep {

	private Boolean fTouchdown;
	private Boolean fBribesChoiceHome;
	private Boolean fBribesChoiceAway;
	private Boolean fArgueTheCallChoiceHome;
	private Boolean fArgueTheCallChoiceAway;
	private boolean fNextSequencePushed;
	private boolean fRemoveUsedSecretWeapons;
	private boolean fNewHalf;
	private boolean fEndGame;
	private boolean fWithinSecretWeaponHandling;

	public StepEndTurn(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_TURN;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			Game game = getGameState().getGame();
			Team team = UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand) ? game.getTeamHome()
					: game.getTeamAway();
			switch (pReceivedCommand.getId()) {
			case CLIENT_ARGUE_THE_CALL:
				ClientCommandArgueTheCall argueTheCallCommand = (ClientCommandArgueTheCall) pReceivedCommand.getCommand();
				argueTheCall(team, argueTheCallCommand.getPlayerIds());
				fWithinSecretWeaponHandling = true;
				commandStatus = StepCommandStatus.EXECUTE_STEP;
				break;
			case CLIENT_USE_INDUCEMENT:
				ClientCommandUseInducement inducementCommand = (ClientCommandUseInducement) pReceivedCommand.getCommand();
				if (inducementCommand.getInducementType().getUsage() == Usage.AVOID_BAN) {
					fWithinSecretWeaponHandling = true;
					if (!useSecretWeaponBribes(team, inducementCommand.getPlayerIds())) {
						if (UtilServerSteps.checkCommandIsFromHomePlayer(getGameState(), pReceivedCommand)) {
							fBribesChoiceHome = null;
						} else {
							fBribesChoiceHome = null;
						}
					}
				}
				commandStatus = StepCommandStatus.EXECUTE_STEP;
				break;
			default:
				break;
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {

		Game game = getGameState().getGame();
		game.getFieldModel().clearMultiBlockTargets();
		UtilServerDialog.hideDialog(getGameState());
		boolean isHomeTurnEnding = game.isHomePlaying();

		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		Kickoff kickoffGenerator = (Kickoff) factory.forName(SequenceGenerator.Type.Kickoff.name());
		EndGame endGenerator = (EndGame) factory.forName(SequenceGenerator.Type.EndGame.name());

		if (!fWithinSecretWeaponHandling) {

			if ((game.getTurnMode() == TurnMode.BLITZ) || (game.getTurnMode() == TurnMode.KICKOFF_RETURN)
					|| (game.getTurnMode() == TurnMode.PASS_BLOCK) || (game.getTurnMode() == TurnMode.ILLEGAL_SUBSTITUTION)
					|| game.getTurnMode() == TurnMode.SWARMING) {
				publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
				getResult().setNextAction(StepAction.NEXT_STEP);
				return;
			}

			if (fTouchdown == null) {
				fTouchdown = UtilServerSteps.checkTouchdown(getGameState());
			}

			markPlayedAndSecretWeapons();

			fEndGame = false;
			fNewHalf = UtilServerSteps.checkEndOfHalf(getGameState());

			if (!fNextSequencePushed) {

				fNextSequencePushed = true;

				Player<?> touchdownPlayer = null;
				if (fTouchdown) {

					touchdownPlayer = game.getFieldModel().getPlayer(game.getFieldModel().getBallCoordinate());
					boolean offTurnTouchDown;
					if (touchdownPlayer != null) {

						GameResult gameResult = game.getGameResult();
						PlayerResult touchdownPlayerResult = gameResult.getPlayerResult(touchdownPlayer);
						touchdownPlayerResult.setTouchdowns(touchdownPlayerResult.getTouchdowns() + 1);

						if (game.getTeamHome().hasPlayer(touchdownPlayer)) {
							gameResult.getTeamResultHome().setScore(gameResult.getTeamResultHome().getScore() + 1);
							offTurnTouchDown = !game.isHomePlaying();

						} else {
							gameResult.getTeamResultAway().setScore(gameResult.getTeamResultAway().getScore() + 1);
							offTurnTouchDown = game.isHomePlaying();
						}

						// in the case of a caught kick-off that results in a TD
						offTurnTouchDown |= (game.getTurnMode() == TurnMode.KICKOFF);
						game.setHomePlaying(game.getTeamHome().hasPlayer(touchdownPlayer));

						if (offTurnTouchDown) {
							game.getTurnData().setTurnNr(game.getTurnData().getTurnNr() + 1);
							fNewHalf = UtilServerSteps.checkEndOfHalf(getGameState());
						}

					}

					game.setTurnMode(TurnMode.SETUP);
					game.setSetupOffense(false);

				} else {

					switch (game.getTurnMode()) {
						case NO_PLAYERS_TO_FIELD:
						game.getTurnDataHome().setTurnNr(game.getTurnDataHome().getTurnNr() + 2);
						game.getTurnDataAway().setTurnNr(game.getTurnDataAway().getTurnNr() + 2);
						fNewHalf = UtilServerSteps.checkEndOfHalf(getGameState());
						game.setTurnMode(TurnMode.SETUP);
						game.setSetupOffense(false);
						fTouchdown = true;
						break;
						case KICKOFF:
						game.setHomePlaying(!game.isHomePlaying());
						game.getTurnData().setTurnNr(game.getTurnData().getTurnNr() + 1);
						game.getTurnData().setTurnStarted(false);
						game.getTurnData().setFirstTurnAfterKickoff(true);
						game.setTurnMode(TurnMode.REGULAR);
						break;
						case REGULAR:
						if (fNewHalf) {
							game.setTurnMode(TurnMode.SETUP);
							game.setSetupOffense(false);
						} else {
							game.setHomePlaying(!game.isHomePlaying());
							game.getTurnData().setTurnNr(game.getTurnData().getTurnNr() + 1);
						}
						game.getTurnData().setTurnStarted(false);
						game.getTurnData().setFirstTurnAfterKickoff(false);
						break;
						default:
							break;
					}

				}

				UtilPlayer.refreshPlayersForTurnStart(game);
				game.getFieldModel().clearMoveSquares();
				game.getFieldModel().clearTrackNumbers();
				game.getFieldModel().clearDiceDecorations();

				List<KnockoutRecovery> knockoutRecoveries = new ArrayList<>();
				List<HeatExhaustion> heatExhaustions = new ArrayList<>();
				List<Player<?>> unzappedPlayers = new ArrayList<>();
				int faintingCount = 0;
				if (fNewHalf || fTouchdown) {
					for (Player<?> player : game.getPlayers()) {
						if (player instanceof ZappedPlayer) {
							getGameState().getServer().getCommunication().sendUnzapPlayer(getGameState(), (ZappedPlayer) player);
							Team team = game.findTeam(player);
							player = ((ZappedPlayer) player).getOriginalPlayer();
							team.addPlayer(player);
							unzappedPlayers.add(player);
							getGameState().removeZappedPlayer(player);
						}
						PlayerState playerState = game.getFieldModel().getPlayerState(player);
						FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
						if (playerState.getBase() == PlayerState.KNOCKED_OUT) {
							KnockoutRecovery knockoutRecovery = recoverKnockout(player);
							if (knockoutRecovery != null) {
								knockoutRecoveries.add(knockoutRecovery);
								if (knockoutRecovery.isRecovering()) {
									game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.RESERVE));
									UtilBox.putPlayerIntoBox(game, player);
								}
							}
						}
						if (playerState.getBase() == PlayerState.EXHAUSTED) {
							game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.RESERVE));
						}
					}

					if (Weather.SWELTERING_HEAT == game.getFieldModel().getWeather()) {
						faintingCount = getGameState().getDiceRoller().rollDice(3);
						for (Team team: new Team[] {game.getTeamHome(), game.getTeamAway()}) {
							List<Player<?>> onPitch = Arrays.stream(team.getPlayers()).filter(player -> {
								FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
								return (playerCoordinate != null) && !playerCoordinate.isBoxCoordinate();
							}).collect(Collectors.toList());
							for (int i = 0; i < faintingCount && !onPitch.isEmpty(); i++) {
								int index = getGameState().getDiceRoller().rollDice(onPitch.size()) - 1;
								Player<?> player = onPitch.remove(index);
								PlayerState playerState = game.getFieldModel().getPlayerState(player);
								game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.EXHAUSTED));
								UtilBox.putPlayerIntoBox(game, player);
								heatExhaustions.add(new HeatExhaustion(player.getId(), true, 0));
							}
						}
					}

					UtilBox.putAllPlayersIntoBox(game);
				}
				if (fNewHalf) {
					if (game.getHalf() > 2) {
						fEndGame = true;
					} else if (game.getHalf() > 1) {
						GameResult gameResult = game.getGameResult();
						if (UtilGameOption.isOptionEnabled(game, GameOptionId.OVERTIME)
								&& (gameResult.getTeamResultHome().getScore() == gameResult.getTeamResultAway().getScore())) {
							UtilServerGame.startHalf(this, game.getHalf() + 1);
							kickoffGenerator.pushSequence(new Kickoff.SequenceParams(getGameState(), true));
							fRemoveUsedSecretWeapons = true;
						} else {
							fEndGame = true;
						}
					} else {
						UtilServerGame.startHalf(this, game.getHalf() + 1);
						kickoffGenerator.pushSequence(new Kickoff.SequenceParams(getGameState(), false));
						fRemoveUsedSecretWeapons = true;
					}
					getResult().setSound(SoundId.WHISTLE);
				} else if (fTouchdown) {
					game.getFieldModel().setBallCoordinate(null);
					game.getFieldModel().setBallInPlay(false);
					getGameState().getServer().getCommunication().sendSound(getGameState(), SoundId.TOUCHDOWN);
					getResult().setSound(SoundId.WHISTLE);
					if (game.getHalf() == 3) {
						endGenerator.pushSequence(new EndGame.SequenceParams(getGameState(), false));
					} else {
						kickoffGenerator.pushSequence(new Kickoff.SequenceParams(getGameState(), false));
						fRemoveUsedSecretWeapons = true;
					}
				} else if (game.getTurnMode() != TurnMode.REGULAR) {
					UtilBox.refreshBoxes(game);
					getResult().setSound(SoundId.DING);
					kickoffGenerator.pushSequence(new Kickoff.SequenceParams(getGameState(), false));
					fRemoveUsedSecretWeapons = true;
				} else {
					getResult().setSound(SoundId.DING);
					((com.fumbbl.ffb.server.step.generator.common.Inducement) factory.forName(SequenceGenerator.Type.Inducement.name()))
						.pushSequence(new SequenceParams(getGameState(), InducementPhase.START_OF_OWN_TURN,
							game.isHomePlaying()));
				}

				KnockoutRecovery[] knockoutRecoveryArray = knockoutRecoveries.toArray(new KnockoutRecovery[0]);
				HeatExhaustion[] heatExhaustionArray = heatExhaustions.toArray(new HeatExhaustion[0]);
				String touchdownPlayerId = (touchdownPlayer != null) ? touchdownPlayer.getId() : null;
				getResult().addReport(
					new ReportTurnEnd(touchdownPlayerId, knockoutRecoveryArray, heatExhaustionArray, unzappedPlayers, faintingCount));

				if (game.isTurnTimeEnabled()) {
					UtilServerTimer.stopTurnTimer(getGameState(), System.currentTimeMillis());
					game.setTurnTime(0);
				}

				deactivateCardsAndPrayers(InducementDuration.UNTIL_END_OF_TURN, isHomeTurnEnding);
				deactivateCardsAndPrayers(InducementDuration.UNTIL_END_OF_OPPONENTS_TURN, isHomeTurnEnding);

				if (fNewHalf) {
					deactivateCardsAndPrayers(InducementDuration.UNTIL_END_OF_HALF, isHomeTurnEnding);
				}

				if (fNewHalf || fTouchdown) {
					deactivateCardsAndPrayers(InducementDuration.UNTIL_END_OF_DRIVE, isHomeTurnEnding);
					reportSecretWeaponsUsed();
					removeBrilliantCoachingReRolls(true);
					removeBrilliantCoachingReRolls(false);
				}

			}

		}

		if (fBribesChoiceAway == null) {
			fBribesChoiceAway = false;
			if (!fEndGame && (fNewHalf || fTouchdown) && askForSecretWeaponBribes(game.getTeamAway())) {
				fBribesChoiceAway = null;
			}
		}

		if ((fBribesChoiceHome == null) && (fBribesChoiceAway != null)) {
			fBribesChoiceHome = false;
			if (!fEndGame && (fNewHalf || fTouchdown) && askForSecretWeaponBribes(game.getTeamHome())) {
				fBribesChoiceHome = null;
			}
		}

		if ((fBribesChoiceHome != null) && (fBribesChoiceAway != null) && (fArgueTheCallChoiceAway == null)) {
			fArgueTheCallChoiceAway = false;
			if (!fEndGame && (fNewHalf || fTouchdown) && askForArgueTheCall(game.getTeamAway())) {
				fArgueTheCallChoiceAway = null;
			}
		}

		if ((fBribesChoiceHome != null) && (fBribesChoiceAway != null) && (fArgueTheCallChoiceHome == null)
				&& (fArgueTheCallChoiceAway != null)) {
			fArgueTheCallChoiceHome = false;
			if (!fEndGame && (fNewHalf || fTouchdown) && askForArgueTheCall(game.getTeamHome())) {
				fArgueTheCallChoiceHome = null;
			}
		}

		if (fEndGame || ((fArgueTheCallChoiceHome != null) && (fArgueTheCallChoiceAway != null)
				&& (fBribesChoiceHome != null) && (fBribesChoiceAway != null))) {

			if (!fEndGame && fRemoveUsedSecretWeapons) {
				removeUsedSecretWeapons();
			}

			game.startTurn();
			UtilServerGame.updateLeaderReRolls(this);

			if (fEndGame) {
				endGenerator.pushSequence(new EndGame.SequenceParams(getGameState(), false));
			}

			if (!fEndGame && game.isTurnTimeEnabled()) {
				UtilServerTimer.startTurnTimer(getGameState(), System.currentTimeMillis());
			}

			updateFumbblGame(getGameState(), fNewHalf, fTouchdown);

			getResult().setNextAction(StepAction.NEXT_STEP);

		}

	}

	private void removeBrilliantCoachingReRolls(boolean homeTeam) {
		String teamId;
		TurnData turnData;

		if (homeTeam) {
			teamId = getGameState().getGame().getTeamHome().getId();
			turnData = getGameState().getGame().getTurnDataHome();
		} else {
			teamId = getGameState().getGame().getTeamAway().getId();
			turnData = getGameState().getGame().getTurnDataAway();
		}

		int reRollsBrilliantCoaching = turnData.getReRollsBrilliantCoachingOneDrive();
		if (reRollsBrilliantCoaching > 0) {
			turnData.setReRollsBrilliantCoachingOneDrive(0);
			turnData.setReRolls(turnData.getReRolls() - reRollsBrilliantCoaching);
			getResult().addReport(new ReportBrilliantCoachingReRollsLost(teamId, reRollsBrilliantCoaching));
		}

	}

	private void markPlayedAndSecretWeapons() {
		Game game = getGameState().getGame();
		if (game.getTurnMode() == TurnMode.REGULAR) {
			for (Player<?> player : game.getPlayers()) {
				PlayerState playerState = game.getFieldModel().getPlayerState(player);
				PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
				if (playerState.canBeSetUp() && playerState.getBase() != PlayerState.RESERVE) {
					boolean hasSecretWeapon = player.hasSkillProperty(NamedProperties.getsSentOffAtEndOfDrive);
					if (!hasSecretWeapon && player instanceof ZappedPlayer) {
						hasSecretWeapon = (((ZappedPlayer) player).getOriginalPlayer()).hasSkillProperty(NamedProperties.getsSentOffAtEndOfDrive);
					}
					if (hasSecretWeapon) {
						playerResult.setHasUsedSecretWeapon(true);
					}
					if ((game.isHomePlaying() && game.getTeamHome().hasPlayer(player))
							|| (!game.isHomePlaying() && game.getTeamAway().hasPlayer(player))) {
						playerResult.setTurnsPlayed(playerResult.getTurnsPlayed() + 1);
					}
				}
			}
		}
	}

	private void reportSecretWeaponsUsed() {
		ReportSecretWeaponBan reportBan = new ReportSecretWeaponBan();
		Game game = getGameState().getGame();
		for (Player<?> player : game.getPlayers()) {
			PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
			Skill skillRequiringPlayerToBeSentOff = player.getSkillWithProperty(NamedProperties.getsSentOffAtEndOfDrive);
			if (playerResult.hasUsedSecretWeapon() && skillRequiringPlayerToBeSentOff != null) {
				// special for stunty leeg -> roll for secret weapon ban
				int penalty = player.getSkillIntValue(skillRequiringPlayerToBeSentOff);
				if (penalty > 0) {
					int[] roll = getGameState().getDiceRoller().rollSecretWeapon();
					int total = roll[0] + roll[1];
					boolean banned = (total >= penalty);
					reportBan.add(player.getId(), total, banned);
					playerResult.setHasUsedSecretWeapon(banned);
					// lrb6 secret weapon use (auto-ban)
				} else {
					reportBan.add(player.getId(), 0, true);
				}
			}
		}
		if (ArrayTool.isProvided(reportBan.getPlayerIds())) {
			getResult().addReport(reportBan);
		}
	}

	private void removeUsedSecretWeapons() {
		Game game = getGameState().getGame();
		for (Player<?> player : game.getPlayers()) {
			PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
			if (playerResult.hasUsedSecretWeapon()) {
				PlayerState playerState = game.getFieldModel().getPlayerState(player);
				game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.BANNED));
				playerResult.setSendToBoxByPlayerId(null);
				playerResult.setSendToBoxReason(SendToBoxReason.SECRET_WEAPON_BAN);
				playerResult.setSendToBoxTurn(game.getTurnData().getTurnNr());
				playerResult.setSendToBoxHalf(game.getHalf());
				UtilBox.putPlayerIntoBox(game, player);
				playerResult.setHasUsedSecretWeapon(false);
			}
		}
		UtilServerGame.updateLeaderReRolls(this);
	}

	private KnockoutRecovery recoverKnockout(Player<?> pPlayer) {
		if (pPlayer != null) {
			String playerId = pPlayer.getId();
			int recoveryRoll = getGameState().getDiceRoller().rollKnockoutRecovery();
			Game game = getGameState().getGame();
			InducementSet inducementSet = (pPlayer.getTeam() == game.getTeamHome())
					? game.getTurnDataHome().getInducementSet()
					: game.getTurnDataAway().getInducementSet();
			int bloodweiserKegValue = inducementSet.getInducementMapping().entrySet().stream().filter(entry -> entry.getKey().getUsage() == Usage.KNOCKOUT_RECOVERY).map(entry -> entry.getValue().getValue()).findFirst().orElse(0);
			boolean isRecovering = DiceInterpreter.getInstance().isRecoveringFromKnockout(recoveryRoll, bloodweiserKegValue);
			return new KnockoutRecovery(playerId, isRecovering, recoveryRoll, bloodweiserKegValue);
		} else {
			return null;
		}
	}

	private static void updateFumbblGame(GameState pGameState, boolean pNewHalf, boolean pTouchdown) {
		FantasyFootballServer server = pGameState.getServer();
		if (server.getMode() == ServerMode.FUMBBL) {
			Game game = pGameState.getGame();
			if (!game.isTesting() && ((game.getTurnMode() == TurnMode.REGULAR) || pNewHalf || pTouchdown)) {
				server.getRequestProcessor().add(new FumbblRequestUpdateGamestate(pGameState));
			}
		}
	}

	private void argueTheCall(Team pTeam, String[] pPlayerIds) {
		Game game = getGameState().getGame();
		TurnData turnData;
		if (game.getTeamHome() == pTeam) {
			fArgueTheCallChoiceHome = ArrayTool.isProvided(pPlayerIds);
			turnData = game.getTurnDataHome();
		} else {
			fArgueTheCallChoiceAway = ArrayTool.isProvided(pPlayerIds);
			turnData = game.getTurnDataAway();
		}
		if (ArrayTool.isProvided(pPlayerIds)) {
			for (String playerId : pPlayerIds) {
				Player<?> player = pTeam.getPlayerById(playerId);
				if ((player != null) && !turnData.isCoachBanned()) {
					int roll = getGameState().getDiceRoller().rollArgueTheCall();
					boolean successful = DiceInterpreter.getInstance().isArgueTheCallSuccessful(roll);
					boolean coachBanned = DiceInterpreter.getInstance().isCoachBanned(roll);
					getResult().addReport(new ReportArgueTheCallRoll(player.getId(), successful, coachBanned, roll, false));
					if (successful) {
						PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
						playerResult.setHasUsedSecretWeapon(false);
					}
					if (coachBanned) {
						turnData.setCoachBanned(true);
					}
				}
			}
		}
	}

	private boolean useSecretWeaponBribes(Team pTeam, String[] pPlayerIds) {
		boolean allSuccessful = true;
		Game game = getGameState().getGame();
		if (game.getTeamHome() == pTeam) {
			fBribesChoiceHome = ArrayTool.isProvided(pPlayerIds);
		} else {
			fBribesChoiceAway = ArrayTool.isProvided(pPlayerIds);
		}
		InducementSet inducementSet = (game.getTeamHome() == pTeam) ? game.getTurnDataHome().getInducementSet()
				: game.getTurnDataAway().getInducementSet();
		Optional<InducementType> bribesType = inducementSet.getInducementTypes().stream().filter(type -> type.getUsage() == Usage.AVOID_BAN).findFirst();

		if (bribesType.isPresent() && ArrayTool.isProvided(pPlayerIds)
				&& UtilServerInducementUse.useInducement(getGameState(), pTeam, bribesType.get(), pPlayerIds.length)) {
			for (String playerId : pPlayerIds) {
				Player<?> player = pTeam.getPlayerById(playerId);
				if (player != null) {
					int roll = getGameState().getDiceRoller().rollBribes();
					boolean successful = DiceInterpreter.getInstance().isBribesSuccessful(roll);
					getResult().addReport(new ReportBribesRoll(player.getId(), successful, roll));
					if (successful) {
						PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
						playerResult.setHasUsedSecretWeapon(false);
					} else {
						allSuccessful = false;
					}
				}
			}
		}
		return allSuccessful;
	}

	private void deactivateCardsAndPrayers(InducementDuration pDuration, boolean pIsHomeTurnEnding) {
		if (pDuration == null) {
			return;
		}
		deactivateCards(pDuration, pIsHomeTurnEnding);
		deactivatePrayers(pDuration, pIsHomeTurnEnding);
	}

	private void deactivatePrayers(InducementDuration duration, boolean isHomeTurnEnding) {
		Game game = getGameState().getGame();
		for (Prayer prayer : game.getTurnDataHome().getInducementSet().getPrayers()) {
			if (duration == prayer.getDuration()) {
				if (duration == InducementDuration.UNTIL_END_OF_OPPONENTS_TURN && isHomeTurnEnding) {
					continue;
				}
				deactivatePrayer(prayer, game.getTurnDataHome().getInducementSet());
			}
		}
		for (Prayer prayer : game.getTurnDataAway().getInducementSet().getPrayers()) {
			if (duration == prayer.getDuration()) {
				if (duration == InducementDuration.UNTIL_END_OF_OPPONENTS_TURN && !isHomeTurnEnding) {
					continue;
				}
				deactivatePrayer(prayer, game.getTurnDataAway().getInducementSet());
			}
		}
	}

	private void deactivatePrayer(Prayer prayer, InducementSet inducementSet) {
		inducementSet.removePrayer(prayer);
		PrayerHandlerFactory handlerFactory = getGameState().getGame().getFactory(FactoryType.Factory.PRAYER_HANDLER);
		handlerFactory.forPrayer(prayer).ifPresent(handler -> handler.removeEffect(getGameState().getGame()));
	}

	private void deactivateCards(InducementDuration pDuration, boolean pIsHomeTurnEnding) {

		Game game = getGameState().getGame();
		for (Card card : game.getTurnDataHome().getInducementSet().getActiveCards()) {
			if (pDuration == card.getDuration()) {
				if (pDuration == InducementDuration.UNTIL_END_OF_OPPONENTS_TURN && pIsHomeTurnEnding) {
					continue;
				}
				UtilServerCards.deactivateCard(this, card);
			}
		}
		for (Card card : game.getTurnDataAway().getInducementSet().getActiveCards()) {
			if (pDuration == card.getDuration()) {
				if (pDuration == InducementDuration.UNTIL_END_OF_OPPONENTS_TURN && !pIsHomeTurnEnding) {
					continue;
				}
				UtilServerCards.deactivateCard(this, card);
			}
		}
	}

	private boolean askForSecretWeaponBribes(Team team) {
		Game game = getGameState().getGame();
		List<String> playerIds = getPlayerIds(team, game);
		if (playerIds.size() > 0) {
			InducementSet inducementSet = (game.getTeamHome() == team) ? game.getTurnDataHome().getInducementSet()
					: game.getTurnDataAway().getInducementSet();
			Optional<InducementType> bribesType = inducementSet.getInducementTypes().stream().filter(type -> type.getUsage() == Usage.AVOID_BAN).findFirst();
			if (bribesType.isPresent() && inducementSet.hasUsesLeft(bribesType.get())) {
				Inducement bribes = inducementSet.get(bribesType.get());
				DialogBribesParameter dialogParameter = new DialogBribesParameter(team.getId(), bribes.getUsesLeft());
				dialogParameter.addPlayerIds(playerIds.toArray(new String[0]));
				UtilServerDialog.showDialog(getGameState(), dialogParameter,
						(game.isHomePlaying() && (team != game.getTeamHome()))
								|| (!game.isHomePlaying() && (team != game.getTeamAway())));
				return true;
			}
		}
		return false;
	}

	private boolean askForArgueTheCall(Team team) {
		Game game = getGameState().getGame();
		if (!UtilGameOption.isOptionEnabled(game, GameOptionId.ARGUE_THE_CALL)) {
			return false;
		}
		List<String> playerIds = getPlayerIds(team, game);
		if (playerIds.size() > 0) {
			TurnData turnData = (game.getTeamHome() == team) ? game.getTurnDataHome() : game.getTurnDataAway();
			if (!turnData.isCoachBanned()) {
				DialogArgueTheCallParameter dialogParameter = new DialogArgueTheCallParameter(team.getId(), false);
				dialogParameter.addPlayerIds(playerIds.toArray(new String[0]));
				UtilServerDialog.showDialog(getGameState(), dialogParameter,
						(game.isHomePlaying() && (team != game.getTeamHome()))
								|| (!game.isHomePlaying() && (team != game.getTeamAway())));
				return true;
			}
		}
		return false;
	}

	private List<String> getPlayerIds(Team team, Game game) {
		List<String> playerIds = new ArrayList<>();
		for (Player<?> player : team.getPlayers()) {
			PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
			PlayerState playerState = game.getFieldModel().getPlayerState(player);
			if (playerResult.hasUsedSecretWeapon() && (playerState.getBase() != PlayerState.BANNED)) {
				playerIds.add(player.getId());
			}
		}
		return playerIds;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.TOUCHDOWN.addTo(jsonObject, fTouchdown);
		IServerJsonOption.ARGUE_THE_CALL_CHOICE_HOME.addTo(jsonObject, fArgueTheCallChoiceHome);
		IServerJsonOption.ARGUE_THE_CALL_CHOICE_AWAY.addTo(jsonObject, fArgueTheCallChoiceAway);
		IServerJsonOption.BRIBES_CHOICE_HOME.addTo(jsonObject, fBribesChoiceHome);
		IServerJsonOption.BRIBES_CHOICE_AWAY.addTo(jsonObject, fBribesChoiceAway);
		IServerJsonOption.NEXT_SEQUENCE_PUSHED.addTo(jsonObject, fNextSequencePushed);
		IServerJsonOption.REMOVE_USED_SECRET_WEAPONS.addTo(jsonObject, fRemoveUsedSecretWeapons);
		IServerJsonOption.NEW_HALF.addTo(jsonObject, fNewHalf);
		IServerJsonOption.END_GAME.addTo(jsonObject, fEndGame);
		IServerJsonOption.WITHIN_SECRET_WEAPON_HANDLING.addTo(jsonObject, fWithinSecretWeaponHandling);
		return jsonObject;
	}

	@Override
	public StepEndTurn initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fTouchdown = IServerJsonOption.TOUCHDOWN.getFrom(source, jsonObject);
		fArgueTheCallChoiceHome = IServerJsonOption.ARGUE_THE_CALL_CHOICE_HOME.getFrom(source, jsonObject);
		fArgueTheCallChoiceAway = IServerJsonOption.ARGUE_THE_CALL_CHOICE_AWAY.getFrom(source, jsonObject);
		fBribesChoiceHome = IServerJsonOption.BRIBES_CHOICE_HOME.getFrom(source, jsonObject);
		fBribesChoiceAway = IServerJsonOption.BRIBES_CHOICE_AWAY.getFrom(source, jsonObject);
		fNextSequencePushed = IServerJsonOption.NEXT_SEQUENCE_PUSHED.getFrom(source, jsonObject);
		fRemoveUsedSecretWeapons = IServerJsonOption.REMOVE_USED_SECRET_WEAPONS.getFrom(source, jsonObject);
		fNewHalf = IServerJsonOption.NEW_HALF.getFrom(source, jsonObject);
		fEndGame = IServerJsonOption.END_GAME.getFrom(source, jsonObject);
		Boolean withinSecretWeaponHandling = IServerJsonOption.WITHIN_SECRET_WEAPON_HANDLING.getFrom(source, jsonObject);
		fWithinSecretWeaponHandling = (withinSecretWeaponHandling != null) ? withinSecretWeaponHandling : false;
		return this;
	}

}
