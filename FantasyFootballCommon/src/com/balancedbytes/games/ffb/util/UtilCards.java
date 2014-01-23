package com.balancedbytes.games.ffb.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.InducementSet;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;

/**
 * 
 * @author Kalimar
 */
public final class UtilCards {
	
	public static boolean hasSkill(Game pGame, Player pPlayer, Skill pSkill) {
		if ((pGame == null) || (pPlayer == null) || (pSkill == null)) {
			return false;
		}
		Set<Skill> cardSkills = findSkillsProvidedByCards(pGame, pPlayer);
		return (pPlayer.hasSkill(pSkill) || cardSkills.contains(pSkill));
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
	
	private static Set<Skill> findSkillsProvidedByCards(Game pGame, Player pPlayer) {
		Set<Skill> cardSkills = new HashSet<Skill>();
		if ((pGame == null) || (pPlayer == null)) {
			return cardSkills;
		}
		Card[] playerCards = pGame.getFieldModel().getCards(pPlayer);
		for (Card card : playerCards) {
			switch (card) {
				case BEGUILING_BRACERS:
					cardSkills.add(Skill.BONE_HEAD);
					cardSkills.add(Skill.HYPNOTIC_GAZE);
					cardSkills.add(Skill.SIDE_STEP);
					break;
				case FAWNDOUGHS_HEADBAND:
					cardSkills.add(Skill.ACCURATE);
					cardSkills.add(Skill.PASS);
					break;
				case FORCE_SHIELD:
					cardSkills.add(Skill.FEND);
					cardSkills.add(Skill.SURE_HANDS);
					break;
				case GLOVES_OF_HOLDING:
					cardSkills.add(Skill.CATCH);
					cardSkills.add(Skill.SURE_HANDS);
					break;
				case MAGIC_GLOVES_OF_JARK_LONGARM:
					cardSkills.add(Skill.PASS_BLOCK);
					break;
				case RABBITS_FOOT:
					cardSkills.add(Skill.PRO);
					break;
				case WAND_OF_SMASHING:
					cardSkills.add(Skill.MIGHTY_BLOW);
					break;
				default:
					break;
			}
		}
		return cardSkills;
	}
	
	public static int getPlayerStrength(Game pGame, Player pPlayer) {
		if ((pGame == null) || (pPlayer == null)) {
			return 0;
		}
		int strength = pPlayer.getStrength();
		InducementSet inducementSet = (pPlayer.getTeam() == pGame.getTeamHome()) ? pGame.getTurnDataHome().getInducementSet() : pGame.getTurnDataAway().getInducementSet();
		for (Card card : pGame.getFieldModel().getCards(pPlayer)) {
			switch (card) {
				case GIKTAS_STRENGTH_OF_DA_BEAR:
					if (inducementSet.isActive(card)) {
						strength += 1;
					} else {
						strength -= 1;
					}
					break;
				case WAND_OF_SMASHING:
					strength += 1;
					break;
  			default:
  				break;
			}
		}
		return strength;
	}
	
	public static Skill[] findAllSkills(Game pGame, Player pPlayer) {
		Set<Skill> allSkills = findSkillsProvidedByCards(pGame, pPlayer);
		for (Skill skill : pPlayer.getSkills()) {
			allSkills.add(skill);
		}
		return allSkills.toArray(new Skill[allSkills.size()]);
	}
	
  public static Player[] findAllowedPlayersForCard(Game pGame, Card pCard) {
  	if ((pGame == null) || (pCard == null) || !pCard.getTarget().isPlayedOnPlayer()) {
  		return new Player[0];
  	}
  	List<Player> allowedPlayers = new ArrayList<Player>();
  	Team ownTeam = pGame.getTurnDataHome().getInducementSet().isAvailable(pCard) ? pGame.getTeamHome() : pGame.getTeamAway();
  	Team otherTeam = (pGame.getTeamHome() == ownTeam) ? pGame.getTeamAway() : pGame.getTeamHome();
  	for (Player player : pGame.getPlayers()) {
  		PlayerState playerState = pGame.getFieldModel().getPlayerState(player);
  		FieldCoordinate playerCoordinate = pGame.getFieldModel().getPlayerCoordinate(player);
  		boolean playerInGame = ((playerState != null) && !playerState.isCasualty() && (playerState.getBase() != PlayerState.BANNED) && (playerState.getBase() != PlayerState.MISSING));
  		boolean playerAllowed = (playerInGame && ownTeam.hasPlayer(player));
  		switch (pCard) {
  			case FORCE_SHIELD:
  				playerAllowed &= UtilPlayer.hasBall(pGame, player);
  				break;
  			case RABBITS_FOOT:
  				playerAllowed &= !hasSkill(pGame, player, Skill.LONER);
  				break;
  			case CHOP_BLOCK:
  			  playerAllowed &= playerState.isActive() && (UtilPlayer.findAdjacentBlockablePlayers(pGame, otherTeam, playerCoordinate).length > 0);
  			  break;
  			default:
  				break;
  		}
  		if (playerAllowed) {
  			allowedPlayers.add(player);
  		}
  	}
  	return allowedPlayers.toArray(new Player[allowedPlayers.size()]);
  }
  
  public static Card[] findAllActiveCards(Game pGame) {
  	List<Card> allActiveCards = new ArrayList<Card>();
  	for (Card card : pGame.getTurnDataHome().getInducementSet().getActiveCards()) {
  		allActiveCards.add(card);
  	}
  	for (Card card : pGame.getTurnDataAway().getInducementSet().getActiveCards()) {
  		allActiveCards.add(card);
  	}
  	return allActiveCards.toArray(new Card[allActiveCards.size()]);
  }

  public static boolean isCardActive(Game pGame, Card pCard) {
    for (Card card : findAllActiveCards(pGame)) {
      if (card == pCard) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasCard(Game pGame, Player pPlayer, Card pCard) {
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
  
}
