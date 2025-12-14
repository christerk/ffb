package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Optional;

/**
 * @author Kalimar
 */
public class HandOverLogicModule extends MoveLogicModule {

	public HandOverLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.HAND_OVER;
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return super.playerInteraction(player);
		} else {
			return handOver(player);
		}
	}

	@Override
	public InteractionResult playerPeek(Player<?> pPlayer) {
		if (canPlayerGetHandOver(pPlayer)) {
			return InteractionResult.perform();
		} else {
			return InteractionResult.ignore();
		}
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate pCoordinate) {
		return InteractionResult.delegate(ClientStateId.MOVE);
	}

	public boolean canPlayerGetHandOver(Player<?> pCatcher) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((pCatcher != null) && (actingPlayer.getPlayer() != null)) {
			FieldModel fieldModel = game.getFieldModel();
			FieldCoordinate throwerCoordinate = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer());
			FieldCoordinate catcherCoordinate = fieldModel.getPlayerCoordinate(pCatcher);
			PlayerState catcherState = fieldModel.getPlayerState(pCatcher);
			return (throwerCoordinate.isAdjacent(catcherCoordinate) && (catcherState != null)
				&& (!actingPlayer.isSufferingAnimosity() || actingPlayer.getRace().equals(pCatcher.getRace()))
				&& (catcherState.hasTacklezones()
				&& (game.getTeamHome() == pCatcher.getTeam() || actingPlayer.getPlayerAction() == PlayerAction.HAND_OVER)));
		}
		return false;
	}

	private InteractionResult handOver(Player<?> pCatcher) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (UtilPlayer.hasBall(game, actingPlayer.getPlayer()) && canPlayerGetHandOver(pCatcher)) {
			client.getCommunication().sendHandOver(actingPlayer.getPlayerId(), pCatcher);
			return InteractionResult.handled();
		}
		return InteractionResult.ignore();
	}

	public boolean ballInHand() {
		Game game = client.getGame();
		return UtilPlayer.hasBall(game, game.getActingPlayer().getPlayer());
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		ActionContext actionContext = new ActionContext();
		Game game = client.getGame();

		if (ballInHand()) {
			actionContext.add(ClientAction.HAND_OVER);
			if (PlayerAction.HAND_OVER == actingPlayer.getPlayerAction()) {
				actionContext.add(Influences.HANDS_OVER_TO_ANYONE);
			}
		}

		if (isJumpAvailableAsNextMove(game, actingPlayer, true)) {
			actionContext.add(ClientAction.JUMP);
			if (actingPlayer.isJumping()) {
				actionContext.add(Influences.IS_JUMPING);
			} else {
				Optional<Skill> boundingLeap = isBoundingLeapAvailable(game, actingPlayer);
				if (boundingLeap.isPresent()) {
					actionContext.add(ClientAction.BOUNDING_LEAP);
				}
			}
		}

		if (isTreacherousAvailable(actingPlayer)) {
			actionContext.add(ClientAction.TREACHEROUS);
		}
		if (isWisdomAvailable(actingPlayer)) {
			actionContext.add(ClientAction.WISDOM);
		}
		if (isRaidingPartyAvailable(actingPlayer)) {
			actionContext.add(ClientAction.RAIDING_PARTY);
		}
		if (isBalefulHexAvailable(actingPlayer)) {
			actionContext.add(ClientAction.BALEFUL_HEX);
		}
		if (isBlackInkAvailable(actingPlayer)) {
			actionContext.add(ClientAction.BLACK_INK);
		}
		if (isCatchOfTheDayAvailable(actingPlayer)) {
			actionContext.add(ClientAction.CATCH_OF_THE_DAY);
		}
		if (isThenIStartedBlastinAvailable(actingPlayer)) {
			actionContext.add(ClientAction.THEN_I_STARTED_BLASTIN);
		}

		if (isFumblerooskieAvailable()) {
			actionContext.add(ClientAction.FUMBLEROOSKIE);
		}

		actionContext.add(ClientAction.END_MOVE);
		if (actingPlayer.hasActed()) {
			actionContext.add(Influences.HAS_ACTED);
		}

		return actionContext;
	}
}
