package com.fumbbl.ffb.server.step.action.pass;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PathFinderWithPassBlockSupport;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogPassBlockParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.JumpMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.ReportPassBlock;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.common.Move;
import com.fumbbl.ffb.server.step.generator.common.Select;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilPassing;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;
import java.util.Set;

/**
 * Step in pass sequence to handle skill PASS_BLOCK.
 *
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 *
 * Expects stepParameter END_PLAYER_ACTION to be set by a preceding step.
 * (parameter is consumed on TurnMode.PASS_BLOCK) Expects stepParameter END_TURN
 * to be set by a preceding step. (parameter is consumed on TurnMode.PASS_BLOCK)
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepPassBlock extends AbstractStep {

	private String fGotoLabelOnEnd;
	private TurnMode fOldTurnMode;
	private boolean fEndTurn;
	private boolean fEndPlayerAction;
	private PlayerState[] fOldPlayerStates;

	public StepPassBlock(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.PASS_BLOCK;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
				// mandatory
				case GOTO_LABEL_ON_END:
					fGotoLabelOnEnd = (String) parameter.getValue();
					break;
				default:
					break;
				}
			}
		}
		if (fGotoLabelOnEnd == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
		}
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		Game game = getGameState().getGame();
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case END_PLAYER_ACTION:
				fEndPlayerAction = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				if (game.getTurnMode() == TurnMode.PASS_BLOCK) {
					consume(pParameter);
				}
				return true;
			case END_TURN:
				fEndTurn = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				if (game.getTurnMode() == TurnMode.PASS_BLOCK) {
					consume(pParameter);
				}
				return true;
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

	private void executeStep() {

		Game game = getGameState().getGame();
		if (game.getThrower() == null) {
			return;
		}

		// TODO: pass block on dump off (ends the turn AFTER the block)

		// no pass block for bombs or hand over or dump off (atm)
		if (game.getTurnMode().isBombTurn() || (game.getThrowerAction() == PlayerAction.DUMP_OFF)
				|| (game.getThrowerAction() == PlayerAction.HAND_OVER)
				|| (game.getThrowerAction() == PlayerAction.HAND_OVER_MOVE)) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}

		Team opposingTeam = UtilPlayer.findOtherTeam(game, game.getThrower());
		Set<Player<?>> passBlockers = findPassBlockers(opposingTeam, false);
		if (passBlockers.size() == 0) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			return;
		}

		ActingPlayer actingPlayer = game.getActingPlayer();
		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		Move moveGenerator = (Move) factory.forName(SequenceGenerator.Type.Move.name());
		Select selectGenerator = (Select) factory.forName(SequenceGenerator.Type.Select.name());
		Select.SequenceParams selectParams = new Select.SequenceParams(getGameState(), false);

		if (game.getTurnMode() == TurnMode.PASS_BLOCK) {

			Set<FieldCoordinate> validEndCoordinates = UtilPassing.findValidPassBlockEndCoordinates(game);
			// check if actingPlayer has dropped (failed dodge)
			if (actingPlayer.getPlayer() != null) {
				PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
				if (!playerState.hasTacklezones() || fEndPlayerAction) {
					UtilServerSteps.changePlayerAction(this, null, null, false);
					fEndTurn = true;
					fEndPlayerAction = false;
				}
			}

			if (fEndPlayerAction) {
				FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
				if (validEndCoordinates.contains(playerCoordinate) || !actingPlayer.hasActed()) {
					UtilServerSteps.changePlayerAction(this, null, null, false);
					if (checkNoPlayerActive(passBlockers)) {
						fEndTurn = true;
					} else {
						fEndPlayerAction = false;
						getGameState().pushCurrentStepOnStack();

						selectGenerator.pushSequence(selectParams);
					}
				} else {
					fEndPlayerAction = false;
					getGameState().pushCurrentStepOnStack();
					moveGenerator.pushSequence(new Move.SequenceParams(getGameState()));
				}
			}

			if (fEndTurn && (actingPlayer.getPlayer() != null)) {
				FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
				if (!validEndCoordinates.contains(playerCoordinate)) {
					fEndTurn = false;
					getGameState().pushCurrentStepOnStack();
					moveGenerator.pushSequence(new Move.SequenceParams(getGameState()));
				}
			}

			if (fEndTurn) {

				Player<?>[] players = opposingTeam.getPlayers();
				for (int i = 0; i < players.length; i++) {
					PlayerState playerState = game.getFieldModel().getPlayerState(players[i]);
					FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(players[i]);
					if ((playerPosition != null) && !playerPosition.isBoxCoordinate() && playerState.hasTacklezones()) {
						game.getFieldModel().setPlayerState(players[i], fOldPlayerStates[i]);
					}
				}

				actingPlayer.setPlayer(game.getThrower());
				actingPlayer.setPlayerAction(game.getThrowerAction());
				actingPlayer.setHasPassed(true);

				game.setTurnMode(fOldTurnMode);
				if (fOldTurnMode != TurnMode.DUMP_OFF) {
					game.setHomePlaying(!game.isHomePlaying());
				}

				FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
				if (game.getThrowerAction() == PlayerAction.HAIL_MARY_PASS) {
					// reset ball
					game.getFieldModel().setBallInPlay(true);
					game.getFieldModel().setBallCoordinate(throwerCoordinate);
					game.getFieldModel().setBallMoving(false);
				} else if (game.getThrowerAction() == PlayerAction.HAIL_MARY_BOMB) {
					game.getFieldModel().setBombCoordinate(null);
				} else {
					// force rangeRuler redraw (reset by StepInterception)
					game.getFieldModel().setRangeRuler(null);
				}

			}

		} else {

			Set<Player<?>> availablePassBlockers = findPassBlockers(opposingTeam, true);
			if (availablePassBlockers.size() == 0) {

				getResult().addReport(new ReportPassBlock(opposingTeam.getId(), false));

			} else {

				fOldTurnMode = game.getTurnMode();
				game.setTurnMode(TurnMode.PASS_BLOCK);

				game.setHomePlaying(!game.isHomePlaying());
				game.getActingPlayer().setPlayerId(null);

				Player<?>[] players = opposingTeam.getPlayers();
				fOldPlayerStates = new PlayerState[players.length];
				for (int i = 0; i < players.length; i++) {
					PlayerState playerState = game.getFieldModel().getPlayerState(players[i]);
					FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(players[i]);
					if ((playerPosition != null) && !playerPosition.isBoxCoordinate()) {
						fOldPlayerStates[i] = playerState;
						game.getFieldModel().setPlayerState(players[i],
								playerState.changeActive(availablePassBlockers.contains(players[i])));
					}
				}

				// mark pass coordinate with faded ball for pass block
				if (game.getThrowerAction() == PlayerAction.HAIL_MARY_PASS) {
					game.getFieldModel().setBallInPlay(false);
					game.getFieldModel().setBallCoordinate(game.getPassCoordinate());
					game.getFieldModel().setBallMoving(true);
				}

				if (game.getThrowerAction() == PlayerAction.HAIL_MARY_BOMB) {
					game.getFieldModel().setBombCoordinate(game.getPassCoordinate());
				}

				game.setDialogParameter(new DialogPassBlockParameter());

				getGameState().pushCurrentStepOnStack();
				selectGenerator.pushSequence(selectParams);

			}

		}

		getResult().setNextAction(StepAction.NEXT_STEP);

	}

	private boolean checkNoPlayerActive(Set<Player<?>> pPlayers) {
		Game game = getGameState().getGame();
		for (Player<?> player : pPlayers) {
			PlayerState playerState = game.getFieldModel().getPlayerState(player);
			if (playerState.isActive()) {
				return false;
			}
		}
		return true;
	}

	private Set<Player<?>> findPassBlockers(Team pTeam, boolean pCheckCanReach) {
		Set<Player<?>> passBlockers = new HashSet<>();
		Game game = getGameState().getGame();
		Player<?>[] players = pTeam.getPlayers();
		JumpMechanic mechanic = (JumpMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.JUMP.name());
		Set<FieldCoordinate> validPassBlockEndCoordinates = UtilPassing.findValidPassBlockEndCoordinates(game);
		for (Player<?> player : players) {
			if (player.hasSkillProperty(NamedProperties.canMoveWhenOpponentPasses)) {
				PlayerState playerState = game.getFieldModel().getPlayerState(player);
				FieldCoordinate startPosition = game.getFieldModel().getPlayerCoordinate(player);
				if (!pCheckCanReach || (playerState.hasTacklezones()
						&& ArrayTool.isProvided(PathFinderWithPassBlockSupport.allowPassBlockMove(game, player, startPosition, 3, mechanic.canJump(game, player, startPosition), validPassBlockEndCoordinates)))) {
					passBlockers.add(player);
				}
			}
		}
		return passBlockers;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
		IServerJsonOption.OLD_TURN_MODE.addTo(jsonObject, fOldTurnMode);
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
		if (fOldPlayerStates != null) {
			int[] playerStateIds = new int[fOldPlayerStates.length];
			for (int i = 0; i < fOldPlayerStates.length; i++) {
				if (fOldPlayerStates[i] != null) {
					playerStateIds[i] = fOldPlayerStates[i].getId();
				} else {
					playerStateIds[i] = 0;
				}
			}
			IServerJsonOption.OLD_PLAYER_STATES.addTo(jsonObject, playerStateIds);
		}
		return jsonObject;
	}

	@Override
	public StepPassBlock initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(game, jsonObject);
		fOldTurnMode = (TurnMode) IServerJsonOption.OLD_TURN_MODE.getFrom(game, jsonObject);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(game, jsonObject);
		fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(game, jsonObject);
		fOldPlayerStates = null;
		int[] playerStateIds = IServerJsonOption.OLD_PLAYER_STATES.getFrom(game, jsonObject);
		if (ArrayTool.isProvided(playerStateIds)) {
			fOldPlayerStates = new PlayerState[playerStateIds.length];
			for (int i = 0; i < playerStateIds.length; i++) {
				if (playerStateIds[i] > 0) {
					fOldPlayerStates[i] = new PlayerState(playerStateIds[i]);
				} else {
					fOldPlayerStates[i] = null;
				}
			}
		}
		return this;
	}

}
