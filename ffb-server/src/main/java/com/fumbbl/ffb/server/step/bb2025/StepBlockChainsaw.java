package com.fumbbl.ffb.server.step.bb2025;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionString;
import com.fumbbl.ffb.report.ReportChainsawRoll;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeChainsaw;
import com.fumbbl.ffb.server.model.DropPlayerContext;
import com.fumbbl.ffb.server.model.SteadyFootingContext;
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

import java.util.Optional;

/**
 * Step in block sequence to handle skill CHAINSAW.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE. Needs to be
 * initialized with stepParameter GOTO_LABEL_ON_SUCCESS.
 * <p>
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * Sets stepParameter END_TURN for all steps on the stack. Sets stepParameter
 * INJURY_RESULT for all steps on the stack.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepBlockChainsaw extends AbstractStepWithReRoll {

	private String fGotoLabelOnSuccess;
	private String fGotoLabelOnFailure;
	private boolean usingChainsaw;

	public StepBlockChainsaw(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.BLOCK_CHAINSAW;
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
		if (parameter != null && parameter.getKey() == StepParameterKey.USING_CHAINSAW) {
			usingChainsaw = (boolean) parameter.getValue();
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
		if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.blocksLikeChainsaw) && usingChainsaw) {
			actingPlayer.markSkillUsed(NamedProperties.blocksLikeChainsaw);
			if (actingPlayer.getPlayerAction() == PlayerAction.MAXIMUM_CARNAGE) {
				Optional<Skill> skillWithProperty = UtilCards.getSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.canPerformSecondChainsawAttack);
				if (skillWithProperty.isPresent()) {
					actingPlayer.markSkillUsed(NamedProperties.canPerformSecondChainsawAttack);
					getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skillWithProperty.get(), true, SkillUse.PERFORM_SECOND_CHAINSAW_ATTACK));
				}
			}
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
					FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
					PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
					InjuryResult injuryResultDefender = UtilServerInjury.handleInjury(this, new InjuryTypeChainsaw(),
						actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, null, ApothecaryMode.DEFENDER);
					publishParameter(new StepParameter(StepParameterKey.DROP_PLAYER_CONTEXT,
						new DropPlayerContext(injuryResultDefender, GameOptionString.CHAINSAW_TURNOVER_ALL_AV_BREAKS.equalsIgnoreCase(chainsawOption),
							true, fGotoLabelOnSuccess, game.getDefenderId(),
							ApothecaryMode.DEFENDER, true, defenderState.isProneOrStunned())));
					// we usually do not need that but in case the player can continue after a chainsaw blitz we remove the state
					// as this can be confusing on the UI side, e.g. with Maximum Carnage
					game.getFieldModel().setPlayerState(game.getDefender(), defenderState.removeSelectedBlitzTarget());
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

				boolean causesTurnOver = causesTurnOver(UtilPlayer.hasBall(game, actingPlayer.getPlayer()), chainsawOption, injuryResultAttacker.injuryContext());
				boolean modifiedInjuryCausesTurnover = false;
				if (injuryResultAttacker.injuryContext().getModifiedInjuryContext() != null) {
					modifiedInjuryCausesTurnover = causesTurnOver(UtilPlayer.hasBall(game, actingPlayer.getPlayer()), chainsawOption, injuryResultAttacker.injuryContext().getModifiedInjuryContext());
				}

				DropPlayerContext dropPlayerContext =
					new DropPlayerContext(injuryResultAttacker, causesTurnOver, true, fGotoLabelOnFailure,
						actingPlayer.getPlayer().getId(), ApothecaryMode.ATTACKER, true, false, null, modifiedInjuryCausesTurnover,
						GameOptionString.CHAINSAW_TURNOVER_KICKBACK.equals(chainsawOption), null);
				publishParameter(new StepParameter(StepParameterKey.STEADY_FOOTING_CONTEXT, new SteadyFootingContext(dropPlayerContext)));
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
		} else {
			getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	private boolean causesTurnOver(boolean playerHasBall, String chainsawOption, InjuryContext injuryContext) {
		boolean causesTurnOver = false;
		if (injuryContext.isArmorBroken()) {
			causesTurnOver = playerHasBall;
			if (!GameOptionString.CHAINSAW_TURNOVER_NEVER.equalsIgnoreCase(chainsawOption)) {
				causesTurnOver = true;
			}
		} else if (GameOptionString.CHAINSAW_TURNOVER_KICKBACK.equals(chainsawOption)) {
			causesTurnOver = true;
		}

		return causesTurnOver;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_SUCCESS.addTo(jsonObject, fGotoLabelOnSuccess);
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
		IServerJsonOption.USING_CHAINSAW.addTo(jsonObject, usingChainsaw);
		return jsonObject;
	}

	@Override
	public StepBlockChainsaw initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnSuccess = IServerJsonOption.GOTO_LABEL_ON_SUCCESS.getFrom(source, jsonObject);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		usingChainsaw = IServerJsonOption.USING_CHAINSAW.getFrom(source, jsonObject);
		return this;
	}

}
