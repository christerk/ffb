package com.balancedbytes.games.ffb.server.step.phase.inducement;

import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.dialog.DialogWizardSpellParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.inducement.Usage;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.AnimationType;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.commands.ClientCommandWizardSpell;
import com.balancedbytes.games.ffb.report.ReportWizardUse;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.factory.SequenceGeneratorFactory;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.generator.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.generator.common.SpecialEffect.SequenceParams;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.server.util.UtilServerInducementUse;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Step in inducement sequence to handle wizard.
 *
 * Needs to be initialized with stepParameter HOME_TEAM.
 *
 * May push SpellEffect sequences onto the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public final class StepWizard extends AbstractStep {

	private SpecialEffect fWizardSpell;
	private FieldCoordinate fTargetCoordinate;
	private boolean fEndInducement;
	private TurnMode fOldTurnMode;
	private boolean fHomeTeam;

	public StepWizard(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.WIZARD;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
				// mandatory
				case HOME_TEAM:
					fHomeTeam = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					break;
				default:
					break;
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
			Game game = getGameState().getGame();
			switch (pReceivedCommand.getId()) {
			case CLIENT_WIZARD_SPELL:
				ClientCommandWizardSpell wizardSpellCommand = (ClientCommandWizardSpell) pReceivedCommand.getCommand();
				if (wizardSpellCommand.getWizardSpell() == null) { // cancel spellcasting
					fEndInducement = true;
				} else {
					fWizardSpell = wizardSpellCommand.getWizardSpell();
					if (game.isHomePlaying()) {
						fTargetCoordinate = wizardSpellCommand.getTargetCoordinate();
					} else {
						fTargetCoordinate = wizardSpellCommand.getTargetCoordinate().transform();
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
		UtilServerDialog.hideDialog(getGameState());
		if (fEndInducement) {
			// cancel spellcasting, no spell effect sequences added to the stack
			if (fOldTurnMode != null) {
				game.setTurnMode(fOldTurnMode);
			}
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else if ((fWizardSpell != null) && (fTargetCoordinate != null) && (game.getTurnMode() == TurnMode.WIZARD)) {

			Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
			getResult().addReport(new ReportWizardUse(team.getId(), fWizardSpell));
			team.getInducementSet().getInducementMapping().keySet().stream()
				.filter(inducementType -> inducementType.getUsage() == Usage.SPELL
					&& inducementType.effects().contains(fWizardSpell)).findFirst().ifPresent(type -> {

					UtilServerInducementUse.useInducement(getGameState(), team, type, 1);
					List<Player<?>> affectedPlayers = new ArrayList<>();
					if (fWizardSpell == SpecialEffect.ZAP) {
						getResult().setAnimation(new Animation(AnimationType.SPELL_ZAP, fTargetCoordinate));
						affectedPlayers.add(game.getFieldModel().getPlayer(fTargetCoordinate));
					}
					if (fWizardSpell == SpecialEffect.LIGHTNING) {
						getResult().setAnimation(new Animation(AnimationType.SPELL_LIGHTNING, fTargetCoordinate));
						addToAffectedPlayers(affectedPlayers, game.getFieldModel().getPlayer(fTargetCoordinate));
					}
					if (fWizardSpell == SpecialEffect.FIREBALL) {
						getResult().setAnimation(new Animation(AnimationType.SPELL_FIREBALL, fTargetCoordinate));
						FieldCoordinate[] targetCoordinates = game.getFieldModel().findAdjacentCoordinates(fTargetCoordinate,
							FieldCoordinateBounds.FIELD, 1, true);
						for (int i = targetCoordinates.length - 1; i >= 0; i--) {
							addToAffectedPlayers(affectedPlayers, game.getFieldModel().getPlayer(targetCoordinates[i]));
						}
					}
					if (fOldTurnMode != null) {
						game.setTurnMode(fOldTurnMode);
					}
					UtilServerGame.syncGameModel(this);
					PlayerState bloodSpotInjury = new PlayerState(
						(fWizardSpell == SpecialEffect.FIREBALL) ? PlayerState.HIT_BY_FIREBALL : PlayerState.HIT_BY_LIGHTNING);
					game.getFieldModel().add(new BloodSpot(fTargetCoordinate, bloodSpotInjury));
					SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
					com.balancedbytes.games.ffb.server.step.generator.common.SpecialEffect generator =
						(com.balancedbytes.games.ffb.server.step.generator.common.SpecialEffect) factory.forName(SequenceGenerator.Type.SpecialEffect.name());
					affectedPlayers.stream().map(affectedPlayer -> new SequenceParams(getGameState(), fWizardSpell, affectedPlayer.getId(),
						true)).forEach(generator::pushSequence);
				});
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			if (game.getTurnMode() != (TurnMode.WIZARD)) {
				fOldTurnMode = game.getTurnMode();
			}
			game.setTurnMode(TurnMode.WIZARD);

			UtilServerDialog.showDialog(getGameState(), new DialogWizardSpellParameter(), false);
		}
	}

	private void addToAffectedPlayers(List<Player<?>> pAffectedPlayers, Player<?> pPlayer) {
		Game game = getGameState().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((pPlayer != null) && (playerState != null) && (playerState.getBase() != PlayerState.PRONE)
				&& (playerState.getBase() != PlayerState.STUNNED)) {
			pAffectedPlayers.add(pPlayer);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.WIZARD_SPELL.addTo(jsonObject, fWizardSpell);
		IServerJsonOption.TARGET_COORDINATE.addTo(jsonObject, fTargetCoordinate);
		IServerJsonOption.END_INDUCEMENT.addTo(jsonObject, fEndInducement);
		IServerJsonOption.OLD_TURN_MODE.addTo(jsonObject, fOldTurnMode);
		IServerJsonOption.HOME_TEAM.addTo(jsonObject, fHomeTeam);
		return jsonObject;
	}

	@Override
	public StepWizard initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fWizardSpell = (SpecialEffect) IServerJsonOption.WIZARD_SPELL.getFrom(game, jsonObject);
		fTargetCoordinate = IServerJsonOption.TARGET_COORDINATE.getFrom(game, jsonObject);
		fEndInducement = IServerJsonOption.END_INDUCEMENT.getFrom(game, jsonObject);
		fOldTurnMode = (TurnMode) IServerJsonOption.OLD_TURN_MODE.getFrom(game, jsonObject);
		Boolean homeTeam = IServerJsonOption.HOME_TEAM.getFrom(game, jsonObject);
		fHomeTeam = (homeTeam != null) ? homeTeam : false;
		return this;
	}

}
