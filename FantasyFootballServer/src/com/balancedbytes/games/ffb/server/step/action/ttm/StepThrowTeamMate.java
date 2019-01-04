package com.balancedbytes.games.ffb.server.step.action.ttm;

import java.util.Set;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassModifierFactory;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportThrowTeamMateRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPassing;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in ttm sequence to actual throw the team mate.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Expects stepParameter THROWN_PLAYER_ID to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_STATE to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_HAS_BALL to be set by a preceding step.
 * 
 * Pushes new scatterPlayerSequence on the stack.
 * 
 * @author Kalimar
 */
public final class StepThrowTeamMate extends AbstractStepWithReRoll {
	
  private String fGotoLabelOnFailure;
  private String fThrownPlayerId;
  private PlayerState fThrownPlayerState;
  private boolean fThrownPlayerHasBall;
	
	public StepThrowTeamMate(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.THROW_TEAM_MATE;
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
				case THROWN_PLAYER_ID:
					fThrownPlayerId = (String) pParameter.getValue();
					return true;
				case THROWN_PLAYER_STATE:
					fThrownPlayerState = (PlayerState) pParameter.getValue();
					return true;
				case THROWN_PLAYER_HAS_BALL:
					fThrownPlayerHasBall = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
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
		if ((pReceivedCommand != null) && (commandStatus == StepCommandStatus.UNHANDLED_COMMAND)) {
			switch (pReceivedCommand.getId()) {
			  case CLIENT_USE_SKILL:
			    ClientCommandUseSkill useSkillCommand = (ClientCommandUseSkill) pReceivedCommand.getCommand();
			    switch (useSkillCommand.getSkill()) {
			      case PASS:
			        if (useSkillCommand.isSkillUsed()) {
			          setReRollSource(ReRollSource.PASS);
			        } else {
			          setReRollSource(null);
			        }
			        break;
		        default:
		        	break;
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
    ActingPlayer actingPlayer = game.getActingPlayer();
    actingPlayer.setHasPassed(true);
    game.setConcessionPossible(false);
    game.getTurnData().setPassUsed(true);
    UtilServerDialog.hideDialog(getGameState());
    Player thrower = game.getActingPlayer().getPlayer();
    boolean doRoll = true;
    if (ReRolledAction.THROW_TEAM_MATE == getReRolledAction()) {
      if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), thrower)) {
      	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
        doRoll = false;
      }
    }
    if (doRoll) {
      PassModifierFactory passModifierFactory = new PassModifierFactory();
      FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(thrower);
      PassingDistance passingDistance = UtilPassing.findPassingDistance(game, throwerCoordinate, game.getPassCoordinate(), true);
      Set<PassModifier> passModifiers = passModifierFactory.findPassModifiers(game, thrower, passingDistance, true);
      int minimumRoll = DiceInterpreter.getInstance().minimumRollThrowTeamMate(thrower, passingDistance, passModifiers);
      int roll = getGameState().getDiceRoller().rollSkill();
      boolean successful = !DiceInterpreter.getInstance().isPassFumble(roll, actingPlayer.getPlayer(), passingDistance, passModifiers);
      PassModifier[] passModifierArray = passModifierFactory.toArray(passModifiers);
      boolean reRolled = ((getReRolledAction() == ReRolledAction.THROW_TEAM_MATE) && (getReRollSource() != null));
      getResult().addReport(new ReportThrowTeamMateRoll(thrower.getId(), successful, roll, minimumRoll, reRolled, passModifierArray, passingDistance, fThrownPlayerId));
      if (successful) {
  		Player thrownPlayer = game.getPlayerById(fThrownPlayerId);
  		boolean hasSwoop = thrownPlayer != null && thrownPlayer.hasSkill(Skill.SWOOP);
      	SequenceGenerator.getInstance().pushScatterPlayerSequence(getGameState(), fThrownPlayerId, fThrownPlayerState, fThrownPlayerHasBall, throwerCoordinate, hasSwoop, true);
      	getResult().setNextAction(StepAction.NEXT_STEP);
      } else {
        if (getReRolledAction() != ReRolledAction.THROW_TEAM_MATE) {
          setReRolledAction(ReRolledAction.THROW_TEAM_MATE);
          if (UtilCards.hasSkill(game, thrower, Skill.PASS)) {
            UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(thrower.getId(), Skill.PASS, minimumRoll), false);
          } else {
            if (!UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledAction.THROW_TEAM_MATE, minimumRoll, false)) {
            	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
            }
          }
        } else {
        	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
        }
      }
    }
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
    IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, fThrownPlayerId);
    IServerJsonOption.THROWN_PLAYER_STATE.addTo(jsonObject, fThrownPlayerState);
    IServerJsonOption.THROWN_PLAYER_HAS_BALL.addTo(jsonObject, fThrownPlayerHasBall);
    return jsonObject;
  }
  
  @Override
  public StepThrowTeamMate initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(jsonObject);
    fThrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(jsonObject);
    fThrownPlayerState = IServerJsonOption.THROWN_PLAYER_STATE.getFrom(jsonObject);
    fThrownPlayerHasBall = IServerJsonOption.THROWN_PLAYER_HAS_BALL.getFrom(jsonObject);
    return this;
  }
}
