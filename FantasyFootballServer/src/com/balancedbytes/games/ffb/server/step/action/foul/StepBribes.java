package com.balancedbytes.games.ffb.server.step.action.foul;

import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.dialog.DialogArgueTheCallParameter;
import com.balancedbytes.games.ffb.dialog.DialogBribesParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.commands.ClientCommandArgueTheCall;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseInducement;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.report.ReportArgueTheCallRoll;
import com.balancedbytes.games.ffb.report.ReportBribesRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerInducementUse;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in foul sequence to handle bribes.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 * 
 * Sets stepParameter FOULER_HAS_BALL for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepBribes extends AbstractStep {
	
	private String fGotoLabelOnEnd;
	private Boolean fArgueTheCallChoice;
	private Boolean fArgueTheCallSuccessful;
  private Boolean fBribesChoice;
  private Boolean fBribeSuccessful;

	public StepBribes(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.BRIBES;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  				case GOTO_LABEL_ON_END:
  					fGotoLabelOnEnd = (String) parameter.getValue();
  					break;
					default:
						break;
  			}
  		}
  	}
  	if (!StringTool.isProvided(fGotoLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
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
    if ((pReceivedCommand != null) && (commandStatus == StepCommandStatus.UNHANDLED_COMMAND)) {
      Game game = getGameState().getGame();
      ActingPlayer actingPlayer = game.getActingPlayer();
      switch (pReceivedCommand.getId()) {
        case CLIENT_ARGUE_THE_CALL:
          ClientCommandArgueTheCall argueTheCallCommand = (ClientCommandArgueTheCall) pReceivedCommand.getCommand();
          fArgueTheCallChoice = argueTheCallCommand.hasPlayerId(actingPlayer.getPlayerId());
          fArgueTheCallSuccessful = null;
          commandStatus = StepCommandStatus.EXECUTE_STEP;
          break;
        case CLIENT_USE_INDUCEMENT:
          ClientCommandUseInducement inducementCommand = (ClientCommandUseInducement) pReceivedCommand.getCommand();
          if (InducementType.BRIBES == inducementCommand.getInducementType()) {
            fBribesChoice = inducementCommand.hasPlayerId(actingPlayer.getPlayerId());
            fBribeSuccessful = null;
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
    if (fArgueTheCallChoice == null) {
      boolean foulerHasBall = game.getFieldModel().getBallCoordinate().equals(game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer()));
      publishParameter(new StepParameter(StepParameterKey.FOULER_HAS_BALL, foulerHasBall));
      askForArgueTheCall();
    }
    if ((fArgueTheCallChoice != null) && (fArgueTheCallSuccessful == null)) {
      int roll = getGameState().getDiceRoller().rollArgueTheCall();
      fArgueTheCallSuccessful = DiceInterpreter.getInstance().isArgueTheCallSuccessful(roll);
      boolean coachBanned = DiceInterpreter.getInstance().isCoachBanned(roll);
      getResult().addReport(new ReportArgueTheCallRoll(actingPlayer.getPlayerId(), fArgueTheCallSuccessful, coachBanned, roll));
      if (coachBanned) {
        game.getTurnData().setCoachBanned(true);
      }
    }
    if ((fBribesChoice == null) && ((fArgueTheCallSuccessful == null) || !fArgueTheCallSuccessful)) {
      askForBribes();
    }
    if ((fArgueTheCallChoice != null) && (fBribesChoice != null)) {
    	if (fBribesChoice) {
		  	if (fBribeSuccessful == null) {
		      Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
		      if (UtilServerInducementUse.useInducement(getGameState(), team, InducementType.BRIBES, 1)) {
		        int roll = getGameState().getDiceRoller().rollBribes();
		        fBribeSuccessful = DiceInterpreter.getInstance().isBribesSuccessful(roll);
		        getResult().addReport(new ReportBribesRoll(actingPlayer.getPlayerId(), fBribeSuccessful, roll));
		        if (!fBribeSuccessful) {
		        	askForBribes();
		        }
		      }
		  	}
      }
    }
    if (((fArgueTheCallSuccessful != null) && fArgueTheCallSuccessful) || ((fBribeSuccessful != null) && fBribeSuccessful)) {
      getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
      return;
    }
    if ((fArgueTheCallChoice != null) && (fBribesChoice != null)) {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
	
  private void askForBribes() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (game.getTurnData().getInducementSet().hasUsesLeft(InducementType.BRIBES)) {
      Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
      DialogBribesParameter dialogParameter = new DialogBribesParameter(team.getId() , 1);
      dialogParameter.addPlayerId(actingPlayer.getPlayerId());
      UtilServerDialog.showDialog(getGameState(), dialogParameter);
    	fBribesChoice = null;
    } else {
    	fBribesChoice = false;
    }
  }
  
  
  private void askForArgueTheCall() {
    Game game = getGameState().getGame();
    if (!UtilGameOption.isOptionEnabled(game, GameOptionId.ARGUE_THE_CALL)) {
      fArgueTheCallChoice = false;
      return;
    }
    if (game.getTurnData().isCoachBanned()) {
      fArgueTheCallChoice = false;
      return;
    }
    ActingPlayer actingPlayer = game.getActingPlayer();
    Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
    DialogArgueTheCallParameter dialogParameter = new DialogArgueTheCallParameter(team.getId()); 
    dialogParameter.addPlayerId(actingPlayer.getPlayerId());
    UtilServerDialog.showDialog(getGameState(), dialogParameter);
    fArgueTheCallChoice = null;
  }

  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
    IServerJsonOption.BRIBES_CHOICE.addTo(jsonObject, fBribesChoice);
    IServerJsonOption.BRIBE_SUCCESSFUL.addTo(jsonObject, fBribeSuccessful);
    return jsonObject;
  }
  
  @Override
  public StepBribes initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(jsonObject);
    fBribesChoice = IServerJsonOption.BRIBES_CHOICE.getFrom(jsonObject);
    fBribeSuccessful = IServerJsonOption.BRIBE_SUCCESSFUL.getFrom(jsonObject);
    return this;
  }

}
