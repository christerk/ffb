package com.fumbbl.ffb.server.step.action.ktm;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.KickTeamMateRange;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.bb2016.ReportKickTeamMateRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.generator.ScatterPlayer;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;

/**
 * Step in ttm sequence to actual throw the team mate.
 *
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 *
 * Expects stepParameter THROWN_PLAYER_ID to be set by a preceding step. Expects
 * stepParameter THROWN_PLAYER_STATE to be set by a preceding step. Expects
 * stepParameter THROWN_PLAYER_HAS_BALL to be set by a preceding step.
 *
 * Pushes new scatterPlayerSequence on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public final class StepKickTeamMate extends AbstractStepWithReRoll {

	private String fGotoLabelOnFailure;
	private String fKickedPlayerId;
	private PlayerState fKickedPlayerState;
	private boolean fKickedPlayerHasBall;
	private int fNumDice;
	private int fDistance;
	private int[] fRolls;

	public StepKickTeamMate(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.KICK_TEAM_MATE;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
				// mandatory
				case GOTO_LABEL_ON_FAILURE:
					fGotoLabelOnFailure = (String) parameter.getValue();
					break;
				default:
					break;
				}
			}
		}
		if (fGotoLabelOnFailure == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
			case KICKED_PLAYER_ID:
				fKickedPlayerId = (String) parameter.getValue();
				return true;
			case KICKED_PLAYER_STATE:
				fKickedPlayerState = (PlayerState) parameter.getValue();
				return true;
			case KICKED_PLAYER_HAS_BALL:
				fKickedPlayerHasBall = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
				return true;
			case NR_OF_DICE:
				fNumDice = (parameter.getValue() != null) ? Math.max(0, Math.min((Integer) parameter.getValue(), 2)) : 0;
				break;
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
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		actingPlayer.setHasBlocked(true);
		game.setConcessionPossible(false);
		game.getTurnData().setBlitzUsed(true);
		UtilServerDialog.hideDialog(getGameState());
		Player<?> kicker = game.getActingPlayer().getPlayer();
		boolean doRoll = true;
		if (ReRolledActions.KICK_TEAM_MATE == getReRolledAction()) {
			if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), kicker)) {
				FieldCoordinate kickerCoordinate = game.getFieldModel().getPlayerCoordinate(kicker);
				Player<?> kickedPlayer = game.getPlayerById(fKickedPlayerId);
				boolean successful = fNumDice == 1 || fRolls[0] != fRolls[1];

				executeKick(kickedPlayer, kickerCoordinate, successful);

				doRoll = false;
			}
		}
		if (doRoll) {
			Player<?> kickedPlayer = game.getPlayerById(fKickedPlayerId);
			FieldCoordinate kickerCoordinate = game.getFieldModel().getPlayerCoordinate(kicker);
			FieldCoordinate kickedPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(kickedPlayer);

			Direction d = FieldCoordinate.getDirection(kickerCoordinate, kickedPlayerCoordinate);

			fRolls = new int[fNumDice];
			for (int i = 0; i < fNumDice; i++) {
				fRolls[i] = getGameState().getDiceRoller().rollSkill();
			}

			boolean successful = fNumDice == 1 || fRolls[0] != fRolls[1];
			fDistance = fRolls[0] + (fNumDice > 1 ? fRolls[1] : 0);

			FieldCoordinate targetCoordinate = kickedPlayerCoordinate;
			targetCoordinate = targetCoordinate.move(d, fDistance);
			game.setPassCoordinate(targetCoordinate);

			boolean reRolled = ((getReRolledAction() == ReRolledActions.KICK_TEAM_MATE) && (getReRollSource() != null));
			getResult().addReport(
					new ReportKickTeamMateRoll(kicker.getId(), kickedPlayer.getId(), successful, fRolls, reRolled, fDistance));

			boolean act = false;

			boolean allowKtmReroll = UtilGameOption.isOptionEnabled(game, GameOptionId.ALLOW_KTM_REROLL);

			if (allowKtmReroll && getReRolledAction() != ReRolledActions.KICK_TEAM_MATE) {
				setReRolledAction(ReRolledActions.KICK_TEAM_MATE);
				if (!UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(),
						ReRolledActions.KICK_TEAM_MATE, 0, false)) {
					act = true;
				}
			} else {
				act = true;
			}

			if (act) {
				executeKick(kickedPlayer, kickerCoordinate, successful);
			}
		}
	}

	private void executeKick(Player<?> kickedPlayer, FieldCoordinate kickerCoordinate, boolean successful) {
		if (successful) {
			Game game = getGameState().getGame();
			boolean hasSwoop = kickedPlayer != null
					&& kickedPlayer.hasSkillProperty(NamedProperties.ttmScattersInSingleDirection);
			game.getFieldModel().setPlayerState(game.getDefender(), fKickedPlayerState.changeBase(PlayerState.PICKED_UP));
			SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
			((ScatterPlayer) factory.forName(SequenceGenerator.Type.ScatterPlayer.name()))
				.pushSequence(new ScatterPlayer.SequenceParams(getGameState(), fKickedPlayerId, fKickedPlayerState,
					fKickedPlayerHasBall, kickerCoordinate, hasSwoop, true));
			publishParameter(new StepParameter(StepParameterKey.IS_KICKED_PLAYER, true));
			if (fDistance >= 9) {
				publishParameter(new StepParameter(StepParameterKey.KTM_MODIFIER, KickTeamMateRange.LONG));
			} else if (fDistance >= 6) {
				publishParameter(new StepParameter(StepParameterKey.KTM_MODIFIER, KickTeamMateRange.MEDIUM));
			}
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
		}
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
		IServerJsonOption.KICKED_PLAYER_ID.addTo(jsonObject, fKickedPlayerId);
		IServerJsonOption.KICKED_PLAYER_STATE.addTo(jsonObject, fKickedPlayerState);
		IServerJsonOption.KICKED_PLAYER_HAS_BALL.addTo(jsonObject, fKickedPlayerHasBall);
		return jsonObject;
	}

	@Override
	public StepKickTeamMate initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		fKickedPlayerId = IServerJsonOption.KICKED_PLAYER_ID.getFrom(source, jsonObject);
		fKickedPlayerState = IServerJsonOption.KICKED_PLAYER_STATE.getFrom(source, jsonObject);
		fKickedPlayerHasBall = IServerJsonOption.KICKED_PLAYER_HAS_BALL.getFrom(source, jsonObject);
		return this;
	}
}
