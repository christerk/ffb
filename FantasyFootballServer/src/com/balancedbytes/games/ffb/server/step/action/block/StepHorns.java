package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.report.ReportSkillUse;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle skill HORNS.
 * 
 * @author Kalimar
 */
public class StepHorns extends AbstractStep {
	
	private boolean fUsingHorns;
	
	public StepHorns(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.HORNS;
	}
	
	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
  private void executeStep() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    fUsingHorns = (UtilCards.hasSkill(game, actingPlayer, Skill.HORNS) && (PlayerAction.BLITZ == actingPlayer.getPlayerAction()));
    if (fUsingHorns) {
      actingPlayer.setStrength(actingPlayer.getStrength() + 1);
      getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), Skill.HORNS, true, SkillUse.INCREASE_STRENGTH_BY_1));
    }
    getResult().setNextAction(StepAction.NEXT_STEP);
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addBoolean(fUsingHorns);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fUsingHorns = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = toJsonValueTemp();
    IServerJsonOption.USING_HORNS.addTo(jsonObject, fUsingHorns);
    return jsonObject;
  }
  
  public StepHorns initFrom(JsonValue pJsonValue) {
    initFromTemp(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fUsingHorns = IServerJsonOption.USING_HORNS.getFrom(jsonObject);
    return this;
  }

}
