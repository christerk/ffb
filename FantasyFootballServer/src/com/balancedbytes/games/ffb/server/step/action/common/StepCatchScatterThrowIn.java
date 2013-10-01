package com.balancedbytes.games.ffb.server.step.action.common;

import java.util.Set;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CatchModifier;
import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.GameOption;
import com.balancedbytes.games.ffb.InducementDuration;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogPlayerChoiceParameter;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.AnimationType;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPlayerChoice;
import com.balancedbytes.games.ffb.report.ReportCatchRoll;
import com.balancedbytes.games.ffb.report.ReportScatterBall;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.report.ReportThrowIn;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.UtilSteps;
import com.balancedbytes.games.ffb.server.util.UtilCatchScatterThrowIn;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilInjury;
import com.balancedbytes.games.ffb.server.util.UtilReRoll;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * Step in any sequence to handle scattering the ball and throw-ins.
 * Consumes all expected stepParameters.
 * 
 * Expects stepParameter CATCH_SCATTER_THROWIN_MODE to be set by a preceding step.
 * Expects stepParameter THROW_IN_COORDINATE to be set by a preceding step.
 * 
 * Sets stepParameter CATCHER_ID for all steps on the stack.
 * Sets stepParameter INJURY_RESULT for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepCatchScatterThrowIn extends AbstractStepWithReRoll {
	
	private String fCatcherId;
	private FieldCoordinateBounds fScatterBounds;
	private CatchScatterThrowInMode fCatchScatterThrowInMode;
	private FieldCoordinate fThrowInCoordinate;
	private Boolean fDivingCatchChoice;
	private boolean fBombMode;
	
	public StepCatchScatterThrowIn(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.CATCH_SCATTER_THROW_IN;
	}
	
	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
	@Override
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pNetCommand.getId()) {
	      case CLIENT_PLAYER_CHOICE:
	        ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pNetCommand;
	        if (PlayerChoiceMode.DIVING_CATCH == playerChoiceCommand.getMode()) {
            fDivingCatchChoice = StringTool.isProvided(playerChoiceCommand.getPlayerId());
            fCatcherId = playerChoiceCommand.getPlayerId();
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
	
	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case CATCH_SCATTER_THROW_IN_MODE:
					fCatchScatterThrowInMode = (CatchScatterThrowInMode) pParameter.getValue();
					pParameter.consume();
					return true;
				case THROW_IN_COORDINATE:
					fThrowInCoordinate = (FieldCoordinate) pParameter.getValue();
					pParameter.consume();
					return true;
				default:
					break;
			}
		}
		return false;
	}
    
  private void executeStep() {
  	getResult().reset();
  	Game game = getGameState().getGame();
  	UtilDialog.hideDialog(getGameState());
  	getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, "executeStep(" + fCatchScatterThrowInMode + ")");
    if (fCatchScatterThrowInMode == null) {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    	return;
    }
  	if (game.getTurnMode() == TurnMode.KICKOFF) {
	    if (game.isHomePlaying()) {
	    	fScatterBounds = FieldCoordinateBounds.HALF_AWAY;
	    } else {
	    	fScatterBounds = FieldCoordinateBounds.HALF_HOME;
	    }
  	} else {
  		fScatterBounds = FieldCoordinateBounds.FIELD;
  	}
  	Player playerUnderBall = game.getFieldModel().getPlayer(game.getFieldModel().getBallCoordinate());
    switch (fCatchScatterThrowInMode) {
    	case CATCH_BOMB:
    	case CATCH_ACCURATE_BOMB:
    		fBombMode = true;
    		if (!StringTool.isProvided(fCatcherId)) {
    	  	Player playerUnderBomb = game.getFieldModel().getPlayer(game.getFieldModel().getBombCoordinate());
    			fCatcherId = (playerUnderBomb != null) ? playerUnderBomb.getId() : null;
    		}
        if (StringTool.isProvided(fCatcherId)) {
          PlayerState catcherState = game.getFieldModel().getPlayerState(game.getPlayerById(fCatcherId));
          if ((catcherState != null) && catcherState.hasTacklezones() && game.getFieldModel().isBombMoving()) {
          	fCatchScatterThrowInMode = catchBall();
          } else {
          	fCatchScatterThrowInMode = CatchScatterThrowInMode.SCATTER_BALL;
          }
        } else {
        	fCatchScatterThrowInMode = CatchScatterThrowInMode.CATCH_BOMB;
        	fCatchScatterThrowInMode = divingCatch(game.getFieldModel().getBombCoordinate());
        }
        if ((fCatchScatterThrowInMode == CatchScatterThrowInMode.FAILED_CATCH) || (fCatchScatterThrowInMode == CatchScatterThrowInMode.SCATTER_BALL)) {
        	game.getFieldModel().setBombMoving(true);
        	fCatchScatterThrowInMode = null;
        }
        break;
      case CATCH_ACCURATE_PASS:
      case CATCH_HAND_OFF:
      case CATCH_SCATTER:
    		if (!StringTool.isProvided(fCatcherId)) {
    			fCatcherId = (playerUnderBall != null) ? playerUnderBall.getId() : null;
    		}
        if (StringTool.isProvided(fCatcherId)) {
          PlayerState catcherState = game.getFieldModel().getPlayerState(game.getPlayerById(fCatcherId));
          if ((catcherState != null) && catcherState.hasTacklezones() && game.getFieldModel().isBallInPlay() && game.getFieldModel().isBallMoving()) {
          	fCatchScatterThrowInMode = catchBall();
          } else {
          	fCatchScatterThrowInMode = CatchScatterThrowInMode.SCATTER_BALL;
          }
        } else {
        	fCatchScatterThrowInMode = CatchScatterThrowInMode.SCATTER_BALL;
        }
        break;
      case CATCH_KICKOFF:
      case CATCH_THROW_IN:
      case CATCH_MISSED_PASS:
        if (playerUnderBall != null) {
        	fCatchScatterThrowInMode = CatchScatterThrowInMode.CATCH_SCATTER;
        } else {
        	fCatchScatterThrowInMode = divingCatch(game.getFieldModel().getBallCoordinate());
        }
      	break;
      case THROW_IN:
        if (fThrowInCoordinate != null) {
        	fCatchScatterThrowInMode = throwInBall();
        } else {
        	fCatchScatterThrowInMode = CatchScatterThrowInMode.SCATTER_BALL;
        }
        break;
      case FAILED_CATCH:
      case FAILED_PICK_UP:
      	if ((playerUnderBall != null) && game.getFieldModel().isBallInPlay() && game.getOptions().getOptionValue(GameOption.SPIKED_BALL).isEnabled()) {
          InjuryResult injuryResultCatcher = UtilInjury.handleInjury(this, InjuryType.STAB, null, playerUnderBall, game.getFieldModel().getBallCoordinate(), null, ApothecaryMode.CATCHER);
          if (injuryResultCatcher.isArmorBroken()) {
            UtilInjury.dropPlayer(this, playerUnderBall);
          }
          publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultCatcher));
      	}
      	// drop through to regular scatter
      case SCATTER_BALL:
        if (game.getFieldModel().isBallInPlay()) {
        	fCatchScatterThrowInMode = scatterBall();
        } else {
        	fCatchScatterThrowInMode = null;
        }
        break;
      default:
      	break;
    }
    if ((getReRolledAction() != null) || (game.getDialogParameter() != null)) {
    	getResult().setNextAction(StepAction.CONTINUE);
    } else {
      // repeat this step until it is finished
      if (fCatchScatterThrowInMode != null) {
      	getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, "pushCurrentStepOnStack()");
      	fDivingCatchChoice = null;
      	getGameState().pushCurrentStepOnStack();
      } else {
      	Player catcher = null;
      	if (fBombMode) {
      		catcher = !game.getFieldModel().isBombMoving() ? game.getFieldModel().getPlayer(game.getFieldModel().getBombCoordinate()) : null;
      	} else {
        	catcher = game.getFieldModel().getPlayer(game.getFieldModel().getBallCoordinate());
      	}
      	publishParameter(new StepParameter(StepParameterKey.CATCHER_ID, (catcher != null) ? catcher.getId() : null));
      	deactivateCards();
      }
    	getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  private void deactivateCards() {
  	Game game = getGameState().getGame();
  	for (Player player : game.getPlayers()) {
    	for (Card card : game.getFieldModel().getCards(player)) {
    		if ((InducementDuration.WHILE_HOLDING_THE_BALL == card.getDuration()) && !UtilPlayer.hasBall(game, player)) {
    			UtilSteps.deactivateCard(this, card);
    		}
    	}
  	}
  }
  
  private CatchScatterThrowInMode divingCatch(FieldCoordinate pCoordinate) {
  	Game game = getGameState().getGame();
  	if (fDivingCatchChoice == null) { 
    	fCatcherId = null;
    	Player[] divingCatchersHome = UtilCatchScatterThrowIn.findDivingCatchers(getGameState(), game.getTeamHome(), pCoordinate);
    	Player[] divingCatchersAway = UtilCatchScatterThrowIn.findDivingCatchers(getGameState(), game.getTeamAway(), pCoordinate);
    	if (ArrayTool.isProvided(divingCatchersHome) && ArrayTool.isProvided(divingCatchersAway)) {
      	fDivingCatchChoice = false;
    		getResult().addReport(new ReportSkillUse(Skill.DIVING_CATCH, false, SkillUse.CANCEL_DIVING_CATCH));
    	} else if (ArrayTool.isProvided(divingCatchersHome)) {
    		UtilDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(game.getTeamHome().getId(), PlayerChoiceMode.DIVING_CATCH, divingCatchersHome, null, 1));
    	} else if (ArrayTool.isProvided(divingCatchersAway)) {
    		UtilDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(game.getTeamAway().getId(), PlayerChoiceMode.DIVING_CATCH, divingCatchersAway, null, 1));
    	} else {
      	fDivingCatchChoice = false;
    	}
  	}
    if (fDivingCatchChoice != null) { 
  	  if (fDivingCatchChoice) {
    		Player divingCatcher = game.getPlayerById(fCatcherId);
    		if (getReRollSource() == null) {
    			getResult().addReport(new ReportSkillUse(divingCatcher.getId(), Skill.DIVING_CATCH, true, SkillUse.CATCH_BALL));
    		}
    		return catchBall();
  	  } else {
  	  	return CatchScatterThrowInMode.SCATTER_BALL;
  	  }
    }
    return fCatchScatterThrowInMode;
  }

  private CatchScatterThrowInMode catchBall() {
  	
  	getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, "catchBall()");
  
    Game game = getGameState().getGame();   
  	Player catcher = game.getPlayerById(fCatcherId); 
  	if ((catcher == null) || UtilCards.hasSkill(game, catcher, Skill.NO_HANDS)) {
  		return CatchScatterThrowInMode.SCATTER_BALL;
  	}
    FieldCoordinate catcherCoordinate = game.getFieldModel().getPlayerCoordinate(catcher);
  	
  	boolean doRoll = true;
    if (ReRolledAction.CATCH == getReRolledAction()) {
      if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), catcher)) {
      	doRoll = false;
      }
    }

    if (doRoll) {
	    
    	Set<CatchModifier> catchModifiers = CatchModifier.findCatchModifiers(game, catcher, fCatchScatterThrowInMode);
	    int minimumRoll = DiceInterpreter.getInstance().minimumRollCatch(catcher, catchModifiers);
	    boolean reRolled = ((getReRolledAction() == ReRolledAction.CATCH) && (getReRollSource() != null));
	    int roll = getGameState().getDiceRoller().rollSkill();
	    boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
	    getResult().addReport(new ReportCatchRoll(catcher.getId(), successful, roll, minimumRoll, CatchModifier.toArray(catchModifiers), reRolled, fCatchScatterThrowInMode.isBomb()));

	    if (successful) {

	    	if (fCatchScatterThrowInMode.isBomb()) {
			    game.getFieldModel().setBombCoordinate(catcherCoordinate);
			    game.getFieldModel().setBombMoving(false);
	    	} else {
			    game.getFieldModel().setBallCoordinate(catcherCoordinate);
			    game.getFieldModel().setBallMoving(false);
	    	}
	      getResult().setSound(Sound.CATCH);
	      setReRolledAction(null);
	      if (((fCatchScatterThrowInMode == CatchScatterThrowInMode.CATCH_HAND_OFF) || (fCatchScatterThrowInMode == CatchScatterThrowInMode.CATCH_ACCURATE_PASS))
      		&& (game.getTurnMode() != TurnMode.DUMP_OFF)
      		&& ((game.isHomePlaying() && game.getTeamAway().hasPlayer(catcher)) || (!game.isHomePlaying() && game.getTeamHome().hasPlayer(catcher)))
      	) {
	      	publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
	      }
	      return null;
	    
	    } else {
	      if (getReRolledAction() != ReRolledAction.CATCH) {
	        if (UtilCards.hasSkill(game, catcher, Skill.CATCH)) {
	          setReRolledAction(ReRolledAction.CATCH);
	          setReRollSource(ReRollSource.CATCH);
	          return catchBall();
	        } else {
	        	if (UtilReRoll.askForReRollIfAvailable(getGameState(), catcher, ReRolledAction.CATCH, minimumRoll, false)) {
	            setReRolledAction(ReRolledAction.CATCH);
	        		return fCatchScatterThrowInMode;
	        	}
	        }
	      }
	    }
	    
    }

    setReRolledAction(null);
    if (catcherCoordinate != null) {
    	if (fCatchScatterThrowInMode.isBomb()) {
    		game.getFieldModel().setBombCoordinate(catcherCoordinate);
    		game.getFieldModel().setBombMoving(true);
    	} else {
		    game.getFieldModel().setBallCoordinate(catcherCoordinate);
		    game.getFieldModel().setBallMoving(true);
    	}
    }
  	return CatchScatterThrowInMode.FAILED_CATCH;
    
  }
  
  private CatchScatterThrowInMode scatterBall() {
    
  	getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, "scatterBall()");

  	Game game = getGameState().getGame();
    setReRolledAction(null);
    setReRollSource(null);

    int roll = getGameState().getDiceRoller().rollScatterDirection();
    Direction direction = DiceInterpreter.getInstance().interpretScatterDirectionRoll(roll);
    FieldCoordinate ballCoordinateStart = game.getFieldModel().getBallCoordinate();
    FieldCoordinate ballCoordinateEnd = UtilCatchScatterThrowIn.findScatterCoordinate(ballCoordinateStart, direction, 1);
    FieldCoordinate lastValidCoordinate = fScatterBounds.isInBounds(ballCoordinateEnd) ? ballCoordinateEnd : ballCoordinateStart;
    getResult().addReport(new ReportScatterBall(new Direction[] { direction }, new int[] { roll }, false));
    getResult().setSound(Sound.BOUNCE);
    
    game.getFieldModel().setBallCoordinate(ballCoordinateEnd);
    game.getFieldModel().setBallMoving(true);
    
    if (fScatterBounds.isInBounds(ballCoordinateEnd)) {
      Player player = game.getFieldModel().getPlayer(ballCoordinateEnd);
      if (player != null) {
        PlayerState playerState = game.getFieldModel().getPlayerState(player);
        if (playerState.hasTacklezones()) {
          fCatcherId = player.getId();
          return CatchScatterThrowInMode.CATCH_SCATTER;
        } else {
          return CatchScatterThrowInMode.SCATTER_BALL;
        }
      }
    } else {
      if (fScatterBounds.equals(FieldCoordinateBounds.FIELD)) {
        fThrowInCoordinate = lastValidCoordinate;
        return CatchScatterThrowInMode.THROW_IN;
      } else {
      	publishParameter(new StepParameter(StepParameterKey.TOUCHBACK, true));
      }
    }
    
    return null;
    
  }
  
  private CatchScatterThrowInMode throwInBall() {
    
  	getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, "throwInBall()");

  	Game game = getGameState().getGame();
    DiceRoller diceRoller = getGameState().getDiceRoller();
    DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();
    FieldCoordinate ballCoordinateStart = fThrowInCoordinate;
  	fCatcherId = null;
    
    int directionRoll = diceRoller.rollThrowInDirection();
    Direction direction = diceInterpreter.interpretThrowInDirectionRoll(ballCoordinateStart, directionRoll);
    int[] distanceRoll = diceRoller.rollThrowInDistance();
    int distance = distanceRoll[0] + distanceRoll[1];
    FieldCoordinate ballCoordinateEnd = ballCoordinateStart;
    FieldCoordinate lastValidCoordinate = ballCoordinateEnd;
    for (int i = 0; i < distance; i++) {
    	ballCoordinateEnd = UtilCatchScatterThrowIn.findScatterCoordinate(ballCoordinateStart, direction, i);
    	if (FieldCoordinateBounds.FIELD.isInBounds(ballCoordinateEnd)) {
    		lastValidCoordinate = ballCoordinateEnd;
    	}
    }

    getResult().addReport(new ReportThrowIn(direction, directionRoll, distanceRoll));
    getResult().setAnimation(new Animation(AnimationType.PASS, ballCoordinateStart, lastValidCoordinate, null));
    
    game.getFieldModel().setBallMoving(true);
    
    if (ballCoordinateEnd.equals(lastValidCoordinate)) {
      game.getFieldModel().setBallCoordinate(lastValidCoordinate);
    	fThrowInCoordinate = null;
      return CatchScatterThrowInMode.CATCH_THROW_IN;
    } else {
      game.getFieldModel().setBallCoordinate(null);
    	fThrowInCoordinate = lastValidCoordinate;
    	return CatchScatterThrowInMode.THROW_IN;
    }
    
  }
  
  // ByteArraySerialization
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fCatcherId);
  	if (fScatterBounds != null) {
  		pByteList.addBoolean(true);
  		fScatterBounds.addTo(pByteList);
  	} else {
  		pByteList.addBoolean(false);
  	}
  	pByteList.addByte((byte) ((fCatchScatterThrowInMode != null) ? fCatchScatterThrowInMode.getId() : 0));
  	pByteList.addFieldCoordinate(fThrowInCoordinate);
  	pByteList.addBoolean(fDivingCatchChoice);
  	pByteList.addBoolean(fBombMode);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fCatcherId = pByteArray.getString();
  	if (pByteArray.getBoolean()) {
  		fScatterBounds = new FieldCoordinateBounds();
  		fScatterBounds.initFrom(pByteArray);
  	} else {
  		fScatterBounds = null;
  	}
  	fCatchScatterThrowInMode = CatchScatterThrowInMode.fromId(pByteArray.getByte());
  	fThrowInCoordinate = pByteArray.getFieldCoordinate();
  	fDivingCatchChoice = pByteArray.getBoolean();
  	fBombMode = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
    
}
