package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.DodgeModifiers.DodgeContext;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.SkillConstants;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Kalimar
 */
public class DodgeModifierFactory implements IRollModifierFactory {

	static DodgeModifiers dodgeModifiers;

	public DodgeModifierFactory() {
		dodgeModifiers = new DodgeModifiers();
	}

	public DodgeModifier forName(String pName) {
		return dodgeModifiers.values().get(pName.toLowerCase());
	}

	public Set<DodgeModifier> findDodgeModifiers(Game pGame, FieldCoordinate pCoordinateFrom,
			FieldCoordinate pCoordinateTo, int pTacklezoneModifier) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();

		DodgeContext context = new DodgeContext(actingPlayer, pCoordinateFrom);
		Set<DodgeModifier> accumulatedModifiers = new HashSet<>(UtilCards.getDodgeModifiers(actingPlayer, context));

		DodgeModifier tacklezoneModifier = findTacklezoneModifier(pGame, pCoordinateTo, pTacklezoneModifier);

		int prehensileTails = findNumberOfPrehensileTails(pGame, pCoordinateFrom);

		DodgeModifier prehensileTailModifier = dodgeModifiers.prehensileTailModifier(prehensileTails);

		if (prehensileTailModifier != null) {
			accumulatedModifiers.add(prehensileTailModifier);
		}

		boolean preventStunty = UtilCards.hasSkillWithProperty(actingPlayer.getPlayer(),
				NamedProperties.preventStuntyDodgeModifier);
		if (tacklezoneModifier != null && (context.addTackleZoneModifier || preventStunty)) {
			accumulatedModifiers.add(tacklezoneModifier);
		}

		return accumulatedModifiers;
	}

	public DodgeModifier[] toArray(Set<DodgeModifier> pDodgeModifierSet) {
		if (pDodgeModifierSet != null) {
			DodgeModifier[] dodgeModifierArray = pDodgeModifierSet.toArray(new DodgeModifier[0]);
			Arrays.sort(dodgeModifierArray, Comparator.comparing(DodgeModifier::getName));
			return dodgeModifierArray;
		} else {
			return new DodgeModifier[0];
		}
	}

	private DodgeModifier findTacklezoneModifier(Game pGame, FieldCoordinate pCoordinateTo, int pModifier) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		Team otherTeam = UtilPlayer.findOtherTeam(pGame, actingPlayer.getPlayer());
		int tacklezones = pModifier;
		Player<?>[] adjacentPlayers = UtilPlayer.findAdjacentPlayersWithTacklezones(pGame, otherTeam, pCoordinateTo, false);
		for (Player<?> player : adjacentPlayers) {
			if (!UtilCards.hasSkillWithProperty(player, NamedProperties.hasNoTacklezone)) {
				tacklezones++;
			}
		}
		for (Map.Entry<String, DodgeModifier> entry : dodgeModifiers.values().entrySet()) {
			DodgeModifier modifier = entry.getValue();
			if (modifier.isTacklezoneModifier() && (modifier.getModifier() == tacklezones)) {
				return modifier;
			}
		}
		return null;
	}

	private int findNumberOfPrehensileTails(Game pGame, FieldCoordinate pCoordinateFrom) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		Team otherTeam = UtilPlayer.findOtherTeam(pGame, actingPlayer.getPlayer());
		int nrOfPrehensileTails = 0;
		Player<?>[] opponents = UtilPlayer.findAdjacentPlayersWithTacklezones(pGame, otherTeam, pCoordinateFrom, true);
		for (Player<?> opponent : opponents) {
			if (UtilCards.hasSkill(pGame, opponent, SkillConstants.PREHENSILE_TAIL)) {
				nrOfPrehensileTails++;
			}
		}
		return nrOfPrehensileTails;
	}
}
