package com.balancedbytes.games.ffb.util;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.property.CancelSkillProperty;
import com.balancedbytes.games.ffb.model.property.ISkillProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Kalimar
 */
public final class UtilCards {

	public static boolean hasSkill(Game pGame, Player<?> pPlayer, Skill pSkill) {
		if ((pGame == null) || (pPlayer == null) || (pSkill == null)) {
			return false;
		}

		return pPlayer.getSkillsIncludingTemporaryOnes().contains(pSkill);
	}

	public static boolean hasSkillWithProperty(Game game, Player<?> player, ISkillProperty property) {
		return Arrays.stream(findAllSkills(game, player)).anyMatch(skill -> skill.hasSkillProperty(property));
	}

	public static Optional<Skill> getSkillWithProperty(Game game, Player<?> player, ISkillProperty property) {
		return Arrays.stream(findAllSkills(game, player)).filter(skill -> skill.hasSkillProperty(property)).findFirst();
	}

	public static boolean hasUncanceledSkillWithProperty(Game game, Player<?> player, ISkillProperty property) {
		Skill[] skills = findAllSkills(game, player);
		return Arrays.stream(skills).anyMatch(skill -> skill.hasSkillProperty(property))
			&& Arrays.stream(skills)
			.flatMap(skill -> skill.getSkillProperties().stream())
			.noneMatch(skillProperty -> skillProperty instanceof CancelSkillProperty && ((CancelSkillProperty) skillProperty).cancelsProperty(property));
	}

	public static boolean hasSkillToCancelProperty(Game game, Player<?> player, ISkillProperty property) {
		return Arrays.stream(findAllSkills(game, player))
			.flatMap(skill -> skill.getSkillProperties().stream())
			.anyMatch(skillProperty -> skillProperty instanceof CancelSkillProperty && ((CancelSkillProperty) skillProperty).cancelsProperty(property));
	}

	public static boolean hasSkill(Game pGame, ActingPlayer pActingPlayer, Skill pSkill) {
		if (pActingPlayer == null) {
			return false;
		}
		return hasSkill(pGame, pActingPlayer.getPlayer(), pSkill);
	}

	public static boolean hasUnusedSkill(Game pGame, ActingPlayer pActingPlayer, Skill pSkill) {
		if (pActingPlayer == null) {
			return false;
		}
		return (hasSkill(pGame, pActingPlayer.getPlayer(), pSkill) && !pActingPlayer.isSkillUsed(pSkill));
	}

	public static int getPlayerStrength(Game pGame, Player<?> pPlayer) {
		if ((pGame == null) || (pPlayer == null)) {
			return 0;
		}


		return pPlayer.getStrength();
	}

	public static int getPlayerMovement(Game pGame, Player<?> pPlayer) {
		if ((pGame == null) || (pPlayer == null)) {
			return 0;
		}

		return pPlayer.getMovement();
	}

	public static Skill[] findAllSkills(Game pGame, Player<?> pPlayer) {
		return pPlayer.getSkillsIncludingTemporaryOnes().toArray(new Skill[0]);
	}

	public static Card[] findAllActiveCards(Game pGame) {
		List<Card> allActiveCards = new ArrayList<>();
		Collections.addAll(allActiveCards, pGame.getTurnDataHome().getInducementSet().getActiveCards());
		Collections.addAll(allActiveCards, pGame.getTurnDataAway().getInducementSet().getActiveCards());
		return allActiveCards.toArray(new Card[0]);
	}



	public static boolean hasCard(Game pGame, Player<?> pPlayer, Card pCard) {
		if ((pGame == null) || (pPlayer == null) || (pCard == null)) {
			return false;
		}
		for (Card card : pGame.getFieldModel().getCards(pPlayer)) {
			if (card == pCard) {
				return true;
			}
		}
		return false;
	}

	public static Skill getSkillCancelling(Player<?> player, Skill skill) {
		for (Skill playerSkill : player.getSkills()) {
			if (playerSkill.canCancel(skill)) {
				return playerSkill;
			}
		}
		return null;
	}

	public static boolean cancelsSkill(Player<?> player, Skill skill) {
		return getSkillCancelling(player, skill) != null;
	}

	public static Skill getUnusedSkillWithProperty(Game game, ActingPlayer actingPlayer, ISkillProperty property) {
		for (Skill playerSkill : UtilCards.findAllSkills(game, actingPlayer.getPlayer())) {
			if (playerSkill.hasSkillProperty(property) && !actingPlayer.isSkillUsed(playerSkill)) {
				return playerSkill;
			}
		}
		return null;
	}

	public static boolean hasUnusedSkillWithProperty(ActingPlayer actingPlayer, ISkillProperty property) {
		for (Skill playerSkill : actingPlayer.getPlayer().getSkills()) {
			if (playerSkill.hasSkillProperty(property) && !actingPlayer.isSkillUsed(playerSkill)) {
				return true;
			}
		}
		return false;
	}

	public static ReRollSource getRerollSource(Game game, Player<?> player, ReRolledAction action) {
		for (Skill playerSkill : UtilCards.findAllSkills(game, player)) {
			ReRollSource source = playerSkill.getRerollSource(action);
			if (source != null) {
				return source;
			}
		}
		return null;
	}

	public static ReRollSource getUnusedRerollSource(ActingPlayer actingPlayer, ReRolledAction action) {
		for (Skill playerSkill : actingPlayer.getPlayer().getSkills()) {
			ReRollSource source = playerSkill.getRerollSource(action);
			if (source != null && !actingPlayer.isSkillUsed(playerSkill)) {
				return source;
			}
		}
		return null;
	}
}
