package com.fumbbl.ffb.server;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InjuryResultTest {

	@Test
	void tracksSecretWeaponMovedToKnockoutBox() {
		assertTrue(InjuryResult.shouldTrackSecretWeaponSendOff(secretWeaponPlayer(), injuryContext(PlayerState.KNOCKED_OUT)));
	}

	@Test
	void tracksSecretWeaponMovedToReserveBox() {
		assertTrue(InjuryResult.shouldTrackSecretWeaponSendOff(secretWeaponPlayer(), injuryContext(PlayerState.RESERVE)));
	}

	@Test
	void doesNotTrackSecretWeaponMovedToCasualtyBox() {
		assertFalse(InjuryResult.shouldTrackSecretWeaponSendOff(secretWeaponPlayer(), injuryContext(PlayerState.BADLY_HURT)));
		assertFalse(InjuryResult.shouldTrackSecretWeaponSendOff(secretWeaponPlayer(), injuryContext(PlayerState.SERIOUS_INJURY)));
		assertFalse(InjuryResult.shouldTrackSecretWeaponSendOff(secretWeaponPlayer(), injuryContext(PlayerState.RIP)));
	}

	@Test
	void doesNotTrackPlayersWithoutSecretWeapon() {
		assertFalse(InjuryResult.shouldTrackSecretWeaponSendOff(playerWithoutSecretWeapon(), injuryContext(PlayerState.KNOCKED_OUT)));
	}

	@SuppressWarnings("unchecked")
	private Player<?> secretWeaponPlayer() {
		Player<?> player = mock(Player.class);
		when(player.hasSkillProperty(NamedProperties.getsSentOffAtEndOfDrive)).thenReturn(true);
		return player;
	}

	@SuppressWarnings("unchecked")
	private Player<?> playerWithoutSecretWeapon() {
		Player<?> player = mock(Player.class);
		when(player.hasSkillProperty(NamedProperties.getsSentOffAtEndOfDrive)).thenReturn(false);
		return player;
	}

	private InjuryContext injuryContext(int playerState) {
		InjuryContext context = new InjuryContext();
		context.setInjury(new PlayerState(playerState));
		return context;
	}

}
