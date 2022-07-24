package com.fumbbl.ffb.util;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Set;

/**
 * @author Kalimar
 */
public class UtilActingPlayer {

	public static boolean changeActingPlayer(Game pGame, String pActingPlayerId, PlayerAction pPlayerAction,
																					 boolean jumping) {

		boolean changed = false;

		FieldModel fieldModel = pGame.getFieldModel();
		ActingPlayer actingPlayer = pGame.getActingPlayer();

		Player<?> oldPlayer = actingPlayer.getPlayer();
		Player<?> newPlayer = pGame.getPlayerById(pActingPlayerId);

		if ((oldPlayer != null) && (oldPlayer != newPlayer)) {
			changed = true;
			PlayerState currentState = pGame.getFieldModel().getPlayerState(oldPlayer);
			if (currentState.getBase() == PlayerState.MOVING) {
				boolean isThrowBombAction = PlayerAction.THROW_BOMB == actingPlayer.getPlayerAction();
				boolean isHailMaryBombAction = PlayerAction.HAIL_MARY_BOMB == actingPlayer.getPlayerAction();
				if (actingPlayer.hasActed() && ((!isThrowBombAction && !isHailMaryBombAction)
					|| (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.enableThrowBombAction)
					&& !UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.enableThrowBombAction)))) {
					pGame.getFieldModel().setPlayerState(oldPlayer,
						currentState.changeBase(PlayerState.STANDING).changeActive(false));
				} else if (actingPlayer.isStandingUp()) {
					pGame.getFieldModel().setPlayerState(oldPlayer, currentState.changeBase(PlayerState.PRONE));
				} else {
					pGame.getFieldModel().setPlayerState(oldPlayer, currentState.changeBase(PlayerState.STANDING));
				}

			}
			if (!actingPlayer.hasActed()) {
				GameMechanic mechanic = (GameMechanic) pGame.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
				Set<String> enhancementsToRemove = mechanic.enhancementsToRemoveAtEndOfTurn(pGame.getFactory(FactoryType.Factory.SKILL));
				enhancementsToRemove.forEach(enhancement -> pGame.getFieldModel().removeSkillEnhancements(actingPlayer.getPlayer(), enhancement));

				SkillFactory skillFactory = pGame.getFactory(FactoryType.Factory.SKILL);
				actingPlayer.getSkillsGrantedBy().forEach((key, value) -> {
					if (key != null && value != null) {
						Skill skill = skillFactory.forName(key);
						value.stream().map(pGame::getPlayerById).forEach(player -> player.markUnused(skill, pGame));
					}
				});
			}

			pGame.getActingPlayer().setPlayer(null);
		}

		if (newPlayer != null) {
			if (newPlayer != oldPlayer) {
				changed = true;
				actingPlayer.setPlayer(newPlayer);
				PlayerState oldState = pGame.getFieldModel().getPlayerState(newPlayer);
				actingPlayer.setOldPlayerState(oldState);
				actingPlayer.setStandingUp(oldState.getBase() == PlayerState.PRONE);
				// show acting player as moving
				fieldModel.setPlayerState(newPlayer, oldState.changeBase(PlayerState.MOVING));
			}
			actingPlayer.setPlayerAction(pPlayerAction);
			actingPlayer.setJumping(jumping);
		}

		if (changed) {
			fieldModel.clearTrackNumbers();
			fieldModel.clearDiceDecorations();
			fieldModel.clearPushbackSquares();
			fieldModel.clearMoveSquares();
			Player<?> player = pGame.getActingPlayer().getPlayer();
			if (player != null) {
				PlayerState playerState = pGame.getFieldModel().getPlayerState(player);
				Skill skillThatAllowsReroll = player.getSkillWithProperty(NamedProperties.canRerollOncePerTurn);
				if (playerState.hasUsedPro()) {
					pGame.getActingPlayer().markSkillUsed(skillThatAllowsReroll);
				}
			}
			Player<?>[] players = pGame.getPlayers();
			for (Player<?> value : players) {
				PlayerState playerState = fieldModel.getPlayerState(value);
				if ((playerState.getBase() == PlayerState.BLOCKED) || ((playerState.getBase() == PlayerState.MOVING)
					&& (value != actingPlayer.getPlayer()) && (value != pGame.getThrower()))) {
					fieldModel.setPlayerState(value, playerState.changeBase(PlayerState.STANDING));
				}
			}
		}

		return changed;

	}

}
