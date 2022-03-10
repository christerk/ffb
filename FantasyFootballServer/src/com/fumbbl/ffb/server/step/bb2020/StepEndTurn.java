package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.HeatExhaustion;
import com.fumbbl.ffb.KnockoutRecovery;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogArgueTheCallParameter;
import com.fumbbl.ffb.dialog.DialogBriberyAndCorruptionParameter;
import com.fumbbl.ffb.dialog.DialogBribesParameter;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementDuration;
import com.fumbbl.ffb.inducement.InducementPhase;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.inducement.bb2020.BriberyAndCorruptionAction;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.FieldModel;
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
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.net.commands.ClientCommandUseInducement;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.ReportBribesRoll;
import com.fumbbl.ffb.report.ReportSecretWeaponBan;
import com.fumbbl.ffb.report.bb2020.ReportArgueTheCallRoll;
import com.fumbbl.ffb.report.bb2020.ReportBriberyAndCorruptionReRoll;
import com.fumbbl.ffb.report.bb2020.ReportBrilliantCoachingReRollsLost;
import com.fumbbl.ffb.report.bb2020.ReportPrayerEnd;
import com.fumbbl.ffb.report.bb2020.ReportTurnEnd;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.PrayerState;
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
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.Inducement.SequenceParams;
import com.fumbbl.ffb.server.step.generator.common.Kickoff;
import com.fumbbl.ffb.server.util.UtilServerCards;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerInducementUse;
import com.fumbbl.ffb.server.util.UtilServerTimer;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilBox;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fumbbl.ffb.server.step.StepParameter.from;

/**
 * Step in any sequence to end a turn.
 * <p>
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
	private int turnNr, half;
	private List<String> playerIdsNaturalOnes = new ArrayList<>();
	private Set<String> playerIdsFailedBribes = new HashSet<>();

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
			boolean friendsWithTheRef = getGameState().getPrayerState().isFriendsWithRef(team);
			TurnData turnData = team == game.getTeamHome() ? game.getTurnDataHome() : game.getTurnDataAway();
			Optional<InducementType> briberyReRoll = turnData.getInducementSet().getInducementMapping().keySet()
				.stream().filter(key -> key.getUsage() == Usage.REROLL_ARGUE).findFirst();
			boolean canUseReRoll = briberyReRoll.isPresent() && turnData.getInducementSet().hasUsesLeft(briberyReRoll.get());

			switch (pReceivedCommand.getId()) {
				case CLIENT_PLAYER_CHOICE:
					ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
					if (playerChoiceCommand.getPlayerChoiceMode() == PlayerChoiceMode.BRIBERY_AND_CORRUPTION) {
						String playerId = playerChoiceCommand.getPlayerId();
						playerIdsNaturalOnes.clear();

						if (canUseReRoll && StringTool.isProvided(playerId)) {
							reRollArgue(team, friendsWithTheRef, playerId, turnData, briberyReRoll.get());
						}
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_USE_RE_ROLL:
					ClientCommandUseReRoll useReRollCommand = (ClientCommandUseReRoll) pReceivedCommand.getCommand();
					if (useReRollCommand.getReRolledAction() == ReRolledActions.ARGUE_THE_CALL) {
						String playerId = playerIdsNaturalOnes.get(0);
						playerIdsNaturalOnes.clear();

						if (useReRollCommand.getReRollSource() == ReRollSources.BRIBERY_AND_CORRUPTION && canUseReRoll) {
							reRollArgue(team, friendsWithTheRef, playerId, turnData, briberyReRoll.get());
						}

						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_ARGUE_THE_CALL:
					ClientCommandArgueTheCall argueTheCallCommand = (ClientCommandArgueTheCall) pReceivedCommand.getCommand();
					argueTheCall(team, argueTheCallCommand.getPlayerIds(), friendsWithTheRef);
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
								fBribesChoiceAway = null;
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

	private void reRollArgue(Team team, boolean friendsWithTheRef, String playerId, TurnData turnData, InducementType briberyReRoll) {
		Inducement inducement = turnData.getInducementSet().getInducementMapping().get(briberyReRoll);
		inducement.setUses(inducement.getUses() + 1);
		turnData.getInducementSet().addInducement(inducement);
		getResult().addReport(new ReportBriberyAndCorruptionReRoll(team.getId(), BriberyAndCorruptionAction.USED));
		argueTheCall(team, new String[]{playerId}, friendsWithTheRef);
	}

	private void executeStep() {

		Game game = getGameState().getGame();
		game.getFieldModel().clearMultiBlockTargets();
		UtilServerDialog.hideDialog(getGameState());
		getGameState().getPassState().setOriginalBombardier(null);

		boolean isHomeTurnEnding = game.isHomePlaying();
		if (turnNr == 0) {
			// work around as UtilServer#startHalf is currently called before weapons are removed and we need these values for sendToBoxReason
			turnNr = game.getTurnData().getTurnNr();
			half = game.getHalf();
		}

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

			if (handleStallers()) {
				return;
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
						for (Team team : new Team[]{game.getTeamHome(), game.getTeamAway()}) {
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
					GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());

					if (mechanic.touchdownEndsGame(game)) {
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

				if (fNewHalf || fTouchdown) {
					reportSecretWeaponsUsed();
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

		if (playerIdsNaturalOnes.size() == 1) {
			Team team = game.findTeam(game.getPlayerById(playerIdsNaturalOnes.get(0)));
			UtilServerDialog.showDialog(getGameState(), new DialogBriberyAndCorruptionParameter(team.getId()), false);
			return;
		} else if (!playerIdsNaturalOnes.isEmpty()) {
			Player<?>[] players = playerIdsNaturalOnes.stream().map(game::getPlayerById).toArray(Player[]::new);
			Team team = game.findTeam(players[0]);
			UtilServerDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(team.getId(), PlayerChoiceMode.BRIBERY_AND_CORRUPTION, players,
				null, 1), false);
			return;
		}

		if ((fBribesChoiceHome != null) && (fBribesChoiceAway != null) && (fArgueTheCallChoiceAway == null)) {
			fArgueTheCallChoiceAway = false;
			boolean friendsWithTheRef = getGameState().getPrayerState().isFriendsWithRef(game.getTeamAway());

			if (!fEndGame && (fNewHalf || fTouchdown) && askForArgueTheCall(game.getTeamAway(), friendsWithTheRef)) {
				fArgueTheCallChoiceAway = null;
			}
		}

		if ((fBribesChoiceHome != null) && (fBribesChoiceAway != null) && (fArgueTheCallChoiceHome == null)
			&& (fArgueTheCallChoiceAway != null)) {
			fArgueTheCallChoiceHome = false;
			boolean friendsWithTheRef = getGameState().getPrayerState().isFriendsWithRef(game.getTeamHome());

			if (!fEndGame && (fNewHalf || fTouchdown) && askForArgueTheCall(game.getTeamHome(), friendsWithTheRef)) {
				fArgueTheCallChoiceHome = null;
			}
		}

		if (fEndGame || ((fArgueTheCallChoiceHome != null) && (fArgueTheCallChoiceAway != null)
			&& (fBribesChoiceHome != null) && (fBribesChoiceAway != null))) {

			if (!fEndGame && fRemoveUsedSecretWeapons) {
				removeUsedSecretWeapons();
			}

			deactivateCardsAndPrayers(InducementDuration.UNTIL_END_OF_TURN, isHomeTurnEnding);
			deactivateCardsAndPrayers(InducementDuration.UNTIL_END_OF_OPPONENTS_TURN, isHomeTurnEnding);

			if (fNewHalf) {
				deactivateCardsAndPrayers(InducementDuration.UNTIL_END_OF_HALF, isHomeTurnEnding);
			}
			if (fNewHalf || fTouchdown) {
				deactivateCardsAndPrayers(InducementDuration.UNTIL_END_OF_DRIVE, isHomeTurnEnding);
				removeBrilliantCoachingReRolls(true);
				removeBrilliantCoachingReRolls(false);
			}

			game.startTurn();
			UtilServerGame.updatePlayerStateDependentProperties(this);

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


	private boolean handleStallers() {
		PrayerState prayerState = getGameState().getPrayerState();
		if (!fTouchdown) {
			Game game = getGameState().getGame();
			FieldModel fieldModel = game.getFieldModel();

			Optional<? extends Player<?>> staller = prayerState.getStallerIds().stream().map(game::getPlayerById)
				.filter(player -> fieldModel.getPlayerState(player).getBase() == PlayerState.STANDING).findFirst();

			if (staller.isPresent()) {
				prayerState.removeStaller(staller.get());
				getGameState().pushCurrentStepOnStack();
				Sequence sequence = new Sequence(getGameState());
				sequence.add(StepId.STALLING_PLAYER, from(StepParameterKey.PLAYER_ID, staller.get().getId()));
				sequence.add(StepId.PLACE_BALL);
				sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.HIT_PLAYER));
				sequence.add(StepId.CATCH_SCATTER_THROW_IN);
				getGameState().getStepStack().push(sequence.getSequence());
				getResult().setNextAction(StepAction.NEXT_STEP);
				return true;
			}
		}
		prayerState.clearStallers();
		return false;
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
			if (!fNewHalf || getGameState().getGame().getHalf() > 2) {
				turnData.setReRolls(Math.max(turnData.getReRolls() - reRollsBrilliantCoaching, 0));
			}
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
				playerResult.setHasUsedSecretWeapon(false);
				if (!PlayerState.REMOVED_FROM_PLAY.contains(playerState.getBase())) {
					game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.BANNED));
					playerResult.setSendToBoxByPlayerId(null);
					playerResult.setSendToBoxReason(SendToBoxReason.SECRET_WEAPON_BAN);
					playerResult.setSendToBoxTurn(turnNr);
					playerResult.setSendToBoxHalf(half);
					UtilBox.putPlayerIntoBox(game, player);
				}
			}
		}
		UtilServerGame.updatePlayerStateDependentProperties(this);
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

	private void argueTheCall(Team pTeam, String[] pPlayerIds, boolean friendsWithTheRef) {
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
			Optional<InducementType> briberyReRoll = turnData.getInducementSet().getInducementMapping().keySet().stream()
				.filter(inducement -> inducement.getUsage() == Usage.REROLL_ARGUE).findFirst();

			for (String playerId : pPlayerIds) {
				Player<?> player = pTeam.getPlayerById(playerId);
				if ((player != null) && !turnData.isCoachBanned()) {
					int roll = getGameState().getDiceRoller().rollArgueTheCall();
					int modifiedRoll = friendsWithTheRef && roll > 1 ? roll + 1 : roll;

					boolean successful = DiceInterpreter.getInstance().isArgueTheCallSuccessful(modifiedRoll);
					boolean coachBanned = DiceInterpreter.getInstance().isCoachBanned(modifiedRoll);
					getResult().addReport(new ReportArgueTheCallRoll(player.getId(), successful, coachBanned, roll, false, friendsWithTheRef));
					if (successful) {
						PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
						playerResult.setHasUsedSecretWeapon(false);
					}
					boolean canBeReRolled = roll == 1 && briberyReRoll.isPresent() && turnData.getInducementSet().hasUsesLeft(briberyReRoll.get());

					if (canBeReRolled && coachBanned) {
						reRollArgue(pTeam, friendsWithTheRef, playerId, turnData, briberyReRoll.get());
					} else if (coachBanned) {
						turnData.setCoachBanned(true);
					} else if (canBeReRolled) {
						playerIdsNaturalOnes.add(playerId);
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
						playerIdsFailedBribes.add(playerId);
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
		for (Prayer prayer : new HashSet<>(game.getTurnDataHome().getInducementSet().getPrayers())) {
			if (duration == prayer.getDuration()) {
				if (duration == InducementDuration.UNTIL_END_OF_OPPONENTS_TURN && isHomeTurnEnding) {
					continue;
				}
				deactivatePrayer(prayer, game.getTurnDataHome().getInducementSet(), game.getTeamHome());
			}
		}
		for (Prayer prayer : new HashSet<>(game.getTurnDataAway().getInducementSet().getPrayers())) {
			if (duration == prayer.getDuration()) {
				if (duration == InducementDuration.UNTIL_END_OF_OPPONENTS_TURN && !isHomeTurnEnding) {
					continue;
				}
				deactivatePrayer(prayer, game.getTurnDataAway().getInducementSet(), game.getTeamAway());
			}
		}
	}

	private void deactivatePrayer(Prayer prayer, InducementSet inducementSet, Team team) {
		inducementSet.removePrayer(prayer);
		PrayerHandlerFactory handlerFactory = getGameState().getGame().getFactory(FactoryType.Factory.PRAYER_HANDLER);
		handlerFactory.forPrayer(prayer).ifPresent(handler -> handler.removeEffect(getGameState(), team));
		getResult().addReport(new ReportPrayerEnd(prayer));
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
		List<String> playerIds = getPlayerIds(team, game).stream().filter(id -> !playerIdsFailedBribes.contains(id)).collect(Collectors.toList());
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

	private boolean askForArgueTheCall(Team team, boolean friendsWithTheRef) {
		Game game = getGameState().getGame();
		if (!UtilGameOption.isOptionEnabled(game, GameOptionId.ARGUE_THE_CALL)) {
			return false;
		}
		List<String> playerIds = getPlayerIds(team, game);
		if (playerIds.size() > 0) {
			TurnData turnData = (game.getTeamHome() == team) ? game.getTurnDataHome() : game.getTurnDataAway();
			if (!turnData.isCoachBanned()) {
				DialogArgueTheCallParameter dialogParameter = new DialogArgueTheCallParameter(team.getId(), false, friendsWithTheRef);
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
			if (playerResult.hasUsedSecretWeapon() && !PlayerState.REMOVED_FROM_PLAY.contains(playerState.getBase())) {
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
		IServerJsonOption.HALF.addTo(jsonObject, half);
		IServerJsonOption.TURN_NR.addTo(jsonObject, turnNr);
		IServerJsonOption.PLAYER_IDS_NATURAL_ONES.addTo(jsonObject, playerIdsNaturalOnes);
		IServerJsonOption.PLAYER_IDS_FAILED_BRIBE.addTo(jsonObject, playerIdsFailedBribes);
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
		half = IServerJsonOption.HALF.getFrom(source, jsonObject);
		turnNr = IServerJsonOption.TURN_NR.getFrom(source, jsonObject);
		playerIdsNaturalOnes = Arrays.stream(IServerJsonOption.PLAYER_IDS_NATURAL_ONES.getFrom(source, jsonObject)).collect(Collectors.toList());
		String[] failedBribes = IServerJsonOption.PLAYER_IDS_FAILED_BRIBE.getFrom(source, jsonObject);
		if (ArrayTool.isProvided(failedBribes)) {
			playerIdsFailedBribes = Arrays.stream(failedBribes).collect(Collectors.toSet());
		}
		return this;
	}

}
