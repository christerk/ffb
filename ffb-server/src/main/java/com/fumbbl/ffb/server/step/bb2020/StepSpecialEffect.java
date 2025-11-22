package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.ZappedPlayer;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.report.ReportSpecialEffectRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeBombWithModifier;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeFireball;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.mixed.StepCatchScatterThrowIn;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.util.UtilPlayer;

/**
 * Step in inducement sequence to handle spell effect.
 * <p>
 * Needs to be initialized with stepParameter PLAYER_ID. Needs to be initialized
 * with stepParameter ROLL_FOR_EFFECT. Needs to be initialized with
 * stepParameter SPECIAL_EFFECT.
 * <p>
 * Sets stepParameter END_TURN for all steps on the stack. Sets stepParameter
 * INJURY_RESULT for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepSpecialEffect extends AbstractStep {

	private String fGotoLabelOnFailure;
	private String fPlayerId;
	private boolean fRollForEffect;
	private SpecialEffect fSpecialEffect;

	public StepSpecialEffect(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.SPECIAL_EFFECT;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
				// mandatory
				case GOTO_LABEL_ON_FAILURE:
					fGotoLabelOnFailure = (String) parameter.getValue();
					break;
				// mandatory
				case PLAYER_ID:
					fPlayerId = (String) parameter.getValue();
					break;
				// mandatory
				case ROLL_FOR_EFFECT:
					fRollForEffect = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
					break;
				// mandatory
				case SPECIAL_EFFECT:
					fSpecialEffect = (SpecialEffect) parameter.getValue();
					break;
				default:
					break;
				}
			}
		}
		if (fGotoLabelOnFailure == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
		}
		if (fPlayerId == null) {
			throw new StepException("StepParameter " + StepParameterKey.PLAYER_ID + " is not initialized.");
		}
		if (fSpecialEffect == null) {
			throw new StepException("StepParameter " + StepParameterKey.SPECIAL_EFFECT + " is not initialized.");
		}
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {

		Game game = getGameState().getGame();
		boolean suppressEndTurn = false;
		Player<?> player = game.getPlayerById(fPlayerId);
		if (player != null) {

			PlayerState state = game.getFieldModel().getPlayerState(player);
			boolean isStanding = !state.isProneOrStunned() && !state.isStunned();
			boolean isActive = state.isActive();

			boolean successful = true;

			if (fRollForEffect) {
				int roll = getGameState().getDiceRoller().rollWizardSpell();
				successful = DiceInterpreter.getInstance().isSpecialEffectSuccesful(fSpecialEffect, player, roll);
				getResult().addReport(new ReportSpecialEffectRoll(fSpecialEffect, player.getId(), roll, successful));
			} else {
				getResult().addReport(new ReportSpecialEffectRoll(fSpecialEffect, player.getId(), 0, true));
			}

			if (successful) {

				FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
				if (fSpecialEffect == SpecialEffect.ZAP && player instanceof RosterPlayer) {
					ZappedPlayer zappedPlayer = new ZappedPlayer();
					zappedPlayer.init((RosterPlayer) player, game.getRules());
					Team team = game.findTeam(player);
					team.addPlayer(zappedPlayer);
					getGameState().addZappedPlayer(player);
					getGameState().getServer().getCommunication().sendZapPlayer(getGameState(), (RosterPlayer) player);
					if (FieldCoordinate.equals(game.getFieldModel().getBallCoordinate(), playerCoordinate)) {
						getGameState().getStepStack().push(new StepCatchScatterThrowIn(getGameState()));
						publishParameter(
								new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
					}
				}
				if (fSpecialEffect == SpecialEffect.FIREBALL) {
					publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, UtilServerInjury.handleInjury(this,
							new InjuryTypeFireball(), null, player, playerCoordinate, null, null, ApothecaryMode.SPECIAL_EFFECT)));
					publishParameters(UtilServerInjury.dropPlayer(this, player, ApothecaryMode.SPECIAL_EFFECT, true));
				}
				if (fSpecialEffect == SpecialEffect.BOMB) {
					boolean bombFromHome = game.getTurnMode() == TurnMode.BOMB_HOME || game.getTurnMode() == TurnMode.BOMB_HOME_BLITZ;
					boolean bombFromAway = game.getTurnMode() == TurnMode.BOMB_AWAY || game.getTurnMode() == TurnMode.BOMB_AWAY_BLITZ;

					boolean playerHitIsFromBombTeam = (bombFromHome && game.getTeamHome().hasPlayer(player)) || (bombFromAway && game.getTeamAway().hasPlayer(player));

					suppressEndTurn = !(playerHitIsFromBombTeam && UtilPlayer.hasBall(game, player));
					GameOptionBoolean bomberTurnoverIgnored = (GameOptionBoolean) game.getOptions().getOptionWithDefault(GameOptionId.BOMBER_PLACED_PRONE_IGNORES_TURNOVER);
					if (player.getId().equals(getGameState().getPassState().getOriginalBombardier()) && !bomberTurnoverIgnored.isEnabled()) {
						suppressEndTurn = false;
					}
					publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, UtilServerInjury.handleInjury(this,
						new InjuryTypeBombWithModifier(), null, player, playerCoordinate, null, null, ApothecaryMode.SPECIAL_EFFECT)));
					StepParameterSet parameterSet = UtilServerInjury.dropPlayer(this, player, ApothecaryMode.SPECIAL_EFFECT, true);
					PlayerState newState = game.getFieldModel().getPlayerState(player);
					if (!player.getId().equalsIgnoreCase(getGameState().getPassState().getOriginalBombardier()) && newState.isProneOrStunned()) {
						game.getFieldModel().setPlayerState(player, newState.changeActive(isActive));
					}
					if (suppressEndTurn) {
						parameterSet.remove(StepParameterKey.END_TURN);
					}
					publishParameters(parameterSet);
				}

				// check end turn
				if (isStanding) {
					Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
					if ((TurnMode.BOMB_HOME == game.getTurnMode()) || (TurnMode.BOMB_HOME_BLITZ == game.getTurnMode())) {
						actingTeam = game.getTeamHome();
					}
					if ((TurnMode.BOMB_AWAY == game.getTurnMode()) || (TurnMode.BOMB_AWAY_BLITZ == game.getTurnMode())) {
						actingTeam = game.getTeamAway();
					}
					if (actingTeam.hasPlayer(player) && (fSpecialEffect != SpecialEffect.FIREBALL) && !suppressEndTurn) {
						publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
					}
				}

				getResult().setNextAction(StepAction.NEXT_STEP);

			} else {
				getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
			}

		}

	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IServerJsonOption.ROLL_FOR_EFFECT.addTo(jsonObject, fRollForEffect);
		IServerJsonOption.SPECIAL_EFFECT.addTo(jsonObject, fSpecialEffect);
		return jsonObject;
	}

	@Override
	public StepSpecialEffect initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		fPlayerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fRollForEffect = IServerJsonOption.ROLL_FOR_EFFECT.getFrom(source, jsonObject);
		fSpecialEffect = (SpecialEffect) IServerJsonOption.SPECIAL_EFFECT.getFrom(source, jsonObject);
		return this;
	}

}
