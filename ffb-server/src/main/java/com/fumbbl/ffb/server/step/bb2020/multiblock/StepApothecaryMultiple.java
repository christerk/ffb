package com.fumbbl.ffb.server.step.bb2020.multiblock;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.ApothecaryStatus;
import com.fumbbl.ffb.ApothecaryType;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.bb2020.InjuryDescription;
import com.fumbbl.ffb.dialog.DialogApothecaryChoiceParameter;
import com.fumbbl.ffb.dialog.DialogUseApothecariesParameter;
import com.fumbbl.ffb.dialog.DialogUseMortuaryAssistantsParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandApothecaryChoice;
import com.fumbbl.ffb.net.commands.ClientCommandUseApothecary;
import com.fumbbl.ffb.net.commands.ClientCommandUseIgors;
import com.fumbbl.ffb.report.ReportApothecaryChoice;
import com.fumbbl.ffb.report.ReportInducement;
import com.fumbbl.ffb.report.bb2020.ReportApothecaryRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerInducementUse;
import com.fumbbl.ffb.server.util.UtilServerInjury;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepApothecaryMultiple extends AbstractStep {

	private static final Set<ApothecaryStatus> ignoreForIgorCheck = new HashSet<ApothecaryStatus>() {{
		add(ApothecaryStatus.WAIT_FOR_IGOR_USE);
		add(ApothecaryStatus.DO_NOT_USE_IGOR);
	}};

	private ApothecaryMode apothecaryMode;
	private List<InjuryResult> injuryResults = new ArrayList<>(), regenerationFailedResults = new ArrayList<>();
	private String teamId;

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
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
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
						handleApothecaryChoice(choiceResult.get(), apothecaryChoiceCommand.getPlayerState(), apothecaryChoiceCommand.getSeriousInjury());
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_USE_APOTHECARIES:
					injuryResults.stream()
						.filter(injuryResult -> injuryResult.injuryContext().fApothecaryStatus == ApothecaryStatus.WAIT_FOR_APOTHECARY_USE)
						.forEach(injuryResult -> injuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.DO_NOT_USE_APOTHECARY));
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_USE_APOTHECARY:
					ClientCommandUseApothecary useApothecaryCommand = (ClientCommandUseApothecary) pReceivedCommand.getCommand();
					Game game = getGameState().getGame();
					injuryResults.stream()
						.filter(injuryResult -> injuryResult.injuryContext().fApothecaryStatus == ApothecaryStatus.WAIT_FOR_APOTHECARY_USE)
						.forEach(injuryResult -> {
							ApothecaryStatus newStatus;
							if (injuryResult.injuryContext().getDefenderId().equalsIgnoreCase(useApothecaryCommand.getPlayerId())) {

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
							} else {
								newStatus = ApothecaryStatus.DO_REQUEST;
							}
							injuryResult.injuryContext().setApothecaryStatus(newStatus);

						});
					commandStatus = StepCommandStatus.EXECUTE_STEP;

					break;
				case CLIENT_USE_IGORS:
					ClientCommandUseIgors useIgorsCommand = (ClientCommandUseIgors) pReceivedCommand.getCommand();
					List<InjuryDescription> igorInjuryDescriptions = useIgorsCommand.getInjuryDescriptions();
					boolean emptyIgorSelection = igorInjuryDescriptions.isEmpty();
					injuryResults.stream()
						.filter(injuryResult -> injuryResult.injuryContext().getApothecaryStatus() == ApothecaryStatus.WAIT_FOR_IGOR_USE)
						.forEach(injuryResult -> {
							Optional<InjuryDescription> injuryDescription = find(igorInjuryDescriptions, injuryResult.injuryContext());
							ApothecaryStatus newStatus;
							if (emptyIgorSelection) {
								newStatus = ApothecaryStatus.DO_NOT_USE_IGOR;
							} else if (injuryDescription.isPresent()) {
								igorInjuryDescriptions.remove(injuryDescription.get());
								newStatus = ApothecaryStatus.USE_IGOR;
							} else {
								newStatus = ApothecaryStatus.WAIT_FOR_IGOR_USE;
							}
							injuryResult.injuryContext().setApothecaryStatus(newStatus);
						});
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
		if (injuryResults.isEmpty()) {
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			UtilServerDialog.hideDialog(getGameState());
			Game game = getGameState().getGame();
			Map<ApothecaryStatus, List<InjuryResult>> groupedInjuries = injuryResults.stream()
				.collect(Collectors.groupingBy(injuryResult -> injuryResult.injuryContext().getApothecaryStatus()));

			List<InjuryResult> doRequest = groupedInjuries.get(ApothecaryStatus.DO_REQUEST);
			if (doRequest != null && !doRequest.isEmpty()) {
				List<InjuryDescription> injuryDescriptions = new ArrayList<>();
				int remainingApos = remainingApos();
				doRequest.forEach(injuryResult -> {
					injuryResult.report(this);
					UtilServerGame.syncGameModel(this);
					InjuryContext injuryContext = injuryResult.injuryContext();
					List<ApothecaryType> apothecaryTypes = ApothecaryType.forPlayer(game, game.getPlayerById(injuryContext.fDefenderId), injuryContext.getPlayerState());
					if (remainingApos > 0 && !apothecaryTypes.isEmpty()) {
						injuryContext.setApothecaryStatus(ApothecaryStatus.WAIT_FOR_APOTHECARY_USE);
						injuryDescriptions.add(new InjuryDescription(injuryContext.getDefenderId(), injuryContext.getPlayerState(), injuryContext.fSeriousInjury, apothecaryTypes));
					} else {
						injuryContext.setApothecaryStatus(ApothecaryStatus.DO_NOT_USE_APOTHECARY);
					}
				});

				if (!injuryDescriptions.isEmpty()) {
					UtilServerDialog.showDialog(getGameState(), new DialogUseApothecariesParameter(teamId, injuryDescriptions), true);
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
					}
				}
			}


			List<InjuryResult> doNotUseApo = groupedInjuries.get(ApothecaryStatus.DO_NOT_USE_APOTHECARY);
			if (doNotUseApo != null) {
				doNotUseApo.forEach(injuryResult ->
					getResult().addReport(
						new ReportApothecaryRoll(injuryResult.injuryContext().getDefenderId(), null, null, null, null, injuryResult.injuryContext().casualtyModifiers))
				);
			}

			List<InjuryResult> noApo = groupedInjuries.get(ApothecaryStatus.NO_APOTHECARY);
			if (noApo != null) {
				noApo.forEach(injuryResult -> injuryResult.report(this));
			}

			Team team = game.getTeamById(teamId);
			InducementSet inducementSet = getTurnData().getInducementSet();
			List<InducementType> regenerationTypes = inducementSet.getInducementMapping().keySet().stream()
				.filter(type -> type.hasUsage(Usage.REGENERATION) && inducementSet.hasUsesLeft(type))
				.sorted(Comparator.comparingInt(InducementType::getPriority)).collect(Collectors.toList());

			int usesLeft = regenerationTypes.stream().mapToInt(type -> inducementSet.get(type).getUsesLeft()).sum();

			List<InjuryResult> injuriesToUseIgorOn = injuryResults.stream().filter(injuryResult ->
				injuryResult.injuryContext().getApothecaryStatus() == ApothecaryStatus.USE_IGOR).collect(Collectors.toList());
			for (InjuryResult injuryResult : injuriesToUseIgorOn) {
				Player<?> player = game.getPlayerById(injuryResult.injuryContext().getDefenderId());
				PlayerState playerState = injuryResult.injuryContext().getPlayerState();
				injuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.DO_NOT_USE_IGOR);

				if (playerState != null && playerState.isCasualty()
					&& player.hasSkillProperty(NamedProperties.canRollToSaveFromInjury)
					&& injuryResult.injuryContext().getInjuryType().canUseApo()
					&& usesLeft > 0) {
					InducementType inducementType = regenerationTypes.get(0);
					UtilServerInducementUse.useInducement(getGameState(), team, inducementType, 1);
					if (inducementType.hasUsage(Usage.APOTHECARY_JOURNEYMEN) && getTurnData().getPlagueDoctors() > 0) {
						getTurnData().setPlagueDoctors(getTurnData().getPlagueDoctors() - 1);
					}
					usesLeft--;

					if (!inducementSet.hasUsesLeft(inducementType)) {
						regenerationTypes.remove(inducementType);
					}

					getResult().addReport(new ReportInducement(teamId, inducementType, 0));
					if (UtilServerInjury.handleRegeneration(this, player, playerState)) {
						regenerationFailedResults.remove(injuryResult);
					}
				}
			}

			injuryResults.stream()
				.filter(injuryResult -> !ignoreForIgorCheck.contains(injuryResult.injuryContext().getApothecaryStatus()))
				.forEach(injuryResult -> {
						injuryResult.applyTo(this);

						Player<?> player = game.getPlayerById(injuryResult.injuryContext().getDefenderId());
						PlayerState playerState = injuryResult.injuryContext().getPlayerState();
						injuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.DO_NOT_USE_IGOR);

						if (playerState != null && playerState.isCasualty()
							&& player.hasSkillProperty(NamedProperties.canRollToSaveFromInjury)
							&& injuryResult.injuryContext().getInjuryType().canUseApo()
							// roll regen before checking for player type, as mercs and stars can use their regen skill
							&& !UtilServerInjury.handleRegeneration(this, player, playerState)
							&& player.getPlayerType() != PlayerType.STAR
							&& player.getPlayerType() != PlayerType.MERCENARY) {
							injuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.WAIT_FOR_IGOR_USE);
							regenerationFailedResults.add(injuryResult);
						}
					}
				);

			if (usesLeft > 0) {
				List<InjuryResult> regenerationToReRoll = injuryResults.stream()
					.filter(injuryResult -> injuryResult.injuryContext().getApothecaryStatus() == ApothecaryStatus.WAIT_FOR_IGOR_USE).collect(Collectors.toList());

				if (!regenerationToReRoll.isEmpty()) {
					List<InjuryDescription> injuryDescriptions = new ArrayList<>();
					regenerationToReRoll.forEach(injuryResult -> {
						InjuryContext injuryContext = injuryResult.injuryContext();
						injuryDescriptions.add(new InjuryDescription(injuryContext.getDefenderId(), injuryContext.getPlayerState(), injuryContext.fSeriousInjury, Collections.emptyList()));
					});

					UtilServerDialog.showDialog(getGameState(), new DialogUseMortuaryAssistantsParameter(teamId, injuryDescriptions, Math.min(usesLeft, injuryDescriptions.size())), true);
					return;
				}
			}

			// this only happens in case of a double attacker down
			if (injuryResults.size() == 2
				&& injuryResults.get(0).injuryContext().getDefenderId().equals(
				injuryResults.get(1).injuryContext().getDefenderId())
				&& !regenerationFailedResults.isEmpty()
			) {
				// reset the player states again to make sure we have defined base state to reapply the injuries
				Player<?> player = game.getPlayerById(injuryResults.get(0).injuryContext().getDefenderId());
				PlayerState playerState = game.getFieldModel().getPlayerState(player);
				game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.RESERVE));
				GameResult gameResult = game.getGameResult();
				PlayerResult playerResult = gameResult.getPlayerResult(player);
				playerResult.setSeriousInjury(null);
				playerResult.setSeriousInjuryDecay(null);

				injuryResults.stream().filter(regenerationFailedResults::contains)
					.forEach(injuryResult -> {
						injuryResult.applyTo(this, false);
						UtilServerGame.syncGameModel(this);
					});
			}

			for (InjuryResult injuryResult : injuryResults) {
				if (UtilServerInjury.handleInjurySideEffects(this, injuryResult)) {
					UtilServerGame.syncGameModel(this);
					break;
				}
			}

			getResult().setNextAction(StepAction.NEXT_STEP);

		}
	}

	private void useApo(TurnData turnData, ApothecaryType apothecaryType) {
		turnData.useApothecary(apothecaryType);
		if (apothecaryType == ApothecaryType.PLAGUE) {
			turnData.getInducementSet().getInducementTypes().stream()
				.filter(inducementType -> inducementType.hasUsage(Usage.REGENERATION) && inducementType.hasUsage(Usage.APOTHECARY_JOURNEYMEN) && turnData.getInducementSet().hasUsesLeft(inducementType))
				.findFirst().ifPresent(inducementType -> UtilServerInducementUse.useInducement(inducementType, 1, turnData.getInducementSet()));
		}
	}

	private Optional<InjuryDescription> find(List<InjuryDescription> injuryDescriptions, InjuryContext injuryContext) {
		return injuryDescriptions.stream().filter(injuryDescription -> injuryDescription.getPlayerId().equals(injuryContext.getDefenderId())
			&& injuryDescription.getPlayerState().equals(injuryContext.getPlayerState())).findFirst();
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
			RollMechanic rollMechanic = ((RollMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name()));
			InjuryResult newInjuryResult = new InjuryResult();
			newInjuryResult.injuryContext().setDefenderId(injuryResult.injuryContext().getDefenderId());
			newInjuryResult.injuryContext().setCasualtyRoll(rollMechanic.rollCasualty(getGameState().getDiceRoller()));
			newInjuryResult.injuryContext().setInjury(
				rollMechanic.interpretCasualtyRollAndAddModifiers(game, newInjuryResult.injuryContext(), game.getPlayerById(injuryResult.injuryContext().getDefenderId()), false));
			newInjuryResult.injuryContext().setSeriousInjury(
				rollMechanic.interpretSeriousInjuryRoll(game, newInjuryResult.injuryContext()));
			apothecaryChoice = (newInjuryResult.injuryContext().getPlayerState().getBase() != PlayerState.BADLY_HURT);
			getResult()
				.addReport(new ReportApothecaryRoll(defender.getId(), newInjuryResult.injuryContext().getCasualtyRoll(),
					newInjuryResult.injuryContext().getPlayerState(), newInjuryResult.injuryContext().getSeriousInjury(), newInjuryResult.injuryContext().getOriginalSeriousInjury(), injuryResult.injuryContext().casualtyModifiers));
			if (apothecaryChoice) {
				UtilServerDialog.showDialog(getGameState(),
					new DialogApothecaryChoiceParameter(defender.getId(), injuryResult.injuryContext().getPlayerState(),
						injuryResult.injuryContext().getSeriousInjury(), newInjuryResult.injuryContext().getPlayerState(),
						newInjuryResult.injuryContext().getSeriousInjury()),
					true);
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

	private void handleApothecaryChoice(InjuryResult injuryResult, PlayerState pPlayerState, SeriousInjury pSeriousInjury) {
		injuryResult.injuryContext().setInjury(pPlayerState);
		injuryResult.injuryContext().setSeriousInjury(pSeriousInjury);
		injuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.RESULT_CHOICE);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.APOTHECARY_MODE.addTo(jsonObject, apothecaryMode);
		JsonArray regeneratedInjuriesAsJson = new JsonArray();
		regenerationFailedResults.stream().map(InjuryResult::toJsonValue).forEach(regeneratedInjuriesAsJson::add);
		IServerJsonOption.INJURY_RESULTS_REGENERATION_FAILED.addTo(jsonObject, regeneratedInjuriesAsJson);
		JsonArray injuriesAsJson = new JsonArray();
		injuryResults.stream().map(InjuryResult::toJsonValue).forEach(injuriesAsJson::add);
		IServerJsonOption.INJURY_RESULTS.addTo(jsonObject, injuriesAsJson);

		return jsonObject;
	}

	@Override
	public StepApothecaryMultiple initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		apothecaryMode = (ApothecaryMode) IServerJsonOption.APOTHECARY_MODE.getFrom(source, jsonObject);
		JsonArray regenerationInjuryResultObject = IServerJsonOption.INJURY_RESULTS_REGENERATION_FAILED.getFrom(source, jsonObject);
		if (regenerationInjuryResultObject != null) {
			regenerationFailedResults = regenerationInjuryResultObject.values().stream().map(value -> new InjuryResult().initFrom(source, value)).collect(Collectors.toList());
		}
		JsonArray injuryResultObject = IServerJsonOption.INJURY_RESULTS.getFrom(source, jsonObject);
		if (injuryResultObject != null) {
			injuryResults = injuryResultObject.values().stream().map(value -> new InjuryResult().initFrom(source, value)).collect(Collectors.toList());
		}
		return this;
	}

}
