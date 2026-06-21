package com.fumbbl.ffb.server;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class InjuryResultTest {

	@Test
	void tracksSecretWeaponMovedToKnockoutBox() {
		Assertions.assertTrue(InjuryResult.shouldTrackSecretWeaponSendOff(secretWeaponPlayer(), injuryContext(PlayerState.KNOCKED_OUT)));
	}

	@Test
	void tracksSecretWeaponMovedToReserveBox() {
		Assertions.assertTrue(InjuryResult.shouldTrackSecretWeaponSendOff(secretWeaponPlayer(), injuryContext(PlayerState.RESERVE)));
	}

	@Test
	void doesNotTrackSecretWeaponMovedToCasualtyBox() {
		Assertions.assertFalse(InjuryResult.shouldTrackSecretWeaponSendOff(secretWeaponPlayer(), injuryContext(PlayerState.BADLY_HURT)));
		Assertions.assertFalse(InjuryResult.shouldTrackSecretWeaponSendOff(secretWeaponPlayer(), injuryContext(PlayerState.SERIOUS_INJURY)));
		Assertions.assertFalse(InjuryResult.shouldTrackSecretWeaponSendOff(secretWeaponPlayer(), injuryContext(PlayerState.RIP)));
	}

	@Test
	void doesNotTrackPlayersWithoutSecretWeapon() {
		Assertions.assertFalse(InjuryResult.shouldTrackSecretWeaponSendOff(playerWithoutSecretWeapon(), injuryContext(PlayerState.KNOCKED_OUT)));
	}

	@SuppressWarnings("unchecked")
	private Player<?> secretWeaponPlayer() {
		Player<?> player = Mockito.mock(Player.class);
		Mockito.when(player.hasSkillProperty(NamedProperties.getsSentOffAtEndOfDrive)).thenReturn(true);
		return player;
	}

	@SuppressWarnings("unchecked")
	private Player<?> playerWithoutSecretWeapon() {
		Player<?> player = Mockito.mock(Player.class);
		Mockito.when(player.hasSkillProperty(NamedProperties.getsSentOffAtEndOfDrive)).thenReturn(false);
		return player;
	}

	private InjuryContext injuryContext(int playerStateId) {
		InjuryContext context = new InjuryContext();
		context.setInjury(new PlayerState(playerStateId));
		return context;
	}

}
