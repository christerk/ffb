package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;
import java.util.Set;

/**
 * Finds the modifiers an opponent could still add by declaring Diving Tackle.
 * <p>
 * Diving Tackle is declared after the roll has been made, so any roll the moving player is told about while an
 * eligible tackler is adjacent has to include its modifier. That holds for the move square preview as well as for
 * the modifier combinations offered to rescue a roll, as a combination that only succeeds without Diving Tackle
 * would be spent for nothing.
 */
public class DivingTackleModifierService {

	/**
	 * @return the jump modifiers an opponent could still add, empty when the jumping player cannot be tackled or no
	 * eligible tackler is adjacent
	 */
	public Set<JumpModifier> jumpModifiers(Game game, Player<?> player, FieldCoordinate from, FieldCoordinate to) {
		if (UtilCards.hasSkillToCancelProperty(player, NamedProperties.canAttemptToTackleJumpingPlayer)) {
			return new HashSet<>();
		}
		Player<?>[] divingTacklers =
			UtilPlayer.findEligibleDivingTacklers(game, from, to, NamedProperties.canAttemptToTackleJumpingPlayer);
		if (!ArrayTool.isProvided(divingTacklers)) {
			return new HashSet<>();
		}
		return divingTacklers[0].getSkillsIncludingTemporaryOnes().stream()
			.filter(skill -> skill.hasSkillProperty(NamedProperties.canAttemptToTackleJumpingPlayer))
			.findFirst().map(skill -> new HashSet<>(skill.getJumpModifiers())).orElseGet(HashSet::new);
	}

	/**
	 * @return the dodge modifiers an opponent could still add, empty when no eligible tackler is adjacent
	 */
	public Set<DodgeModifier> dodgeModifiers(Game game, FieldCoordinate from, FieldCoordinate to) {
		Player<?>[] divingTacklers =
			UtilPlayer.findEligibleDivingTacklers(game, from, to, NamedProperties.canAttemptToTackleDodgingPlayer);
		if (!ArrayTool.isProvided(divingTacklers)) {
			return new HashSet<>();
		}
		return divingTacklers[0].getSkillsIncludingTemporaryOnes().stream()
			.filter(skill -> skill.hasSkillProperty(NamedProperties.canAttemptToTackleDodgingPlayer))
			.findFirst().map(skill -> new HashSet<>(skill.getDodgeModifiers())).orElseGet(HashSet::new);
	}
}
