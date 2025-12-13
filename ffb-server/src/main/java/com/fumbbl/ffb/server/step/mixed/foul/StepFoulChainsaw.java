package com.fumbbl.ffb.server.step.mixed.foul;

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
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionString;
import com.fumbbl.ffb.report.ReportChainsawRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeChainsaw;
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
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

/**
 * Step in foul sequence to handle skill CHAINSAW.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * <p>
 * Sets stepParameter END_TURN for all steps on the stack. Sets stepParameter
 * INJURY_RESULT for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepFoulChainsaw extends AbstractStepWithReRoll {

	private String fGotoLabelOnFailure;
	private boolean usingChainsaw;

	public StepFoulChainsaw(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.FOUL_CHAINSAW;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_FAILURE) {
					fGotoLabelOnFailure = (String) parameter.getValue();
				}
			}
		}
		if (!StringTool.isProvided(fGotoLabelOnFailure)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null && parameter.getKey() == StepParameterKey.USING_CHAINSAW) {
			usingChainsaw = parameter.getValue() != null && (boolean) parameter.getValue();
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
		if (UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.blocksLikeChainsaw) && usingChainsaw) {
			boolean dropChainsawPlayer = false;
			if (ReRolledActions.CHAINSAW == getReRolledAction()) {
				if ((getReRollSource() == null)
					|| !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
					dropChainsawPlayer = true;
				}
			}
			String chainsawOption = game.getOptions().getOptionWithDefault(GameOptionId.CHAINSAW_TURNOVER).getValueAsString();

			if (!dropChainsawPlayer) {
				boolean reRolled = ((getReRolledAction() == ReRolledActions.CHAINSAW) && (getReRollSource() != null));
				if (!reRolled) {
					getResult().setSound(SoundId.CHAINSAW);
				}
				int roll = getGameState().getDiceRoller().rollChainsaw();
				int minimumRoll = DiceInterpreter.getInstance().minimumRollChainsaw();
				boolean successful = (roll >= minimumRoll);
				getResult().addReport(new ReportChainsawRoll(actingPlayer.getPlayerId(), successful, roll,
					minimumRoll, reRolled, null));
				if (successful) {
					publishParameter(StepParameter.from(StepParameterKey.USING_CHAINSAW, true));
					getResult().setNextAction(StepAction.NEXT_STEP);
				} else {
					if (reRolled || !UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(),
						ReRolledActions.CHAINSAW, minimumRoll, false)) {
						dropChainsawPlayer = true;
					}
				}
			}
			if (dropChainsawPlayer) {
				FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
				InjuryResult injuryResultAttacker = UtilServerInjury.handleInjury(this, new InjuryTypeChainsaw(), null,
					actingPlayer.getPlayer(), attackerCoordinate, null, null, ApothecaryMode.ATTACKER);

				boolean causesTurnOver = UtilPlayer.hasBall(game, actingPlayer.getPlayer());
				if (injuryResultAttacker.injuryContext().isArmorBroken()) {
					if (!GameOptionString.CHAINSAW_TURNOVER_NEVER.equalsIgnoreCase(chainsawOption)) {
						causesTurnOver = true;
					}
				} else if (GameOptionString.CHAINSAW_TURNOVER_KICKBACK.equals(chainsawOption)) {
					publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
				}

				publishParameter(StepParameter.from(StepParameterKey.DROP_PLAYER_CONTEXT,
					new DropPlayerContext(injuryResultAttacker, causesTurnOver, true, fGotoLabelOnFailure,
						actingPlayer.getPlayerId(), ApothecaryMode.ATTACKER, true)));
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
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
		IServerJsonOption.USING_CHAINSAW.addTo(jsonObject, usingChainsaw);
		return jsonObject;
	}

	@Override
	public StepFoulChainsaw initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		usingChainsaw = IServerJsonOption.USING_CHAINSAW.getFrom(source, jsonObject);
		return this;
	}

}
