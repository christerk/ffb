package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.BloodSpot;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogWizardSpellParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.commands.ClientCommandWizardSpell;
import com.fumbbl.ffb.report.ReportWizardUse;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.SpecialEffect.SequenceParams;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerInducementUse;

import java.util.ArrayList;
import java.util.List;

/**
 * Step in inducement sequence to handle wizard.
 * <p>
 * Needs to be initialized with stepParameter HOME_TEAM.
 * <p>
 * May push SpellEffect sequences onto the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
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
	public boolean setParameter(StepParameter parameter) {

		if (parameter != null && parameter.getKey() == StepParameterKey.HOME_TEAM) {
			fHomeTeam = (parameter.getValue() != null) && (boolean) parameter.getValue();
			return true;
		}

		return super.setParameter(parameter);
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
						if (fHomeTeam) {
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

			Team team = fHomeTeam ? game.getTeamHome() : game.getTeamAway();
			getResult().addReport(new ReportWizardUse(team.getId(), fWizardSpell));
			InducementSet inducementSet = fHomeTeam ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();

			inducementSet.getInducementMapping().keySet().stream()
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
				com.fumbbl.ffb.server.step.generator.SpecialEffect generator =
					(com.fumbbl.ffb.server.step.generator.SpecialEffect) factory.forName(SequenceGenerator.Type.SpecialEffect.name());
				affectedPlayers.stream().map(affectedPlayer -> new SequenceParams(getGameState(), fWizardSpell, affectedPlayer.getId(),
					true)).forEach(generator::pushSequence);
			});
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			if (game.getTurnMode() != (TurnMode.WIZARD)) {
				fOldTurnMode = game.getTurnMode();
			}
			game.setTurnMode(TurnMode.WIZARD);

			UtilServerDialog.showDialog(getGameState(), new DialogWizardSpellParameter((fHomeTeam ? game.getTeamHome() : game.getTeamAway()).getId()), false);
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
