package com.balancedbytes.games.ffb.server.step.action.foul;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.GameOption;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.report.ReportReferee;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryMode;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in foul sequence to handle the referee and SNEAKY_GIT skill.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 * 
 * Expects stepParameter INJURY_RESULT to be set by a preceding step.
 * 
 * @author Kalimar
 */
public class StepReferee extends AbstractStep {
	
	private String fGotoLabelOnEnd;
	private InjuryResult fInjuryResultDefender;

	public StepReferee(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.REFEREE;
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
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case INJURY_RESULT:
					InjuryResult injuryResult = (InjuryResult) pParameter.getValue();
					if ((injuryResult != null) && (injuryResult.getApothecaryMode() == ApothecaryMode.DEFENDER)) { 
						fInjuryResultDefender = injuryResult;
						return true;
					}
					return false;
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
		if (fInjuryResultDefender != null) {
	    Game game = getGameState().getGame();
	    ActingPlayer actingPlayer = game.getActingPlayer();
	    boolean refereeSpotsFoul = false;
	    if (!UtilCards.isCardActive(game, Card.BLATANT_FOUL)
	      && (!UtilCards.hasSkill(game, actingPlayer, Skill.SNEAKY_GIT)
	    	|| fInjuryResultDefender.isArmorBroken()
	    	|| ((UtilCards.hasSkill(game, actingPlayer, Skill.SNEAKY_GIT) && game.getOptions().getOptionValue(GameOption.SNEAKY_GIT_BAN_TO_KO).isEnabled())))) {
	      int[] armorRoll = fInjuryResultDefender.getArmorRoll();
	      refereeSpotsFoul = (armorRoll[0] == armorRoll[1]);
	    }
	    if (!refereeSpotsFoul && fInjuryResultDefender.isArmorBroken()) {
	      int[] injuryRoll = fInjuryResultDefender.getInjuryRoll();
	      refereeSpotsFoul = (injuryRoll[0] == injuryRoll[1]);
	    }
	    getResult().addReport(new ReportReferee(refereeSpotsFoul));
	    if (refereeSpotsFoul) {
	    	getResult().setSound(Sound.WHISTLE);
	    	getResult().setNextAction(StepAction.NEXT_STEP);
	    } else {
	    	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);
	    }
		}
  }
  
	// ByteArray serialization
	
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnEnd);
  	if (fInjuryResultDefender != null) {
  		pByteList.addBoolean(true);
  		fInjuryResultDefender.addTo(pByteList);
  	} else {
  		pByteList.addBoolean(false);
  	}
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnEnd = pByteArray.getString();
  	if (pByteArray.getBoolean()) {
  		fInjuryResultDefender = new InjuryResult();
  		fInjuryResultDefender.initFrom(pByteArray);
  	} else {
  		fInjuryResultDefender = null;
  	}
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
    if (fInjuryResultDefender != null) {
      IServerJsonOption.INJURY_RESULT_DEFENDER.addTo(jsonObject, fInjuryResultDefender.toJsonValue());
    }
    return jsonObject;
  }
  
  @Override
  public StepReferee initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(jsonObject);
    fInjuryResultDefender = null;
    JsonObject injuryResultDefenderObject = IServerJsonOption.INJURY_RESULT_DEFENDER.getFrom(jsonObject);
    if (injuryResultDefenderObject != null) {
      fInjuryResultDefender = new InjuryResult().initFrom(injuryResultDefenderObject);
    }
    return this;
  }

}
