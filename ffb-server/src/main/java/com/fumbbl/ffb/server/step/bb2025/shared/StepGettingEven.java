package com.fumbbl.ffb.server.step.bb2025.shared;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.bb2025.ReportGettingEvenRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerReRoll;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepGettingEven extends AbstractStepWithReRoll {

	public static final int MINIMUM_ROLL = 4;
	private String playerId;
	private Keyword keyword;

	public StepGettingEven(GameState pGameState) {
		super(pGameState);

	}

	public StepId getId() {
		return StepId.GETTING_EVEN;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					case PLAYER_ID:
						playerId = (String) parameter.getValue();
						break;
					case KEYWORD:
						keyword = (Keyword) parameter.getValue();
					default:
						break;
				}
			}
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

		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		Player<?> player = game.getPlayerById(playerId);
		boolean doRoll = true;
		boolean rerolled = ReRolledActions.GETTING_EVEN == getReRolledAction();
		if (rerolled) {
			if ((getReRollSource() == null) ||
				!UtilServerReRoll.useReRoll(this, getReRollSource(), player)) {
				doRoll = false;
			}
		}
		if (doRoll) {
			int roll = getGameState().getDiceRoller().rollSkill();
			boolean success = roll >= MINIMUM_ROLL;
			getResult().addReport(new ReportGettingEvenRoll(playerId, success, roll, MINIMUM_ROLL, rerolled, keyword));
			if (success) {
				game.addHatred(player, keyword);
			} else {
				if (getReRolledAction() == null &&
					UtilServerReRoll.askForReRollIfAvailable(getGameState(), player, ReRolledActions.GETTING_EVEN, MINIMUM_ROLL,
						false)) {
					getResult().setNextAction(StepAction.CONTINUE);
					return;
				}
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IServerJsonOption.KEYWORD.addTo(jsonObject, keyword.getName());
		return jsonObject;
	}

	// JSON serialization

	@Override
	public StepGettingEven initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		keyword = Keyword.forName(IServerJsonOption.KEYWORD.getFrom(source, jsonObject));
		return this;
	}

}
