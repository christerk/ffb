package com.balancedbytes.games.ffb.server.request.fumbbl;

import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.PlayerGender;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.inducement.Inducement;
import com.balancedbytes.games.ffb.inducement.Usage;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.InducementSet;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.TeamResult;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.IXmlWriteable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Kalimar
 */
public class FumbblResult implements IXmlWriteable {

	// --- game result tags --

	private static final String _XML_TAG_GAME_RESULT = "gameResult";

	private static final String _XML_ATTRIBUTE_REPLAY_ID = "replayId";

	// --- team result tags --

	private static final String _XML_TAG_TEAM_RESULT = "teamResult";

	private static final String XML_ATTRIBUTE_TEAM_ID = "teamId";

	private static final String _XML_TAG_SCORE = "score";
	private static final String _XML_TAG_CONCEDED = "conceded";
	private static final String _XML_TAG_FAME = "fame";
	private static final String _XML_TAG_SPECTATORS = "spectators";
	private static final String _XML_TAG_WINNINGS = "winnings";
	private static final String _XML_TAG_FAN_FACTOR_MODIFIER = "fanFactorModifier";
	private static final String _XML_TAG_SPIRALLING_EXPENSES = "spirallingExpenses";

	private static final String _XML_TAG_CASUALTIES_SUFFERED = "casualtiesSuffered";
	private static final String _XML_ATTRIBUTE_BADLY_HURT = "badlyHurt";
	private static final String _XML_ATTRIBUTE_SERIOUS_INJURY = "seriousInjury";
	private static final String _XML_ATTRIBUTE_RIP = "rip";

	private static final String _XML_TAG_INDUCEMENT_LIST = "inducementList";
	private static final String _XML_TAG_INDUCEMENT = "inducement";
	private static final String _XML_TAG_STAR_PLAYER = "starPlayer";
	private static final String _XML_TAG_MERCENARY = "mercenary";
	private static final String _XML_TAG_CARD = "card";

	private static final String _XML_ATTRIBUTE_TYPE = "type";
	private static final String _XML_ATTRIBUTE_VALUE = "value";
	private static final String _XML_ATTRIBUTE_NAME = "name";
	private static final String _XML_ATTRIBUTE_POSITION_ID = "positionId";
	private static final String _XML_ATTRIBUTE_ADDED_SKILL = "addedSkill";

	private static final String _XML_TAG_PETTY_CASH_TRANSFERRED = "pettyCashTransferred";
	private static final String _XML_TAG_PETTY_CASH_USED = "pettyCashUsed";
	private static final String _XML_TAG_TEAM_VALUE = "teamValue";

	private static final String _XML_TAG_PLAYER_RESULT_LIST = "playerResultList";

	// --- player result tags ---

	private static final String _XML_TAG_PLAYER_RESULT = "playerResult";

	private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
	private static final String _XML_ATTRIBUTE_PLAYER_TYPE = "playerType";
	private static final String _XML_ATTRIBUTE_PLAYER_NAME = "name";
	private static final String _XML_ATTRIBUTE_PLAYER_GENDER = "gender";

	private static final String _XML_TAG_STAR_PLAYER_POINTS = "starPlayerPoints";
	private static final String _XML_ATTRIBUTE_CURRENT = "current";
	private static final String _XML_ATTRIBUTE_EARNED = "earned";

	private static final String _XML_TAG_COMPLETIONS = "completions";
	private static final String _XML_TAG_TOUCHDOWNS = "touchdowns";
	private static final String _XML_TAG_DEFLECTIONS = "deflections";
	private static final String _XML_TAG_INTERCEPTIONS = "interceptions";
	private static final String _XML_TAG_CASUALTIES = "casualties";
	private static final String _XML_TAG_PLAYER_AWARDS = "playerAwards";

	private static final String _XML_TAG_STATISTICS = "statistics";
	private static final String _XML_TAG_BLOCKS = "blocks";
	private static final String _XML_TAG_FOULS = "fouls";
	private static final String _XML_TAG_RUSHING = "rushing";
	private static final String _XML_TAG_PASSING = "passing";
	private static final String _XML_TAG_TURNS_PLAYED = "turnsPlayed";

	private static final String _XML_TAG_DEFECTING = "defecting";

	private static final String _XML_TAG_INJURY = "injury";

	private Game fGame;

	public FumbblResult(Game pGame) {
		fGame = pGame;
	}

	public Game getGame() {
		return fGame;
	}

	// XML serialization

	public void addToXml(TransformerHandler pHandler) {

		if (getGame() != null) {

			GameResult gameResult = getGame().getGameResult();

			AttributesImpl attributes = new AttributesImpl();
			UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_REPLAY_ID, getGame().getId());
			UtilXml.startElement(pHandler, _XML_TAG_GAME_RESULT, attributes);

			addToXml(pHandler, gameResult.getTeamResultHome());
			addToXml(pHandler, gameResult.getTeamResultAway());

			UtilXml.endElement(pHandler, _XML_TAG_GAME_RESULT);

		}

	}

	public String toXml(boolean pIndent) {
		return UtilXml.toXml(this, pIndent);
	}

	private void addToXml(TransformerHandler pHandler, TeamResult pTeamResult) {

		if ((getGame() != null) && (pTeamResult != null)) {

			AttributesImpl attributes = new AttributesImpl();
			UtilXml.addAttribute(attributes, XML_ATTRIBUTE_TEAM_ID, pTeamResult.getTeam().getId());
			UtilXml.startElement(pHandler, _XML_TAG_TEAM_RESULT, attributes);

			UtilXml.addValueElement(pHandler, _XML_TAG_SCORE, pTeamResult.getScore());
			UtilXml.addValueElement(pHandler, _XML_TAG_CONCEDED, pTeamResult.hasConceded());
			if (pTeamResult.getSpectators() > 0) {
				UtilXml.addValueElement(pHandler, _XML_TAG_SPECTATORS, pTeamResult.getSpectators());
				UtilXml.addValueElement(pHandler, _XML_TAG_FAME, pTeamResult.getFame());
			}
			if (pTeamResult.getWinnings() > 0) {
				UtilXml.addValueElement(pHandler, _XML_TAG_WINNINGS, pTeamResult.getWinnings());
			}
			if (pTeamResult.getFanFactorModifier() != 0) {
				UtilXml.addValueElement(pHandler, _XML_TAG_FAN_FACTOR_MODIFIER, pTeamResult.getFanFactorModifier());
			}
			if (pTeamResult.getSpirallingExpenses() > 0) {
				UtilXml.addValueElement(pHandler, _XML_TAG_SPIRALLING_EXPENSES, pTeamResult.getSpirallingExpenses());
			}
			if (pTeamResult.getPettyCashTransferred() > 0) {
				UtilXml.addValueElement(pHandler, _XML_TAG_PETTY_CASH_TRANSFERRED, pTeamResult.getPettyCashTransferred());
			}
			if (pTeamResult.getPettyCashUsed() > 0) {
				UtilXml.addValueElement(pHandler, _XML_TAG_PETTY_CASH_USED, pTeamResult.getPettyCashUsed());
			}
			if (pTeamResult.getTeamValue() > 0) {
				UtilXml.addValueElement(pHandler, _XML_TAG_TEAM_VALUE, pTeamResult.getTeamValue());
			}

			attributes = new AttributesImpl();
			UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_BADLY_HURT, pTeamResult.getBadlyHurtSuffered());
			UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SERIOUS_INJURY, pTeamResult.getSeriousInjurySuffered());
			UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_RIP, pTeamResult.getRipSuffered());
			UtilXml.addEmptyElement(pHandler, _XML_TAG_CASUALTIES_SUFFERED, attributes);

			if (getGame().getTeamHome() == pTeamResult.getTeam()) {
				addToXml(pHandler, pTeamResult.getTeam(), getGame().getTurnDataHome().getInducementSet());
			} else {
				addToXml(pHandler, pTeamResult.getTeam(), getGame().getTurnDataAway().getInducementSet());
			}

			UtilXml.startElement(pHandler, _XML_TAG_PLAYER_RESULT_LIST);
			Player<?>[] players = pTeamResult.getTeam().getPlayers();
			for (Player<?> player : players) {
				addToXml(pHandler, pTeamResult.getPlayerResult(player), player);
			}
			UtilXml.endElement(pHandler, _XML_TAG_PLAYER_RESULT_LIST);

			UtilXml.endElement(pHandler, _XML_TAG_TEAM_RESULT);

		}

	}

	private void addToXml(TransformerHandler pHandler, Team pTeam, InducementSet pInducementSet) {

		if ((getGame() != null) && (pTeam != null) && (pInducementSet != null)) {

			List<Inducement> inducements = new ArrayList<>();
			for (Inducement inducement : pInducementSet.getInducements()) {
				if (!Usage.EXCLUDE_FROM_RESULT.contains(inducement.getType().getUsage())) {
					inducements.add(inducement);
				}
			}

			List<Player<?>> starPlayers = new ArrayList<>();
			List<Player<?>> mercenaries = new ArrayList<>();
			for (Player<?> player : pTeam.getPlayers()) {
				if (player.getPlayerType() == PlayerType.STAR) {
					starPlayers.add(player);
				}
				if (player.getPlayerType() == PlayerType.MERCENARY) {
					mercenaries.add(player);
				}
			}

			Card[] cards = pInducementSet.getAllCards();

			if ((inducements.size() > 0) || (starPlayers.size() > 0) || (mercenaries.size() > 0)
					|| ArrayTool.isProvided(cards)) {

				UtilXml.startElement(pHandler, _XML_TAG_INDUCEMENT_LIST);

				// Inducements
				for (Inducement inducement : inducements) {
					// TODO: handle bribes coming from Get The Ref
					if (inducement.getValue() > 0) {
						AttributesImpl attributes = new AttributesImpl();
						UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TYPE, inducement.getType().getName());
						UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_VALUE, inducement.getValue());
						UtilXml.addEmptyElement(pHandler, _XML_TAG_INDUCEMENT, attributes);
					}
				}

				// Star Players
				for (Player<?> starPlayer : starPlayers) {
					AttributesImpl attributes = new AttributesImpl();
					UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NAME, starPlayer.getName());
					UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_POSITION_ID, starPlayer.getPositionId());
					UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, starPlayer.getId());
					UtilXml.addEmptyElement(pHandler, _XML_TAG_STAR_PLAYER, attributes);
				}

				// Mercenaries
				for (Player<?> mercenary : mercenaries) {
					Skill addedSkill = null;
					Set<Skill> rosterSkills = new HashSet<>();
					for (Skill skill : mercenary.getPosition().getSkills()) {
						rosterSkills.add(skill);
					}
					for (Skill skill : mercenary.getSkills()) {
						if (!rosterSkills.contains(skill) && (!skill.hasSkillProperty(NamedProperties.hasToRollToUseTeamReroll))) {
							addedSkill = skill;
							break;
						}
					}
					AttributesImpl attributes = new AttributesImpl();
					UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NAME, mercenary.getName());
					UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_POSITION_ID, mercenary.getPositionId());
					UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, mercenary.getId());
					UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ADDED_SKILL,
							(addedSkill != null) ? addedSkill.getName() : null);
					UtilXml.addEmptyElement(pHandler, _XML_TAG_MERCENARY, attributes);
				}

				// Cards
				for (Card card : cards) {
					AttributesImpl attributes = new AttributesImpl();
					UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TYPE, card.getType().getInducementNameSingle());
					UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NAME, card.getName());
					UtilXml.addEmptyElement(pHandler, _XML_TAG_CARD, attributes);
				}

				UtilXml.endElement(pHandler, _XML_TAG_INDUCEMENT_LIST);

			}

		}

	}

	private void addToXml(TransformerHandler pHandler, PlayerResult pPlayerResult, Player<?> player) {

		if ((getGame() != null) && (pPlayerResult != null)) {

			String playerTypeName = (pPlayerResult.getPlayer().getPlayerType() != null)
					? pPlayerResult.getPlayer().getPlayerType().getName()
					: null;

			AttributesImpl attributes = new AttributesImpl();
			UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, pPlayerResult.getPlayerId());
			UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_TYPE, playerTypeName);
			PlayerGender gender = player.getPlayerGender() != null ? player.getPlayerGender() : PlayerGender.NEUTRAL;
			UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_GENDER, gender.getName());
			UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_NAME, player.getName());
			UtilXml.startElement(pHandler, _XML_TAG_PLAYER_RESULT, attributes);

			UtilXml.addValueElement(pHandler, _XML_TAG_DEFECTING, pPlayerResult.isDefecting());

			if (pPlayerResult.totalEarnedSpps() > 0) {

				attributes = new AttributesImpl();
				UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CURRENT, pPlayerResult.getCurrentSpps());
				UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_EARNED, pPlayerResult.totalEarnedSpps());
				UtilXml.startElement(pHandler, _XML_TAG_STAR_PLAYER_POINTS, attributes);

				if (pPlayerResult.getCompletions() > 0) {
					UtilXml.addValueElement(pHandler, _XML_TAG_COMPLETIONS, pPlayerResult.getCompletions());
				}
				if (pPlayerResult.getTouchdowns() > 0) {
					UtilXml.addValueElement(pHandler, _XML_TAG_TOUCHDOWNS, pPlayerResult.getTouchdowns());
				}
				if (pPlayerResult.getDeflections() > 0) {
					UtilXml.addValueElement(pHandler, _XML_TAG_DEFLECTIONS, pPlayerResult.getDeflections());
				}
				if (pPlayerResult.getInterceptions() > 0) {
					UtilXml.addValueElement(pHandler, _XML_TAG_INTERCEPTIONS, pPlayerResult.getInterceptions());
				}
				if (pPlayerResult.getCasualties() > 0) {
					UtilXml.addValueElement(pHandler, _XML_TAG_CASUALTIES, pPlayerResult.getCasualties());
				}
				if (pPlayerResult.getPlayerAwards() > 0) {
					UtilXml.addValueElement(pHandler, _XML_TAG_PLAYER_AWARDS, pPlayerResult.getPlayerAwards());
				}

				UtilXml.endElement(pHandler, _XML_TAG_STAR_PLAYER_POINTS);

			}

			if (((pPlayerResult.totalEarnedSpps() > 0) || (pPlayerResult.getBlocks() > 0) || (pPlayerResult.getFouls() > 0)
					|| (pPlayerResult.getRushing() != 0) || (pPlayerResult.getPassing() != 0)
					|| (pPlayerResult.getTurnsPlayed() > 0))) {

				UtilXml.startElement(pHandler, _XML_TAG_STATISTICS);

				if (pPlayerResult.getBlocks() > 0) {
					UtilXml.addValueElement(pHandler, _XML_TAG_BLOCKS, pPlayerResult.getBlocks());
				}
				if (pPlayerResult.getFouls() > 0) {
					UtilXml.addValueElement(pHandler, _XML_TAG_FOULS, pPlayerResult.getFouls());
				}
				if (pPlayerResult.getRushing() != 0) {
					UtilXml.addValueElement(pHandler, _XML_TAG_RUSHING, pPlayerResult.getRushing());
				}
				if (pPlayerResult.getPassing() != 0) {
					UtilXml.addValueElement(pHandler, _XML_TAG_PASSING, pPlayerResult.getPassing());
				}
				if (pPlayerResult.getTurnsPlayed() > 0) {
					UtilXml.addValueElement(pHandler, _XML_TAG_TURNS_PLAYED, pPlayerResult.getTurnsPlayed());
				}

				UtilXml.endElement(pHandler, _XML_TAG_STATISTICS);

			}

			if (pPlayerResult.getSeriousInjury() != null) {
				UtilXml.addValueElement(pHandler, _XML_TAG_INJURY, pPlayerResult.getSeriousInjury().getName());
			}

			if (pPlayerResult.getSeriousInjuryDecay() != null) {
				UtilXml.addValueElement(pHandler, _XML_TAG_INJURY, pPlayerResult.getSeriousInjuryDecay().getName());
			}

			UtilXml.endElement(pHandler, _XML_TAG_PLAYER_RESULT);

		}

	}

}
