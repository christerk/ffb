package com.balancedbytes.games.ffb.server.step.bb2020.multiblock;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.ApothecaryStatus;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.bb2020.InjuryDescription;
import com.balancedbytes.games.ffb.dialog.DialogApothecaryChoiceParameter;
import com.balancedbytes.games.ffb.dialog.DialogUseApothecariesParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.TurnData;
import com.balancedbytes.games.ffb.net.commands.ClientCommandApothecaryChoice;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseApothecaries;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseInducement;
import com.balancedbytes.games.ffb.report.ReportApothecaryChoice;
import com.balancedbytes.games.ffb.report.ReportApothecaryRoll;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.mechanic.RollMechanic;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepApothecaryMultiple extends AbstractStep {

	private ApothecaryMode fApothecaryMode;
	private List<InjuryResult> injuryResults = new ArrayList<>();
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
					Optional<InjuryResult> choiceResult = injuryResults.stream().filter(injuryResult -> injuryResult.injuryContext().getDefenderId().equals(apothecaryChoiceCommand.getPlayerId()))
						.findFirst();
					if (choiceResult.isPresent()) {
						handleApothecaryChoice(choiceResult.get(), apothecaryChoiceCommand.getPlayerState(), apothecaryChoiceCommand.getSeriousInjury());
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_USE_APOTHECARIES:
					ClientCommandUseApothecaries useApothecaryCommand = (ClientCommandUseApothecaries) pReceivedCommand.getCommand();
					injuryResults.stream()
						.filter(injuryResult -> injuryResult.injuryContext().fApothecaryStatus == ApothecaryStatus.WAIT_FOR_APOTHECARY_USE)
						.forEach(injuryResult -> {
							ApothecaryStatus newStatus;
							if (!ArrayTool.isProvided(useApothecaryCommand.getPlayerIds())) {
								newStatus = ApothecaryStatus.DO_NOT_USE_APOTHECARY;
							} else if (useApothecaryCommand.hasPlayerId(injuryResult.injuryContext().fDefenderId)) {
								newStatus = ApothecaryStatus.USE_APOTHECARY;
							} else {
								newStatus = ApothecaryStatus.DO_REQUEST;
							}
							injuryResult.injuryContext().setApothecaryStatus(newStatus);
						});
					commandStatus = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_USE_INDUCEMENT:
					ClientCommandUseInducement inducementCommand = (ClientCommandUseInducement) pReceivedCommand.getCommand();
/*					if (inducementCommand.getInducementType().getUsage() == Usage.REGENERATION) {
						if ((fInjuryResult != null)
							&& (fInjuryResult.injuryContext().getApothecaryStatus() == ApothecaryStatus.WAIT_FOR_IGOR_USE)) {
							if (inducementCommand.hasPlayerId(fInjuryResult.injuryContext().getDefenderId())) {
								fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.USE_IGOR);
							} else {
								fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.DO_NOT_USE_IGOR);
							}
							commandStatus = StepCommandStatus.EXECUTE_STEP;
						}
					}*/
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
			switch (parameter.getKey()) {
				case INJURY_RESULT:
					InjuryResult injuryResult = (InjuryResult) parameter.getValue();
					if (injuryResult != null) {
						String defenderId = injuryResult.injuryContext().getDefenderId();
						if (teamId.equals(getGameState().getGame().getPlayerById(defenderId).getTeam().getId())) {
							injuryResults.add(injuryResult);
							consume(parameter);
							return true;
						}
					}
					return false;
				default:
					break;
			}
		}
		return false;
	}

	private void executeStep() {
		if (injuryResults.isEmpty()) {
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			UtilServerDialog.hideDialog(getGameState());
			boolean doNextStep = true;
			Game game = getGameState().getGame();
			Map<ApothecaryStatus, List<InjuryResult>> groupedInjuries = injuryResults.stream()
				.collect(Collectors.groupingBy(injuryResult -> injuryResult.injuryContext().getApothecaryStatus()));

			List<InjuryResult> useApo = groupedInjuries.get(ApothecaryStatus.USE_APOTHECARY);

			if (useApo != null && !useApo.isEmpty()) {
				for (InjuryResult injuryResult: useApo) {
					if (rollApothecary(injuryResult)) {
						injuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.WAIT_FOR_APOTHECARY_CHOICE);
						return;
					} else {
						injuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.RESULT_CHOICE);
					}
				}
			}

			List<InjuryResult> doRequest = groupedInjuries.get(ApothecaryStatus.DO_REQUEST);
			if (doRequest != null && !doRequest.isEmpty()) {
				List<InjuryDescription> injuryDescriptions = new ArrayList<>();
				doRequest.forEach(injuryResult -> {
					injuryResult.report(this);
					injuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.WAIT_FOR_APOTHECARY_USE);
					InjuryContext injuryContext = injuryResult.injuryContext();
					injuryDescriptions.add(new InjuryDescription(injuryContext.getDefenderId(), injuryContext.getPlayerState(), injuryContext.fSeriousInjury));
				});
				TurnData turnData = game.getTeamById(teamId) == game.getTeamHome() ? game.getTurnDataHome() : game.getTurnDataAway();

				UtilServerDialog.showDialog(getGameState(), new DialogUseApothecariesParameter(teamId, injuryDescriptions, turnData.getApothecaries()), true);

				return;
			}

			List<InjuryResult> doNotUseApo = groupedInjuries.get(ApothecaryStatus.DO_NOT_USE_APOTHECARY);
			if (doNotUseApo != null) {
				doNotUseApo.forEach(injuryResult ->
				getResult().addReport(
					new ReportApothecaryRoll(injuryResult.injuryContext().getDefenderId(), null, null, null))
				);
			}

			List<InjuryResult> noApo = groupedInjuries.get(ApothecaryStatus.NO_APOTHECARY);
			if (noApo != null) {
				noApo.forEach(injuryResult -> injuryResult.report(this));
			}

			/*
			if (doNextStep) {
				Player<?> player = game.getPlayerById(fInjuryResult.injuryContext().getDefenderId());
				switch (fInjuryResult.injuryContext().getApothecaryStatus()) {
					case DO_NOT_USE_IGOR:
						break;
					case USE_IGOR:
						Team team = game.getTeamHome().hasPlayer(player) ? game.getTeamHome() : game.getTeamAway();
						InducementSet inducementSetIgor = game.isHomePlaying() ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
						inducementSetIgor.getInducementMapping().keySet().stream().filter(type -> type.getUsage() == Usage.REGENERATION)
							.findFirst().ifPresent(type -> {
							UtilServerInducementUse.useInducement(getGameState(), team, type, 1);
							getResult().addReport(new ReportInducement(team.getId(), type, 0));
							boolean success = UtilServerInjury.handleRegeneration(this, player);
							if (success) {
								curePoison();
							}
						});
						break;
					default:
						fInjuryResult.applyTo(this);
						PlayerState playerState = game.getFieldModel().getPlayerState(player);
						if ((playerState != null) && playerState.isCasualty()
							&& player.hasSkillProperty(NamedProperties.canRollToSaveFromInjury)
							&& (fInjuryResult.injuryContext().getInjuryType().canUseApo())) {
							if (!UtilServerInjury.handleRegeneration(this, player)) {
								InducementSet inducementSet = game.getTeamHome().hasPlayer(player)
									? game.getTurnDataHome().getInducementSet()
									: game.getTurnDataAway().getInducementSet();
								boolean hasInducement = inducementSet.getInducementMapping().keySet().stream().anyMatch(type -> type.getUsage() == Usage.REGENERATION
									&& inducementSet.hasUsesLeft(type));
									if (hasInducement && player.getPlayerType() != PlayerType.STAR) {
										game.setDialogParameter(new DialogUseIgorParameter(player.getId()));
										fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.WAIT_FOR_IGOR_USE);
										doNextStep = false;
									}
							} else {
								curePoison();
							}
						}
						break;
				}
			}
			if (doNextStep) {
				UtilServerInjury.handleRaiseDead(this, fInjuryResult);
				getResult().setNextAction(StepAction.NEXT_STEP);
			}*/
		}
	}

	private boolean rollApothecary(InjuryResult injuryResult) {
		Game game = getGameState().getGame();
		Player<?> defender = game.getPlayerById(injuryResult.injuryContext().getDefenderId());
		if (game.getTeamHome().hasPlayer(defender)) {
			game.getTurnDataHome().useApothecary();
		} else {
			game.getTurnDataAway().useApothecary();
		}
		boolean apothecaryChoice = ((injuryResult.injuryContext().getPlayerState().getBase() != PlayerState.BADLY_HURT)
			&& (injuryResult.injuryContext().getPlayerState().getBase() != PlayerState.KNOCKED_OUT));
		if (apothecaryChoice) {
			RollMechanic rollMechanic = ((RollMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name()));
			InjuryResult newInjuryResult = new InjuryResult();
			newInjuryResult.injuryContext().setDefenderId(injuryResult.injuryContext().getDefenderId());
			newInjuryResult.injuryContext().setCasualtyRoll(rollMechanic.rollCasualty(getGameState().getDiceRoller()));
			newInjuryResult.injuryContext().setInjury(
				rollMechanic.interpretCasualtyRollAndAddModifiers(game, newInjuryResult.injuryContext(), game.getPlayerById(injuryResult.injuryContext().getDefenderId())));
			newInjuryResult.injuryContext().setSeriousInjury(
				rollMechanic.interpretSeriousInjuryRoll(newInjuryResult.injuryContext()));
			apothecaryChoice = (newInjuryResult.injuryContext().getPlayerState().getBase() != PlayerState.BADLY_HURT);
			getResult()
				.addReport(new ReportApothecaryRoll(defender.getId(), newInjuryResult.injuryContext().getCasualtyRoll(),
					newInjuryResult.injuryContext().getPlayerState(), newInjuryResult.injuryContext().getSeriousInjury()));
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
		IServerJsonOption.APOTHECARY_MODE.addTo(jsonObject, fApothecaryMode);
		JsonArray injuriesAsJson = new JsonArray();
		injuryResults.stream().map(InjuryResult::toJsonValue).forEach(injuriesAsJson::add);
		IServerJsonOption.INJURY_RESULTS.addTo(jsonObject, injuriesAsJson);
		return jsonObject;
	}

	@Override
	public StepApothecaryMultiple initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fApothecaryMode = (ApothecaryMode) IServerJsonOption.APOTHECARY_MODE.getFrom(source, jsonObject);
		JsonArray injuryResultObject = IServerJsonOption.INJURY_RESULTS.getFrom(source, jsonObject);
		if (injuryResultObject != null) {
			injuryResults = injuryResultObject.values().stream().map(value -> new InjuryResult().initFrom(source, value)).collect(Collectors.toList());
		}
		return this;
	}

}
