package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.CardTarget;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.ReportCardDeactivated;
import com.fumbbl.ffb.report.ReportPlayCard;
import com.fumbbl.ffb.server.factory.CardHandlerFactory;
import com.fumbbl.ffb.server.inducements.CardHandler;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Kalimar
 */
public class UtilServerCards {

	public static Player<?>[] findAllowedPlayersForCard(Game pGame, Card pCard) {
		if ((pGame == null) || (pCard == null) || !pCard.getTarget().isPlayedOnPlayer()) {
			return new Player[0];
		}
		List<Player<?>> allowedPlayers = new ArrayList<>();
		Team ownTeam = pGame.getTurnDataHome().getInducementSet().isAvailable(pCard) ? pGame.getTeamHome()
				: pGame.getTeamAway();
		Team otherTeam = (pGame.getTeamHome() == ownTeam) ? pGame.getTeamAway() : pGame.getTeamHome();
		for (Player<?> player : pGame.getPlayers()) {
			PlayerState playerState = pGame.getFieldModel().getPlayerState(player);
			boolean playerAllowed = ((playerState != null) && !playerState.isCasualty()
					&& (playerState.getBase() != PlayerState.BANNED) && (playerState.getBase() != PlayerState.MISSING));
			if (pCard.getTarget() == CardTarget.OWN_PLAYER) {
				playerAllowed &= ownTeam.hasPlayer(player);
			}
			if (pCard.getTarget() == CardTarget.OPPOSING_PLAYER) {
				playerAllowed &= otherTeam.hasPlayer(player);
			}

			Optional<CardHandler> handler = ((CardHandlerFactory) pGame.getFactory(FactoryType.Factory.CARD_HANDLER)).forCard(pCard);

			if (handler.isPresent()) {
				playerAllowed &= handler.get().allowsPlayer(pGame, pCard, player);
			}

			if (playerAllowed) {
				allowedPlayers.add(player);
			}
		}

		return allowedPlayers.toArray(new Player[0]);
	}

	public static boolean activateCard(IStep pStep, Card pCard, boolean pHomeTeam, String pPlayerId) {

		if ((pStep == null) || (pCard == null)) {
			return true;
		}

		// play animation first before activating card and its effects
		pStep.getResult().setAnimation(new Animation(pCard));
		UtilServerGame.syncGameModel(pStep);

		Game game = pStep.getGameState().getGame();
		Team ownTeam = pHomeTeam ? game.getTeamHome() : game.getTeamAway();
		if (StringTool.isProvided(pPlayerId)) {
			pStep.getResult().addReport(new ReportPlayCard(ownTeam.getId(), pCard, pPlayerId));
		} else {
			pStep.getResult().addReport(new ReportPlayCard(ownTeam.getId(), pCard));
		}

		InducementSet inducementSet = pHomeTeam ? game.getTurnDataHome().getInducementSet()
				: game.getTurnDataAway().getInducementSet();
		inducementSet.activateCard(pCard);
		Player<?> player = game.getPlayerById(pPlayerId);
		if (player != null) {
			game.getFieldModel().addCard(player, pCard);
		}
		Optional<CardHandler> cardHandler = ((CardHandlerFactory) game.getFactory(FactoryType.Factory.CARD_HANDLER))
				.forCard(pCard);
		return cardHandler.map(handler -> handler.activate(pCard, pStep, player)).orElse(true);
	}

	public static void deactivateCard(IStep pStep, Card pCard) {

		if ((pStep == null) || (pCard == null)) {
			return;
		}

		Game game = pStep.getGameState().getGame();
		if (game.getTurnDataHome().getInducementSet().isActive(pCard)) {
			game.getTurnDataHome().getInducementSet().deactivateCard(pCard);
		} else if (game.getTurnDataAway().getInducementSet().isActive(pCard)) {
			game.getTurnDataAway().getInducementSet().deactivateCard(pCard);
		} else {
			return;
		}

		pStep.getResult().addReport(new ReportCardDeactivated(pCard));

		Player<?> player = null;
		if (pCard.getTarget().isPlayedOnPlayer()) {
			player = game.getFieldModel().findPlayer(pCard);
			if (player != null) {
				if (pCard.isRemainsInPlay()) {
					game.getFieldModel().keepDeactivatedCard(player, pCard);
				} else {
					game.getFieldModel().removeCard(player, pCard);
				}
			}
		}

		Player<?> finalPlayer = player;
		((CardHandlerFactory) game.getFactory(FactoryType.Factory.CARD_HANDLER))
			.forCard(pCard).ifPresent(handler -> handler.deactivate(pCard, pStep, finalPlayer));
	}
	
}
