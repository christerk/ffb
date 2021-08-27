package com.fumbbl.ffb.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;

public class UtilPassing {

	public static double RULER_WIDTH = 1.74;

	public static Player<?>[] findInterceptors(Game pGame, Player<?> pThrower, FieldCoordinate pTargetCoordinate) {
		List<Player<?>> interceptors = new ArrayList<>();
		if ((pTargetCoordinate != null) && (pThrower != null)) {
			FieldCoordinate throwerCoordinate = pGame.getFieldModel().getPlayerCoordinate(pThrower);
			Team otherTeam = pGame.getTeamHome().hasPlayer(pThrower) ? pGame.getTeamAway() : pGame.getTeamHome();
			Player<?>[] otherPlayers = otherTeam.getPlayers();
			for (Player<?> otherPlayer : otherPlayers) {
				PlayerState interceptorState = pGame.getFieldModel().getPlayerState(otherPlayer);
				FieldCoordinate interceptorCoordinate = pGame.getFieldModel().getPlayerCoordinate(otherPlayer);
				if ((interceptorCoordinate != null) && (interceptorState != null) && interceptorState.hasTacklezones()
					&& !otherPlayer.hasSkillProperty(NamedProperties.preventCatch)) {
					if (canIntercept(throwerCoordinate, pTargetCoordinate, interceptorCoordinate)) {
						interceptors.add(otherPlayer);
					}
				}
			}
		}
		return interceptors.toArray(new Player[0]);
	}

	private static boolean canIntercept(FieldCoordinate pThrowerCoordinate, FieldCoordinate pTargetCoordinate,
			FieldCoordinate pIinterceptorCoordinate) {
		int receiverX = pTargetCoordinate.getX() - pThrowerCoordinate.getX();
		int receiverY = pTargetCoordinate.getY() - pThrowerCoordinate.getY();
		int interceptorX = pIinterceptorCoordinate.getX() - pThrowerCoordinate.getX();
		int interceptorY = pIinterceptorCoordinate.getY() - pThrowerCoordinate.getY();
		int a = ((receiverX - interceptorX) * (receiverX - interceptorX))
				+ ((receiverY - interceptorY) * (receiverY - interceptorY));
		int b = (interceptorX * interceptorX) + (interceptorY * interceptorY);
		int c = (receiverX * receiverX) + (receiverY * receiverY);
		double d1 = Math.abs((receiverY * (interceptorX + 0.5)) - (receiverX * (interceptorY + 0.5)));
		double d2 = Math.abs((receiverY * (interceptorX + 0.5)) - (receiverX * (interceptorY - 0.5)));
		double d3 = Math.abs((receiverY * (interceptorX - 0.5)) - (receiverX * (interceptorY + 0.5)));
		double d4 = Math.abs((receiverY * (interceptorX - 0.5)) - (receiverX * (interceptorY - 0.5)));
		return (c > a) && (c > b) && (RULER_WIDTH > (2 * Math.min(Math.min(Math.min(d1, d2), d3), d4) / Math.sqrt(c)));
	}

	public static Set<FieldCoordinate> findValidPassBlockEndCoordinates(Game pGame) {

		Set<FieldCoordinate> validCoordinates = new HashSet<>();

		// Sanity checks
		if ((pGame == null) || (pGame.getThrower() == null) || (pGame.getPassCoordinate() == null)) {
			return validCoordinates;
		}

		ActingPlayer actingPlayer = pGame.getActingPlayer();

		// Add the thrower tacklezone
		FieldCoordinate[] neighbours = pGame.getFieldModel().findAdjacentCoordinates(
				pGame.getFieldModel().getPlayerCoordinate(pGame.getThrower()), FieldCoordinateBounds.FIELD, 1, false);
		for (FieldCoordinate c : neighbours) {
			Player<?> playerInTz = pGame.getFieldModel().getPlayer(c);
			if ((playerInTz == null) || (playerInTz == actingPlayer.getPlayer())) {
				validCoordinates.add(c);
			}
		}

		Player<?> targetPlayer = pGame.getFieldModel().getPlayer(pGame.getPassCoordinate());

		if (PlayerAction.HAIL_MARY_PASS == pGame.getThrowerAction()) {

			if (targetPlayer != null) {
				validCoordinates.add(pGame.getPassCoordinate());
			}

		} else {

			validCoordinates.addAll(findInterceptCoordinates(pGame));

			// If there's a target, add the target's tacklezones
			if (targetPlayer != null) {
				neighbours = pGame.getFieldModel().findAdjacentCoordinates(pGame.getPassCoordinate(),
						FieldCoordinateBounds.FIELD, 1, false);
				for (FieldCoordinate c : neighbours) {
					Player<?> playerInTz = pGame.getFieldModel().getPlayer(c);
					if ((playerInTz == null) || (playerInTz == actingPlayer.getPlayer())) {
						validCoordinates.add(c);
					}
				}
			} else {
				validCoordinates.add(pGame.getPassCoordinate());
			}

		}

		return validCoordinates;

	}

	private static Set<FieldCoordinate> findInterceptCoordinates(Game pGame) {

		FieldModel fieldModel = pGame.getFieldModel();
		Set<FieldCoordinate> eligibleCoordinates = new HashSet<>();
		Set<FieldCoordinate> closedSet = new HashSet<>();
		List<FieldCoordinate> openSet = new ArrayList<>();
		FieldCoordinate throwerCoord = fieldModel.getPlayerCoordinate(pGame.getThrower());

		// Start with the thrower's location.
		openSet.add(throwerCoord);

		while (!openSet.isEmpty()) {
			// Get an unprocessed coordinate
			FieldCoordinate currentCoordinate = openSet.remove(0);

			// Since coordinates may be added multiple times to the open set, let's check if
			// we already processed this coordinate
			if (closedSet.contains(currentCoordinate)) {
				continue;
			}

			if (currentCoordinate.equals(throwerCoord)
					|| canIntercept(throwerCoord, pGame.getPassCoordinate(), currentCoordinate)) {
				// This coordinate is eligible to intercept, so we add it to the list...
				eligibleCoordinates.add(currentCoordinate);

				// ... and queue all adjacent non-processed squares for processing
				FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(currentCoordinate,
						FieldCoordinateBounds.FIELD, 1, false);
				for (FieldCoordinate c : adjacentCoordinates)
					if (!closedSet.contains(c)) {
						openSet.add(c);
					}
			}

			// Mark the coordinate as processed
			closedSet.add(currentCoordinate);
		}

		// Remove coordinates occupied by players in the list.
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		FieldCoordinate actingPlayerPosition = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer());
		FieldCoordinate[] playerCoordinates = fieldModel.getPlayerCoordinates();
		for (FieldCoordinate pCoord : playerCoordinates) {
			if (!pCoord.equals(actingPlayerPosition)) {
				eligibleCoordinates.remove(pCoord);
			}
		}

		return eligibleCoordinates;

	}

}
