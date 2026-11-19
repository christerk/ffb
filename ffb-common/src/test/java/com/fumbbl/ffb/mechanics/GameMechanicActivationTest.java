package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.skill.bb2025.SquigRage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameMechanicActivationTest {

	private final GameMechanic bb2016 = new com.fumbbl.ffb.mechanics.bb2016.GameMechanic();
	private final GameMechanic bb2020 = new com.fumbbl.ffb.mechanics.bb2020.GameMechanic();
	private final GameMechanic bb2025 = new com.fumbbl.ffb.mechanics.bb2025.GameMechanic();

	@Test
	void onlySquigRageActivatesWithoutDeclaredActionInBb2025() {
		assertTrue(bb2025.isActivationWithoutDeclaredAction(PlayerAction.SQUIG_RAGE));
		assertFalse(bb2025.isActivationWithoutDeclaredAction(PlayerAction.MOVE));
		assertFalse(bb2025.isActivationWithoutDeclaredAction(null));
	}

	@Test
	void olderRulesetsNeverActivateWithoutDeclaredAction() {
		for (PlayerAction action : PlayerAction.values()) {
			assertFalse(bb2016.isActivationWithoutDeclaredAction(action));
			assertFalse(bb2020.isActivationWithoutDeclaredAction(action));
		}
	}

	@Test
	void proneSquigPlayerOnlyActivatedByRollRemainsProneInBb2025() {
		ActingPlayer actingPlayer = proneSquigActingPlayer();

		assertTrue(bb2025.remainsProneWhenDeactivated(actingPlayer));
	}

	@Test
	void olderRulesetsNeverKeepPlayersProne() {
		ActingPlayer actingPlayer = proneSquigActingPlayer();

		assertFalse(bb2016.remainsProneWhenDeactivated(actingPlayer));
		assertFalse(bb2020.remainsProneWhenDeactivated(actingPlayer));
	}

	private ActingPlayer proneSquigActingPlayer() {
		SquigRage squigRage = new SquigRage();
		Player<?> player = mock(Player.class);
		ActingPlayer actingPlayer = mock(ActingPlayer.class);
		when(player.getSkillWithProperty(NamedProperties.requiresRageRollBeforeDeclaringAction)).thenReturn(squigRage);
		doReturn(player).when(actingPlayer).getPlayer();
		when(actingPlayer.isSkillUsed(squigRage)).thenReturn(true);
		when(actingPlayer.isStandingUp()).thenReturn(true);
		return actingPlayer;
	}
}
