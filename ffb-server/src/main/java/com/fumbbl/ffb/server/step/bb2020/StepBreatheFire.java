package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.BreatheFireResult;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.bb2020.ReportBreatheFire;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeBreatheFire;
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

import java.util.Arrays;


@RulesCollection(RulesCollection.Rules.BB2020)
public class StepBreatheFire extends AbstractStepWithReRoll {

	private String fGotoLabelOnSuccess;
	private String fGotoLabelOnFailure;
	private String gotoOnEnd;
	private boolean usingBreatheFire;
	private BreatheFireResult result;

	public StepBreatheFire(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.BREATHE_FIRE;
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
					case GOTO_LABEL_ON_END:
						gotoOnEnd = (String) parameter.getValue();
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
		if (parameter != null && parameter.getKey() == StepParameterKey.USING_BREATHE_FIRE) {
			usingBreatheFire = (boolean) parameter.getValue();
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
		if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canPerformArmourRollInsteadOfBlockThatMightFailWithTurnover) && usingBreatheFire) {
			actingPlayer.markSkillUsed(NamedProperties.canPerformArmourRollInsteadOfBlockThatMightFailWithTurnover);
			if (ReRolledActions.BREATHE_FIRE == getReRolledAction()) {
				if ((getReRollSource() != null)
					&& UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
					result = null;
				}
			}
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
			if (result == null) {
				boolean reRolled = ((getReRolledAction() == ReRolledActions.BREATHE_FIRE) && (getReRollSource() != null));

				int roll = getGameState().getDiceRoller().rollSkill();
				boolean strongOpponent = game.getDefender().getStrengthWithModifiers(game) > 4;
				int minimumRoll = strongOpponent ? 3 : 2;
				int proneRoll = strongOpponent ? 5 : 4;
				int effectiveRoll = strongOpponent ? roll - 1 : roll;
				result = evaluate(roll, effectiveRoll);
				boolean successful = result == BreatheFireResult.KNOCK_DOWN;

				getResult().addReport(new ReportBreatheFire(actingPlayer.getPlayerId(), successful, roll,
					minimumRoll, reRolled, game.getDefenderId(), result, strongOpponent));

				if (successful) {
					InjuryResult injuryResultDefender = UtilServerInjury.handleInjury(this, new InjuryTypeBreatheFire(),
						actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, null, ApothecaryMode.DEFENDER);
					publishParameter(new StepParameter(StepParameterKey.DROP_PLAYER_CONTEXT,
						new DropPlayerContext(injuryResultDefender, false, true, fGotoLabelOnSuccess,
							game.getDefenderId(), ApothecaryMode.DEFENDER, true)));
					getResult().setNextAction(StepAction.NEXT_STEP);
					getResult().setAnimation(new Animation(AnimationType.BREATHE_FIRE, playerCoordinate, game.getFieldModel().getPlayerCoordinate(game.getDefender())));
				} else {
					if (getReRolledAction() != ReRolledActions.BREATHE_FIRE && UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(),
						ReRolledActions.BREATHE_FIRE, 0, Arrays.asList(result.getMessage(), "You need a:", "  • 6 to knock your opponent down",
							"  • " + proneRoll + "+ to place your opponent prone", "  • " + minimumRoll + "+ to avoid a turnover"))) {
						return;
					}
				}
			}
			PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
			switch (result) {
				case FAILURE: {
					InjuryResult injuryResultAttacker = UtilServerInjury.handleInjury(this, new InjuryTypeBreatheFire(), null,
						actingPlayer.getPlayer(), playerCoordinate, null, null, ApothecaryMode.ATTACKER);
					publishParameter(new StepParameter(StepParameterKey.DROP_PLAYER_CONTEXT,
						new DropPlayerContext(injuryResultAttacker, true, true, fGotoLabelOnFailure, actingPlayer.getPlayerId(), ApothecaryMode.ATTACKER, false)));
					getResult().setNextAction(StepAction.NEXT_STEP);
					getResult().setAnimation(new Animation(AnimationType.BREATHE_FIRE, playerCoordinate, playerCoordinate));
					break;
				}
				case NO_EFFECT:
					getResult().setNextAction(StepAction.GOTO_LABEL, gotoOnEnd);
					game.getFieldModel().setPlayerState(game.getDefender(), defenderState.removeAllTargetSelections());
					getResult().setAnimation(new Animation(AnimationType.BREATHE_FIRE, playerCoordinate, game.getFieldModel().getPlayerCoordinate(game.getDefender())));
					break;
				case PRONE:
					getResult().setNextAction(StepAction.GOTO_LABEL, gotoOnEnd);
					game.getFieldModel().setPlayerState(game.getDefender(), defenderState.changeBase(PlayerState.PRONE).removeAllTargetSelections());
					getResult().setAnimation(new Animation(AnimationType.BREATHE_FIRE, playerCoordinate, game.getFieldModel().getPlayerCoordinate(game.getDefender())));
					break;
				default:
					// SUCCESS is already handled above
					break;
			}

		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	private BreatheFireResult evaluate(int roll, int effectiveRoll) {
		if (roll == 6) {
			return BreatheFireResult.KNOCK_DOWN;
		}

		if (roll == 1 || effectiveRoll == 1) {
			return BreatheFireResult.FAILURE;
		}

		if (effectiveRoll < 4) {
			return BreatheFireResult.NO_EFFECT;
		}

		return BreatheFireResult.PRONE;

	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_SUCCESS.addTo(jsonObject, fGotoLabelOnSuccess);
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
		IServerJsonOption.USING_BREATHE_FIRE.addTo(jsonObject, usingBreatheFire);
		if (result != null) {
			IServerJsonOption.STATUS.addTo(jsonObject, result.name());
		}
		return jsonObject;
	}

	@Override
	public StepBreatheFire initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnSuccess = IServerJsonOption.GOTO_LABEL_ON_SUCCESS.getFrom(source, jsonObject);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		usingBreatheFire = IServerJsonOption.USING_BREATHE_FIRE.getFrom(source, jsonObject);
		if (IServerJsonOption.STATUS.isDefinedIn(jsonObject)) {
			result = BreatheFireResult.valueOf(IServerJsonOption.STATUS.getFrom(source, jsonObject));
		}
		return this;
	}


}
