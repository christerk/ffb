package com.balancedbytes.games.ffb.server.step.phase.special;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.RosterPlayer;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.ZappedPlayer;
import com.balancedbytes.games.ffb.report.ReportSpecialEffectRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryMode;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in inducement sequence to handle spell effect.
 * 
 * Needs to be initialized with stepParameter PLAYER_ID.
 * Needs to be initialized with stepParameter ROLL_FOR_EFFECT.
 * Needs to be initialized with stepParameter SPECIAL_EFFECT.
 * 
 * Sets stepParameter END_TURN for all steps on the stack.
 * Sets stepParameter INJURY_RESULT for all steps on the stack.
 *
 * @author Kalimar
 */
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
		
		Player player = game.getPlayerById(fPlayerId);
		if (player != null) {
			
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
				if (fSpecialEffect == SpecialEffect.LIGHTNING) {
					publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT,
						UtilServerInjury.handleInjury(this, InjuryType.LIGHTNING, null, player, playerCoordinate, null, ApothecaryMode.SPECIAL_EFFECT)));
				}
				if (fSpecialEffect == SpecialEffect.ZAP && player instanceof RosterPlayer) {
					ZappedPlayer zappedPlayer = new ZappedPlayer();
					zappedPlayer.init((RosterPlayer) player);
					player = zappedPlayer;
					Team team = game.findTeam(player);
					team.addPlayer(player);
					getGameState().getServer().getCommunication().sendAddPlayer(getGameState(), team.getId(), player,  game.getFieldModel().getPlayerState(player), game.getGameResult().getPlayerResult(player));

				}
				if (fSpecialEffect == SpecialEffect.FIREBALL) {
					publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT,
						UtilServerInjury.handleInjury(this, InjuryType.FIREBALL, null, player, playerCoordinate, null, ApothecaryMode.SPECIAL_EFFECT)));
				}
				if (fSpecialEffect == SpecialEffect.BOMB) {
					publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT,
						UtilServerInjury.handleInjury(this, InjuryType.BOMB, null, player, playerCoordinate, null, ApothecaryMode.SPECIAL_EFFECT)));
				}

				publishParameters(UtilServerInjury.dropPlayer(this, player, ApothecaryMode.SPECIAL_EFFECT));

				// check end turn
				Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
				if ((TurnMode.BOMB_HOME == game.getTurnMode()) || (TurnMode.BOMB_HOME_BLITZ == game.getTurnMode())) {
					actingTeam = game.getTeamHome();
				}
				if ((TurnMode.BOMB_AWAY == game.getTurnMode()) || (TurnMode.BOMB_AWAY_BLITZ == game.getTurnMode())) {
					actingTeam = game.getTeamAway();
				}
				if (actingTeam.hasPlayer(player) && (fSpecialEffect != SpecialEffect.FIREBALL)) {
					publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
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
  public StepSpecialEffect initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(jsonObject);
    fPlayerId = IServerJsonOption.PLAYER_ID.getFrom(jsonObject);
    fRollForEffect = IServerJsonOption.ROLL_FOR_EFFECT.getFrom(jsonObject);
    fSpecialEffect = (SpecialEffect) IServerJsonOption.SPECIAL_EFFECT.getFrom(jsonObject);
    return this;
  }

}
