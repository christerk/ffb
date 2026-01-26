package com.fumbbl.ffb.server.step.bb2025.block;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.bb2025.ReportChompRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerReRoll;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepChomp extends AbstractStepWithReRoll {

	private boolean usingChomp;
	private String gotoLabelOnEnd;

	public StepChomp(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.CHOMP;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
					case GOTO_LABEL_ON_END:
						gotoLabelOnEnd = (String) parameter.getValue();
						break;

					default:
						break;
				}
			}
		}

	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null && parameter.getKey() == StepParameterKey.USING_CHOMP) {
			usingChomp = (boolean) parameter.getValue();
			return true;
		}

		return super.setParameter(parameter);
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
		getResult().setNextAction(StepAction.NEXT_STEP);
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canPinPlayers) && usingChomp &&
			!actingPlayer.isStandingUp()) {
			getResult().setNextAction(StepAction.GOTO_LABEL, gotoLabelOnEnd);

			if (ReRolledActions.CHOMP == getReRolledAction()) {
				if ((getReRollSource() == null)
					&& !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
					return;
				}
			}

			boolean reRolled = ((getReRolledAction() == ReRolledActions.CHOMP) && (getReRollSource() != null));
			if (!reRolled) {
				getResult().setSound(SoundId.BLOCK);
			}
			int roll = getGameState().getDiceRoller().rollChainsaw();
			int minimumRoll = 3;
			boolean successful = (roll >= minimumRoll);
			getResult().addReport(new ReportChompRoll(actingPlayer.getPlayerId(), successful, roll,
				minimumRoll, reRolled, actingPlayer.getPlayerId(), game.getDefenderId()));
			if (successful) {
				game.getFieldModel().addChomp(actingPlayer.getPlayer(), game.getDefender());
			} else if (!reRolled && UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(),
				ReRolledActions.CHOMP, minimumRoll, false)) {
				getResult().setNextAction(StepAction.CONTINUE);
			}
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.USING_CHOMP.addTo(jsonObject, usingChomp);
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, gotoLabelOnEnd);
		return jsonObject;
	}

	@Override
	public StepChomp initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		usingChomp = IServerJsonOption.USING_CHOMP.getFrom(source, jsonObject);
		gotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
		return this;
	}

}
