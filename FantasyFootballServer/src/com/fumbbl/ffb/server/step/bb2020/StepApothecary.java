package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.ApothecaryStatus;
import com.fumbbl.ffb.ApothecaryType;
import com.fumbbl.ffb.CardEffect;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.dialog.DialogApothecaryChoiceParameter;
import com.fumbbl.ffb.dialog.DialogUseApothecaryParameter;
import com.fumbbl.ffb.dialog.DialogUseMortuaryAssistantParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandApothecaryChoice;
import com.fumbbl.ffb.net.commands.ClientCommandUseApothecary;
import com.fumbbl.ffb.net.commands.ClientCommandUseInducement;
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
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInducementUse;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.util.StringTool;

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
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepApothecary extends AbstractStep {

	private ApothecaryMode fApothecaryMode;
	private InjuryResult fInjuryResult;
	private boolean fShowReport;
	private boolean fDefenderPoisoned;
	private boolean fAttackerPoisoned;

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
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_APOTHECARY_CHOICE:
					ClientCommandApothecaryChoice apothecaryChoiceCommand = (ClientCommandApothecaryChoice) pReceivedCommand
						.getCommand();
					if ((fInjuryResult != null) && StringTool.isEqual(apothecaryChoiceCommand.getPlayerId(),
						fInjuryResult.injuryContext().getDefenderId())) {
						handleApothecaryChoice(apothecaryChoiceCommand.getPlayerState(), apothecaryChoiceCommand.getSeriousInjury());
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
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_USE_INDUCEMENT:
					ClientCommandUseInducement inducementCommand = (ClientCommandUseInducement) pReceivedCommand.getCommand();
					if (inducementCommand.getInducementType().hasUsage(Usage.REGENERATION)) {
						if ((fInjuryResult != null)
							&& (fInjuryResult.injuryContext().getApothecaryStatus() == ApothecaryStatus.WAIT_FOR_IGOR_USE)) {
							if (inducementCommand.hasPlayerId(fInjuryResult.injuryContext().getDefenderId())) {
								fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.USE_IGOR);
							} else {
								fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.DO_NOT_USE_IGOR);
							}
							commandStatus = StepCommandStatus.EXECUTE_STEP;
						}
					}
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
				case USING_PILING_ON:
					Boolean usingPilingOn = (Boolean) parameter.getValue();
					if ((ApothecaryMode.DEFENDER == fApothecaryMode) && (usingPilingOn != null) && !usingPilingOn) {
						fShowReport = false;
						return true;
					}
					return false;
				case DEFENDER_POISONED:
					fDefenderPoisoned = (Boolean) parameter.getValue();
					return fApothecaryMode == ApothecaryMode.DEFENDER;
				case ATTACKER_POISONED:
					fAttackerPoisoned = (Boolean) parameter.getValue();
					return fApothecaryMode == ApothecaryMode.ATTACKER;
				default:
					break;
			}
		}
		return false;
	}

	private void executeStep() {
		if (fInjuryResult == null) {
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			UtilServerDialog.hideDialog(getGameState());
			boolean doNextStep = true;
			Game game = getGameState().getGame();
			if (fInjuryResult.injuryContext().getApothecaryStatus() != null) {
				switch (fInjuryResult.injuryContext().getApothecaryStatus()) {
					case DO_REQUEST:
						if (fShowReport) {
							fInjuryResult.report(this);
						}
						UtilServerDialog.showDialog(getGameState(),
							new DialogUseApothecaryParameter(fInjuryResult.injuryContext().getDefenderId(),
								fInjuryResult.injuryContext().getPlayerState(), fInjuryResult.injuryContext().getSeriousInjury()),
							true);
						fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.WAIT_FOR_APOTHECARY_USE);
						doNextStep = false;
						break;
					case USE_APOTHECARY:
						if (rollApothecary()) {
							fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.WAIT_FOR_APOTHECARY_USE);
							doNextStep = false;
						} else {
							fInjuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.RESULT_CHOICE);
						}
						break;
					case DO_NOT_USE_APOTHECARY:
						getResult()
							.addReport(new ReportApothecaryRoll(fInjuryResult.injuryContext().getDefenderId(), null, null, null, null, fInjuryResult.injuryContext().casualtyModifiers));
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
			if (doNextStep) {
				Player<?> player = game.getPlayerById(fInjuryResult.injuryContext().getDefenderId());
				switch (fInjuryResult.injuryContext().getApothecaryStatus()) {
					case DO_NOT_USE_IGOR:
						break;
					case USE_IGOR:
						Team team = game.getTeamHome().hasPlayer(player) ? game.getTeamHome() : game.getTeamAway();
						InducementSet inducementSetIgor = game.getTeamHome().hasPlayer(player) ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
						inducementSetIgor.getInducementMapping().keySet().stream().filter(type -> type.hasUsage(Usage.REGENERATION))
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
								boolean hasInducement = inducementSet.getInducementMapping().keySet().stream().anyMatch(type -> type.hasUsage(Usage.REGENERATION)
									&& inducementSet.hasUsesLeft(type));
									if (hasInducement && player.getPlayerType() != PlayerType.STAR && player.getPlayerType() != PlayerType.MERCENARY) {
										game.setDialogParameter(new DialogUseMortuaryAssistantParameter(player.getId()));
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
				UtilServerInjury.handleInjurySideEffects(this, fInjuryResult);
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
		}
	}

	private boolean rollApothecary() {
		Game game = getGameState().getGame();
		Player<?> defender = game.getPlayerById(fInjuryResult.injuryContext().getDefenderId());
		if (game.getTeamHome().hasPlayer(defender)) {
			game.getTurnDataHome().useApothecary(ApothecaryType.forPlayer(game, defender));
		} else {
			game.getTurnDataAway().useApothecary(ApothecaryType.forPlayer(game, defender));
		}
		boolean apothecaryChoice = ((fInjuryResult.injuryContext().getPlayerState().getBase() != PlayerState.BADLY_HURT)
			&& (fInjuryResult.injuryContext().getPlayerState().getBase() != PlayerState.KNOCKED_OUT));
		if (apothecaryChoice) {
			RollMechanic rollMechanic = ((RollMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name()));
			InjuryResult newInjuryResult = new InjuryResult();
			newInjuryResult.injuryContext().setDefenderId(fInjuryResult.injuryContext().getDefenderId());
			newInjuryResult.injuryContext().setCasualtyRoll(rollMechanic.rollCasualty(getGameState().getDiceRoller()));
			newInjuryResult.injuryContext().setInjury(
				rollMechanic.interpretCasualtyRollAndAddModifiers(game, newInjuryResult.injuryContext(), game.getPlayerById(fInjuryResult.injuryContext().getDefenderId()), false));
			newInjuryResult.injuryContext().setSeriousInjury(
				rollMechanic.interpretSeriousInjuryRoll(game, newInjuryResult.injuryContext()));
			apothecaryChoice = (newInjuryResult.injuryContext().getPlayerState().getBase() != PlayerState.BADLY_HURT);
			getResult()
				.addReport(new ReportApothecaryRoll(defender.getId(), newInjuryResult.injuryContext().getCasualtyRoll(),
					newInjuryResult.injuryContext().getPlayerState(), newInjuryResult.injuryContext().getSeriousInjury(), newInjuryResult.injuryContext().getOriginalSeriousInjury(), fInjuryResult.injuryContext().casualtyModifiers));
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
			} else {
				curePoison();
				fInjuryResult.injuryContext().setInjury(new PlayerState(PlayerState.RESERVE));
			}
			getResult().addReport(
				new ReportApothecaryChoice(defender.getId(), fInjuryResult.injuryContext().getPlayerState(), null));
		}
		return apothecaryChoice;
	}

	private void curePoison() {
		Game game = getGameState().getGame();
		Player<?> player = game.getPlayerById(fInjuryResult.injuryContext().getDefenderId());
		if (fDefenderPoisoned && fApothecaryMode == ApothecaryMode.DEFENDER) {
			game.getFieldModel().removeCardEffect(player, CardEffect.POISONED);
		} else if (fAttackerPoisoned && fApothecaryMode == ApothecaryMode.ATTACKER) {
			game.getFieldModel().removeCardEffect(player, CardEffect.POISONED);
		}
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
		return this;
	}

}
