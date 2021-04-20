package com.fumbbl.ffb.server.step.action.foul;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.ReportFoul;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeFoul;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerInjury;

/**
 * Step in foul sequence to handle the actual foul.
 * 
 * Sets stepParameter INJURY_RESULT for all steps on the stack.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepFoul extends AbstractStep {

	public StepFoul(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.FOUL;
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
		getResult().addReport(new ReportFoul(game.getDefenderId()));
		if (!actingPlayer.getPlayer().hasSkillProperty(NamedProperties.blocksLikeChainsaw)) {
			getResult().setSound(SoundId.FOUL);
		}
		UtilServerGame.syncGameModel(this);
		FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
		InjuryResult injuryResultDefender = UtilServerInjury.handleInjury(this, new InjuryTypeFoul(),
				actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, ApothecaryMode.DEFENDER);
		publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultDefender));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// ByteArray serialization

	public int getByteArraySerializationVersion() {
		return 1;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		return super.toJsonValue();
	}

	@Override
	public StepFoul initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		return this;
	}

}
