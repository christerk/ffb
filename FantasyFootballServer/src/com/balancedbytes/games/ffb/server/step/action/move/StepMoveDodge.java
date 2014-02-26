package com.balancedbytes.games.ffb.server.step.action.move;

import java.util.Set;

import com.balancedbytes.games.ffb.DodgeModifier;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.GameOption;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.ActionStatus;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilReRoll;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in move sequence to handle skill DODGE.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Expects stepParameter COORDINATE_FROM to be set by a preceding step.
 * Expects stepParameter COORDINATE_TO to be set by a preceding step.
 * Expects stepParameter DODGE_ROLL to be set by a preceding step.
 * Expects stepParameter USING_BREAK_TACKLE to be set by a preceding step.
 * Expects stepParameter USING_DIVING_TACKLE to be set by a preceding step.
 * 
 * Sets stepParameter DODGE_ROLL for all steps on the stack.
 * Sets stepParameter INJURY_TYPE for all steps on the stack.
 * Sets stepParameter USING_BREAK_TACKLE for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepMoveDodge extends AbstractStepWithReRoll {
	
	private String fGotoLabelOnFailure;
	private FieldCoordinate fCoordinateFrom;
	private FieldCoordinate fCoordinateTo;
	private int fDodgeRoll;
	private Boolean fUsingDivingTackle;
	private boolean fUsingBreakTackle;
	
	public StepMoveDodge(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.MOVE_DODGE;
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
					default:
						break;
  			}
  		}
  	}
  	if (fGotoLabelOnFailure == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
  	}
  }
  
	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case COORDINATE_FROM:
					fCoordinateFrom = (FieldCoordinate) pParameter.getValue();
					return true;
				case COORDINATE_TO:
					fCoordinateTo = (FieldCoordinate) pParameter.getValue();
					return true;
				case DODGE_ROLL:
					fDodgeRoll = (Integer) pParameter.getValue();
					return true;
				case USING_BREAK_TACKLE:
					fUsingBreakTackle = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					return true;
				case USING_DIVING_TACKLE:
					fUsingDivingTackle = (Boolean) pParameter.getValue();
					return true;
				default:
					break;
			}
		}
		return false;
	}
	
	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
  @Override
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
    if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
      executeStep();
    }
    return commandStatus;
  }
	
  private void executeStep() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    boolean doDodge = actingPlayer.isDodging();
    if (doDodge) {
      if (ReRolledAction.DODGE == getReRolledAction()) {
        if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
          failDodge();
          doDodge = false;
        }
      }
      if (doDodge) {
        boolean doRoll = (((getReRolledAction() == ReRolledAction.DODGE) && (getReRollSource() != null)) || (fUsingDivingTackle == null)); 
        switch (dodge(doRoll)) {
          case SUCCESS:
          	getResult().setNextAction(StepAction.NEXT_STEP);
            break;
          case FAILURE:
          	if (game.getOptions().getOptionValue(GameOption.STAND_FIRM_NO_DROP_ON_FAILED_DODGE).isEnabled()) {
          		publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
            	getResult().setNextAction(StepAction.NEXT_STEP);
          	} else {
          		failDodge();
          	}
            break;
          default:
          	break;
        }
      }
    } else {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  private void failDodge() {
  	publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, InjuryType.DROP_DODGE));
  	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
  }
  
  private ActionStatus dodge(boolean pDoRoll) {
    
    ActionStatus status = null;
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    
    if (pDoRoll) {
      publishParameter(new StepParameter(StepParameterKey.DODGE_ROLL, getGameState().getDiceRoller().rollSkill()));
    }
    Set<DodgeModifier> dodgeModifiers = DodgeModifier.findDodgeModifiers(game, fCoordinateFrom, fCoordinateTo, 0);
    if (fUsingBreakTackle) {
    	dodgeModifiers.add(DodgeModifier.BREAK_TACKLE);
    }
    if ((fUsingDivingTackle != null) && fUsingDivingTackle) {
      dodgeModifiers.add(DodgeModifier.DIVING_TACKLE);
    }
    
    int minimumRoll = DiceInterpreter.getInstance().minimumRollDodge(game, actingPlayer.getPlayer(), dodgeModifiers);
    boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(fDodgeRoll, minimumRoll);
    
    if (successful) {
      if (dodgeModifiers.remove(DodgeModifier.BREAK_TACKLE)) {
				int minimumRollWithoutBreakTackle = DiceInterpreter.getInstance().minimumRollDodge(game, actingPlayer.getPlayer(), dodgeModifiers);
        if (!DiceInterpreter.getInstance().isSkillRollSuccessful(fDodgeRoll, minimumRollWithoutBreakTackle)) {
          dodgeModifiers.add(DodgeModifier.BREAK_TACKLE);
        } else {
					minimumRoll = minimumRollWithoutBreakTackle;
        }
      }
    } else {
    	if (pDoRoll && dodgeModifiers.remove(DodgeModifier.BREAK_TACKLE)) {
  			minimumRoll = DiceInterpreter.getInstance().minimumRollDodge(game, actingPlayer.getPlayer(), dodgeModifiers);
  			if (!fUsingBreakTackle) {
  				getResult().addReport(new ReportSkillUse(null, Skill.BREAK_TACKLE, false, SkillUse.WOULD_NOT_HELP));
  			}
    	}
    }  	

    DodgeModifier[] dodgeModifierArray = DodgeModifier.toArray(dodgeModifiers);
    boolean reRolled = ((getReRolledAction() == ReRolledAction.DODGE) && (getReRollSource() != null));
    getResult().addReport(new ReportSkillRoll(ReportId.DODGE_ROLL, actingPlayer.getPlayerId(), successful, (pDoRoll ? fDodgeRoll : 0), minimumRoll, reRolled, dodgeModifierArray));
    
    if (successful) {
      status = ActionStatus.SUCCESS;
    } else {
      status = ActionStatus.FAILURE;
      if (getReRolledAction() != ReRolledAction.DODGE) {
        setReRolledAction(ReRolledAction.DODGE);
        boolean useDodgeSkill = UtilCards.hasUnusedSkill(game, game.getActingPlayer(), Skill.DODGE); 
        if (useDodgeSkill) {
          Team otherTeam = UtilPlayer.findOtherTeam(game, actingPlayer.getPlayer());
          Player[] opponents = UtilPlayer.findAdjacentPlayersWithTacklezones(game, otherTeam, fCoordinateFrom, false);
          for (int i = 0; i < opponents.length; i++) {
            if (UtilCards.hasSkill(game, opponents[i], Skill.TACKLE)) {
              useDodgeSkill = false;
              break;
            }
          }
        }
        if (useDodgeSkill) {
          setReRollSource(ReRollSource.DODGE);
          UtilReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer());
          status = dodge(true);
        } else {
          if (UtilReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledAction.DODGE, minimumRoll, false)) {
            status = ActionStatus.WAITING_FOR_RE_ROLL;
          }
        }
      }
    }
    
    if (dodgeModifiers.contains(DodgeModifier.BREAK_TACKLE) && ((status == ActionStatus.SUCCESS))) {
      fUsingBreakTackle = true;
      actingPlayer.markSkillUsed(Skill.BREAK_TACKLE);
      publishParameter(new StepParameter(StepParameterKey.USING_BREAK_TACKLE, fUsingBreakTackle));
    }

    return status;
    
  }
    
  // ByteArray serialization
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnFailure = pByteArray.getString();
  	fCoordinateFrom = pByteArray.getFieldCoordinate();
  	fCoordinateTo = pByteArray.getFieldCoordinate();
  	fDodgeRoll = pByteArray.getByte();
  	fUsingDivingTackle = pByteArray.getBoolean();
  	fUsingBreakTackle = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
    IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, fCoordinateFrom);
    IServerJsonOption.COORDINATE_TO.addTo(jsonObject, fCoordinateTo);
    IServerJsonOption.DODGE_ROLL.addTo(jsonObject, fDodgeRoll);
    IServerJsonOption.USING_DIVING_TACKLE.addTo(jsonObject, fUsingDivingTackle);
    IServerJsonOption.USING_BREAK_TACKLE.addTo(jsonObject, fUsingBreakTackle);
    return jsonObject;
  }
  
  @Override
  public StepMoveDodge initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(jsonObject);
    fCoordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(jsonObject);
    fCoordinateTo = IServerJsonOption.COORDINATE_TO.getFrom(jsonObject);
    fDodgeRoll = IServerJsonOption.DODGE_ROLL.getFrom(jsonObject);
    fUsingDivingTackle = IServerJsonOption.USING_DIVING_TACKLE.getFrom(jsonObject);
    fUsingBreakTackle = IServerJsonOption.USING_BREAK_TACKLE.getFrom(jsonObject);
    return this;
  }
  	
}
