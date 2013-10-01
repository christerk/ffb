package com.balancedbytes.games.ffb.server.step.phase.kickoff;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.Direction;
import com.balancedbytes.games.ffb.DirectionFactory;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.Team;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogKickSkillParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportKickoffScatter;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.util.UtilCatchScatterThrowIn;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * Step in kickoff sequence to scatter the kick.
 * 
 * Expects stepParameter KICKOFF_START_COORDINATE to be set by a preceding step.
 *
 * Sets stepParameter KICKING_PLAYER_COORDINATE for all steps on the stack.
 * Sets stepParameter KICKOFF_BOUNDS for all steps on the stack.
 * Sets stepParameter TOUCHBACK for all steps on the stack.
 * 
 * @author Kalimar
 */
public final class StepKickoffScatterRoll extends AbstractStep {
	
	protected FieldCoordinate fKickoffStartCoordinate;
	protected Boolean fUseKickChoice;
	protected Direction fScatterDirection;
	protected int fScatterDistance;
	protected FieldCoordinate fKickingPlayerCoordinate;
	protected FieldCoordinateBounds fKickoffBounds;
	protected boolean fTouchback;
	
	public StepKickoffScatterRoll(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.KICKOFF_SCATTER_ROLL;
	}
	
	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case KICKOFF_START_COORDINATE:
					fKickoffStartCoordinate = (FieldCoordinate) pParameter.getValue();
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
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pNetCommand.getId()) {
	      case CLIENT_USE_SKILL:
	        ClientCommandUseSkill skillUseCommand = (ClientCommandUseSkill) pNetCommand;
	        if (skillUseCommand.getSkill() == Skill.KICK) {
	        	fUseKickChoice = skillUseCommand.isSkillUsed();
	          commandStatus = StepCommandStatus.EXECUTE_STEP;
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

  private void executeStep() {

  	Game game = getGameState().getGame();
    Player kickingPlayer = findKickingPlayer();

    if (fUseKickChoice == null) {

      int rollScatterDirection = getGameState().getDiceRoller().rollScatterDirection();
      fScatterDirection = DiceInterpreter.getInstance().interpretScatterDirectionRoll(rollScatterDirection);
      fScatterDistance = getGameState().getDiceRoller().rollScatterDistance();

      FieldCoordinate ballCoordinateEnd = UtilCatchScatterThrowIn.findScatterCoordinate(fKickoffStartCoordinate, fScatterDirection, fScatterDistance);
      getResult().addReport(new ReportKickoffScatter(ballCoordinateEnd, fScatterDirection, rollScatterDirection, fScatterDistance));
      
      if (kickingPlayer != null) {
      	fKickingPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(kickingPlayer);
        if (UtilCards.hasSkill(game, kickingPlayer, Skill.KICK) && ((game.isHomePlaying()&& FieldCoordinateBounds.CENTER_FIELD_HOME.isInBounds(game.getFieldModel().getPlayerCoordinate(kickingPlayer))) || (!game.isHomePlaying() && FieldCoordinateBounds.CENTER_FIELD_AWAY.isInBounds(game.getFieldModel().getPlayerCoordinate(kickingPlayer))))) {
          FieldCoordinate ballCoordinateEndWithKick = UtilCatchScatterThrowIn.findScatterCoordinate(fKickoffStartCoordinate, fScatterDirection, fScatterDistance / 2);
          UtilDialog.showDialog(getGameState(), new DialogKickSkillParameter(kickingPlayer.getId(),ballCoordinateEnd, ballCoordinateEndWithKick));
        } else {
        	fUseKickChoice = false;
        }
      } else {
        if (game.isHomePlaying()) {
        	fKickingPlayerCoordinate = new FieldCoordinate(0, 7);
        } else {
        	fKickingPlayerCoordinate = new FieldCoordinate(25, 7);
        }
        fUseKickChoice = false;
      }

    }
    
    if (fUseKickChoice != null) {
    
      int distance = fUseKickChoice ? fScatterDistance / 2 : fScatterDistance;
      FieldCoordinate ballCoordinateEnd = UtilCatchScatterThrowIn.findScatterCoordinate(fKickoffStartCoordinate, fScatterDirection, distance);
      FieldCoordinate lastValidCoordinate = ballCoordinateEnd;
      while (!FieldCoordinateBounds.FIELD.isInBounds(lastValidCoordinate)) {
        lastValidCoordinate = UtilCatchScatterThrowIn.findScatterCoordinate(fKickoffStartCoordinate, fScatterDirection, --distance);
      }
      game.getFieldModel().setBallInPlay(false);
      game.getFieldModel().setBallCoordinate(lastValidCoordinate);
      game.getFieldModel().setBallMoving(true);
      
      if (game.isHomePlaying() && FieldCoordinateBounds.HALF_AWAY.isInBounds(ballCoordinateEnd)) {
      	fKickoffBounds = FieldCoordinateBounds.HALF_AWAY;
      }
      if (!game.isHomePlaying() && FieldCoordinateBounds.HALF_HOME.isInBounds(ballCoordinateEnd)) {
      	fKickoffBounds = FieldCoordinateBounds.HALF_HOME;
      }
      fTouchback = (fKickoffBounds == null);
      
      if (fUseKickChoice) {
        getResult().addReport(new ReportSkillUse(kickingPlayer.getId(), Skill.KICK, true, SkillUse.HALVE_KICKOFF_SCATTER));
      }

      publishParameter(new StepParameter(StepParameterKey.KICKING_PLAYER_COORDINATE, fKickingPlayerCoordinate));
      publishParameter(new StepParameter(StepParameterKey.KICKOFF_BOUNDS, fKickoffBounds));
      publishParameter(new StepParameter(StepParameterKey.TOUCHBACK, fTouchback));
      getResult().setNextAction(StepAction.NEXT_STEP);
  
    }
    
  }
  
  private Player findKickingPlayer() {
    Game game = getGameState().getGame();
    Player kickingPlayer = null;
    Team kickingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
    Player[] players = kickingTeam.getPlayers();
    List<Player> playersOnField = new ArrayList<Player>();
    for (int i = 0; i < players.length; i++) {
      FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(players[i]);
      if ((playerCoordinate != null) && !playerCoordinate.isBoxCoordinate()) {
        playersOnField.add(players[i]);
      }
      if ((game.isHomePlaying() && FieldCoordinateBounds.CENTER_FIELD_HOME.isInBounds(playerCoordinate))
        || (!game.isHomePlaying() && FieldCoordinateBounds.CENTER_FIELD_AWAY.isInBounds(playerCoordinate))) {
        if (UtilCards.hasSkill(game, players[i], Skill.KICK)) {
          kickingPlayer = players[i];
          break;
        } else {
          if (kickingPlayer != null) {
            FieldCoordinate kickingPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(kickingPlayer);
            if ((game.isHomePlaying() && (playerCoordinate.getX() < kickingPlayerCoordinate.getX()))
              || (!game.isHomePlaying() && (playerCoordinate.getX() > kickingPlayerCoordinate.getX()))) {
              kickingPlayer = players[i];
            }
          } else {
            kickingPlayer = players[i];
          }           
        }
      }
    }
    if (kickingPlayer == null) {
      kickingPlayer = getGameState().getDiceRoller().randomPlayer(playersOnField.toArray(new Player[playersOnField.size()]));
    }
    return kickingPlayer;
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }

  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addFieldCoordinate(fKickoffStartCoordinate);
  	pByteList.addBoolean(fUseKickChoice);
  	pByteList.addByte((byte) ((fScatterDirection != null) ? fScatterDirection.getId() : 0));
  	pByteList.addByte((byte) fScatterDistance);
  	pByteList.addFieldCoordinate(fKickingPlayerCoordinate);
  	if (fKickoffBounds != null) {
  		pByteList.addBoolean(true);
  		fKickoffBounds.addTo(pByteList);
  	} else {
  		pByteList.addBoolean(false);
  	}
  	pByteList.addBoolean(fTouchback);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fKickoffStartCoordinate = pByteArray.getFieldCoordinate();
  	fUseKickChoice = pByteArray.getBoolean();
  	fScatterDirection = new DirectionFactory().forId(pByteArray.getByte());
  	fScatterDistance = pByteArray.getByte();
  	fKickingPlayerCoordinate = pByteArray.getFieldCoordinate();
  	if (pByteArray.getBoolean()) {
  		fKickoffBounds = new FieldCoordinateBounds();
  		fKickoffBounds.initFrom(pByteArray);
  	} else {
  		fKickoffBounds = null;
  	}
  	fTouchback = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }

}
