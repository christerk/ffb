package com.fumbbl.ffb.server.step.mixed.block;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.mixed.ReportProjectileVomit;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeProjectileVomit;
import com.fumbbl.ffb.server.model.DropPlayerContext;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepProjectileVomit extends AbstractStepWithReRoll {

	private String fGotoLabelOnSuccess;
	private String fGotoLabelOnFailure;
	private boolean usingVomit;

	public StepProjectileVomit(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.PROJECTILE_VOMIT;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
				case GOTO_LABEL_ON_FAILURE:
					fGotoLabelOnFailure = (String) parameter.getValue();
					break;
				case GOTO_LABEL_ON_SUCCESS:
					fGotoLabelOnSuccess = (String) parameter.getValue();
					break;
				default:
					break;
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnFailure)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
		}
		if (!StringTool.isProvided(fGotoLabelOnSuccess)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_SUCCESS + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null && parameter.getKey() == StepParameterKey.USING_VOMIT) {
			usingVomit = (boolean) parameter.getValue();
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
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canPerformArmourRollInsteadOfBlockThatMightFail) && usingVomit) {
			actingPlayer.markSkillUsed(NamedProperties.canPerformArmourRollInsteadOfBlockThatMightFail);
			boolean dropSelf = false;
			if (ReRolledActions.PROJECTILE_VOMIT == getReRolledAction()) {
				if ((getReRollSource() == null)
						|| !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
					dropSelf = true;
				}
			}
			if (!dropSelf) {
				boolean reRolled = ((getReRolledAction() == ReRolledActions.PROJECTILE_VOMIT) && (getReRollSource() != null));

				int roll = getGameState().getDiceRoller().rollSkill();
				int minimumRoll = DiceInterpreter.getInstance().minimumRollProjectileVomit();
				boolean successful = (roll >= minimumRoll);
				getResult().addReport(new ReportProjectileVomit(actingPlayer.getPlayerId(), successful, roll,
					minimumRoll, reRolled, game.getDefenderId()));
				getResult().setSound(SoundId.VOMIT);
				if (successful) {
					FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
					InjuryResult injuryResultDefender = UtilServerInjury.handleInjury(this, new InjuryTypeProjectileVomit(),
						actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, null, ApothecaryMode.DEFENDER);
					publishParameter(new StepParameter(StepParameterKey.DROP_PLAYER_CONTEXT,
						new DropPlayerContext(injuryResultDefender, false, true, fGotoLabelOnSuccess,
							game.getDefenderId(), ApothecaryMode.DEFENDER, true)));
					getResult().setNextAction(StepAction.NEXT_STEP);
				} else {
					if (getReRolledAction() == ReRolledActions.PROJECTILE_VOMIT || !UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(),
							ReRolledActions.PROJECTILE_VOMIT, minimumRoll, false)) {
						dropSelf = true;
					}
				}
			}
			if (dropSelf) {
				FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
				InjuryResult injuryResultAttacker = UtilServerInjury.handleInjury(this, new InjuryTypeProjectileVomit(), null,
					actingPlayer.getPlayer(), attackerCoordinate, null, null, ApothecaryMode.ATTACKER);
				publishParameter(new StepParameter(StepParameterKey.DROP_PLAYER_CONTEXT,
					new DropPlayerContext(injuryResultAttacker, false, true, fGotoLabelOnFailure, actingPlayer.getPlayerId(), ApothecaryMode.ATTACKER, true)));
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_SUCCESS.addTo(jsonObject, fGotoLabelOnSuccess);
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
		IServerJsonOption.USING_VOMIT.addTo(jsonObject, usingVomit);
		return jsonObject;
	}

	@Override
	public StepProjectileVomit initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnSuccess = IServerJsonOption.GOTO_LABEL_ON_SUCCESS.getFrom(source, jsonObject);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		usingVomit = IServerJsonOption.USING_VOMIT.getFrom(source, jsonObject);
		return this;
	}

}
