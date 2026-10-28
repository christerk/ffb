package com.fumbbl.ffb.server.step.bb2025.mutliblock;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryStatus;
import com.fumbbl.ffb.ApothecaryType;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.KeywordChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PositionChoiceMode;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.bb2020.InjuryDescription;
import com.fumbbl.ffb.dialog.DialogApothecaryChoiceParameter;
import com.fumbbl.ffb.dialog.DialogReRollRegenerationMultipleParameter;
import com.fumbbl.ffb.dialog.DialogSelectKeywordParameter;
import com.fumbbl.ffb.dialog.DialogSelectPositionParameter;
import com.fumbbl.ffb.dialog.DialogUseApothecariesParameter;
import com.fumbbl.ffb.dialog.DialogUseInducementParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.json.JsonArrayOption;
import com.fumbbl.ffb.json.JsonIntArrayOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.InjuryMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandApothecaryChoice;
import com.fumbbl.ffb.net.commands.ClientCommandKeywordSelection;
import com.fumbbl.ffb.net.commands.ClientCommandPositionSelection;
import com.fumbbl.ffb.net.commands.ClientCommandUseApothecary;
import com.fumbbl.ffb.net.commands.ClientCommandUseInducement;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRollForTarget;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.ReportApothecaryChoice;
import com.fumbbl.ffb.report.ReportInducement;
import com.fumbbl.ffb.report.mixed.ReportApothecaryRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.ReRollOptions;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.server.mechanic.StateMechanic;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerInducementUse;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.server.util.bb2025.KnockoutReserveService;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.RaiseType;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepApothecaryMultiple extends AbstractStep {

	private static final Set<ApothecaryStatus> KNOCKOUT_RESERVE_STATUSES = EnumSet.of(
		ApothecaryStatus.WAIT_FOR_PASTRIES_USE, ApothecaryStatus.DO_NOT_USE_PASTRIES, ApothecaryStatus.USE_PASTRIES);
	private static final Set<ApothecaryStatus> APOTHECARY_TREATED_STATUSES = EnumSet.of(
		ApothecaryStatus.USE_APOTHECARY, ApothecaryStatus.WAIT_FOR_APOTHECARY_CHOICE, ApothecaryStatus.RESULT_CHOICE);

	private List<InjuryResult> injuryResults = new ArrayList<>(), regenerationFailedResults,
		gettingEvenResults = new ArrayList<>(), deadResults = new ArrayList<>();
	private String teamId;
	private Map<String, List<Keyword>> availableKeyWordsMap = new HashMap<>();

	public StepApothecaryMultiple(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.APOTHECARY_MULTIPLE;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				// mandatory
				if (parameter.getKey() == StepParameterKey.ACTING_TEAM) {
					boolean handleActingTeam = parameter.getValue() != null && (boolean) parameter.getValue();
					Team actingTeam = getGameState().getGame().getActingTeam();
					teamId = handleActingTeam ? actingTeam.getId() : getGameState().getGame().getOtherTeam(actingTeam).getId();
				}
			}
		}
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		Game game = getGameState().getGame();
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_USE_INDUCEMENT:
					ClientCommandUseInducement clientCommandUseInducement =
						(ClientCommandUseInducement) pReceivedCommand.getCommand();
					Optional<InjuryResult> knockoutReserveResult = injuryWaitingForKnockoutReserve();
					if (knockoutReserveResult.isPresent()) {
						handleKnockoutReserve(knockoutReserveResult.get(), clientCommandUseInducement.getInducementType());
					} else if (ArrayTool.isProvided(clientCommandUseInducement.getPlayerIds())) {
						regenerationFailedResults.stream().filter(
								result -> result.injuryContext().getDefenderId().equals(clientCommandUseInducement.getPlayerIds()[0]))
							.findFirst().ifPresent(result -> {
								boolean doRoll = false;
								Player<?> player = injuredPlayer(result, game);
								InducementType inducementType = clientCommandUseInducement.getInducementType();
								if (inducementType != null) {
									if (UtilServerInducementUse.useInducement(inducementType, 1, getTurnData().getInducementSet())) {
										if (inducementType.hasUsage(Usage.APOTHECARY_JOURNEYMEN)) {
											TurnData turnData =
												game.getTeamHome().hasPlayer(player) ? game.getTurnDataHome() : game.getTurnDataAway();
											turnData.useApothecary(ApothecaryType.PLAGUE);
										}

										getResult().addReport(new ReportInducement(player.getTeam().getId(), inducementType, 1));
										doRoll = true;
									}
								} else {
									doRoll = UtilServerReRoll.useReRoll(this, ReRollSources.TEAM_RE_ROLL, player);
								}
								if (doRoll) {
									if (UtilServerInjury.handleRegeneration(this, player, result.injuryContext().getPlayerState(), true)) {
										result.injuryContext().setInjury(game.getFieldModel().getPlayerState(player));
										result.injuryContext().setApothecaryStatus(ApothecaryStatus.NO_APOTHECARY);
										result.injuryContext().setSeriousInjury(null);
										regenerationFailedResults.remove(result);
									}
								}
								result.passedRegeneration();
							});

					} else {
						regenerationFailedResults.forEach(InjuryResult::passedRegeneration);
					}
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_USE_RE_ROLL:
					ClientCommandUseReRoll clientCommandUseReRoll = (ClientCommandUseReRoll) pReceivedCommand.getCommand();
					if (clientCommandUseReRoll.getReRolledAction() == ReRolledActions.REGENERATION) {
						ReRollSource reRollSource = clientCommandUseReRoll.getReRollSource();

						Optional<InjuryResult> preRegen =
							regenerationFailedResults.stream().filter(InjuryResult::isPreRegeneration).findFirst();
						if (preRegen.isPresent()) {
							InjuryResult result = preRegen.get();
							rerollRegen(game, reRollSource, result);
							result.passedRegeneration();
							commandStatus = StepCommandStatus.EXECUTE_STEP;
						}
					}
					break;
				case CLIENT_USE_RE_ROLL_FOR_TARGET:
					ClientCommandUseReRollForTarget clientCommandUseReRollForTarget =
						(ClientCommandUseReRollForTarget) pReceivedCommand.getCommand();
					if (clientCommandUseReRollForTarget.getReRolledAction() == ReRolledActions.REGENERATION) {
						String targetId = clientCommandUseReRollForTarget.getTargetId();
						ReRollSource targetReRollSource = clientCommandUseReRollForTarget.getReRollSource();
						if (targetId != null && targetReRollSource != null) {
							regenerationFailedResults.stream()
								.filter(result -> result.injuryContext().getDefenderId().equals(targetId))
								.findFirst().ifPresent(result -> {
									rerollRegen(game, targetReRollSource, result);
									result.passedRegeneration();
								});
						} else {
							regenerationFailedResults.forEach(InjuryResult::passedRegeneration);
						}
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_USE_SKILL:
					ClientCommandUseSkill commandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
					if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canRerollSingleDieOncePerPeriod)) {
						Optional<InjuryResult> preRegen =
							regenerationFailedResults.stream().filter(InjuryResult::isPreRegeneration).filter(
								injuryResult -> injuryResult.injuryContext().fDefenderId.equalsIgnoreCase(
									commandUseSkill.getPlayerId())).findFirst();
						if (preRegen.isPresent()) {
							InjuryResult result = preRegen.get();
							if (commandUseSkill.isSkillUsed()) {
								rerollRegen(game, commandUseSkill.getSkill().getRerollSource(ReRolledActions.SINGLE_DIE), result);
							}
							result.passedRegeneration();
							commandStatus = StepCommandStatus.EXECUTE_STEP;
						}
					}
					break;
				case CLIENT_APOTHECARY_CHOICE:
					ClientCommandApothecaryChoice apothecaryChoiceCommand = (ClientCommandApothecaryChoice) pReceivedCommand
						.getCommand();
					Optional<InjuryResult> choiceResult = injuryResults.stream().filter(injuryResult ->
							injuryResult.injuryContext().getDefenderId().equals(apothecaryChoiceCommand.getPlayerId())
								&& injuryResult.injuryContext().getPlayerState().equals(apothecaryChoiceCommand.getOldPlayerState())
								&& injuryResult.injuryContext().getApothecaryStatus() == ApothecaryStatus.WAIT_FOR_APOTHECARY_CHOICE
						)
						.findFirst();
					if (choiceResult.isPresent()) {
						handleApothecaryChoice(choiceResult.get(), apothecaryChoiceCommand.getPlayerState(),
							apothecaryChoiceCommand.getSeriousInjury());
						regenerationFailedResults.remove(choiceResult.get());
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_USE_APOTHECARIES:
					injuryResults.stream()
						.filter(injuryResult -> injuryResult.injuryContext().fApothecaryStatus ==
							ApothecaryStatus.WAIT_FOR_APOTHECARY_USE)
						.forEach(
							injuryResult -> injuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.DO_NOT_USE_APOTHECARY));
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_USE_APOTHECARY:
					ClientCommandUseApothecary useApothecaryCommand = (ClientCommandUseApothecary) pReceivedCommand.getCommand();
					injuryResults.stream()
						.filter(injuryResult ->
							injuryResult.injuryContext().fApothecaryStatus == ApothecaryStatus.WAIT_FOR_APOTHECARY_USE
								&& injuryResult.injuryContext().getDefenderId().equals(useApothecaryCommand.getPlayerId())
								&& useApothecaryCommand.getSeriousInjury() == injuryResult.injuryContext().getSeriousInjury()
								&& injuryResult.injuryContext().getPlayerState().equals(useApothecaryCommand.getPlayerState()))
						.findFirst().ifPresent(injuryResult -> {
							ApothecaryStatus newStatus;

							if (useApothecaryCommand.isApothecaryUsed()) {
								int remainingApos = remainingApos();
								if (remainingApos <= 0) {
									newStatus = ApothecaryStatus.DO_NOT_USE_APOTHECARY;
								} else {
									newStatus = ApothecaryStatus.USE_APOTHECARY;

									Player<?> defender = game.getPlayerById(injuryResult.injuryContext().getDefenderId());
									ApothecaryType apothecaryType = useApothecaryCommand.getApothecaryType();
									if (game.getTeamHome().hasPlayer(defender)) {
										useApo(game.getTurnDataHome(), apothecaryType);
									} else {
										useApo(game.getTurnDataAway(), apothecaryType);
									}
								}
							} else {
								newStatus = ApothecaryStatus.DO_NOT_USE_APOTHECARY;
							}

							injuryResult.injuryContext().setApothecaryStatus(newStatus);

						});
					injuryResults.stream()
						.filter(result -> result.injuryContext().getApothecaryStatus() == ApothecaryStatus.WAIT_FOR_APOTHECARY_USE)
						.forEach(result -> result.injuryContext().setApothecaryStatus(ApothecaryStatus.DO_REQUEST));
					commandStatus = StepCommandStatus.EXECUTE_STEP;

					break;
				case CLIENT_KEYWORD_SELECTION:
					UtilServerDialog.hideDialog(getGameState());
					ClientCommandKeywordSelection commandKeywordSelection =
						(ClientCommandKeywordSelection) pReceivedCommand.getCommand();
					if (!gettingEvenResults.isEmpty() &&
						gettingEvenResults.get(0).injuryContext().getDefenderId().equals(commandKeywordSelection.getPlayerId())) {
						List<Keyword> selectedKeywords = commandKeywordSelection.getKeywords();
						Collections.reverse(selectedKeywords);
						selectedKeywords.forEach(keyword -> pushGettingEven(commandKeywordSelection.getPlayerId(), keyword));
						gettingEvenResults.remove(0);

						checkGettingEven();
						commandStatus = StepCommandStatus.SKIP_STEP;
					}
					break;
				case CLIENT_POSITION_SELECTION:
					UtilServerDialog.hideDialog(getGameState());
					ClientCommandPositionSelection commandPositionSelection =
						(ClientCommandPositionSelection) pReceivedCommand.getCommand();
					commandStatus = StepCommandStatus.SKIP_STEP;
					Team teamById = game.getTeamById(commandPositionSelection.getTeamId());
					InjuryResult deadResult = deadResults.remove(0);
					RosterPosition position = teamById.getRoster().getPositionById(commandPositionSelection.getPosition()[0]);
					TeamResult teamResult = teamById == game.getTeamHome() ? game.getGameResult().getTeamResultHome()
						: game.getGameResult().getTeamResultAway();
					raisePlayer(deadResult, game, teamById, teamResult, position);
					checkRaiseDead();
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

	private void rerollRegen(Game game, ReRollSource reRollSource, InjuryResult result) {
		Player<?> player = injuredPlayer(result, game);

		if (UtilServerReRoll.useReRoll(this, reRollSource, player)) {
			if (UtilServerInjury.handleRegeneration(this, player, result.injuryContext().getPlayerState(),
				true)) {
				result.injuryContext().setInjury(game.getFieldModel().getPlayerState(player));
				result.injuryContext().setApothecaryStatus(ApothecaryStatus.NO_APOTHECARY);
				result.injuryContext().setSeriousInjury(null);
				regenerationFailedResults.remove(result);
			}
		}
	}

	private void checkGettingEven() {
		Game game = getGameState().getGame();
		if (gettingEvenResults.isEmpty()) {
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			InjuryResult injuryResult = gettingEvenResults.get(0);
			String defenderId = injuryResult.injuryContext().fDefenderId;
			showGettingEvenDialog(game.getPlayerById(defenderId),
				availableKeyWordsMap.get(String.valueOf(injuryResults.indexOf(injuryResult))), game);
			getResult().setNextAction(StepAction.CONTINUE);
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			if (parameter.getKey() == StepParameterKey.INJURY_RESULT) {
				InjuryResult injuryResult = (InjuryResult) parameter.getValue();
				if (injuryResult != null) {
					String defenderId = injuryResult.injuryContext().getDefenderId();
					if (teamId.equals(getGameState().getGame().getPlayerById(defenderId).getTeam().getId())) {
						injuryResults.add(injuryResult);
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}

	private void executeStep() {
		getResult().setNextAction(StepAction.NEXT_STEP);
		if (injuryResults.isEmpty()) {
			return;
		}
		UtilServerDialog.hideDialog(getGameState());

		if (!regenerationHandled()) {
			getResult().setNextAction(StepAction.CONTINUE);
			return;
		}

		// only report cas rolls, first report happens in regenerationHandled
		regenerationFailedResults.forEach(result -> result.report(this));

		Game game = getGameState().getGame();
		Map<ApothecaryStatus, List<InjuryResult>> groupedInjuries = regenerationFailedResults.stream()
			.collect(Collectors.groupingBy(injuryResult -> injuryResult.injuryContext().getApothecaryStatus()));

		List<InjuryResult> doRequest = groupedInjuries.get(ApothecaryStatus.DO_REQUEST);
		if (doRequest != null && !doRequest.isEmpty()) {
			List<InjuryDescription> injuryDescriptions = new ArrayList<>();
			int remainingApos = remainingApos();
			doRequest.forEach(injuryResult -> {
				injuryResult.report(this);
				UtilServerGame.syncGameModel(this);
				InjuryContext injuryContext = injuryResult.injuryContext();
				List<ApothecaryType> apothecaryTypes =
					ApothecaryType.forPlayer(game, game.getPlayerById(injuryContext.fDefenderId),
						injuryContext.getPlayerState());
				if (remainingApos > 0 && !apothecaryTypes.isEmpty()) {
					injuryContext.setApothecaryStatus(ApothecaryStatus.WAIT_FOR_APOTHECARY_USE);
					injuryDescriptions.add(new InjuryDescription(injuryContext.getDefenderId(), injuryContext.getPlayerState(),
						injuryContext.fSeriousInjury, apothecaryTypes));
				} else {
					injuryContext.setApothecaryStatus(ApothecaryStatus.DO_NOT_USE_APOTHECARY);
				}
			});

			if (!injuryDescriptions.isEmpty()) {
				UtilServerDialog.showDialog(getGameState(), new DialogUseApothecariesParameter(teamId, injuryDescriptions),
					true);
				getResult().setNextAction(StepAction.CONTINUE);
				return;
			}
		}

		List<InjuryResult> useApo = groupedInjuries.get(ApothecaryStatus.USE_APOTHECARY);

		if (useApo != null && !useApo.isEmpty()) {
			for (InjuryResult injuryResult : useApo) {
				if (rollApothecary(injuryResult)) {
					injuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.WAIT_FOR_APOTHECARY_CHOICE);
					return;
				} else {
					injuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.RESULT_CHOICE);
					regenerationFailedResults.remove(injuryResult);
				}
			}
		}


		if (offerKnockoutReserve()) {
			return;
		}

		boolean doubleAttackerDown = injuryResults.size() == 2 && game.getTeamById(teamId) == game.getActingTeam();
		// this only happens in case of a double attacker down
		if (doubleAttackerDown) {
			if (regenerationFailedResults.isEmpty()) {
				Optional<InjuryResult> knockoutReserveResult = successfulKnockoutReserve();
				if (knockoutReserveResult.isPresent()) {
					// successful pastries decide the outcome for the player
					knockoutReserveResult.get().applyTo(this, true);
					UtilServerGame.syncGameModel(this);
				} else if (injuryResults.stream().allMatch(result -> result.injuryContext().isReserve())) {
					injuryResults.get(0).applyTo(this, true);
					UtilServerGame.syncGameModel(this);
				}
			} else {
				// reset the player states again to make sure we have defined base state to reapply the injuries
				Player<?> player = game.getPlayerById(regenerationFailedResults.get(0).injuryContext().getDefenderId());
				PlayerState playerState = game.getFieldModel().getPlayerState(player);
				game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.PRONE));
				GameResult gameResult = game.getGameResult();
				PlayerResult playerResult = gameResult.getPlayerResult(player);
				playerResult.setSeriousInjury(null);
				playerResult.setSeriousInjuryDecay(null);

				injuryResults.stream().filter(regenerationFailedResults::contains)
					.forEach(injuryResult -> {
						injuryResult.applyTo(this, true);
						UtilServerGame.syncGameModel(this);
					});
			}
		} else {

			injuryResults
				.forEach(injuryResult -> {
					injuryResult.applyTo(this, true);
					UtilServerGame.syncGameModel(this);
				});
		}

		InjuryMechanic injuryMechanic = game.getMechanic(Mechanic.Type.INJURY);
		StateMechanic stateMechanic = game.getMechanic(Mechanic.Type.STATE);

		for (InjuryResult injuryResult : regenerationFailedResults) {
			if (stateMechanic.handlePumpUp(this, injuryResult)) {
				UtilServerGame.syncGameModel(this);
			}

			if (injuryResult.injuryContext().getPlayerState().isSi()
				&& UtilGameOption.isOptionEnabled(game, GameOptionId.ENABLE_GETTING_EVEN)) {
				Player<?> defender = game.getPlayerById(injuryResult.injuryContext().getDefenderId());
				Player<?> attacker = game.getPlayerById(injuryResult.injuryContext().getAttackerId());
				Set<Keyword> availableKeywords =
					attacker.getPosition().getKeywords().stream().filter(Keyword::isCanGetEvenWith).collect(Collectors.toSet());

				UtilCards.getSkillWithProperty(defender, NamedProperties.canRerollSingleSkull).ifPresent(
					skill -> skill.evaluator().values(skill, defender).stream().map(Keyword::forName)
						.forEach(availableKeywords::remove)
				);

				if (!availableKeywords.isEmpty()) {
					gettingEvenResults.add(injuryResult);
					List<Keyword> keywords = availableKeywords.stream().sorted().collect(Collectors.toList());
					availableKeyWordsMap.put(String.valueOf(injuryResults.indexOf(injuryResult)), keywords);
				}
			}
		}

		Set<Team> raisingTeams = new HashSet<>();
		for (InjuryResult injuryResult : regenerationFailedResults) {
			if (injuryResult.injuryContext().getPlayerState().getBase() == PlayerState.RIP) {
				gettingEvenResults.removeIf(result -> result.injuryContext().getDefenderId()
					.equalsIgnoreCase(injuryResult.injuryContext().getDefenderId()));
				Player<?> defender = game.getPlayerById(injuryResult.injuryContext().getDefenderId());
				Team raisingTeam = game.getOtherTeam(defender.getTeam());
				TeamResult raisingTeamResult =
					raisingTeam == game.getTeamHome() ? game.getGameResult().getTeamResultHome() : game.getGameResult()
						.getTeamResultAway();
				Player<?> attacker = game.getPlayerById(injuryResult.injuryContext().getAttackerId());
				if (!raisingTeams.contains(raisingTeam) &&
					(injuryMechanic.canRaiseDead(raisingTeam, raisingTeamResult, defender) ||
						injuryMechanic.canRaiseInfectedPlayers(raisingTeam, raisingTeamResult, attacker, defender))) {
					deadResults.add(injuryResult);
					raisingTeams.add(raisingTeam);
				}
			}
		}

		for (InjuryResult gettingEven : gettingEvenResults.toArray(new InjuryResult[0])) {
			List<Keyword> keywords = availableKeyWordsMap.get(String.valueOf(injuryResults.indexOf(gettingEven)));
			if (keywords.size() == 1) {
				pushGettingEven(gettingEven.injuryContext().getDefenderId(), keywords.get(0));
				gettingEvenResults.remove(gettingEven);
			}
		}

		if (deadResults.isEmpty()) {
			checkGettingEven();
		} else {
			for (InjuryResult deadResult : deadResults.toArray(new InjuryResult[0])) {
				Team raisingTeam = game.getOtherTeam(game.getPlayerById(deadResult.injuryContext().getDefenderId())
					.getTeam());
				TeamResult raisingTeamResult =
					raisingTeam == game.getTeamHome() ? game.getGameResult().getTeamResultHome() : game.getGameResult()
						.getTeamResultAway();
				List<RosterPosition> raisePositions = injuryMechanic.raisePositions(raisingTeam);
				if (raisePositions.size() < 2) {
					deadResults.remove(deadResult);
					if (raisePositions.size() == 1) {
						raisePlayer(deadResult, game, raisingTeam, raisingTeamResult, raisePositions.get(0));
					}
				}
			}
			checkRaiseDead();
		}
	}

	private boolean offerKnockoutReserve() {
		KnockoutReserveService knockoutReserveService = new KnockoutReserveService();
		Optional<InducementType> inducementType =
			knockoutReserveService.availableInducement(getTurnData().getInducementSet());
		if (!inducementType.isPresent()) {
			return false;
		}
		Optional<InjuryResult> knockedOutResult = regenerationFailedResults.stream()
			.filter(result -> knockoutReserveService.isEligible(result)
				&& canBeMovedToReserves(result))
			.findFirst();
		if (knockedOutResult.isPresent()) {
			InjuryResult result = knockedOutResult.get();
			knockoutReserveService.offer(this, result, injuredPlayer(result, getGameState().getGame()),
				inducementType.get());
			return true;
		}
		return false;
	}

	private Optional<InjuryResult> injuryWaitingForKnockoutReserve() {
		if (regenerationFailedResults == null) {
			return Optional.empty();
		}
		return regenerationFailedResults.stream()
			.filter(result -> result.injuryContext().getApothecaryStatus() == ApothecaryStatus.WAIT_FOR_PASTRIES_USE)
			.findFirst();
	}

	private void handleKnockoutReserve(InjuryResult result, InducementType inducementType) {
		boolean successful = new KnockoutReserveService().use(this, result, injuredPlayer(result, getGameState().getGame()),
			inducementType, getTurnData().getInducementSet());
		if (successful) {
			// the pastries decide the outcome for that player, so none of his other injuries is re-applied afterwards
			String defenderId = result.injuryContext().getDefenderId();
			regenerationFailedResults.removeIf(other -> defenderId.equals(other.injuryContext().getDefenderId()));
		}
	}

	/**
	 * The pastries are offered for a knocked out player at most once in this step, and not at all if the apothecary
	 * treated one of his injuries or if another of his injuries is a casualty that decides his outcome anyway.
	 */
	private boolean canBeMovedToReserves(InjuryResult knockedOutResult) {
		String defenderId = knockedOutResult.injuryContext().getDefenderId();
		return allInjuryResults()
			.filter(result -> defenderId.equals(result.injuryContext().getDefenderId()))
			.noneMatch(result -> {
				ApothecaryStatus status = result.injuryContext().getApothecaryStatus();
				return KNOCKOUT_RESERVE_STATUSES.contains(status) || APOTHECARY_TREATED_STATUSES.contains(status)
					|| ((result != knockedOutResult) && regenerationFailedResults.contains(result)
					&& result.injuryContext().isCasualty());
			});
	}

	private Optional<InjuryResult> successfulKnockoutReserve() {
		return allInjuryResults().filter(result ->
				result.injuryContext().getApothecaryStatus() == ApothecaryStatus.USE_PASTRIES
					&& result.injuryContext().isReserve())
			.findFirst();
	}

	private Stream<InjuryResult> allInjuryResults() {
		return Stream.concat(injuryResults.stream(),
			regenerationFailedResults == null ? Stream.empty() : regenerationFailedResults.stream());
	}

	private void checkRaiseDead() {
		if (deadResults.isEmpty()) {
			checkGettingEven();
		} else {
			InjuryResult deadResult = deadResults.get(0);
			Player<?> defender = getGameState().getGame().getPlayerById(deadResult.injuryContext().getDefenderId());
			Team raisingTeam = getGameState().getGame().getOtherTeam(defender.getTeam());

			InjuryMechanic injuryMechanic = getGameState().getGame().getMechanic(Mechanic.Type.INJURY);
			List<String> raisePositions =
				injuryMechanic.raisePositions(raisingTeam).stream().map(RosterPosition::getId)
					.collect(Collectors.toList());

			UtilServerDialog.showDialog(getGameState(),
				new DialogSelectPositionParameter(raisePositions, PositionChoiceMode.RAISE_DEAD, 1, 1, raisingTeam.getId()),
				true);

			getResult().setNextAction(StepAction.CONTINUE);
		}
	}

	private void raisePlayer(InjuryResult deadResult, Game game, Team raisingTeam, TeamResult raisingTeamResult,
		RosterPosition raisePosition) {
		InjuryMechanic injuryMechanic = game.getMechanic(Mechanic.Type.INJURY);
		Player<?> defender = game.getPlayerById(deadResult.injuryContext().getDefenderId());
		RaiseType raiseType = injuryMechanic.raiseType(raisingTeam);
		RosterPlayer raisedPlayer = UtilServerInjury.raisePlayer(game, raisingTeam, raisingTeamResult, defender.getName(),
			raiseType, defender.getId(), raisePosition);
		UtilServerInjury.sendRaisedPlayer(this, getGameState(), raisingTeam, raisedPlayer, raiseType == RaiseType.ROTTER);
	}

	private void pushGettingEven(String defenderId, Keyword keyword) {
		Sequence sequence = new Sequence(getGameState());
		sequence.add(StepId.GETTING_EVEN, StepParameter.from(StepParameterKey.PLAYER_ID, defenderId),
			StepParameter.from(StepParameterKey.KEYWORD, keyword));
		getGameState().getStepStack().push(sequence.getSequence());
	}

	private void showGettingEvenDialog(Player<?> defender, List<Keyword> keywords, Game game) {
		UtilServerDialog.showDialog(getGameState(),
			new DialogSelectKeywordParameter(defender.getId(), keywords, KeywordChoiceMode.GETTING_EVEN, 1, 1),
			!game.getActingTeam().hasPlayer(defender));
		getResult().setNextAction(StepAction.CONTINUE);
	}

	private void useApo(TurnData turnData, ApothecaryType apothecaryType) {
		turnData.useApothecary(apothecaryType);
		if (apothecaryType == ApothecaryType.PLAGUE) {
			turnData.getInducementSet().getInducementTypes().stream()
				.filter(inducementType -> inducementType.hasUsage(Usage.REGENERATION) &&
					inducementType.hasUsage(Usage.APOTHECARY_JOURNEYMEN) &&
					turnData.getInducementSet().hasUsesLeft(inducementType))
				.findFirst().ifPresent(
					inducementType -> UtilServerInducementUse.useInducement(inducementType, 1, turnData.getInducementSet()));
		}
	}

	private int remainingApos() {
		TurnData turnData = getTurnData();
		return turnData.getApothecaries() + turnData.getPlagueDoctors();
	}

	private TurnData getTurnData() {
		Game game = getGameState().getGame();
		return game.getTeamById(teamId) == game.getTeamHome() ? game.getTurnDataHome() : game.getTurnDataAway();
	}

	private boolean rollApothecary(InjuryResult injuryResult) {
		Game game = getGameState().getGame();
		Player<?> defender = game.getPlayerById(injuryResult.injuryContext().getDefenderId());
		boolean apothecaryChoice = ((injuryResult.injuryContext().getPlayerState().getBase() != PlayerState.BADLY_HURT)
			&& (injuryResult.injuryContext().getPlayerState().getBase() != PlayerState.KNOCKED_OUT));
		if (apothecaryChoice) {
			RollMechanic rollMechanic =
				((RollMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name()));
			InjuryResult newInjuryResult = new InjuryResult();
			newInjuryResult.injuryContext().setDefenderId(injuryResult.injuryContext().getDefenderId());
			newInjuryResult.injuryContext().setCasualtyRoll(rollMechanic.rollCasualty(getGameState().getDiceRoller()));
			newInjuryResult.injuryContext().setInjury(
				rollMechanic.interpretCasualtyRollAndAddModifiers(game, newInjuryResult.injuryContext(),
					game.getPlayerById(injuryResult.injuryContext().getDefenderId()), false));
			newInjuryResult.injuryContext().setSeriousInjury(
				rollMechanic.interpretSeriousInjuryRoll(game, newInjuryResult.injuryContext()));
			apothecaryChoice = (newInjuryResult.injuryContext().getPlayerState().getBase() != PlayerState.BADLY_HURT);
			getResult()
				.addReport(new ReportApothecaryRoll(defender.getId(), newInjuryResult.injuryContext().getCasualtyRoll(),
					newInjuryResult.injuryContext().getPlayerState(), newInjuryResult.injuryContext().getSeriousInjury(),
					newInjuryResult.injuryContext().getOriginalSeriousInjury(), injuryResult.injuryContext().casualtyModifiers));
			if (apothecaryChoice) {
				UtilServerDialog.showDialog(getGameState(),
					new DialogApothecaryChoiceParameter(defender.getId(), injuryResult.injuryContext().getPlayerState(),
						injuryResult.injuryContext().getSeriousInjury(), newInjuryResult.injuryContext().getPlayerState(),
						newInjuryResult.injuryContext().getSeriousInjury()),
					true);
				getResult().setNextAction(StepAction.CONTINUE);
			}
		}
		if (!apothecaryChoice) {
			injuryResult.injuryContext().setSeriousInjury(null);
			if ((injuryResult.injuryContext().getPlayerState().getBase() == PlayerState.KNOCKED_OUT)
				&& (injuryResult.injuryContext().getInjuryType().canApoKoIntoStun())) {
				injuryResult.injuryContext().setInjury(new PlayerState(PlayerState.STUNNED));
			} else {
				injuryResult.injuryContext().setInjury(new PlayerState(PlayerState.RESERVE));
			}
			getResult().addReport(
				new ReportApothecaryChoice(defender.getId(), injuryResult.injuryContext().getPlayerState(), null));
		}
		return apothecaryChoice;
	}

	private void handleApothecaryChoice(InjuryResult injuryResult, PlayerState pPlayerState,
		SeriousInjury pSeriousInjury) {
		injuryResult.injuryContext().setInjury(pPlayerState);
		injuryResult.injuryContext().setSeriousInjury(pSeriousInjury);
		injuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.RESULT_CHOICE);
		regenerationFailedResults.remove(injuryResult);
	}

	private boolean regenerationHandled() {
		Game game = getGameState().getGame();

		List<InjuryResult> failsToProcess = new ArrayList<>();

		if (regenerationFailedResults == null) {
			regenerationFailedResults = new ArrayList<>();
			injuryResults.forEach(result -> {
				// report injuries without cas rolls
				result.report(this);
				// reset reported state, we only execute this line once and after regeneration is handled
				// the execute method will report injuries again but only the cas rolls then,
				// because preRegeneration will be set to false
				result.setAlreadyReported(false);
			});

			Map<Boolean, List<InjuryResult>> regenerationGroups =
				injuryResults.stream().filter(InjuryResult::isPreRegeneration).collect(Collectors.groupingBy(result -> {
					Player<?> defender = game.getPlayerById(result.injuryContext().getDefenderId());
					return defender.hasSkillProperty(NamedProperties.canRollToSaveFromInjury) &&
						result.injuryContext().getInjuryType().canUseApo();
				}));

			regenerationGroups.getOrDefault(false, new ArrayList<>()).forEach(result -> {
				regenerationFailedResults.add(result);
				result.passedRegeneration();
			});

			regenerationGroups.getOrDefault(true, new ArrayList<>()).forEach(result -> {
				Player<?> player = injuredPlayer(result, game);
				PlayerState playerState = result.injuryContext().getPlayerState();

				if (result.injuryContext().isCasualty() && UtilServerInjury.handleRegeneration(this, player, playerState)) {
					result.injuryContext().setInjury(game.getFieldModel().getPlayerState(player));
					result.injuryContext().setSeriousInjury(null);
					result.passedRegeneration();
					result.injuryContext().setApothecaryStatus(ApothecaryStatus.NO_APOTHECARY);
				} else if (result.injuryContext().isCasualty() && regenerationReRollsAvailable(player)) {
					regenerationFailedResults.add(result);
					failsToProcess.add(result);
				} else {
					regenerationFailedResults.add(result);
					result.passedRegeneration();
				}
			});
		} else {
			failsToProcess.addAll(regenerationFailedResults.stream().filter(result -> {
				Player<?> player = injuredPlayer(result, game);
				return result.isPreRegeneration() && regenerationReRollsAvailable(player);
			}).collect(Collectors.toList()));
		}

		if (failsToProcess.isEmpty()) {
			return true;
		}

		boolean askForSingleInjury = failsToProcess.size() == 1;

		if (askForSingleInjury) {
			return handleSingleRegenReRoll(failsToProcess, game);
		} else {
			InjuryResult firstResult = failsToProcess.get(0);
			Player<?> player = injuredPlayer(firstResult, game);
			Team team = player.getTeam();

			List<String> playerIds =
				failsToProcess.stream().map(result -> result.injuryContext().getDefenderId()).collect(
					Collectors.toList());

			InducementType inducement = regenerationInducementType().orElse(null);
			if (inducement == null) {
				RollMechanic rollMechanic = getGameState().getGame().getMechanic(Mechanic.Type.ROLL);
				List<ReRollOptions> options = playerIds.stream().map(id -> rollMechanic.findReRollOptions(getGameState(),
					game.getPlayerById(id), ReRolledActions.REGENERATION, null)).collect(Collectors.toList());

				UtilServerDialog.showDialog(getGameState(),
					new DialogReRollRegenerationMultipleParameter(playerIds, options),
					team != game.getActingTeam());

			} else {
				UtilServerDialog.showDialog(getGameState(),
					new DialogReRollRegenerationMultipleParameter(playerIds, inducement),
					team != game.getActingTeam());
			}
		}

		return false;
	}

	private boolean regenerationReRollsAvailable(Player<?> player) {
		InducementSet inducementSet = getTurnData().getInducementSet();
		List<InducementType> regenerationTypes = regenerationTypes(inducementSet);
		int usesLeft = regenerationTypes.stream().mapToInt(type -> inducementSet.get(type).getUsesLeft()).sum();
		RollMechanic rollMechanic = getGameState().getGame().getMechanic(Mechanic.Type.ROLL);
		return usesLeft > 0 || rollMechanic.findReRollOptions(getGameState(), player, ReRolledActions.REGENERATION, null).canActuallyReRoll();
	}

	private boolean handleSingleRegenReRoll(List<InjuryResult> failedRegens, Game game) {
		InjuryResult result = failedRegens.get(0);
		Player<?> player = injuredPlayer(result, game);

		Optional<InducementType> inducement = regenerationInducementType();
		if (inducement.isPresent()) {
			DialogUseInducementParameter dialogParameter =
				new DialogUseInducementParameter(teamId, new InducementType[]{
					inducement.get()}, player.getId());
			UtilServerDialog.showDialog(getGameState(), dialogParameter, game.getTeamById(teamId) != game.getActingTeam());
			return false;
		} else {
			if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), player, ReRolledActions.REGENERATION, 4, false)) {
				return false;
			} else {
				result.passedRegeneration();
				if (failedRegens.size() > 1) {
					return handleSingleRegenReRoll(failedRegens.subList(1, 1), game);
				}
				return true;
			}
		}

	}

	private Optional<InducementType> regenerationInducementType() {
		InducementSet inducementSet = getTurnData().getInducementSet();
		List<InducementType> regenerationTypes = regenerationTypes(inducementSet);
		return regenerationTypes.stream().findFirst();
	}

	private Player<?> injuredPlayer(InjuryResult result, Game game) {
		return game.getPlayerById(result.injuryContext().getDefenderId());
	}

	private List<InducementType> regenerationTypes(InducementSet inducementSet) {
		return inducementSet.getInducementMapping().keySet().stream()
			.filter(type -> type.hasUsage(Usage.REGENERATION) && inducementSet.hasUsesLeft(type))
			.sorted(Comparator.comparingInt(InducementType::getPriority)).collect(Collectors.toList());
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		// every injury is serialized once, the other lists refer to it by index so they share the same objects
		// after a reload
		addInjuriesToJson(injuryResults, IServerJsonOption.INJURY_RESULTS, jsonObject);
		if (regenerationFailedResults != null) {
			IServerJsonOption.INJURY_RESULT_INDEXES_REGENERATION_FAILED.addTo(jsonObject,
				injuryIndexes(regenerationFailedResults));
		}
		IServerJsonOption.INJURY_RESULT_INDEXES_DEAD.addTo(jsonObject, injuryIndexes(deadResults));
		IServerJsonOption.INJURY_RESULT_INDEXES_GETTING_EVEN.addTo(jsonObject, injuryIndexes(gettingEvenResults));
		// the lists are also written in the previous format so servers not knowing the indexes can still read them
		if (regenerationFailedResults != null) {
			addInjuriesToJson(regenerationFailedResults, IServerJsonOption.INJURY_RESULTS_REGENERATION_FAILED, jsonObject);
		}
		addInjuriesToJson(deadResults, IServerJsonOption.INJURY_RESULTS_DEAD, jsonObject);
		addInjuriesToJson(gettingEvenResults, IServerJsonOption.INJURY_RESULTS_GETTING_EVEN, jsonObject);

		IServerJsonOption.AVAILABLE_KEYWORDS_MAP.addTo(jsonObject, availableKeyWordsMap.entrySet().stream().collect(
			Collectors.toMap(Map.Entry::getKey,
				entry -> entry.getValue().stream().map(Keyword::getName).collect(Collectors.toList()))));
		return jsonObject;
	}

	private void addInjuriesToJson(List<InjuryResult> injuryResults, JsonArrayOption jsonOption,
		JsonObject jsonObject) {
		JsonArray injuriesAsJson = new JsonArray();
		injuryResults.stream().map(InjuryResult::toJsonValue).forEach(injuriesAsJson::add);
		jsonOption.addTo(jsonObject, injuriesAsJson);
	}

	private int[] injuryIndexes(List<InjuryResult> results) {
		return results.stream().mapToInt(this::injuryIndex).filter(index -> index >= 0).toArray();
	}

	private int injuryIndex(InjuryResult result) {
		for (int index = 0; index < injuryResults.size(); index++) {
			if (injuryResults.get(index) == result) {
				return index;
			}
		}
		return -1;
	}

	private List<InjuryResult> injuriesAt(int[] indexes) {
		List<InjuryResult> results = new ArrayList<>();
		for (int index : indexes) {
			results.add(injuryResults.get(index));
		}
		return results;
	}

	@Override
	public StepApothecaryMultiple initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		injuryResults = initInjuries(IServerJsonOption.INJURY_RESULTS, source, jsonObject);
		regenerationFailedResults = initInjuries(IServerJsonOption.INJURY_RESULT_INDEXES_REGENERATION_FAILED,
			IServerJsonOption.INJURY_RESULTS_REGENERATION_FAILED, source, jsonObject);
		deadResults = initInjuries(IServerJsonOption.INJURY_RESULT_INDEXES_DEAD, IServerJsonOption.INJURY_RESULTS_DEAD,
			source, jsonObject);
		if (deadResults == null) {
			deadResults = new ArrayList<>();
		}
		gettingEvenResults = initInjuries(IServerJsonOption.INJURY_RESULT_INDEXES_GETTING_EVEN,
			IServerJsonOption.INJURY_RESULTS_GETTING_EVEN, source, jsonObject);
		if (gettingEvenResults == null) {
			gettingEvenResults = new ArrayList<>();
		}

		teamId = IServerJsonOption.TEAM_ID.getFrom(source, jsonObject);
		if (teamId == null && !injuryResults.isEmpty()) {
			// steps saved before the team id was serialized only hold injuries of the handled team
			teamId = getGameState().getGame().getPlayerById(injuryResults.get(0).injuryContext().getDefenderId())
				.getTeam().getId();
		}

		availableKeyWordsMap = IServerJsonOption.AVAILABLE_KEYWORDS_MAP.getFrom(source, jsonObject).entrySet().stream()
			.collect(
				Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(Keyword::forName).collect(
					Collectors.toList())));
		return this;
	}

	/**
	 * @return the injuries referenced by index, or for steps saved before indexes were used the separately serialized
	 * injuries mapped onto the main list, null if neither is defined
	 */
	private List<InjuryResult> initInjuries(JsonIntArrayOption indexOption, JsonArrayOption legacyOption,
		IFactorySource source, JsonObject jsonObject) {
		if (indexOption.isDefinedIn(jsonObject)) {
			return injuriesAt(indexOption.getFrom(source, jsonObject));
		}
		if (legacyOption.isDefinedIn(jsonObject)) {
			List<InjuryResult> results = reconcileWithInjuryResults(initInjuries(legacyOption, source, jsonObject));
			if (legacyOption == IServerJsonOption.INJURY_RESULTS_REGENERATION_FAILED) {
				// a decided apothecary result always leaves this list, a stale copy may still have kept it there
				results.removeIf(result -> result.injuryContext().getApothecaryStatus() == ApothecaryStatus.RESULT_CHOICE);
			}
			return results;
		}
		return null;
	}

	/**
	 * Steps saved before injuries were referenced by index hold a separate copy of each injury per list. Every copy
	 * is mapped onto the injury of the main list it was copied from: an identical injury of the same player if there
	 * is one, otherwise the next unmatched injury of that player. If the two disagree, the one further along in the
	 * apothecary / pastries handling is kept (see {@link #progress(InjuryResult)}), on a tie the copy. A copy without
	 * any counterpart is added to the main list, so nothing is dropped.
	 */
	private List<InjuryResult> reconcileWithInjuryResults(List<InjuryResult> copies) {
		List<InjuryResult> results = new ArrayList<>();
		Set<Integer> matchedIndexes = new HashSet<>();
		for (InjuryResult copy : copies) {
			JsonObject copyAsJson = copy.toJsonValue();
			String defenderId = copy.injuryContext().getDefenderId();
			int identicalIndex = -1;
			int samePlayerIndex = -1;
			for (int index = 0; index < injuryResults.size() && identicalIndex < 0; index++) {
				InjuryResult candidate = injuryResults.get(index);
				if (!matchedIndexes.contains(index)
					&& StringTool.isEqual(defenderId, candidate.injuryContext().getDefenderId())) {
					if (copyAsJson.equals(candidate.toJsonValue())) {
						identicalIndex = index;
					} else if (samePlayerIndex < 0) {
						samePlayerIndex = index;
					}
				}
			}
			int index = identicalIndex;
			if (index < 0 && samePlayerIndex >= 0) {
				index = samePlayerIndex;
				InjuryResult mainResult = injuryResults.get(index);
				if (progress(copy) >= progress(mainResult)) {
					replaceInjury(mainResult, copy);
				}
			}
			if (index < 0) {
				injuryResults.add(copy);
				index = injuryResults.size() - 1;
			}
			matchedIndexes.add(index);
			results.add(injuryResults.get(index));
		}
		return results;
	}

	/**
	 * @return how far the injury got in this step: undecided apothecary use, apothecary used, apothecary decided
	 * (used with a pending choice, not used or not available) or pastries offered, final result (apothecary result
	 * chosen, pastries used or declined); a passed regeneration counts as a little further
	 */
	private static int progress(InjuryResult injuryResult) {
		ApothecaryStatus status = injuryResult.injuryContext().getApothecaryStatus();
		int stage;
		switch (status == null ? ApothecaryStatus.NO_APOTHECARY : status) {
			case DO_REQUEST:
			case WAIT_FOR_APOTHECARY_USE:
			case WAIT_FOR_IGOR_USE:
				stage = 0;
				break;
			case USE_IGOR:
			case DO_NOT_USE_IGOR:
			case NO_APOTHECARY:
			case DO_NOT_USE_APOTHECARY:
			case USE_APOTHECARY:
				stage = 1;
				break;
			case WAIT_FOR_APOTHECARY_CHOICE:
			case WAIT_FOR_PASTRIES_USE:
				stage = 2;
				break;
			default:
				stage = 3;
				break;
		}
		return 2 * stage + (injuryResult.isPreRegeneration() ? 0 : 1);
	}

	private void replaceInjury(InjuryResult oldResult, InjuryResult newResult) {
		for (List<InjuryResult> results : Arrays.asList(injuryResults, regenerationFailedResults, deadResults,
			gettingEvenResults)) {
			if (results != null) {
				results.replaceAll(result -> result == oldResult ? newResult : result);
			}
		}
	}

	private List<InjuryResult> initInjuries(JsonArrayOption injuryResultsGettingEven, IFactorySource source,
		JsonObject jsonObject) {
		List<InjuryResult> results = new ArrayList<>();
		JsonArray gettingEvenInjuryResultObject = injuryResultsGettingEven.getFrom(source, jsonObject);
		if (gettingEvenInjuryResultObject != null) {
			results.addAll(
				gettingEvenInjuryResultObject.values().stream().map(value -> new InjuryResult().initFrom(source, value))
					.collect(Collectors.toList()));
		}
		return results;
	}

}
