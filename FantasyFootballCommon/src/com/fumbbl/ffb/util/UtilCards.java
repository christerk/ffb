package com.fumbbl.ffb.util;

import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Kalimar
 */
public final class UtilCards {

	public static boolean hasSkill(Player<?> pPlayer, Skill pSkill) {
		if ((pPlayer == null) || (pSkill == null)) {
			return false;
		}

		return pPlayer.getSkillsIncludingTemporaryOnes().contains(pSkill);
	}

	public static boolean hasSkillWithProperty(Player<?> player, ISkillProperty property) {
		return Arrays.stream(findAllSkills(player)).anyMatch(skill -> skill.hasSkillProperty(property));
	}

	public static Optional<Skill> getSkillWithProperty(Player<?> player, ISkillProperty property) {
		return Arrays.stream(findAllSkills(player)).filter(skill -> skill.hasSkillProperty(property)).findFirst();
	}

	public static boolean hasUncanceledSkillWithProperty(Player<?> player, ISkillProperty property) {
		Skill[] skills = findAllSkills(player);
		return Arrays.stream(skills).anyMatch(skill -> skill.hasSkillProperty(property))
			&& Arrays.stream(skills)
			.flatMap(skill -> skill.getSkillProperties().stream())
			.noneMatch(skillProperty -> skillProperty instanceof CancelSkillProperty && ((CancelSkillProperty) skillProperty).cancelsProperty(property));
	}

	public static boolean hasSkillToCancelProperty(Player<?> player, ISkillProperty property) {
		return Arrays.stream(findAllSkills(player))
			.flatMap(skill -> skill.getSkillProperties().stream())
			.anyMatch(skillProperty -> skillProperty instanceof CancelSkillProperty && ((CancelSkillProperty) skillProperty).cancelsProperty(property));
	}

	public static boolean hasSkill(ActingPlayer pActingPlayer, Skill pSkill) {
		if (pActingPlayer == null) {
			return false;
		}
		return hasSkill(pActingPlayer.getPlayer(), pSkill);
	}

	public static boolean hasUnusedSkill(ActingPlayer pActingPlayer, Skill pSkill) {
		if (pActingPlayer == null) {
			return false;
		}
		return (hasSkill(pActingPlayer.getPlayer(), pSkill) && !pActingPlayer.isSkillUsed(pSkill));
	}

	public static Skill[] findAllSkills(Player<?> pPlayer) {
		if (pPlayer == null) {
			return new Skill[0];
		}
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
		for (Skill playerSkill : player.getSkillsIncludingTemporaryOnes()) {
			if (playerSkill.canCancel(skill)) {
				return playerSkill;
			}
		}
		return null;
	}

	public static boolean cancelsSkill(Player<?> player, Skill skill) {
		return getSkillCancelling(player, skill) != null;
	}

	public static Skill getUnusedSkillWithProperty(ActingPlayer actingPlayer, ISkillProperty property) {
		for (Skill playerSkill : UtilCards.findAllSkills(actingPlayer.getPlayer())) {
			if (playerSkill.hasSkillProperty(property) && !actingPlayer.isSkillUsed(playerSkill)) {
				return playerSkill;
			}
		}
		return null;
	}

	public static boolean hasUnusedSkillWithProperty(ActingPlayer actingPlayer, ISkillProperty property) {
		for (Skill playerSkill : actingPlayer.getPlayer().getSkillsIncludingTemporaryOnes()) {
			if (playerSkill.hasSkillProperty(property) && !actingPlayer.isSkillUsed(playerSkill)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasUnusedSkillWithProperty(Player<?> player, ISkillProperty property) {
		for (Skill playerSkill : player.getSkillsIncludingTemporaryOnes()) {
			if (playerSkill.hasSkillProperty(property) && !player.isUsed(playerSkill)) {
				return true;
			}
		}
		return false;
	}

	public static ReRollSource getRerollSource(Player<?> player, ReRolledAction action) {
		return Arrays.stream(UtilCards.findAllSkills(player))
			.filter(skill -> skill.getSkillUsageType() == SkillUsageType.REGULAR)
			.map(skill -> skill.getRerollSource(action))
			.filter(Objects::nonNull)
			.min(Comparator.comparingInt(ReRollSource::getPriority))
			.orElse(null);
	}

	public static ReRollSource getUnusedRerollSource(ActingPlayer actingPlayer, ReRolledAction action) {

		return Arrays.stream(UtilCards.findAllSkills(actingPlayer.getPlayer()))
			.filter(skill -> !actingPlayer.isSkillUsed(skill))
			.map(skill -> skill.getRerollSource(action))
			.filter(Objects::nonNull)
			.min(Comparator.comparingInt(ReRollSource::getPriority))
			.orElse(null);
	}
}
