package com.balancedbytes.games.ffb.server.step.action.move;

import java.util.Set;

import com.balancedbytes.games.ffb.DodgeModifier;
import com.balancedbytes.games.ffb.DodgeModifierFactory;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.dialog.DialogPlayerChoiceParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPlayerChoice;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.model.ServerSkill;
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
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in move sequence to handle the DIVING_TACKLE skill.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL.
 * 
 * Expects stepParameter COORDINATE_FROM to be set by a preceding step.
 * Expects stepParameter COORDINATE_TO to be set by a preceding step.
 * Expects stepParameter DODGE_ROLL to be set by a preceding step.
 * 
 * Sets stepParameter USING_DIVING_TACKLE for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepDivingTackle extends AbstractStep {

  private String fGotoLabelOnSuccess;
  private FieldCoordinate fCoordinateFrom;
  private FieldCoordinate fCoordinateTo;
  private int fDodgeRoll;
  private Boolean fUsingDivingTackle;
  private boolean fUsingBreakTackle;

  public StepDivingTackle(GameState pGameState) {
    super(pGameState);
  }

  public StepId getId() {
    return StepId.DIVING_TACKLE;
  }

  @Override
  public void init(StepParameterSet pParameterSet) {
    if (pParameterSet != null) {
      for (StepParameter parameter : pParameterSet.values()) {
        switch (parameter.getKey()) {
          // mandatory
          case GOTO_LABEL_ON_SUCCESS:
            fGotoLabelOnSuccess = (String) parameter.getValue();
            break;
          default:
            break;
        }
      }
    }
    if (!StringTool.isProvided(fGotoLabelOnSuccess)) {
      throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_SUCCESS + " is not initialized.");
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
    if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
      switch (pReceivedCommand.getId()) {
        case CLIENT_PLAYER_CHOICE:
          ClientCommandPlayerChoice playerChoiceCommand = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
          if (playerChoiceCommand.getPlayerChoiceMode() == PlayerChoiceMode.DIVING_TACKLE) {
            fUsingDivingTackle = StringTool.isProvided(playerChoiceCommand.getPlayerId());
            getGameState().getGame().setDefenderId(playerChoiceCommand.getPlayerId());
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
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (fUsingDivingTackle == null) {
      game.setDefenderId(null);
      fUsingDivingTackle = false;
      if (game.getFieldModel().getPlayer(fCoordinateFrom) == null) {
        Player[] divingTacklers = UtilPlayer.findAdjacentOpposingPlayersWithSkill(game, fCoordinateFrom, ServerSkill.DIVING_TACKLE, true);
        divingTacklers = UtilPlayer.filterThrower(game, divingTacklers);
        if (game.getTurnMode() == TurnMode.DUMP_OFF) {
          divingTacklers = UtilPlayer.filterAttackerAndDefender(game, divingTacklers);
        }
        if (ArrayTool.isProvided(divingTacklers) && (fDodgeRoll > 0)) {
          DodgeModifierFactory modifierFactory = new DodgeModifierFactory();
          Set<DodgeModifier> dodgeModifiers = modifierFactory.findDodgeModifiers(game, fCoordinateFrom, fCoordinateTo, 0);
          dodgeModifiers.add(DodgeModifier.DIVING_TACKLE);
          if (fUsingBreakTackle) {
            dodgeModifiers.add(DodgeModifier.BREAK_TACKLE);
          }
          int minimumRoll = DiceInterpreter.getInstance().minimumRollDodge(game, actingPlayer.getPlayer(), dodgeModifiers);
          int minimumRollWithoutBreakTackle = minimumRoll;
          if (dodgeModifiers.contains(DodgeModifier.BREAK_TACKLE)) {
        	  dodgeModifiers.remove(DodgeModifier.BREAK_TACKLE);
        	  minimumRollWithoutBreakTackle = DiceInterpreter.getInstance().minimumRollDodge(game, actingPlayer.getPlayer(), dodgeModifiers);
        	  dodgeModifiers.add(DodgeModifier.BREAK_TACKLE);
          }
          if (!DiceInterpreter.getInstance().isSkillRollSuccessful(fDodgeRoll, minimumRoll)) {
            String teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
            UtilServerDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(teamId, PlayerChoiceMode.DIVING_TACKLE, divingTacklers, null, 1), true);
            fUsingDivingTackle = null;
          } else if (!DiceInterpreter.getInstance().isSkillRollSuccessful(fDodgeRoll, minimumRollWithoutBreakTackle)) {
        	  // Ask if Diving tackle is going to be used strictly to trigger Break Tackle. The dodge will still succeed.
              String teamId = game.isHomePlaying() ? game.getTeamAway().getId() : game.getTeamHome().getId();
              String[] descriptions = new String[] { "This will NOT trip the dodger, but will force the use of BREAK TACKLE." };
              UtilServerDialog.showDialog(getGameState(), new DialogPlayerChoiceParameter(teamId, PlayerChoiceMode.DIVING_TACKLE, divingTacklers, descriptions, 1), true);
              fUsingDivingTackle = null;
          } else {
            getResult().addReport(new ReportSkillUse(null, ServerSkill.DIVING_TACKLE, false, SkillUse.WOULD_NOT_HELP));
          }
        }
      }
    }
    if (fUsingDivingTackle != null) {
      publishParameter(new StepParameter(StepParameterKey.USING_DIVING_TACKLE, fUsingDivingTackle));
      if (fUsingDivingTackle) {
    	// Implicitly, a DT use is normally only triggered if it makes the dodge fail.
    	  
    	// Check if the dodge is successful with BT (ie. DT was used only to trigger BT)
    	DodgeModifierFactory modifierFactory = new DodgeModifierFactory();
    	Set<DodgeModifier> dodgeModifiers = modifierFactory.findDodgeModifiers(game, fCoordinateFrom, fCoordinateTo, 0);
    	int minimumRoll = DiceInterpreter.getInstance().minimumRollDodge(game, actingPlayer.getPlayer(), dodgeModifiers);
    	if (dodgeModifiers.contains(DodgeModifier.BREAK_TACKLE) && DiceInterpreter.getInstance().isSkillRollSuccessful(fDodgeRoll, minimumRoll)) {
    		// This dodge will be successful with Break Tackle triggered, so mark it as used.
    		  fUsingBreakTackle = true;
    	      actingPlayer.markSkillUsed(ServerSkill.BREAK_TACKLE);
    	      publishParameter(new StepParameter(StepParameterKey.USING_BREAK_TACKLE, fUsingBreakTackle));
    	}
    	
        getResult().addReport(new ReportSkillUse(game.getDefender().getId(), ServerSkill.DIVING_TACKLE, true, SkillUse.STOP_OPPONENT));
        getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnSuccess);
      } else {
        getResult().setNextAction(StepAction.NEXT_STEP);
      }
    }
  }

  // JSON serialization

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_SUCCESS.addTo(jsonObject, fGotoLabelOnSuccess);
    IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, fCoordinateFrom);
    IServerJsonOption.COORDINATE_TO.addTo(jsonObject, fCoordinateTo);
    IServerJsonOption.DODGE_ROLL.addTo(jsonObject, fDodgeRoll);
    IServerJsonOption.USING_DIVING_TACKLE.addTo(jsonObject, fUsingDivingTackle);
    IServerJsonOption.USING_BREAK_TACKLE.addTo(jsonObject, fUsingBreakTackle);
    return jsonObject;
  }

  @Override
  public StepDivingTackle initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnSuccess = IServerJsonOption.GOTO_LABEL_ON_SUCCESS.getFrom(jsonObject);
    fCoordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(jsonObject);
    fCoordinateTo = IServerJsonOption.COORDINATE_TO.getFrom(jsonObject);
    fDodgeRoll = IServerJsonOption.DODGE_ROLL.getFrom(jsonObject);
    fUsingDivingTackle = IServerJsonOption.USING_DIVING_TACKLE.getFrom(jsonObject);
    fUsingBreakTackle = IServerJsonOption.USING_BREAK_TACKLE.getFrom(jsonObject);
    return this;
  }

}
