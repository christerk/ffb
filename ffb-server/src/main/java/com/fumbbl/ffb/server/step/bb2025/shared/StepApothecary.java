package com.fumbbl.ffb.server.step.bb2025.shared;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.ApothecaryStatus;
import com.fumbbl.ffb.ApothecaryType;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.KeywordChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PositionChoiceMode;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.dialog.DialogApothecaryChoiceParameter;
import com.fumbbl.ffb.dialog.DialogSelectKeywordParameter;
import com.fumbbl.ffb.dialog.DialogSelectPositionParameter;
import com.fumbbl.ffb.dialog.DialogUseApothecaryParameter;
import com.fumbbl.ffb.dialog.DialogUseInducementParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.InjuryMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.model.Player;
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
import com.fumbbl.ffb.report.ReportApothecaryChoice;
import com.fumbbl.ffb.report.ReportInducement;
import com.fumbbl.ffb.report.mixed.ReportApothecaryRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
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
import com.fumbbl.ffb.util.RaiseType;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Step in any sequence to handle the apothecary. Offers different modes
 * (ATTACKER, CROWDPUSH, DEFENDER) to modify the behavior.
 * <p>
 * Needs to be initialized with stepParameter APOTHECARY_MODE.
 * <p>
 * Expects stepParameter INJURY_RESULT to be set by a preceding step.
 * (InjuryResult.getApothecaryMode() must match ApothecaryMode of this step)
 * Expects stepParameter USING_PILING_ON to be set by a preceding step (mode
 * DEFENDER).
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepApothecary extends AbstractStep {

	private ApothecaryMode fApothecaryMode;
	private InjuryResult fInjuryResult;
	private boolean fShowReport;
	private ApothecaryType apothecaryType;

	public StepApothecary(GameState pGameState) {
		super(pGameState);
		fShowReport = true;
	}

	public StepId getId() {
		return StepId.APOTHECARY;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				// mandatory
				if (parameter.getKey() == StepParameterKey.APOTHECARY_MODE) {
					fApothecaryMode = (ApothecaryMode) parameter.getValue();
				}
			}
		}
		if (fApothecaryMode == null) {
			throw new StepException("StepParameter " + StepParameterKey.APOTHECARY_MODE + " is not initialized.");
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
				case CLIENT_APOTHECARY_CHOICE:
					ClientCommandApothecaryChoice apothecaryChoiceCommand = (ClientCommandApothecaryChoice) pReceivedCommand
						.getCommand();
					if ((fInjuryResult != null) && StringTool.isEqual(apothecaryChoiceCommand.getPlayerId(),
						fInjuryResult.injuryContext().getDefenderId())) {
						handleApothecaryChoice(apothecaryChoiceCommand.getPlayerState(),
							apothecaryChoiceCommand.getSeriousInjury());
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_USE_APOTHECARY:
					ClientCommandUseApothecary useApothecaryCommand = (ClientCommandUseApothecary) pReceivedCommand.getCommand();
					if ((fInjuryResult != null)
						&& StringTool.isEqual(useApothecaryCommand.getPlayerId(), fInjuryResult.injuryContext().getDefenderId())) {
						fInjuryResult.injuryContext()
							.setApothecaryStatus(useApothecaryCommand.isApothecaryUsed() ? ApothecaryStatus.USE_APOTHECARY
								: ApothecaryStatus.DO_NOT_USE_APOTHECARY);
						apothecaryType = useApothecaryCommand.getApothecaryType();
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_USE_INDUCEMENT:
					ClientCommandUseInducement clientCommandUseInducement =
						(ClientCommandUseInducement) pReceivedCommand.getCommand();
					if (fInjuryResult.injuryContext().getApothecaryStatus() == ApothecaryStatus.WAIT_FOR_IGOR_USE) {
						InducementType inducementType = clientCommandUseInducement.getInducementType();
						if (inducementType != null && inducementType.hasUsage(Usage.REGENERATION)) {
							if (UtilServerInducementUse.useInducement(inducementType, 1, getTurnData().getInducementSet())) {
								Player<?> player = injuredPlayer();
								if (inducementType.hasUsage(Usage.APOTHECARY_JOURNEYMEN)) {
									TurnData turnData =
										game.getTeamHome().hasPlayer(player) ? game.getTurnDataHome() : game.getTurnDataAway();
									turnData.useApothecary(ApothecaryType.PLAGUE);
								}
								getResult().addReport(new ReportInducement(player.getTeam().getId(), inducementType, 1));
								if (UtilServerInjury.handleRegeneration(this, player, fInjuryResult.injuryContext().getPlayerState(),
									true)) {
									fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.RESULT_CHOICE);
								} else {
									fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.DO_REQUEST);
								}
								fInjuryResult.passedRegeneration();
							}
						} else if (inducementType == null) {
							fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.DO_REQUEST);
							fInjuryResult.passedRegeneration();
						}
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_USE_RE_ROLL:
					ClientCommandUseReRoll clientCommandUseReRoll = (ClientCommandUseReRoll) pReceivedCommand.getCommand();
					if (clientCommandUseReRoll.getReRolledAction() == ReRolledActions.REGENERATION) {
						if (clientCommandUseReRoll.getReRollSource() == ReRollSources.TEAM_RE_ROLL) {
							Player<?> player = injuredPlayer();
							if (UtilServerReRoll.useReRoll(this, ReRollSources.TEAM_RE_ROLL, player)) {
								if (UtilServerInjury.handleRegeneration(this, player, fInjuryResult.injuryContext().getPlayerState(),
									true)) {
									fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.RESULT_CHOICE);
								}
							}
							fInjuryResult.passedRegeneration();
						}
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_KEYWORD_SELECTION:
					if (fInjuryResult != null &&
						fInjuryResult.injuryContext().getApothecaryStatus() == ApothecaryStatus.WAIT_FOR_GETTING_EVEN) {
						UtilServerDialog.hideDialog(getGameState());
						ClientCommandKeywordSelection commandKeywordSelection =
							(ClientCommandKeywordSelection) pReceivedCommand.getCommand();
						List<Keyword> keywords = commandKeywordSelection.getKeywords();
						Collections.reverse(keywords);
						keywords.forEach(keyword -> pushGettingEven(commandKeywordSelection.getPlayerId(), keyword));
						getResult().setNextAction(StepAction.NEXT_STEP);
						commandStatus = StepCommandStatus.SKIP_STEP;
					}
					break;
				case CLIENT_POSITION_SELECTION:
					UtilServerDialog.hideDialog(getGameState());
					ClientCommandPositionSelection commandPositionSelection =
						(ClientCommandPositionSelection) pReceivedCommand.getCommand();
					commandStatus = StepCommandStatus.SKIP_STEP;
					Team teamById = game.getTeamById(commandPositionSelection.getTeamId());
					RosterPosition position = teamById.getRoster().getPositionById(commandPositionSelection.getPosition()[0]);
					TeamResult teamResult = teamById == game.getTeamHome() ? game.getGameResult().getTeamResultHome()
						: game.getGameResult().getTeamResultAway();
					raisePlayer(game, teamById, teamResult, position);
					getResult().setNextAction(StepAction.NEXT_STEP);
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
					if ((injuryResult != null) && (fApothecaryMode == injuryResult.injuryContext().getApothecaryMode())) {
						fInjuryResult = injuryResult;
						return true;
					}
					return false;
				default:
					break;
			}
		}
		return false;
	}

	private void executeStep() {
		getResult().setNextAction(StepAction.NEXT_STEP);
		if (fInjuryResult == null) {
			return;
		}

		UtilServerDialog.hideDialog(getGameState());
		boolean doNextStep = true;
		Game game = getGameState().getGame();
		String defenderId = fInjuryResult.injuryContext().getDefenderId();

		if (fInjuryResult.isPreRegeneration()) {
			fInjuryResult.report(this);
			fInjuryResult.setAlreadyReported(false);
			Player<?> player = game.getPlayerById(defenderId);
			PlayerState playerState = fInjuryResult.injuryContext().getPlayerState();
			if ((playerState != null) && playerState.isCasualty()
				&& player.hasSkillProperty(NamedProperties.canRollToSaveFromInjury)
				&& (fInjuryResult.injuryContext().getInjuryType().canUseApo())) {
				if (UtilServerInjury.handleRegeneration(this, player)) {
					fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.RESULT_CHOICE);
					fInjuryResult.passedRegeneration();
				} else {
					Optional<InducementType> inducementType = regenerationInducementType();
					if (inducementType.isPresent()) {
						game.setDialogParameter(new DialogUseInducementParameter(player.getTeam().getId(),
							new InducementType[]{inducementType.get()}));
						fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.WAIT_FOR_IGOR_USE);
						getResult().setNextAction(StepAction.CONTINUE);
						return;
					} else if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), player, ReRolledActions.REGENERATION,
						4, false)) {
						getResult().setNextAction(StepAction.CONTINUE);
						return;
					} else {
						fInjuryResult.passedRegeneration();
					}
				}
			} else {
				fInjuryResult.passedRegeneration();
			}
		}


		if (fInjuryResult.injuryContext().getApothecaryStatus() != null) {
			switch (fInjuryResult.injuryContext().getApothecaryStatus()) {
				case DO_REQUEST:
					if (fShowReport) {
						fInjuryResult.report(this);
					}
					List<ApothecaryType> apothecaryTypes = ApothecaryType.forPlayer(game, game.getPlayerById(defenderId),
						fInjuryResult.injuryContext().getPlayerState());
					UtilServerDialog.showDialog(getGameState(),
						new DialogUseApothecaryParameter(defenderId,
							fInjuryResult.injuryContext().getPlayerState(), fInjuryResult.injuryContext().getSeriousInjury(),
							apothecaryTypes),
						true);
					fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.WAIT_FOR_APOTHECARY_USE);
					getResult().setNextAction(StepAction.CONTINUE);
					return;
				case USE_APOTHECARY:
					if (rollApothecary()) {
						fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.WAIT_FOR_APOTHECARY_USE);
						getResult().setNextAction(StepAction.CONTINUE);
						return;
					} else {
						fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.RESULT_CHOICE);
					}
					break;
				case DO_NOT_USE_APOTHECARY:
					getResult()
						.addReport(new ReportApothecaryRoll(defenderId, null, null, null, null,
							fInjuryResult.injuryContext().casualtyModifiers));
					break;
				case NO_APOTHECARY:
					if (fShowReport) {
						fInjuryResult.report(this);
					}
					break;
				default:
					break;
			}
		}


		fInjuryResult.applyTo(this);

		if (UtilServerInjury.handlePumpUp(this, fInjuryResult)) {
			UtilServerGame.syncGameModel(this);
		}

		PlayerState playerState = fInjuryResult.injuryContext().getPlayerState();
		if (playerState != null) {
			Player<?> defender = game.getPlayerById(defenderId);
			Player<?> attacker = game.getPlayerById(fInjuryResult.injuryContext().getAttackerId());
			if (playerState.isSi()) {
				Set<Keyword> availableKeywords =
					attacker.getPosition().getKeywords().stream().filter(Keyword::isCanGetEvenWith)
						.collect(Collectors.toSet());

				UtilCards.getSkillWithProperty(defender, NamedProperties.canRerollSingleSkull).ifPresent(
					skill -> skill.evaluator().values(skill, defender).stream().map(Keyword::forName)
						.forEach(availableKeywords::remove)
				);

				if (!availableKeywords.isEmpty()) {
					List<Keyword> keywords = availableKeywords.stream().sorted().collect(Collectors.toList());
					if (availableKeywords.size() == 1) {
						pushGettingEven(defenderId, keywords.get(0));
					} else {
						UtilServerDialog.showDialog(getGameState(),
							new DialogSelectKeywordParameter(defenderId, keywords, KeywordChoiceMode.GETTING_EVEN, 1, 1),
							!game.getActingTeam().hasPlayer(defender));

						fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.WAIT_FOR_GETTING_EVEN);
						getResult().setNextAction(StepAction.CONTINUE);
					}
				}
			} else if (playerState.getBase() == PlayerState.RIP) {
				InjuryMechanic injuryMechanic = game.getMechanic(Mechanic.Type.INJURY);
				Team raisingTeam = game.getOtherTeam(game.getPlayerById(fInjuryResult.injuryContext().getDefenderId())
					.getTeam());
				TeamResult raisingTeamResult =
					raisingTeam == game.getTeamHome() ? game.getGameResult().getTeamResultHome() : game.getGameResult()
						.getTeamResultAway();
				if ((injuryMechanic.canRaiseDead(raisingTeam, raisingTeamResult, defender) ||
					injuryMechanic.canRaiseInfectedPlayers(raisingTeam, raisingTeamResult, attacker, defender))) {
					List<RosterPosition> raisePositions = injuryMechanic.raisePositions(raisingTeam);
					if (raisePositions.size() == 1) {
						raisePlayer(game, raisingTeam, raisingTeamResult, raisePositions.get(0));
					} else if (raisePositions.size() > 1) {
						List<String> raisePositionIds =
							injuryMechanic.raisePositions(raisingTeam).stream().map(RosterPosition::getId)
								.collect(Collectors.toList());

						UtilServerDialog.showDialog(getGameState(),
							new DialogSelectPositionParameter(raisePositionIds, PositionChoiceMode.RAISE_DEAD, 1, 1,
								raisingTeam.getId()),
							true);

						getResult().setNextAction(StepAction.CONTINUE);
					}
				}
			}
		}
	}

	private Optional<InducementType> regenerationInducementType() {
		InducementSet inducementSet = getTurnData().getInducementSet();
		List<InducementType> regenerationTypes = regenerationTypes(inducementSet);
		return regenerationTypes.stream().findFirst();
	}

	private List<InducementType> regenerationTypes(InducementSet inducementSet) {
		return inducementSet.getInducementMapping().keySet().stream()
			.filter(type -> type.hasUsage(Usage.REGENERATION) && inducementSet.hasUsesLeft(type))
			.sorted(Comparator.comparingInt(InducementType::getPriority)).collect(Collectors.toList());
	}

	private TurnData getTurnData() {
		Game game = getGameState().getGame();
		Player<?> player = injuredPlayer();
		return game.getTeamHome().hasPlayer(player) ? game.getTurnDataHome() : game.getTurnDataAway();
	}

	private Player<?> injuredPlayer() {
		return getGameState().getGame().getPlayerById(fInjuryResult.injuryContext().getDefenderId());
	}

	private void raisePlayer(Game game, Team raisingTeam, TeamResult raisingTeamResult, RosterPosition raisePosition) {
		InjuryMechanic injuryMechanic = game.getMechanic(Mechanic.Type.INJURY);
		Player<?> defender = game.getPlayerById(fInjuryResult.injuryContext().getDefenderId());
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

	private boolean rollApothecary() {
		Game game = getGameState().getGame();
		Player<?> defender = game.getPlayerById(fInjuryResult.injuryContext().getDefenderId());
		if (apothecaryType == null) {
			apothecaryType = ApothecaryType.forPlayer(game, defender, fInjuryResult.injuryContext().getPlayerState()).get(0);
		}
		if (game.getTeamHome().hasPlayer(defender)) {
			useApo(game.getTurnDataHome(), apothecaryType);
		} else {
			useApo(game.getTurnDataAway(), apothecaryType);
		}
		boolean apothecaryChoice = ((fInjuryResult.injuryContext().getPlayerState().getBase() != PlayerState.BADLY_HURT)
			&& (fInjuryResult.injuryContext().getPlayerState().getBase() != PlayerState.KNOCKED_OUT));
		if (apothecaryChoice) {
			RollMechanic rollMechanic =
				((RollMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name()));
			InjuryResult newInjuryResult = new InjuryResult();
			newInjuryResult.injuryContext().setDefenderId(fInjuryResult.injuryContext().getDefenderId());
			newInjuryResult.injuryContext().setCasualtyRoll(rollMechanic.rollCasualty(getGameState().getDiceRoller()));
			newInjuryResult.injuryContext().setInjury(
				rollMechanic.interpretCasualtyRollAndAddModifiers(game, newInjuryResult.injuryContext(),
					game.getPlayerById(fInjuryResult.injuryContext().getDefenderId()), false));
			newInjuryResult.injuryContext().setSeriousInjury(
				rollMechanic.interpretSeriousInjuryRoll(game, newInjuryResult.injuryContext()));
			apothecaryChoice = (newInjuryResult.injuryContext().getPlayerState().getBase() != PlayerState.BADLY_HURT);
			getResult()
				.addReport(new ReportApothecaryRoll(defender.getId(), newInjuryResult.injuryContext().getCasualtyRoll(),
					newInjuryResult.injuryContext().getPlayerState(), newInjuryResult.injuryContext().getSeriousInjury(),
					newInjuryResult.injuryContext().getOriginalSeriousInjury(), fInjuryResult.injuryContext().casualtyModifiers));
			if (apothecaryChoice) {
				UtilServerDialog.showDialog(getGameState(),
					new DialogApothecaryChoiceParameter(defender.getId(), fInjuryResult.injuryContext().getPlayerState(),
						fInjuryResult.injuryContext().getSeriousInjury(), newInjuryResult.injuryContext().getPlayerState(),
						newInjuryResult.injuryContext().getSeriousInjury()),
					true);
			}
		}
		if (!apothecaryChoice) {
			fInjuryResult.injuryContext().setSeriousInjury(null);
			if ((fInjuryResult.injuryContext().getPlayerState().getBase() == PlayerState.KNOCKED_OUT)
				&& (fInjuryResult.injuryContext().getInjuryType().canApoKoIntoStun())) {
				fInjuryResult.injuryContext().setInjury(new PlayerState(PlayerState.STUNNED));
			}
			getResult().addReport(
				new ReportApothecaryChoice(defender.getId(), fInjuryResult.injuryContext().getPlayerState(), null));
		}
		return apothecaryChoice;
	}

	private void handleApothecaryChoice(PlayerState pPlayerState, SeriousInjury pSeriousInjury) {
		if (fInjuryResult != null) {
			if (pPlayerState.getBase() == PlayerState.BADLY_HURT) {
				fInjuryResult.injuryContext().setInjury(new PlayerState(PlayerState.RESERVE));
				fInjuryResult.injuryContext().setSeriousInjury(null);
			} else {
				fInjuryResult.injuryContext().setInjury(pPlayerState);
				fInjuryResult.injuryContext().setSeriousInjury(pSeriousInjury);
			}
			fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.RESULT_CHOICE);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.APOTHECARY_MODE.addTo(jsonObject, fApothecaryMode);
		if (fInjuryResult != null) {
			IServerJsonOption.INJURY_RESULT.addTo(jsonObject, fInjuryResult.toJsonValue());
		}
		IServerJsonOption.SHOW_REPORT.addTo(jsonObject, fShowReport);
		if (apothecaryType != null) {
			IServerJsonOption.APOTHECARY_TYPE.addTo(jsonObject, apothecaryType.name());
		}
		return jsonObject;
	}

	@Override
	public StepApothecary initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fApothecaryMode = (ApothecaryMode) IServerJsonOption.APOTHECARY_MODE.getFrom(source, jsonObject);
		JsonObject injuryResultObject = IServerJsonOption.INJURY_RESULT.getFrom(source, jsonObject);
		if (injuryResultObject != null) {
			fInjuryResult = new InjuryResult().initFrom(source, injuryResultObject);
		}
		fShowReport = IServerJsonOption.SHOW_REPORT.getFrom(source, jsonObject);
		if (IServerJsonOption.APOTHECARY_TYPE.isDefinedIn(jsonObject)) {
			apothecaryType = ApothecaryType.valueOf(IServerJsonOption.APOTHECARY_TYPE.getFrom(source, jsonObject));
		}
		return this;
	}

}
